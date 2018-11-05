package com.example.xds.multinotesidentification.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.xds.multinotesidentification.R;

public class ShowFreqActivity extends AppCompatActivity implements ShowFreqView{

    TextView freqText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_freq);
        freqText = findViewById(R.id.show_freq_text);
    }

    @Override
    public void onModifyFreqText(String s) {
        freqText.setText(s);
    }
}
