package com.example.xds.multinotesidentification.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.xds.multinotesidentification.R;
import com.example.xds.multinotesidentification.presenter.PresenterImpl;

public class MultiNoteActivity extends AppCompatActivity implements MultiNoteView {

    TextView noteA;
    TextView noteB;
    TextView noteC;
    TextView freqA;
    TextView freqB;
    TextView freqC;
    TextView errorA;
    TextView errorB;
    TextView errorC;

    PresenterImpl presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_note);
        init();
        presenter = new PresenterImpl(this, this);
        presenter.startRecord();
    }


    @Override
    protected void onDestroy() {
        presenter.stopRecord();
        super.onDestroy();
    }

    void init() {
        noteA = findViewById(R.id.multi_note_a);
        noteB = findViewById(R.id.multi_note_b);
        noteC = findViewById(R.id.multi_note_c);

        freqA = findViewById(R.id.multi_freq_a);
        freqB = findViewById(R.id.multi_freq_b);
        freqC = findViewById(R.id.multi_freq_c);

        errorA = findViewById(R.id.multi_error_a);
        errorB = findViewById(R.id.multi_error_b);
        errorC = findViewById(R.id.multi_error_c);
    }

    @Override
    public void onModifyNoteA(String s) {
        noteA.setText(s);
    }

    @Override
    public void onModifyNoteB(String s) {
        noteB.setText(s);

    }

    @Override
    public void onModifyNoteC(String s) {
        noteC.setText(s);

    }

    @Override
    public void onModifyFreqA(String s) {
        freqA.setText(s);
    }

    @Override
    public void onModifyFreqB(String s) {
        freqB.setText(s);

    }

    @Override
    public void onModifyFreqC(String s) {
        freqC.setText(s);

    }

    @Override
    public void onModifyErrorA(String s) {
        errorA.setText(s);
    }

    @Override
    public void onModifyErrorB(String s) {
        errorB.setText(s);

    }

    @Override
    public void onModifyErrorC(String s) {
        errorC.setText(s);

    }
}
