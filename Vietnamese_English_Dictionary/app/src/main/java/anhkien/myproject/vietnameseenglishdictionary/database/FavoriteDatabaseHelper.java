package anhkien.myproject.vietnameseenglishdictionary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "dictionary.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "favorites";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WORD + " TEXT UNIQUE, " + "phonetic TEXT, " +
                    "type TEXT, " +
                    "meaning TEXT, " +
                    "example TEXT, " +
                    "audio TEXT);";

    public FavoriteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
