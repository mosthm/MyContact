package com.example.alifatemeh.mycontact;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private ContactAdapter contactAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=135;
    private String id;
    private String name;
    private String phoneNo;
    private ManagmentContact managmentContact;
    private static AppCompatActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        getSupportActionBar().hide();
        findViews();
        configure();
    }
    private void findViews(){

        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
    }

    private void configure(){

        myFragmentPagerAdapter =new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myFragmentPagerAdapter);
        //connect tablayout to viewpager
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("TAG","response granted");
                    managmentContact=new ManagmentContact(getApplicationContext());
                    managmentContact.readContacts(MainActivity.this);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("TAG","response not granted");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }

    public void requestPermission(){
        //what pernission checked
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            managmentContact=new ManagmentContact(getApplicationContext());
            managmentContact.readContacts(MainActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(broadcastReceivermessage);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                broadcastReceiver,new IntentFilter("intent_info")
        );
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                broadcastReceivermessage,new IntentFilter("intent_ok")
        );
    }

    //define an object of type registerUserCallback
    private BroadcastReceiver broadcastReceivermessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getFragmentManager().popBackStack();
            managmentContact=new ManagmentContact(getApplicationContext());
            managmentContact.readContacts(MainActivity.this);
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            InfoContactFragment infoContactFragment=new InfoContactFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_container,infoContactFragment)
                    .addToBackStack(null)
                    .commit();

        }
    };
}