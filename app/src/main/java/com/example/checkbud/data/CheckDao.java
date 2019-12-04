package com.example.checkbud.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CheckDao {

    //––– CREATE Methods –––//

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createEntry(CheckEntry checkEntry);

    //––– READ Methods –––//

    @Query("SELECT * FROM checkBuddy")
    LiveData<List<CheckEntry>> getAllEntries();

    @Query("SELECT * FROM checkBuddy ORDER BY date DESC LIMIT 1")
    CheckEntry getLastEntry();

    @Query("SELECT * FROM checkBuddy WHERE date IS :date")
    CheckEntry getEntryByDate(String date);

    //––– UPDATE Methods –––//

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateEntry(CheckEntry checkEntry);

    // –––DELETE METHODS –––//
    @Query("DELETE FROM checkBuddy")
    void clearTable();

    @Query ("DELETE FROM checkBuddy Where date IS :date")
    int deleteEntry(String date);
}
