package com.example.checkbud;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.checkbud.data.CheckDb;
import com.example.checkbud.data.CheckEntry;
import com.example.checkbud.data.CheckViewModel;
import com.example.checkbud.data.EntryExecutor;
import com.example.checkbud.databinding.ActivityMainBinding;
import com.example.checkbud.utils.CheckAdapter;
import com.example.checkbud.utils.SwipeToDeleteCallback;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CheckAdapter.CheckAdapterInterface {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinder;

    //variables for storing and confirming info
    private double percentage;
    // integers keeping track of the various values
    public static int validCurr, noteCurr, invalidCurr, total, validDb, noteDb, invalidDb;

    //keys to store boolean info
    private static final String DB_EMPTY_BOOLEAN = "is dB empty";

    //keep track of last use of app
    private static SharedPreferences.Editor sharedPrefEd;
    private SharedPreferences sharedPref;

    private CheckViewModel checkViewModel;
    private static CheckDb checkDb;
    private List<CheckEntry> checkEntryList;
    private CheckAdapter checkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinder = DataBindingUtil.setContentView(this, R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefEd = sharedPref.edit();
        sharedPrefEd.apply();
        checkDb = CheckDb.getInstance(getApplicationContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        checkAdapter = new CheckAdapter(this);
        mainBinder.recycler.setLayoutManager(layoutManager);
        mainBinder.recycler.setAdapter(checkAdapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(checkAdapter, this));
        itemTouchHelper.attachToRecyclerView(mainBinder.recycler);

        //setup viewModel
        checkViewModel = ViewModelProviders.of(this).get(CheckViewModel.class);
        checkViewModel.getCheckEntries().observe(this, new Observer<List<CheckEntry>>() {
            @Override
            public void onChanged(@Nullable List<CheckEntry> checkEntries) {
                if (checkEntries != null) {
                    for (int i = 0; i < checkEntries.size(); i++) {
                        CheckEntry selectedEntry = checkEntries.get(i);
                        validDb += selectedEntry.getValid();
                        invalidDb += selectedEntry.getInvalid();
                        noteDb += selectedEntry.getNote();
                    }
                    checkEntryList = checkEntries;
                }
                updatePercentage();
                displayTotal();

                checkAdapter.setCheckEntries(checkEntries);
            }
        });
    }

    public static void setDbEmptyBoolean(Boolean input) {
        sharedPrefEd.putBoolean(DB_EMPTY_BOOLEAN, input);
        sharedPrefEd.apply();
    }

    private boolean getDbEmptyBoolean() {
        return sharedPref.getBoolean(DB_EMPTY_BOOLEAN, true);
    }

    /**
     * Inflate menu offering a reset button.
     *
     * @param menu the menu to be inflated
     * @return the inflated menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.reset_menu) {
            confirmReset();
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmReset() {
        //Check whether the database is empty and return if true.
        if (getDbEmptyBoolean()) return;

        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this, R.style.dialog);
        alertBuild.setTitle(R.string.alert_title);
        alertBuild.setMessage(R.string.reset_alert);
        alertBuild.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reset();
            }
        });
        alertBuild.setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = alertBuild.create();
        alertDialog.show();
    }

    private void reset() {
        EntryExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < checkEntryList.size(); i++) {
                    checkDb.checkDao().clearTable();
                }
            }
        });

        resetInts();
        displayTotal();
        mainBinder.mainPercentageTv.setText(R.string.percentage_starter);

        setDbEmptyBoolean(true); //reset boolean
    }

    public void buttonClicks(View view) {
        switch (view.getId()) {
            case R.id.main_valid_ib:
                validCurr++;
                updatePercentage();
                displayTotal();
                break;
            case R.id.main_note_ib:
                noteCurr++;
                displayTotal();
                break;
            case R.id.main_invalid_ib:
                invalidCurr++;
                updatePercentage();
                displayTotal();
                break;
        }
        syncData(checkDb);//write to db and update rv
        checkAdapter.notifyDataSetChanged();
    }

    //How many items are validCurr of those flagged as validCurr or invalidCurr – in percent
    private void updatePercentage() {
        double base = validCurr + invalidCurr + validDb + invalidDb;
        if (base != 0) {
            percentage = (validCurr + validDb) / (base / 100);
        } else {
            percentage = 0;
            mainBinder.mainPercentageTv.setText(getString(R.string.percentage_starter));
        }

        displayPercentage();
    }

    //convert double and display
    private void displayPercentage() {
        if (percentage == 0) {
            mainBinder.mainPercentageTv.setText(getString(R.string.percentage_starter));
        } else {
            DecimalFormat percentageFormat = new DecimalFormat("#.00");
            String percentText = percentageFormat.format(percentage) + "%";
            mainBinder.mainPercentageTv.setText(percentText);
        }
    }

    public void displayTotal() {
        total = validCurr + noteCurr + invalidCurr + validDb + noteDb + invalidDb;
        mainBinder.mainTotalTv.setText(String.valueOf(total));
    }

    /**
     * Task to upsert the current days work into the database.
     *
     * @param checkDb Is the database the data is written to
     */
    private void syncData(final CheckDb checkDb) {

        if (invalidCurr == 0 && noteCurr == 0 && validCurr == 0) {

            resetInts();
            return;
        }

        // 2019-03-29
        final String currentDate = String.format(Locale.getDefault(), "%1$tY-%<tm-%<td",
                Calendar.getInstance());
        Log.v(TAG, "Current date: " + currentDate);

        final CheckEntry[] lastEntry = new CheckEntry[1];
        EntryExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                lastEntry[0] = checkDb.checkDao().getLastEntry();
                Log.v(TAG, "last entry: " + lastEntry[0].getDate());

                // If no work has been added on the current day, insert an new row of data with
                // the current values as currently entered by the user in the MainActivity.
                if (lastEntry[0] == null || !lastEntry[0].getDate().equals(currentDate)) {
                    final CheckEntry newEntry = new CheckEntry(currentDate, validCurr,
                            invalidCurr, noteCurr);

                    checkDb.checkDao().createEntry(newEntry);
                    //otherwise update the current days values.
                } else {
                    lastEntry[0].setDate(currentDate);
                    lastEntry[0].setValid(validCurr + lastEntry[0].getValid());
                    lastEntry[0].setInvalid(invalidCurr + lastEntry[0].getInvalid());
                    lastEntry[0].setNote(noteCurr + lastEntry[0].getNote());

                    checkDb.checkDao().updateEntry(lastEntry[0]);

                }
                resetInts();
                setDbEmptyBoolean(false);
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();

            }
        });
    }

    /**
     * reset current values back to 0 on the MainActivity
     */
    private void resetInts() {
        validCurr = 0;
        noteCurr = 0;
        invalidCurr = 0;
        total = 0;
        validDb = 0;
        noteDb = 0;
        invalidDb = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetInts();
    }

    @Override
    public void updateItem(final CheckEntry currentEntry) {
        final CheckEntry[] entry = new CheckEntry[1];
        EntryExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //get entry to update
                entry[0] = checkDb.checkDao().getEntryByDate(currentEntry.getDate());

                checkDb.checkDao().updateEntry(currentEntry);

                //TODO: Update increases total count

            }
        });
    }

    @Override
    public void deleteItem(final CheckEntry selectedEntry, final int checkEntryPosition) {

        final int[] deletedItem = new int[1];

        EntryExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                deletedItem[0] = checkDb.checkDao().deleteEntry(selectedEntry.getDate());
            }
        });

        if (deletedItem[0] != 0) {
            checkAdapter.notifyItemRemoved(checkEntryPosition);
            View view = findViewById(R.id.ConstraintLayout);
            Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                    Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.snack_bar_undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EntryExecutor.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            checkDb.checkDao().createEntry(selectedEntry);
                            checkAdapter.notifyItemInserted(checkEntryPosition);
                        }
                    });
                }
            });
            snackbar.show();
        }
    }
}