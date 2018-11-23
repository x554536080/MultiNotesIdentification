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
    public void onModifyMultiNoteTextA(String n, String f,String e) {
        multiNoteView.onModifyFreqA(f);
        multiNoteView.onModifyNoteA(n);
        multiNoteView.onModifyErrorA(e);
    }

    @Override
    public void onModifyMultiNoteTextB(String n, String f,String e) {
        multiNoteView.onModifyFreqB(f);
        multiNoteView.onModifyNoteB(n);
        multiNoteView.onModifyErrorB(e);
    }

    @Override
    public void onModifyMultiNoteTextC(String n, String f,String e) {
        multiNoteView.onModifyFreqC(f);
        multiNoteView.onModifyNoteC(n);
        multiNoteView.onModifyErrorC(e);
    }

    @Override
    public void onModifySingleNote(String n1, String error) {
        singleNoteView.onModifyErrorText(error);
        singleNoteView.onModifyNoteText(n1);

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
