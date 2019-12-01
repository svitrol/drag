package com.example.svita.drag;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DbClient {
    private Context mCtx;
    private static DbClient mInstance;

    //our app database object
    private mojeDatabase appDatabase;

    private DbClient(Context mCtx) {
        this.mCtx = mCtx;

        //vytvoření app database v Room database builder
        appDatabase = Room.databaseBuilder(mCtx, mojeDatabase.class, "MojePrvky").build();
    }

    public static synchronized DbClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DbClient(mCtx);
        }
        return mInstance;
    }

    public mojeDatabase getAppDatabase() {
        return appDatabase;
    }
}
