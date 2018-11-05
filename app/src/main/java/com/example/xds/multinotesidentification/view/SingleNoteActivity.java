package com.example.xds.multinotesidentification.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.xds.multinotesidentification.R;

public class SingleNoteActivity extends AppCompatActivity implements SingleNoteView {

    TextView textView1;
    TextView textView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_note);
        init();
    }

    void init() {
        textView1 = findViewById(R.id.single_note_text);
        textView2 = findViewById(R.id.single_freq_text);

    }

    @Override
    public void onModifyNoteText(String s) {
        textView1.setText(s);
    }

    @Override
    public void onModifyFreqText(String s) {
        textView2.setText(s);
    }
}
