package com.example.dell.adapter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dell.museumguide.R;

import java.io.IOException;
import java.io.InputStream;

public class ImageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        Drawable drawable = null;
        try {
            InputStream inputStream = getContext().getAssets().open(getArguments().getString("resource"));
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView imgFragment = (ImageView) view.findViewById(R.id.imgFragment);
        imgFragment.setImageDrawable(drawable);

        return view;
    }

    public static ImageFragment newInstance(String resource) {

        ImageFragment f = new ImageFragment();
        Bundle b = new Bundle();
        b.putString("resource", resource);

        f.setArguments(b);

        return f;
    }
}
