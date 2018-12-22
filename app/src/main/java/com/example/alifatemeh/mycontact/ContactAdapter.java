package com.example.alifatemeh.mycontact;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<AndroidContacts> items;
    public boolean flag;
    private Context context;
    //view ra chejori besazam
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //add template
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.template_contact, viewGroup,false);
        return new ViewHolder(view);
    }

    //new view visable
    @Override
    public void onBindViewHolder(@NonNull final ContactAdapter.ViewHolder viewHolder,final int i) {
        viewHolder.contact_name.setText(items.get(i).getName());
        viewHolder.contact_phone.setText(items.get(i).getPhoneNumber());
        String id =items.get(i).getName();
        viewHolder.linerContactname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.linerContactaction.getVisibility()==v.INVISIBLE){
                    viewHolder.linerContactaction.setVisibility(v.VISIBLE);
                }else {
                    viewHolder.linerContactaction.setVisibility(v.INVISIBLE);
                }
            }
        });
        viewHolder.contact_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("intent_info");
                intent.putExtra("info",items.get(i).getId());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
              //  MypreferenceManager.getInstance(context).putPhoneNumber(items.get(i).getId());

                MypreferenceManager.getInstance(context).putId(items.get(i).getId());
                AndroidContacts androidContacts=new AndroidContacts();
                androidContacts.setId(items.get(i).getId());
//                try {
//                    context.startActivity(intent);
//
//                }catch (Exception e){
//
//                }
            }
        });
        viewHolder.contact_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //InfoContactFragment infoContactFragment=new InfoContactFragment();
                PermissionCall permissions = new PermissionCall(context);
                permissions.checkWriteExternalStoragePermission();
                String call=viewHolder.contact_phone.getText().toString();
                if(call.trim().length()>0){
                    String Dial="tel:"+call;
                    context.startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(Dial)));

                }else {
                    //Toast.makeText(getActivity(),"Donot number for Call",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //chan ta item dare recyclerview
    @Override
    public int getItemCount() {
        return items.size();
    }
public void setvisibilityliner(final ContactAdapter.ViewHolder viewHolder, final int i, boolean flag){
   // final ContactAdapter.ViewHolder viewHolder =new ViewHolder();

    if(flag==false){
        flag=true;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.linerContactaction.setVisibility(v.VISIBLE);
            }
        });
    }else if(flag==true) {
        flag = false;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.linerContactaction.setVisibility(v.INVISIBLE);
            }

        });
    }
}

    public ContactAdapter(List<AndroidContacts> items,Context context) {
        this.items = items;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView score;
        public TextView contact_name;
        public TextView contact_phone;
        public LinearLayout linerContactaction;
        public LinearLayout linerContactname;
        public ImageView contact_call;
        public ImageView contact_info;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
          //  score=itemView.findViewById(R.id.player_score);
            contact_name=itemView.findViewById(R.id.contact_name);
            contact_phone=itemView.findViewById(R.id.contact_phone);
            contact_info=itemView.findViewById(R.id.contact_info);
            contact_call=itemView.findViewById(R.id.contact_call);
            linerContactaction=itemView.findViewById(R.id.linerContactaction);
            linerContactname=itemView.findViewById(R.id.linerContactname);
        }
    }
}
