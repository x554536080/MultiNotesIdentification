package com.example.xds.multinotesidentification.presenter;


public interface Presenter {

    void onModifySingleNote(String n1, String error);

    void onModifyMultiNoteTextA(String n, String f, String e);

    void onModifyMultiNoteTextB(String n, String f, String e);

    void onModifyMultiNoteTextC(String n, String f, String e);

    void onModifyShowFreqText(String s);

    void startRecord();

    void stopRecord();
}
