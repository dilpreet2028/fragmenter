package com.dilpreet2028.fragmenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String data = "Hello world";
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("one");

        //using the builder class and passing the required variables.
        DemoFragment fragment = DemoFragmentBuilder.newInstance(arrayList,data);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content , fragment)
                .commit();

    }


}
