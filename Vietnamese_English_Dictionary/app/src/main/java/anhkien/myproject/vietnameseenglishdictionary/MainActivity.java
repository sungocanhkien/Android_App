package anhkien.myproject.vietnameseenglishdictionary;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;

    //Trạng thái ngôn ngữ
    private final FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity created.");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String tag = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment(); // Tạo instance mới mỗi lần
                    tag = "HOME_FRAGMENT";
                    Log.d(TAG, "Home navigation item selected");
                } else if (itemId == R.id.nav_favorite) {
                    selectedFragment = new FavoriteFragment(); // Tạo instance mới mỗi lần
                    tag = "FAVORITE_FRAGMENT";
                    Log.d(TAG, "Favorite navigation item selected");
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment, tag);
                    return true;
                }
                return false;
            }
        });

        // Load Fragment mặc định khi Activity được tạo lần đầu
        if (savedInstanceState == null) {
            // Load HomeFragment làm mặc định
            loadFragment(new HomeFragment(), "HOME_FRAGMENT");
            Log.d(TAG, "Default fragment (HomeFragment) loaded.");
        }
    }

    // Sử dụng replace để load Fragment
    private void loadFragment(Fragment fragment, String tag) {
        Log.d(TAG, "loadFragment: Replacing with fragment with tag: " + tag);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }
}