package com.example.xds.multinotesidentification.presenter;


import android.content.Context;
import android.view.View;

import com.example.xds.multinotesidentification.model.Complex;
import com.example.xds.multinotesidentification.view.MultiNoteActivity;
import com.example.xds.multinotesidentification.view.MultiNoteView;
import com.example.xds.multinotesidentification.view.ShowFreqView;
import com.example.xds.multinotesidentification.view.SingleNoteView;

public class PresenterImpl implements Presenter {

    Context context;
    ShowFreqView showFreqView;
    SingleNoteView singleNoteView;
    MultiNoteView multiNoteView;
    RecordThread recordThread;
    int currentActivity;

    public PresenterImpl(ShowFreqView view, Context context) {
        currentActivity = 2;
        this.context = context;
        showFreqView = view;
        recordThread = new RecordThread(this);
    }

    public PresenterImpl(SingleNoteView view, Context context) {
        currentActivity = 1;
        singleNoteView = view;
        recordThread = new RecordThread(this);
        this.context = context;
    }

    public PresenterImpl(MultiNoteActivity view, Context context) {
        currentActivity = 3;
        multiNoteView = view;
        recordThread = new RecordThread(this);
        this.context = context;
    }

    @Override
    public void onModifyShowFreqText(String s) {
        showFreqView.onModifyFreqText(s);
    }

    @Override
    public void onModifyMultiNoteText(String n1, String n2, String n3, String f1, String f2, String f3) {
        multiNoteView.onModifyFreqA(f1);
        multiNoteView.onModifyFreqB(f2);
        multiNoteView.onModifyFreqC(f3);
        multiNoteView.onModifyNoteA(n1);
        multiNoteView.onModifyNoteB(n2);
        multiNoteView.onModifyNoteC(n3);
    }

    @Override
    public void startRecord() {
        recordThread.isRecording = true;
        recordThread.start();

    }

    @Override
    public void stopRecord() {
        recordThread.isRecording = false;
    }


}
