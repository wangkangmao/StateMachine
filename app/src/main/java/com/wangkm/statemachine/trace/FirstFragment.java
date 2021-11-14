package com.wangkm.statemachine.trace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.wangkm.statemachine.R;


public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        TextView textView = view.findViewById(R.id.text_view);
        textView.setText("This is the first Fragment!");
        return view;
    }
}
