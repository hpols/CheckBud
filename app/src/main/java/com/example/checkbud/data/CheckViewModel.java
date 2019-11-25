package com.example.checkbud.data;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import timber.log.Timber;

public class CheckViewModel extends AndroidViewModel {

    private final LiveData<List<CheckEntry>> checkEntries;

    public CheckViewModel(@NonNull Application application) {
        super(application);
        CheckDb db = CheckDb.getInstance(this.getApplication());
        Timber.d("ViewModel is retrieving the entries from the db");
        checkEntries = db.checkDao().getAllEntries();
    }

    public LiveData<List<CheckEntry>> getCheckEntries() {
        return checkEntries;
    }
}
