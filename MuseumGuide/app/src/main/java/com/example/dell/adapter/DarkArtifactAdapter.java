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

import com.example.dell.model.ArtifactView;
import com.example.dell.museumguide.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by DELL on 3/20/2017.
 */

public class DarkArtifactAdapter extends ArrayAdapter<ArtifactView> {

    Activity activity;
    int resource;
    List<ArtifactView> objects;

    public DarkArtifactAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List<ArtifactView> objects) {
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

        TextView txtArtTitle = (TextView) row.findViewById(R.id.txtArtTitle);
        ImageView imgArt = (ImageView) row.findViewById(R.id.imgArt);

        ArtifactView artifactView = this.objects.get(position);

        txtArtTitle.setText(artifactView.getTitle());
        txtArtTitle.setTextColor(Color.WHITE);

        String image = artifactView.getImage();

        Drawable drawable = null;
        try {
            InputStream inputStream = activity.getAssets().open(image);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imgArt.setImageDrawable(drawable);

        return row;
    }
}
