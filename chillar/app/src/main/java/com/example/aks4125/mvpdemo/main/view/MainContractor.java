package com.example.aks4125.mvpdemo.main.view;

/**
 * Created by akashb on 3/23/2018.
 */

public interface MainContractor {

    interface IMainView {
        void showProgress();

        void stopProgress();

        void showMessage(String msg);

        void processData(Object obj);

    }
    interface IMainPresenter {

        void callAPI(String data);

    }

}
