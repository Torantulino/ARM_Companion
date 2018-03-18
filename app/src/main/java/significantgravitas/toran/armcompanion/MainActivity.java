package significantgravitas.toran.armcompanion;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;


public class MainActivity extends AppCompatActivity implements  DressUpFragment.OnFragmentInteractionListener,
                                                                SaveOutfitFragment.OnFragmentInteractionListener,
                                                                PurchaseOutfitFragment.OnFragmentInteractionListener,
                                                                MyWardrobeFragment.OnFragmentInteractionListener,
                                                                MeFragment.OnFragmentInteractionListener{

    // Global Variables
    TextView mainTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // - Load "Dress up" as startup page -
        // set title
        mainTitle = (TextView)findViewById(R.id.txtAppTitle);
        mainTitle.setText(R.string.nav_dress_up);
        // load fragment
        Fragment fragment = new DressUpFragment();
        try {
            loadFragment(fragment);
        }
        catch (Exception ex)
        {
        Log.e("FragLoadError", ex.getMessage());
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment mainFragment;
                    switch (item.getItemId()) {
                        case R.id.navigation_dress_up:
                            // Set title
                            mainTitle.setText(R.string.nav_dress_up);
                            // Load fragment
                            mainFragment = new DressUpFragment();
                            loadFragment(mainFragment);
                            return true;
                        case R.id.navigation_save_outfit:
                            // Set title
                            mainTitle.setText(R.string.nav_outfit_save);
                            // Load fragment
                            mainFragment = new SaveOutfitFragment();
                            loadFragment(mainFragment);
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



    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragHolder, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    /*
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragHolder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();*/
}

