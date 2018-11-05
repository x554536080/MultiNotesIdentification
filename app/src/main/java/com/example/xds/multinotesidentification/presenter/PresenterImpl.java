package com.example.xds.multinotesidentification.presenter;


import android.view.View;

public class PresenterImpl implements Presenter {

    RecordThread recordThread;

    PresenterImpl(View view){
        recordThread = new RecordThread();
    }
}
