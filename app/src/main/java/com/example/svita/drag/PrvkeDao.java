package com.example.svita.drag;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PrvkeDao {
    @Query("SELECT * FROM ulozcopujde")
    List<UlozCoPujde> getAll();

    @Insert//(onConflict = OnConflictStrategy.REPLACE)
    void insert(UlozCoPujde prvek);

    @Delete
    void delete(UlozCoPujde prvek);

    @Update
    void update(UlozCoPujde prvek);
    @Query("DELETE FROM ulozcopujde")
    public void nukeTable();
}
