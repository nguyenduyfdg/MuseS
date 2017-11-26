package com.example.dell.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.museumguide.R;

public class ContentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        TextView txtFragment = (TextView) view.findViewById(R.id.txtFragment);
        txtFragment.setText(getArguments().getString("text"));

        if (getArguments().getBoolean("dark")) {
            txtFragment.setTextColor(Color.WHITE);
        }
        else {
            txtFragment.setTextColor(Color.BLACK);
        }

        return view;
    }

    public static ContentFragment newInstance(String text, boolean dark) {

        ContentFragment f = new ContentFragment();
        Bundle b = new Bundle();
        b.putString("text", text);
        b.putBoolean("dark", dark);

        f.setArguments(b);

        return f;
    }
}
