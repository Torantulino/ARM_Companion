package significantgravitas.toran.armcompanion;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    // Global Variables
    TextView mainTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mainTitle = (TextView)findViewById(R.id.txtAppTitle);
        mainTitle.setText(R.string.nav_dress_up);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment mainFragment;
                    switch (item.getItemId()) {
                        case R.id.navigation_dress_up:
                            mainTitle.setText(R.string.nav_dress_up);
                            return true;
                        case R.id.navigation_save_outfit:
                            mainTitle.setText(R.string.nav_outfit_save);
                            return true;
                        case R.id.navigation_purchase_outfit:
                            mainTitle.setText(R.string.nav_outfit_purchase);
                            return true;
                        case R.id.navigation_my_wardrobe:
                            mainTitle.setText(R.string.nav_user_wardrobe);
                            return true;
                        case R.id.navigation_me:
                            mainTitle.setText(R.string.nav_user);
                            return true;
                    }
                    return false;
                }
            };

}
