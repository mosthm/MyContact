package com.example.alifatemeh.mycontact;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private void findviews(View view){
        contactsList =(RecyclerView ) view.findViewById(R.id.contacts);
        progressBar=view.findViewById(R.id.progress_bar);
        progressUpdate=view.findViewById(R.id.progressUpdate);
        contact_add=view.findViewById(R.id.contact_add);
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
