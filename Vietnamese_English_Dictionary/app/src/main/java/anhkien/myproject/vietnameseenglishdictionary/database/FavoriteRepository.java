package anhkien.myproject.vietnameseenglishdictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

public class FavoriteRepository {
    private FavoriteDatabaseHelper dbHelper;

    public FavoriteRepository(Context context) {
        dbHelper = new FavoriteDatabaseHelper(context);
    }

    public void addFavorite(String word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoriteDatabaseHelper.COLUMN_WORD, word);
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

    public List<String> getAllFavorites() {
        List<String> favorites = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FavoriteDatabaseHelper.TABLE_NAME,
                new String[]{FavoriteDatabaseHelper.COLUMN_WORD}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                favorites.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favorites;
    }
}
