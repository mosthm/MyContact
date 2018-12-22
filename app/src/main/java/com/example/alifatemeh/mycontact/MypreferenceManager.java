package com.example.alifatemeh.mycontact;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class MypreferenceManager {
    private static MypreferenceManager instance =null;
    private SharedPreferences sharedPreferences=null;
    private SharedPreferences.Editor editor=null;
    public static MypreferenceManager getInstance(Context context){
        if(instance==null){
            instance=new MypreferenceManager(context);
        }
        return instance;
    }
    private MypreferenceManager(Context context){
        sharedPreferences =context.getSharedPreferences("my_preference",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        //editor.pu
    }

    //******************************************************************

    public void deletItem(String phoneNumber){
        editor.remove(phoneNumber);
        editor.commit();
    }

    public void clearEverything(){
        editor.clear();
        editor.apply();
    }

    public String getId(){
        return sharedPreferences.getString("id",null);
    }
    public  void putId(String id){
        //use of editor then write
        editor.putString("id",id);
        editor.apply();
    }
    public String getNameContact(){
        return sharedPreferences.getString("namecontact",null);
    }
    public  void putNameContact(String namecontact){
        //use of editor then write
        editor.putString("namecontact",namecontact);
        editor.apply();
    }    public String getPhoneNumber(){
        return sharedPreferences.getString("password",null);
    }
    public  void putPhoneNumber(String pass){
        //use of editor then write
        editor.putString("password",pass);
        editor.apply();
    }

//    public void putAndroidContacts(AndroidContacts androidContacts){
//        Gson gson =new Gson();
//        String id = gson.toJson(getId(),AndroidContacts.class);
//        String name = gson.toJson(getNameContact(),AndroidContacts.class);
//        String phone = gson.toJson(getPhoneNumber(),AndroidContacts.class);
//        editor.putString("id",id);
//        editor.putString("name",name);
//        editor.putString("phone",phone);
//        editor.apply();
//    }
//    public AndroidContacts getAndroidContacts(){

//        AndroidContacts androidContacts=sharedPreferences.
//       AndroidContacts androidContacts=sharedPreferences.getString("best_user",null);
//        if(androidContacts==null){
//            return null;
//        }
//        Gson gson=new Gson();
//        return gson.fromJson(androidContacts,AndroidContacts.class);
//    }

    //    //******************************************************************
    public void putAndroidContacts (ContatcList contatcList){
        Gson gson=new Gson();
        String androidContactsJson=gson.toJson(contatcList,ContatcList.class);
        editor.putString("contatcList",androidContactsJson);
        editor.apply();
    }
    public ContatcList getAndroidContacts(){
        String androidContactsJson = sharedPreferences.getString("contatcList",null);
        if(androidContactsJson==null){
            return new ContatcList();
        }
        Gson gson=new Gson();
        return gson.fromJson(androidContactsJson,ContatcList.class);
    }
}
