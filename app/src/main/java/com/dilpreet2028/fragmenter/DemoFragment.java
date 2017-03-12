package com.dilpreet2028.fragmenter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dilpreet2028.fragmenter_annotations.Fragmenter;
import com.dilpreet2028.fragmenter_annotations.annotations.Arg;
import com.dilpreet2028.fragmenter_annotations.annotations.FragModule;


@FragModule
public class DemoFragment extends Fragment {


    @Arg
    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_demo, container, false);
        Fragmenter.inject(this);

        ((TextView) view.findViewById(R.id.tv_text)).setText(data);

        return view;
    }

}
