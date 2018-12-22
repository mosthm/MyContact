package com.example.alifatemeh.mycontact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FavoritesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        findViews(view);
//        configure();
        //findviews(view);

//        User bestUser =MyPrefrenceManger.getInstance(getActivity()).getBestUser();
//        highScore.setText("Best Score : "+bestUser.getScore() + " from " + bestUser.getName());
        //configureRankList();
    }
}
