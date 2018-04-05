package com.example.aks4125.mvpdemo.main.presenter;

import android.content.Context;
import android.util.Log;

import com.example.aks4125.mvpdemo.main.view.MainContractor;

/**
 * Created by akashb on 3/23/2018.
 */

public class MainPresenter implements MainContractor.IMainPresenter {

    MainContractor.IMainView mView;
    Context context;

    public MainPresenter(Context context) {
        this.context = context;
    }

    public void setmView(MainContractor.IMainView mView) {
        this.mView = mView;
    }


    @Override
    public void callAPI(String data) {

        Log.d("MVP", "callAPI: ");
        mView.processData("data received");

    }
}
