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
                    readContacts();
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
            readContacts();
        }
    }

    public void readContacts(){
        ContentResolver cr = getContentResolver();
        Cursor cur=null;
        try {
            cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        }catch (Exception e){
            Log.d("TAG", "Error on contacts" + e.getMessage());
        }

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Log.d("TAG", "idb: " + id);
                Log.d("TAG", "NameContact: " + name);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        phoneNumber=pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        Log.d("TAG", "Phone_Number: " + phoneNo);
                    }
//                    updateContact();
                    mangeSaveContact();
                    pCur.close();


                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }
    public void mangeSaveContact(){
        ContatcList contatcListObject= MypreferenceManager.getInstance(this).getAndroidContacts();
        boolean flag=false;
        int i,g;

        if(!(contatcListObject.getContatcList().size()==0)) {
            try {
                for (i = 0; i < contatcListObject.getContatcList().size(); i++) {
                    if (id .equals(contatcListObject.getContatcList().get(i).getId())&& phoneNo .equals(contatcListObject.getContatcList().get(i).getPhoneNumber()) ) {
                        flag = true;
                        //break;
                    }
                }

            }catch (Exception e){

            }
            if(flag ==false){
                updateContact();
            }

        }else {
            flag =false;
            updateContact();
        }
//
    }
    public void updateContact(){

        ContatcList contatcList=MypreferenceManager.getInstance(MainActivity.this ).getAndroidContacts();
        AndroidContacts androidContacts =new AndroidContacts();
        androidContacts.setId(id);
        androidContacts.setName(name);
        androidContacts.setPhoneNumber(phoneNo);
        contatcList.addContatcList(androidContacts);
        MypreferenceManager.getInstance(MainActivity.this).putAndroidContacts(contatcList);

    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(broadcastReceiver);
//        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(broadcastReceivermessage);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                broadcastReceiver,new IntentFilter("intent_info")
        );
//        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
//                broadcastReceivermessage,new IntentFilter("intent_ok")
//        );
    }

    //define an object of type registerUserCallback

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
