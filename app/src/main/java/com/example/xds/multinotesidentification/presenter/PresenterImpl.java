package com.example.xds.multinotesidentification.presenter;


import android.content.Context;
import android.view.View;

import com.example.xds.multinotesidentification.model.Complex;
import com.example.xds.multinotesidentification.view.ShowFreqView;
import com.example.xds.multinotesidentification.view.SingleNoteView;

public class PresenterImpl implements Presenter {

    Context context;
    ShowFreqView showFreqView;
    SingleNoteView singleNoteView;
    RecordThread recordThread;

    public PresenterImpl(ShowFreqView view,Context context) {
        this.context = context;
        showFreqView = view;
        recordThread = new RecordThread(this);
    }

    public PresenterImpl(SingleNoteView view,Context context) {
        singleNoteView = view;
        recordThread = new RecordThread(this);
    }

    @Override
    public void onModifyShowFreqText(String s) {
        showFreqView.onModifyFreqText(s);
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
