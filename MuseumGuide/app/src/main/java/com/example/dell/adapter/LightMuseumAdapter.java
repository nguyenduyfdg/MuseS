package com.example.dell.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.model.MuseumView;
import com.example.dell.museumguide.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by DELL on 3/19/2017.
 */

public class LightMuseumAdapter extends ArrayAdapter<MuseumView> {

    Activity activity;
    int resource;
    List<MuseumView> objects;

    public LightMuseumAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List<MuseumView> objects) {
        super(activity, resource, objects);

        this.activity = activity;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.activity.getLayoutInflater();
        View row = inflater.inflate(this.resource,null);

        TextView txtListName = (TextView) row.findViewById(R.id.txtListName);
        TextView txtListAddress = (TextView) row.findViewById(R.id.txtListAddress);
        ImageView imgList = (ImageView) row.findViewById(R.id.imgList);

        MuseumView museumView = this.objects.get(position);

        txtListName.setText(museumView.getName());
        txtListName.setTextColor(Color.BLACK);

        txtListAddress.setText(museumView.getAddress());
        txtListAddress.setTextColor(Color.BLACK);

        String image = museumView.getImage();

        Drawable drawable = null;
        try {
            InputStream inputStream = activity.getAssets().open(image);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imgList.setImageDrawable(drawable);

        return row;
    }
}
