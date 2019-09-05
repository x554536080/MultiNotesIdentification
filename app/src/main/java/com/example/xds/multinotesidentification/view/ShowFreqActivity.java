package com.example.xds.multinotesidentification.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.xds.multinotesidentification.R;
import com.example.xds.multinotesidentification.presenter.PresenterImpl;

public class ShowFreqActivity extends AppCompatActivity implements ShowFreqView {

    TextView freqText;
    PresenterImpl presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_freq);
        freqText = findViewById(R.id.show_freq_text);
        presenter = new PresenterImpl(this,this);
        presenter.startRecord();
    }

    @Override
    protected void onDestroy() {
        presenter.stopRecord();
        super.onDestroy();
    }

    @Override
    public void onModifyFreqText(String s) {
        freqText.setText(s);
    }
}
