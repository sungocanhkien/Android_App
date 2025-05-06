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
        
        loadFragment(new FragmentMenu("home"));

        //xử lý bottomNavigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    loadFragment(new FragmentMenu("home"));
                    return true;
                case R.id.nav_favorite:
                    loadFragment(new FragmentMenu("favorite"));
                    return true;
            }
            return false;
        });

        
        
        
    }

    private void loadFragment(FragmentMenu home) {
    }
}