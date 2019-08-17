package com.example.xds.multinotesidentification.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.xds.multinotesidentification.R;
import com.example.xds.multinotesidentification.presenter.PresenterImpl;

public class SingleNoteActivity extends AppCompatActivity implements SingleNoteView {

    TextView textView1;
    TextView textView2;
    PresenterImpl presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_note);
        init();
        presenter = new PresenterImpl(this, this);
        presenter.startRecord();
    }

    void init() {
        textView1 = findViewById(R.id.single_note_text);
        textView2 = findViewById(R.id.single_error_text);

    }

    @Override
    protected void onDestroy() {
        presenter = null;
        super.onDestroy();
    }

    @Override
    public void onModifyNoteText(String s) {
        textView1.setText(s);
    }

    @Override
    public void onModifyErrorText(String s) {
        textView2.setText(s);
    }


}
