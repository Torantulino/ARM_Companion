package significantgravitas.toran.armcompanion;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.Manifest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;


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
    BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter(); //TODO: LATER VERSIONS: CHECK IF DEVICE SUPPORTS BLUETOOTH AND TAKE ACTION ACCORDINGLY
    Stack dressUpStack = new Stack();


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
        //Fragment fragment = new DressUpFragment();
        loadFragmentByID(0);
        // Connect to ARM
        connectToARM();
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
                            loadFragmentByID(0);
                            return true;
                        case R.id.navigation_save_outfit:
                            // Set title
                            mainTitle.setText(R.string.nav_outfit_save);
                            // Load fragment
                            loadFragmentByID(6);
                            return true;
                        case R.id.navigation_purchase_outfit:
                            mainTitle.setText(R.string.nav_outfit_purchase);
                            loadFragmentByID(5);
                            return true;
                        case R.id.navigation_my_wardrobe:
                            mainTitle.setText(R.string.nav_user_wardrobe);
                            loadFragmentByID(4);
                            return true;
                        case R.id.navigation_me:
                            mainTitle.setText(R.string.nav_user);
                            loadFragmentByID(3);
                            return true;
                    }
                    return false;
                }
            };



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

    int getAssignedParentByID(int childID){
        if (childID == 0 || (childID >=3 && childID <= 6)){
            Log.v("LOG-TAG", "Fragment " + childID + " is an orphan.");
            return -1;
        }
        if (childID == 1 || childID == 2){
            return 0;
        }
        Log.v("LOG-TAG", "Fragment is Unregistered!!.");
        return -1;
    }

    Fragment lastFragment;
    // Fragment by id for easier external switches
    public void loadFragmentByID(int id) {
        Fragment frag = FragmentMap.get(id);
        // If lastFRAG != frag - Prevent switching to the same fragment.
        if (lastFragment != frag) {
            Log.v("LOG-TAG","- Begin Loading fragment by ID into Main Slot -");
            // If last fragment isn't null:
            if(lastFragment != null) {
                Log.v("LOG-TAG","Detaching current fragment.");
                //Detach Current Fragment:
                FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
                fTrans.detach(getSupportFragmentManager().findFragmentById(lastFragment.getId()));
                fTrans.commit();
            }
            //Check if fragment has already been created:
            if (getSupportFragmentManager().findFragmentById(frag.getId()) != null){
                //Fragment already created: switch
                Log.v("LOG-TAG", "Fragment already created, switching!");
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.attach(frag);
                trans.commit();
                Log.v("LOG-TAG","Switch Complete!");
            }
            else{
                //Fragment not yet created: create and switch
                Log.v("LOG-TAG", "Fragment not yet created, adding to fragment manager.");
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.add(R.id.fragHolder, frag);
                trans.commit();
                Log.v("LOG-TAG","Fragment Added!");
            }
            lastFragment = frag;
        } else {
            Log.v("LOG-TAG","Same fragment, not switching.");
        }
    }

    private void addToHistory(int id){
        //Child of DressUpFragment
        if(getAssignedParentByID(id) == 0){
            dressUpStack.push(id);
        }
        //TODO: IN LATER VERSIONS ADD REST OF PARENTS
    }

    private int getIDFromFrag(Fragment frag){
        for (Map.Entry<Integer, Fragment> entry : FragmentMap.entrySet())
        {
            if(frag == entry.getValue()){
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed(){
        int fragID = getIDFromFrag(lastFragment);

        if(fragID == 0){
            if (!dressUpStack.empty()){
                loadChildFragment((int)dressUpStack.pop(), R.id.linLDressUp, true);
            }
            //Else if stack is empty load main fragment
            else {
                Fragment lastChild = null;
                for (int child: lastChildren)
                {
                    // If of the same parent
                    if(getAssignedParentByID(child) == fragID){
                        lastChild = FragmentMap.get(child);
                    }
                }
                if(lastChild!=null){
                    lastFragment.getChildFragmentManager().beginTransaction().remove(lastFragment.getChildFragmentManager().findFragmentById(R.id.linLDressUp)).commit();
                    lastChildren.remove((Integer)getIDFromFrag(lastChild));
                }
            }
        }
        //TODO: IN LATER VERSIONS ADD FOR OTHER PARENTS
    }

    List<Integer> lastChildren = new ArrayList<>();

    public void loadChildFragment(int id, int targetID, boolean backAction){
        Fragment frag = FragmentMap.get(id);
        Fragment lastChildFrag = null;
        // Find last child for this parent Fragment
        for (int child: lastChildren)
        {
            // If of the same parent
            if(getAssignedParentByID(child) == getAssignedParentByID(id)){
                lastChildFrag = FragmentMap.get(child);
            }
            else{
                Log.v("LOG-TAG","CURRENT FRAGMENT PARENT: " + getAssignedParentByID(id) + ", PAST FRAGMENT PARENT: " + getAssignedParentByID(child));
            }
        }
        Log.v("LOG-TAG","- Begin Loading fragment by ID into Parent Slot. -");
        if (lastChildFrag != frag) {
            // If last fragment isn't null:
            if(lastChildFrag != null) {                                                             
                Log.v("LOG-TAG","Detaching current fragment.");
                //Detach Current Fragment:
                FragmentTransaction fTrans = lastFragment.getChildFragmentManager().beginTransaction();
                fTrans.detach(lastFragment.getChildFragmentManager().findFragmentById(lastChildFrag.getId()));
                fTrans.commit();
            }
            //Check if fragment has already been created:
            if (lastFragment.getChildFragmentManager().findFragmentById(frag.getId()) != null){
                //Fragment already created: switch
                Log.v("LOG-TAG", "Fragment already created, switching!");
                FragmentTransaction trans = lastFragment.getChildFragmentManager().beginTransaction();
                trans.attach(frag);
                trans.commit();
                Log.v("LOG-TAG","Switch Complete!");
            }
            else{
                //Fragment not yet created: create and switch
                Log.v("LOG-TAG", "Fragment not yet created, adding to fragment manager.");
                FragmentTransaction trans = lastFragment.getChildFragmentManager().beginTransaction();
                trans.add(targetID, frag);
                trans.commit();
                Log.v("LOG-TAG","Fragment Added!");
            }
            Log.v("LOG-TAG","LastChildFrag = " + lastChildFrag);
            // If not a back action, add to history
            if(!backAction) {
                // Add child to appropriate history stack
                addToHistory(getIDFromFrag(lastChildFrag));
            }
            // Update last child
            if(lastChildFrag != null) {
                Log.v("LOG-TAG","Attempting to remove child: " + getIDFromFrag(lastChildFrag));
                lastChildren.remove((Integer)getIDFromFrag(lastChildFrag));
                Log.v("LOG-TAG","Child Removed.");
            }
            lastChildren.add(id);
        }
        else {
            Log.v("LOG-TAG","Same fragment, not switching.");
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    // Communication with ARM
    public void connectToARM(){
        //Get all permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Check if Bluetooth permission is granted
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){
                // - Permission Not Granted/Revoked -
                // Request permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 0);

            }
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
                // - Permission Not Granted/Revoked -
                // Request permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
            }
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // - Permission Not Granted/Revoked -
                // Request permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            }
            //Retry
            connectToARM();
        }
        else{
            // - Permission Granted -
            // Make User Device discoverable
            makeDeviceDiscoverable();
            // Start Searching for devices
            blueSearch();
        }
    }

    // Override onRequestPermissionResult to receive user decision
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            //BLUETOOTH_ADMIN
            case 0: {
                //If request was canceled rather than acted on, results length will be 0
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission Granted, Recall connectToARM
                    connectToARM();
                }
                else{
                    //Permission denied.
                    //TODO: Inform user that app required permission to function, exit app if refused to reconsider permission.
                }
                return;
            }
            //BLUETOOTH
            case 1: {
                //If request was canceled rather than acted on, results length will be 0
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission Granted, Recall connectToARM
                    connectToARM();
                }
                else{
                    //Permission denied.
                    //TODO: Inform user that app required permission to function, exit app if refused to reconsider permission.
                }
                return;
            }
            //ACCESS COARSE LOCATION
            case 2: {
                //If request was canceled rather than acted on, results will be 0
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission Granted, Recall connectToARM
                    connectToARM();
                }
                else{
                    //Permission denied.
                    //TODO: Inform user that app required permission to function, exit app if refused to reconsider permission.
                }
                return;
            }
        }
    }

    // Make device discoverable
    private void makeDeviceDiscoverable(){
        Intent intentMakeDiscoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intentMakeDiscoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(intentMakeDiscoverable);
    }

    // Create Broadcast Receiver
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                // Device Found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getName().equals("ARMDevice")){
                    // ARM FOUND
                    Log.v("LOG-TAG", "ARM Device found! (Main Activity, Broadcast Receiver).");
                    //Start Bluetooth Connection Thread
                    BlueConnectThread blueConnectThread = new BlueConnectThread(device);
                    blueConnectThread.start();
                }
                else{
                    Log.v("LOG-TAG", "Device found that does not match name ARMDevice Called:" + device.getName());
                }
                //foundDevices.add(device);
            }
            else{
                Log.e("ERROR-TAG", "Action != intent.getAction (Main Activity, Broadcast Receiver).");
            }
        }

    };

    // Search for bluetooth devices and send them to broadcastReceiver
    private void blueSearch() {
        IntentFilter intentFoundDevice = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFoundDevice);
        defaultAdapter.startDiscovery();
    }
/*
    // Bond with specified device
    public boolean bond(BluetoothDevice bluetoothDevice)
        throws Exception
        {
            Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
            Method bondMethod = class1.getMethod("bond");
            Boolean returnValue = (Boolean) bondMethod.invoke(bluetoothDevice);
            return returnValue.booleanValue();
        }
*/
    /*
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragHolder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();*/
}

