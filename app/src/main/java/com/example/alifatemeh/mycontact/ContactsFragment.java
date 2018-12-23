package com.example.alifatemeh.mycontact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactsFragment  extends Fragment {

    private ContactAdapter contactAdapter;
    private RecyclerView contactsList;
    private ProgressBar progressBar;
    private TextView progressUpdate;
    private ImageView contact_add;
    private ImageView contact_refresh;
    private ManagmentContact managmentContact;

//    private ContactAdapter contactAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=135;
    private String id;
    private String name;
    private String phoneNo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findviews(view);
    }


    public void configureContactList(){
        MypreferenceManager.getInstance(getActivity()).clearEverything();
        managmentContact=new ManagmentContact(getActivity());
        managmentContact.readContacts(getActivity());
        ContatcList contatcListObject= MypreferenceManager.getInstance(getActivity()).getAndroidContacts();
        //sort horof bar alef ba
        Comparator<AndroidContacts> androidContactsComparator = new Comparator<AndroidContacts>() {
            @Override
            public int compare(AndroidContacts o1, AndroidContacts o2) {
                return o1.getName().compareTo(o2.getName());
            }

        };
        Collections.sort(contatcListObject.getContatcList(),androidContactsComparator);
        contactAdapter =new ContactAdapter(contatcListObject.getContatcList(),getActivity());
        contactsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactsList.setAdapter(contactAdapter);
        contact_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MypreferenceManager.getInstance(getActivity()).clearEverything();
                managmentContact=new ManagmentContact(getActivity());
                managmentContact.readContacts(getActivity());
                ContatcList contatcListObject= MypreferenceManager.getInstance(getActivity()).getAndroidContacts();
                //sort horof bar alef ba
                Comparator<AndroidContacts> androidContactsComparator = new Comparator<AndroidContacts>() {
                    @Override
                    public int compare(AndroidContacts o1, AndroidContacts o2) {
                        return o1.getName().compareTo(o2.getName());
                    }

                };
                Collections.sort(contatcListObject.getContatcList(),androidContactsComparator);
                contactAdapter =new ContactAdapter(contatcListObject.getContatcList(),getActivity());
                contactsList.setLayoutManager(new LinearLayoutManager(getActivity()));
                contactsList.setAdapter(contactAdapter);
            }
        });
        contact_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddContactFragment addContactFragment=new AddContactFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.frag_container,addContactFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
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
    private void findviews(View view){
        contactsList =(RecyclerView ) view.findViewById(R.id.contacts);
        progressBar=view.findViewById(R.id.progress_bar);
        progressUpdate=view.findViewById(R.id.progressUpdate);
        contact_add=view.findViewById(R.id.contact_add);
        contact_refresh=view.findViewById(R.id.contact_refresh);
        configureContactList();
        // highScore = (TextView) view.findViewById(R.id.high_score);
    }

    //sort list rooms
//    private class SortRoomsAsync extends AsyncTask<Void ,Integer ,Boolean> {
//        List<Room> rooms;
//
//        public SortRoomsAsync(List<Room> rooms) {
//            this.rooms = rooms;
//        }
//
//        //sort list rooms in thread
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            for (int i=0;i<400000;i++){
//                Collections.sort(rooms, new Comparator<Room>() {
//                    @Override
//                    public int compare(Room x, Room y) {
//                        return x.getName().compareTo(y.getName());
//                    }
//                });
//                if(i%1000000==0){
//                    Log.d("TAG","here "+i );
//                    publishProgress(i/10000);
//                }
//            }
//            return true;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected void onPostExecute(Boolean successful) {
//            super.onPostExecute(successful);
//            progressBar.setVisibility(View.INVISIBLE);
//            roomList.clear();
//            roomList.addAll(this.rooms);
//            roomAdapter.notifyDataSetChanged();
//
////            idRoom.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    Log.d("TAG", "onResponse : " + idRoom.getText());
//////                onSelectedListener.onIdRoomSelected(items.get().getId());
//////                    sendBroadcast(intent);
//////                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
//////                            new Intent(items.get(i).getId())
//////                    );
////                }
////            });
//
//        }
//
//    }
}
