package anhkien.myproject.vietnameseenglishdictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

import anhkien.myproject.vietnameseenglishdictionary.Word;

public class FavoriteRepository {
    private FavoriteDatabaseHelper dbHelper;

    public FavoriteRepository(Context context) {
        dbHelper = new FavoriteDatabaseHelper(context);
    }

    public void addFavorite(Word word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word", word.getWord());
        values.put("phonetic", word.getPhonetic());
        values.put("type", word.getType());
        values.put("meaning", word.getMeaning());
        values.put("example", word.getExample());
        values.put("audio", word.getAudio());  // QUAN TRỌNG

        db.insertWithOnConflict(FavoriteDatabaseHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void removeFavorite(String word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(FavoriteDatabaseHelper.TABLE_NAME, FavoriteDatabaseHelper.COLUMN_WORD + "=?", new String[]{word});
        db.close();
    }

    public boolean isFavorite(String word) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FavoriteDatabaseHelper.TABLE_NAME, null,
                FavoriteDatabaseHelper.COLUMN_WORD + "=?", new String[]{word},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public List<Word> getAllFavorites() {
        List<Word> favorites = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("favorites", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
                String phonetic = cursor.getString(cursor.getColumnIndexOrThrow("phonetic"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
                String example = cursor.getString(cursor.getColumnIndexOrThrow("example"));
                String audio = cursor.getString(cursor.getColumnIndexOrThrow("audio"));
                favorites.add(new Word(word, phonetic, type, meaning, example, audio));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favorites;
    }
}
