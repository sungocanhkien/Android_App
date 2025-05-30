package anhkien.myproject.vietnameseenglishdictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static String DB_PATH = "";
    public static final String DATABASE_NAME = "mydatabase.db";
    public static final int DATABASE_VERSION = 1; // Hằng số cho chế độ tìm kiếm


    private final Context myContext;

    public static final String TABLE_WORDS = "databaseme";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_EXAMPLE = "example";
    public static final String COLUMN_PRONUNCIATION = "pronunciation";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_IS_FAVORITE = "isFavorite";
    private static final String TABLE_CREATE_WORDS_REFERENCE =
            "CREATE TABLE " + TABLE_WORDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_WORD + " TEXT NOT NULL, " +
                    COLUMN_TRANSLATION + " TEXT NOT NULL, " +
                    COLUMN_TYPE + " TEXT, " +
                    COLUMN_EXAMPLE + " TEXT, " +
                    COLUMN_PRONUNCIATION + " TEXT, " +
                    COLUMN_LANGUAGE + " TEXT, " +
                    COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        Log.d(TAG, "DB_PATH: " + DB_PATH);

        File dbDir = new File(DB_PATH);
        if (!dbDir.exists()){
            boolean dirCreated = dbDir.mkdirs();
            Log.d(TAG, "Database directory created: " + dirCreated);
        }

        try {
            createDataBase();
        } catch (IOException e) {
            Log.e(TAG, "Error creating database", e);
            throw new RuntimeException("Error creating database", e);
        }
    }
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            Log.d(TAG, "Database already exists.");

        } else {
            this.getReadableDatabase(); // Gọi để Android tạo file DB trống và thư mục
            this.close(); // Đóng file trống
            try {
                copyDataBase();
                Log.d(TAG, "Database copied successfully from assets.");
            } catch (IOException e) {
                Log.e(TAG, "Error copying database from assets", e);
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        return dbFile.exists();
    }
    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.d(TAG, "Database copied to: " + outFileName);

        // Set version cho database vừa copy
        SQLiteDatabase copiedDb = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.OPEN_READWRITE);
        copiedDb.setVersion(DATABASE_VERSION);
        copiedDb.close();
    }
    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate called - This should ideally not happen if assets copy is successful.");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Nếu phiên bản database trong assets mới hơn phiên bản hiện tại trên thiết bị
        if (newVersion > oldVersion) {
            try {
                Log.d(TAG, "Newer database version found in assets. Deleting old and copying new.");
                // Xóa file DB cũ trước khi copy file mới từ assets
                File dbFile = new File(DB_PATH + DATABASE_NAME);
                if (dbFile.exists()) {
                    boolean deleted = dbFile.delete();
                    Log.d(TAG, "Old database deleted: " + deleted);
                }
                // Cần gọi getReadableDatabase().close() để SQLiteOpenHelper biết là cần tạo lại
                copyDataBase(); // Copy lại file mới từ assets
            } catch (IOException e) {
                Log.e(TAG, "Error upgrading database by copying from assets", e);
            }
        }
    }
    public static final String VI_EN_MODE = "VI_EN"; // Tìm Việt -> Anh
    public static final String EN_VI_MODE = "EN_VI"; // Tìm Anh -> Việt

    public Word searchWord(String keyword, String searchMode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Word foundWord = null;

        String query;
        // Tìm kiếm theo kiểu "bắt đầu bằng" (LIKE 'keyword%')
        // Sử dụng selectionArgs để tránh SQL Injection và xử lý ký tự đặc biệt
        String[] selectionArgs = {keyword + "%"};

        if (VI_EN_MODE.equals(searchMode)) {
            // Người dùng nhập tiếng Việt, muốn tìm nghĩa tiếng Anh
            // -> Tìm trong cột 'word' nơi 'language' là 'vi'
            query = "SELECT * FROM " + TABLE_WORDS + " WHERE " +
                    COLUMN_LANGUAGE + " = ? AND " +
                    COLUMN_WORD + " LIKE ?";
            selectionArgs = new String[]{"vi", keyword + "%"};
        } else if (EN_VI_MODE.equals(searchMode)) {
            // Người dùng nhập tiếng Anh, muốn tìm nghĩa tiếng Việt
            // -> Tìm trong cột 'word' nơi 'language' là 'en'
            query = "SELECT * FROM " + TABLE_WORDS + " WHERE " +
                    COLUMN_LANGUAGE + " = ? AND " +
                    COLUMN_WORD + " LIKE ?";
            selectionArgs = new String[]{"en", keyword + "%"};
        } else {
            Log.e(TAG, "Chế độ tìm kiếm không hợp lệ: " + searchMode);
            // db.close(); // SQLiteOpenHelper sẽ quản lý
            return null;
        }
        Log.d(TAG, "Truy vấn tìm kiếm: " + query + " với từ khóa: " + keyword);

        try {
            cursor = db.rawQuery(query, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                // Lấy index cột một cách an toàn
                int idCol = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int wordCol = cursor.getColumnIndexOrThrow(COLUMN_WORD);
                int transCol = cursor.getColumnIndexOrThrow(COLUMN_TRANSLATION);
                int typeCol = cursor.getColumnIndexOrThrow(COLUMN_TYPE);
                int exCol = cursor.getColumnIndexOrThrow(COLUMN_EXAMPLE);
                int pronCol = cursor.getColumnIndexOrThrow(COLUMN_PRONUNCIATION);
                int langCol = cursor.getColumnIndexOrThrow(COLUMN_LANGUAGE);
                int favCol = cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE);

                foundWord = new Word(
                        cursor.getInt(idCol),
                        cursor.getString(wordCol),
                        cursor.getString(transCol),
                        cursor.getString(typeCol),
                        cursor.getString(exCol),
                        cursor.getString(pronCol),
                        cursor.getString(langCol),
                        cursor.getInt(favCol) == 1 // Chuyển 0/1 thành boolean
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi tìm kiếm từ: " + e.getMessage());
            e.printStackTrace(); // In chi tiết lỗi
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // db.close(); // SQLiteOpenHelper sẽ quản lý việc đóng database
        }
        return foundWord;
    }

    public boolean setFavoriteStatus(int wordId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_FAVORITE, isFavorite ? 1 : 0); // Lưu 1 cho true, 0 cho false

        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(wordId)};

        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_WORDS, values, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi cập nhật trạng thái yêu thích: " + e.getMessage());
            e.printStackTrace();
        }
        // db.close(); // SQLiteOpenHelper sẽ quản lý
        return rowsAffected > 0;
    }

    /* Lấy danh sách tất cả các từ được đánh dấu là yêu thích.
         @return Danh sách các đối tượng Word.*/
    public List<Word> getFavoriteWords() {
        List<Word> favoriteWordsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + TABLE_WORDS + " WHERE " + COLUMN_IS_FAVORITE + " = 1";
        Log.d(TAG, "Truy vấn lấy từ yêu thích: " + query);

        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idCol = cursor.getColumnIndexOrThrow(COLUMN_ID);
                    int wordCol = cursor.getColumnIndexOrThrow(COLUMN_WORD);
                    int transCol = cursor.getColumnIndexOrThrow(COLUMN_TRANSLATION);
                    int typeCol = cursor.getColumnIndexOrThrow(COLUMN_TYPE);
                    int exCol = cursor.getColumnIndexOrThrow(COLUMN_EXAMPLE);
                    int pronCol = cursor.getColumnIndexOrThrow(COLUMN_PRONUNCIATION);
                    int langCol = cursor.getColumnIndexOrThrow(COLUMN_LANGUAGE);
                    int favCol = cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE);

                    Word word = new Word(
                            cursor.getInt(idCol),
                            cursor.getString(wordCol),
                            cursor.getString(transCol),
                            cursor.getString(typeCol),
                            cursor.getString(exCol),
                            cursor.getString(pronCol),
                            cursor.getString(langCol),
                            cursor.getInt(favCol) == 1 // Hoặc đơn giản là true
                    );
                    favoriteWordsList.add(word);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy danh sách từ yêu thích: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // db.close(); // SQLiteOpenHelper sẽ quản lý
        }
        return favoriteWordsList;
    }
}
