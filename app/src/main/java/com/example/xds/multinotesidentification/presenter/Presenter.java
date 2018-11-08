package com.example.xds.multinotesidentification.presenter;


public interface Presenter {
    void onModifyMultiNoteText(String n1, String n2, String n3,
                               String f1, String f2, String f3);

    void onModifyShowFreqText(String s);

    void startRecord();

    void stopRecord();
}
