package anhkien.myproject.vietnameseenglishdictionary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private EditText editTextSearch;
    private ImageButton imagebuttonSearch, imagebuttonSwitchLang;
    private BottomNavigationView bottomNavigationView;

    //Trạng thái ngôn ngữ
    private  boolean isEnglishToVietnamese = true;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        editTextSearch = findViewById(R.id.edtTextSearch);
        imagebuttonSearch = findViewById(R.id.btnSearch);
        imagebuttonSwitchLang = findViewById(R.id.btnChuyenNN);
        bottomNavigationView = findViewById(R.id.bottomMenu);


        //xử lý bottomNavigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home){
                loadFragment(FragmentMenu.newInstance("home", "", true));
                return true;
            } else if (id == R.id.nav_favorite) {
                loadFragment(FragmentMenu.newInstance("favorite", "", true));
                return true;
            }
            return false;
        });

        //xử lý nút chuyển Anh-Việt / Việt-Anh
        imagebuttonSwitchLang.setOnClickListener(v -> {
            isEnglishToVietnamese = !isEnglishToVietnamese;
        });

        //xử lý nút tìm kiếm
        imagebuttonSearch.setOnClickListener(v -> {
            String keyword = editTextSearch.getText().toString().trim();
            if (!keyword.isEmpty()){
                //Truyền từ khóa cho FragmentMenu để xử lý tìm kiếm
                loadFragment(FragmentMenu.newInstance("home", keyword, isEnglishToVietnamese));
            }
        });
        
        
    }

    private void loadFragment(FragmentMenu fragmentMenu) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.hienthiKQ, fragmentMenu)
                .commitNow();
    }
}