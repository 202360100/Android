package com.example.layouts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLinear = findViewById(R.id.btnLinear);
        Button btnRelative = findViewById(R.id.btnRelative);
        Button btnFrame = findViewById(R.id.btnFrame);
        Button btnTable = findViewById(R.id.btnTable);

        btnLinear.setOnClickListener(v ->
                startActivity(new Intent(this, LinearLayoutActivity.class)));

        btnRelative.setOnClickListener(v ->
                startActivity(new Intent(this, RelativeLayoutActivity.class)));

        btnFrame.setOnClickListener(v ->
                startActivity(new Intent(this, FrameLayoutActivity.class)));

        btnTable.setOnClickListener(v ->
                startActivity(new Intent(this, TableLayoutActivity.class)));
    }
}
