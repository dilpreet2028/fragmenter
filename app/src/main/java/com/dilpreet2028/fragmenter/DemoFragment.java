package com.dilpreet2028.fragmenter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dilpreet2028.fragmenter_annotations.Arg;
import com.dilpreet2028.fragmenter_annotations.FragModule;


@FragModule
public class DemoFragment extends Fragment {
    Fragment fragment;

    @Arg
    int demo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

}
