package com.example.gsmakkar1.kitaab;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUtility {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
