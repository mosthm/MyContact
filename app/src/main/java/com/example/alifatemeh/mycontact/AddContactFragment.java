package com.example.alifatemeh.mycontact;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.ContentValues.TAG;

public class AddContactFragment extends Fragment {
    private EditText contactname;
    private EditText contactnumber;
    private EditText contactbirthday;
    private ImageView contactedit,contactdelet,callphone;
    private ContactAdapter contactAdapter;
    private RecyclerView contactsList;
    private int edit=0;
    private ManagmentContact managmentContact;

//        private ContactAdapter contactAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=135;
    private final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS=110;
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE=100;
    private String id;
    private String name;
    private String phoneNo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_infocontact, container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findview(view);

        configureContactadd();
    }

    public void findview(View view){
        contactname=(EditText) view.findViewById(R.id.contact_name);
        contactnumber=(EditText)view.findViewById(R.id.contact_number);
        contactbirthday=(EditText)view.findViewById(R.id.contact_birthday);
        contactdelet=(ImageView) view.findViewById(R.id.contact_delet);
        contactedit=(ImageView) view.findViewById(R.id.contact_edit);
        callphone=(ImageView) view.findViewById(R.id.contact_call);

//        textmessage=view.findViewById(R.id.textmessage);
//        send=view.findViewById(R.id.send);
    }


    public void configureContactadd(){
        contactname.setEnabled(true);
        contactnumber.setEnabled(true);
        contactbirthday.setEnabled(true);
        contactdelet.setVisibility(View.INVISIBLE);
        callphone.setVisibility(View.INVISIBLE);
        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_done_black_24dp);
        contactedit.setImageDrawable(myDrawable);
        contactedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }


    public void Insert2Contacts(Context ctx, String nameSurname, String telephone) {
        if (!isTheNumberExistsinContacts(ctx, telephone)) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            int rawContactInsertIndex = ops.size();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, telephone).build());
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameSurname)
                    .build());
            try {
                ContentProviderResult[] res = ctx.getContentResolver()
                        .applyBatch(ContactsContract.AUTHORITY, ops);
                Toast.makeText(getActivity(), "This Contact ADD!", Toast.LENGTH_SHORT).show();
                MypreferenceManager.getInstance(getActivity()).clearEverything();
                managmentContact=new ManagmentContact(getActivity());
                managmentContact.readContacts(getActivity());

                Intent intent = new Intent("intent_ok");
                intent.putExtra("info","");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                getFragmentManager().popBackStack();
//                InfoContactFragment infoContactFragment=new InfoContactFragment();
//                getFragmentManager().beginTransaction()
//                        .add(R.id.frag_container,infoContactFragment)
//                        .addToBackStack(null)
//                        .commit();
//                ContatcList contatcListObject= MypreferenceManager.getInstance(getActivity()).getAndroidContacts();
//                //sort horof bar alef ba
//                Comparator<AndroidContacts> androidContactsComparator = new Comparator<AndroidContacts>() {
//                    @Override
//                    public int compare(AndroidContacts o1, AndroidContacts o2) {
//                        return o1.getName().compareTo(o2.getName());
//                    }
//                };
//                Collections.sort(contatcListObject.getContatcList(),androidContactsComparator);
//
////                contactsList =(RecyclerView) view.findViewById(R.id.contacts);
//                contactAdapter =new ContactAdapter(contatcListObject.getContatcList(),getActivity());
//                contactsList.setLayoutManager(new LinearLayoutManager(getActivity()));
//                contactsList.setAdapter(contactAdapter);
//                getFragmentManager().popBackStack();
            } catch (Exception e) {

                Log.d(TAG, e.getMessage());
            }
            getFragmentManager().popBackStack();
        }
    }


    public static boolean isTheNumberExistsinContacts(Context ctx, String phoneNumber) {
        Cursor cur = null;
        ContentResolver cr = null;

        try {
            cr = ctx.getContentResolver();

        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }

        try {
            cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null,
                    null, null);
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }

        try {
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    // Log.i("Names", name);
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        // Query phone here. Covered next
                        Cursor phones = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + id, null, null);
                        while (phones.moveToNext()) {
                            String phoneNumberX = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            // Log.i("Number", phoneNumber);

                            phoneNumberX = phoneNumberX.replace(" ", "");
                            phoneNumberX = phoneNumberX.replace("(", "");
                            phoneNumberX = phoneNumberX.replace(")", "");
                            if (phoneNumberX.contains(phoneNumber)) {
                                phones.close();
                                return true;

                            }

                        }
                        phones.close();
                    }

                }
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());

        }

        return false;
    }



    public void readContacts(Context ctx){
        ContentResolver cr = ctx.getContentResolver();
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
                    mangeSaveContact(ctx);
                    pCur.close();


                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }
    public void mangeSaveContact(Context ctx){
        ContatcList contatcListObject= MypreferenceManager.getInstance(ctx).getAndroidContacts();
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
                updateContact(ctx);
            }

        }else {
            flag =false;
            updateContact(ctx);
        }
//
    }
    public void updateContact(Context ctx){

        ContatcList contatcList=MypreferenceManager.getInstance(ctx ).getAndroidContacts();
        AndroidContacts androidContacts =new AndroidContacts();
        androidContacts.setId(id);
        androidContacts.setName(name);
        androidContacts.setPhoneNumber(phoneNo);
        contatcList.addContatcList(androidContacts);
        MypreferenceManager.getInstance(ctx).putAndroidContacts(contatcList);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[1]  == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","response granted");
                    //readContacts();
                    Insert2Contacts(getActivity(),contactname.getText().toString(),contactnumber.getText().toString());
                } else {
                    Log.d("TAG","response not granted");
                }
                return;
            }

        }

    }

    public void requestPermission(){
        //what pernission checked
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            //readContacts();
            Insert2Contacts(getActivity(),contactname.getText().toString(),contactnumber.getText().toString());
        }
    }
}
