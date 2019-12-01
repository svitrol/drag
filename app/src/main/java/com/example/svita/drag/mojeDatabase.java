package com.example.svita.drag;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {UlozCoPujde.class}, version = 1)
public abstract class mojeDatabase extends RoomDatabase {
    public abstract PrvkeDao taskDao();
}
