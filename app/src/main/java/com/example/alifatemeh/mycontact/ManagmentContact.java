package com.example.alifatemeh.mycontact;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class ManagmentContact  {

        private ContactAdapter contactAdapter;
        private TabLayout tabLayout;
        private ViewPager viewPager;
        private MyFragmentPagerAdapter myFragmentPagerAdapter;
        private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=135;
        private String id;
        private String name;
        private String phoneNo;
        private Context context;

    public ManagmentContact(Context context) {
        this.context = context;

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

//                        getFragmentManager().popBackStack();

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
}
