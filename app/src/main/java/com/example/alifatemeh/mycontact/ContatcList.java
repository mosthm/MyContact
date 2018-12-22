package com.example.alifatemeh.mycontact;

import java.util.ArrayList;
import java.util.List;

public class ContatcList {
    private List<AndroidContacts> contatcList;
    public ContatcList(){
        contatcList = new ArrayList<>();
    }
    public  List<AndroidContacts> getContatcList(){
        return contatcList;
    }

    public void setContatcList(List<AndroidContacts> contatcList) {
        this.contatcList = contatcList;
    }

    //add user to List
    public void addContatcList(AndroidContacts androidContacts){
        contatcList.add(androidContacts);
    }
}
