package com.example.checkbud.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

public class CheckViewModel extends AndroidViewModel {

    private static final String TAG = AndroidViewModel.class.getSimpleName();

    private final LiveData<List<CheckEntry>> checkEntries;

    public CheckViewModel(@NonNull Application application) {
        super(application);
        CheckDb db = CheckDb.getInstance(this.getApplication());
        Log.d(TAG,"ViewModel is retrieving the entries from the db");
        checkEntries = db.checkDao().getAllEntries();
    }

    public LiveData<List<CheckEntry>> getCheckEntries() {
        return checkEntries;
    }
}
