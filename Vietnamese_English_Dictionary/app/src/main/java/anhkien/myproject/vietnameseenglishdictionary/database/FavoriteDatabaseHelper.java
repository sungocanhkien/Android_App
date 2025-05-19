package anhkien.myproject.vietnameseenglishdictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import anhkien.myproject.vietnameseenglishdictionary.Word;

public class FavoriteDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "dictionary.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "favorites";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_PHONETIC = "phonetic";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_MEANING = "meaning";
    public static final String COLUMN_EXAMPLE = "example";
    public static final String COLUMN_AUDIO = "audio";


    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WORD + " TEXT UNIQUE, " +
                    COLUMN_PHONETIC + " TEXT, " +
                    COLUMN_TYPE + " TEXT, " +
                    COLUMN_MEANING + " TEXT, " +
                    COLUMN_EXAMPLE + " TEXT, " +
                    COLUMN_AUDIO + " TEXT);";


    public FavoriteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_PHONETIC + " TEXT;");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_TYPE + " TEXT;");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_MEANING + " TEXT;");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_EXAMPLE + " TEXT;");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_AUDIO + " TEXT;");
        }
    }
    public void addFavoriteWord(Word word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word", word.getWord());
        values.put("phonetic", word.getPhonetic());
        values.put("meaning", word.getMeaning());
        values.put("example", word.getExample()); // nếu bạn có cột 'example'
        db.insert(TABLE_NAME, null, values);
        db.close();
    }



}
