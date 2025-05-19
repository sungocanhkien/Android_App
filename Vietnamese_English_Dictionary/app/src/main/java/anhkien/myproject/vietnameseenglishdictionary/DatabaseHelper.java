package anhkien.myproject.vietnameseenglishdictionary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anhkien.myproject.vietnameseenglishdictionary.api.ApiClient;
import anhkien.myproject.vietnameseenglishdictionary.api.DictionaryApi;
import anhkien.myproject.vietnameseenglishdictionary.api.TranslationApi;
import anhkien.myproject.vietnameseenglishdictionary.model.WordResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseHelper {
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
                // Tuy nhiên, vì copy trực tiếp, chỉ cần đảm bảo file không bị khóa.
                copyDataBase(); // Copy lại file mới từ assets
            } catch (IOException e) {
                Log.e(TAG, "Error upgrading database by copying from assets", e);
            }
        }
    }



            // Bước 1: Dịch từ tiếng Việt sang tiếng Anh bằng LibreTranslate
            TranslationApi translationApi = ApiClient.getTranslationRetrofit().create(TranslationApi.class);


            Map<String, Object> body = new HashMap<>();
            body.put("q", word);
            body.put("source", "vi");
            body.put("target", "en");
            body.put("format", "text");

            translationApi.translate(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().containsKey("translatedText")) {
                        String translated = response.body().get("translatedText");

                        // Gọi lại API dictionary với từ đã dịch
                        DictionaryApi dictionaryApi = ApiClient.getRetrofit().create(DictionaryApi.class);
                        dictionaryApi.getMeaning(translated).enqueue(new Callback<List<WordResponse>>() {
                            @Override
                            public void onResponse(Call<List<WordResponse>> call, Response<List<WordResponse>> response) {
                                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                    callback.onSuccess(response.body().get(0));
                                } else {
                                    callback.onFailure("Không tìm thấy nghĩa cho từ đã dịch: " + translated);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<WordResponse>> call, Throwable t) {
                                callback.onFailure("Lỗi mạng khi tra từ đã dịch: " + t.getMessage());
                            }
                        });
                    } else {
                        callback.onFailure("Không thể dịch từ tiếng Việt. Vui lòng thử lại.");
                    }
                }


        }
    }
}
