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

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements  DressUpFragment.OnFragmentInteractionListener,
                                                                GlassesFragment.OnFragmentInteractionListener,
                                                                HeadFragment.OnFragmentInteractionListener,
                                                                MeFragment.OnFragmentInteractionListener,
                                                                MyWardrobeFragment.OnFragmentInteractionListener,
                                                                PurchaseOutfitFragment.OnFragmentInteractionListener,
                                                                SaveOutfitFragment.OnFragmentInteractionListener
                                                                {

    // Global Variables
    TextView mainTitle;
    HashMap<Integer, Fragment> FragmentMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Populate fragment map
        populateFragMap();
        // - Load "Dress up" as startup page -
        // set title
        mainTitle = (TextView)findViewById(R.id.txtAppTitle);
        mainTitle.setText(R.string.nav_dress_up);
        // load fragment
        Fragment fragment = new DressUpFragment();
        loadFragment(fragment);

    }


    // Bottom Nav Bar
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


    private void populateFragMap(){
        Fragment fragment;
        // Dress up
        fragment = new DressUpFragment();
        FragmentMap.put(0, fragment);
        // Glasses
        fragment = new GlassesFragment();
        FragmentMap.put(1, fragment);
        // Head
        fragment = new HeadFragment();
        FragmentMap.put(2, fragment);
        // Me
        fragment = new MeFragment();
        FragmentMap.put(3, fragment);
        // My Wardrobe
        fragment = new MyWardrobeFragment();
        FragmentMap.put(4, fragment);
        // Purchase Outfit
        fragment = new PurchaseOutfitFragment();
        FragmentMap.put(5, fragment);
        // Save Outfit
        fragment = new SaveOutfitFragment();
        FragmentMap.put(6, fragment);

    }


    // Fragment by id for easier external switches
    public void loadFragmentByID(int id) {
        Fragment frag = FragmentMap.get(id);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragHolder, frag).commit();
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

