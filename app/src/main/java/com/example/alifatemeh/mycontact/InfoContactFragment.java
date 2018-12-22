package com.example.alifatemeh.mycontact;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class InfoContactFragment extends Fragment {
    private EditText contactname;
    private EditText contactnumber;
    private EditText contactbirthday;
    private ImageView contactedit,contactdelet,callphone;
    private int edit=0;
    private final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS=110;
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE=100;
    private SharedPreferences.Editor mEditor;
    private MypreferenceManager mSharedPreferences;
    private ListView favoriteList;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_infocontact, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findview(view);
        intent = getActivity().getIntent();
        String idContact = intent.getStringExtra("intent_info");
        Log.d("TAG", "info : " + idContact);
        AndroidContacts androidContacts = new AndroidContacts();
        androidContacts.setId(MypreferenceManager.getInstance(getActivity()).getId());
        mangeSaveContact();
        contactedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editclick();

            }
        });
        contactdelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
        callphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionCall();
            }
        });
//        androidContacts.setName(MypreferenceManager.getInstance(getActivity()).getNameContact());
//        contactname.setText(androidContacts.getName());
//        findviews(view);
//        progressBar.setVisibility(View.VISIBLE);
//        for (int i=0;i<400000;i++){
//
//        }
//        progressBar.setVisibility(View.INVISIBLE);
//        configureContactList();
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[1]  == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","response granted");
                    //readContacts();
                    deleteContact(getActivity(), contactnumber.getText().toString());
                } else {
                    Log.d("TAG","response not granted");
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[2]  == PackageManager.PERMISSION_GRANTED) {
                    mackeCallPhone();
                } else {
                    mackeDialPhone();
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
            deleteContact(getActivity(), contactnumber.getText().toString());
        }
    }
    public void requestPermissionCall(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CALL_PHONE)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE},MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            mackeCallPhone();
        }
    }
    public void mackeCallPhone(){
        String call=contactnumber.getText().toString();
        if(call.trim().length()>0){
            String Dial="tel:"+call;
            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(Dial)));

        }else {
            Toast.makeText(getActivity(),"Donot number for Call",Toast.LENGTH_SHORT).show();
        }
    }
    public void mackeDialPhone(){
        String call=contactnumber.getText().toString();
        if(call.trim().length()>0){
            String Dial="tel:"+call;
            startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse(Dial)));

        }else {
            Toast.makeText(getActivity(),"Donot number for Call",Toast.LENGTH_SHORT).show();
        }
    }
    public  void deleteContact(Context ctx, String phoneNumber) {
        ContatcList contatcListObject= MypreferenceManager.getInstance(getActivity()).getAndroidContacts();
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null,null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI,lookupKey);
                    ctx.getContentResolver().delete(uri, null, null);
                } while (cur.moveToNext());
//                mEditor= mSharedPreferences.edit();
//                mEditor.remove(contactnumber.getText().toString());
//                mEditor.commit();
            }
//            MypreferenceManager mypreferenceManager;
            mSharedPreferences.deletItem(phoneNumber);

            Toast.makeText(getActivity(), "This Contact Dellet!",
                    Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        // return false;
    }


    public void editclick(){
        if(edit==0){
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_done_black_24dp);
            contactedit.setImageDrawable(myDrawable);
            contactname.setEnabled(true);
            contactnumber.setEnabled(true);
            contactbirthday.setEnabled(true);
            edit=1;

        }else {
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_edit_black_24dp);
            contactedit.setImageDrawable(myDrawable);
            contactname.setEnabled(false);
            contactnumber.setEnabled(false);
            contactbirthday.setEnabled(false);
            edit=0;
        }
    }

    public void Insert2Contacts(Context ctx, String nameSurname,String telephone) {
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
            } catch (Exception e) {

                Log.d(TAG, e.getMessage());
            }
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


    public void mangeSaveContact(){
        ContatcList contatcListObject= MypreferenceManager.getInstance(getActivity()).getAndroidContacts();
        boolean flag=false;
        int i,g;
        g=contatcListObject.getContatcList().size();
        g=+1;

        if(!(contatcListObject.getContatcList().size()==0)) {
            for (i = 1; i < contatcListObject.getContatcList().size(); i++) {
                if (MypreferenceManager.getInstance(getActivity()).getId().equals(contatcListObject.getContatcList().get(i).getId()) ) {

                    contactname.setText(contatcListObject.getContatcList().get(i).getName());
                    contactnumber.setText(contatcListObject.getContatcList().get(i).getPhoneNumber());
                    contactbirthday.setText(contatcListObject.getContatcList().get(i).getBirth());
                    break;
                }
            }
        }else {
            flag =false;
//            updateContact();
        }
    }
}
