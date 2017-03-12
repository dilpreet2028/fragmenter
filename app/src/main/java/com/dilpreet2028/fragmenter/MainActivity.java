package com.dilpreet2028.fragmenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dilpreet2028.fragmenter_annotations.Injector;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.content,
                DemoFragmentBuilder.newInstance("Hello world")).commit();


    }
}
