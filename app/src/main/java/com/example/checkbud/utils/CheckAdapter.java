package com.example.checkbud.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkbud.R;
import com.example.checkbud.data.CheckEntry;
import com.example.checkbud.databinding.InfoListBinding;
import com.example.checkbud.databinding.UpdateListBinding;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * {@link CheckAdapter} uses a {@link Cursor} to populate a {@link RecyclerView}.
 */
public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.CheckViewHolder> {

    private static ArrayList<viewState> itemViewState;
    private CheckEntry recentlyDeletedCheckEntry;
    private int recentlyDeletedCheckEntryPosition;
    private Activity activity;
    private Context ctxt;

    private enum viewState {
        INFO_VIEW,
        UPDATE_VIEW
    }

    private final static int INFO_VIEW_STATE = 0;
    private final static int UPDATE_VIEW_STATE = 1;

    private static boolean infoView = false;

    //context and cursor to be used throughout this adapter.
    private List<CheckEntry> checkEntries;

    private final CheckAdapterInterface checkAdapterInterface;

    public interface CheckAdapterInterface {
        void updateItem(CheckEntry currentEntry);

        void deleteItem(CheckEntry selectedEntry, int recentlyDeletedCheckEntryPosition);
    }


    /**
     * Constructor
     *
     * @param checkAdapterInterface is the interface passing info to MainActivity
     */
    public CheckAdapter(CheckAdapterInterface checkAdapterInterface) {
        this.checkAdapterInterface = checkAdapterInterface;

    }

    /**
     * Setup a new view and increase the count.
     *
     * @param parent   that contains the viewHolders.
     * @param viewType not needed for this build
     * @return the new holder.
     */
    @NonNull
    @Override
    public CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case UPDATE_VIEW_STATE:
                UpdateListBinding updateListBinding =
                        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                                R.layout.update_list, parent, false);

                return new CheckViewHolder(updateListBinding);

            case INFO_VIEW_STATE:
            default:
                InfoListBinding infoListBinding =
                        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                                R.layout.info_list, parent, false);

                return new CheckViewHolder(infoListBinding);
        }
    }

    /**
     * get data for specified position to display
     *
     * @param holder   the viewHolder to be populated
     * @param position of the database provided by the cursor.
     */
    @Override
    public void onBindViewHolder(@NonNull CheckViewHolder holder, int position) {
        CheckEntry currentCheckEntry = checkEntries.get(position);
        holder.currentEntry = currentCheckEntry;

        switch (holder.getItemViewType()) {
            case UPDATE_VIEW_STATE:
                holder.bindUpdate(currentCheckEntry);
                break;
            case INFO_VIEW_STATE:
            default:
                holder.bindInfo(currentCheckEntry);
                break;
        }

    }

    /**
     * retrieve amount of items to be displayed from cursor
     *
     * @return the the amount.
     */
    @Override
    public int getItemCount() {
        if (checkEntries == null) return 0;
        return checkEntries.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemViewState.get(position) == viewState.UPDATE_VIEW)
            return UPDATE_VIEW_STATE;
        return INFO_VIEW_STATE;
    }


    public void setCheckEntries(List<CheckEntry> checkEntries) {
        this.checkEntries = checkEntries;
        Collections.reverse(this.checkEntries);

        itemViewState = new ArrayList<>();
        for (int i = 0; i < checkEntries.size(); i++) {
            itemViewState.add(i, viewState.INFO_VIEW);
        }
        notifyDataSetChanged();
    }

    public void deleteTask(int position) {
        recentlyDeletedCheckEntry = checkEntries.get(position);
        recentlyDeletedCheckEntryPosition = position;
        checkEntries.remove(position);
        checkAdapterInterface.deleteItem(recentlyDeletedCheckEntry, recentlyDeletedCheckEntryPosition);
        notifyItemRemoved(position);
    }

    /**
     * holding the child views for the viewHolder to be constructed for viewing the db
     */
    class CheckViewHolder extends RecyclerView.ViewHolder {

        CheckEntry currentEntry;
        private InfoListBinding infoListBinding;
        private UpdateListBinding updateListBinding;

        final Calendar myCalendar = Calendar.getInstance();

        /**
         * Constructor
         *
         * @param infoListBinding is the databinding of the item when the user is viewing
         */
        CheckViewHolder(final InfoListBinding infoListBinding) {
            super(infoListBinding.getRoot());
            this.infoListBinding = infoListBinding;

            infoListBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemViewState.set(getAdapterPosition(), viewState.UPDATE_VIEW);
                    notifyItemChanged(getAdapterPosition());
                    Toast.makeText(view.getContext(), "You can now edit.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        /**
         * Constructor
         *
         * @param updateListBinding is the databinding of the item when the user is updating
         */
        CheckViewHolder(final UpdateListBinding updateListBinding) {
            super(updateListBinding.getRoot());
            this.updateListBinding = updateListBinding;

            final boolean[] hasUpdated = new boolean[1];


            //https://stackoverflow.com/a/14933515/7601437
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                    hasUpdated[0] = true;
                }
            };

            updateListBinding.infoDateEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    new DatePickerDialog(view.getContext(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            updateListBinding.infoInvalidEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!editable.toString().equals("")) {
                        currentEntry.setInvalid(Integer.parseInt(editable.toString()));
                        hasUpdated[0] = true;
                    }

                }
            });
            updateListBinding.infoNoteEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!editable.toString().equals("")) {
                        currentEntry.setNote(Integer.parseInt(editable.toString()));
                        hasUpdated[0] = true;
                    }
                }
            });
            updateListBinding.infoValidEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!editable.toString().equals("")) {
                        currentEntry.setValid(Integer.parseInt(editable.toString()));
                        hasUpdated[0] = true;
                    }
                }
            });

            updateListBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemViewState.set(getAdapterPosition(), viewState.INFO_VIEW);
                    if (hasUpdated[0]) {
                        checkAdapterInterface.updateItem(currentEntry);
                    }
                    notifyItemChanged(getAdapterPosition());
                    Toast.makeText(view.getContext(), "You have finished editing." + infoView,
                            Toast.LENGTH_SHORT).show();

                    //TODO: Datepicker is activated
                    return true;
                }
            });

            //TODO: setup swipe to delete an entry
        }

        void bindInfo(CheckEntry checkEntry) {
            infoListBinding.setCheckEntry(checkEntry);
            infoListBinding.executePendingBindings();
        }

        void bindUpdate(CheckEntry checkEntry) {
            updateListBinding.setCheckEntry(checkEntry);
            updateListBinding.executePendingBindings();
        }

        private void updateLabel() {
            String myFormat = "yyyy-MM-dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            updateListBinding.infoDateEt.setText(sdf.format(myCalendar.getTime()));
            currentEntry.setDate(sdf.format(myCalendar.getTime()));
        }
    }
}
