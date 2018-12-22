package com.example.alifatemeh.mycontact;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AddContactFragment extends Fragment {
    private EditText contactname;
    private EditText contactnumber;
    private EditText contactbirthday;
    private ImageView contactedit,contactdelet,callphone;
    private int edit=0;
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
                Insert2Contacts(getActivity(),contactname.getText().toString(),contactnumber.getText().toString());
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
                getFragmentManager().popBackStack();
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
}
