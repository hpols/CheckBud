package com.example.checkbud.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "checkBuddy")
public class CheckEntry {

    //Setting up the columns
    private static final String ID = "id"; //
    private static final String VALID = "valid"; //counting confirmed valid items
    private static final String INVALID = "invalid"; // counting confirmed invalid items
    private static final String NOTE = "note"; // counting items requiring a note
    private static final String DATE = "date"; // date of day worked

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    private int id;
    @ColumnInfo(name = VALID)
    private int valid;
    @ColumnInfo(name = INVALID)
    private int invalid;
    @ColumnInfo(name = NOTE)
    private int note;
    @ColumnInfo(name = DATE)
    private String date;


    //constructor without ID
    @Ignore
    public CheckEntry(String date, int valid, int invalid, int note) {
        this.date = date;
        this.valid = valid;
        this.invalid = invalid;
        this.note = note;
    }

    //constructor with ID for Room
    public CheckEntry(int id, String date, int valid, int invalid, int note) {
        this.id = id;
        this.date = date;
        this.valid = valid;
        this.invalid = invalid;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public CheckEntry setId(int id) {
        this.id = id;
        return this;
    }

    public int getValid() {
        return valid;
    }

    public CheckEntry setValid(int valid) {
        this.valid = valid;
        return this;
    }

    public int getInvalid() {
        return invalid;
    }

    public CheckEntry setInvalid(int invalid) {
        this.invalid = invalid;
        return this;
    }

    public int getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public CheckEntry setNote(int note) {
        this.note = note;
        return this;
    }

    public CheckEntry setDate(String date) {
        this.date = date;
        return this;
    }
}
