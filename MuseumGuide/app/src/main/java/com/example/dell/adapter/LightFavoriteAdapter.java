package com.example.dell.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.model.ArtifactView;
import com.example.dell.model.FavoriteMuseumView;
import com.example.dell.museumguide.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LightFavoriteAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<FavoriteMuseumView> object;
    private LayoutInflater inflater;

    public LightFavoriteAdapter (Context context, ArrayList<FavoriteMuseumView> object) {
        this.context = context;
        this.object = object;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return object.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return object.get(groupPosition).getArtifactViews().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return object.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return object.get(groupPosition).getArtifactViews().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return object.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return object.get(groupPosition).getArtifactViews().get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_museum,null);

        TextView txtListName = (TextView) convertView.findViewById(R.id.txtListName);
        TextView txtListAddress = (TextView) convertView.findViewById(R.id.txtListAddress);
        ImageView imgList = (ImageView) convertView.findViewById(R.id.imgList);

        FavoriteMuseumView favoriteMuseumView = this.object.get(groupPosition);

        convertView.setBackgroundResource(R.color.colorAccentAlpha);

        txtListName.setText(favoriteMuseumView.getName());
        txtListName.setTextColor(Color.BLACK);

        txtListAddress.setText(favoriteMuseumView.getAddress());
        txtListAddress.setTextColor(Color.BLACK);

        String image = favoriteMuseumView.getImage();

        Drawable drawable = null;
        try {
            InputStream inputStream = context.getAssets().open(image);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imgList.setImageDrawable(drawable);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_artifact,null);

        TextView txtArtTitle = (TextView) convertView.findViewById(R.id.txtArtTitle);
        ImageView imgArt = (ImageView) convertView.findViewById(R.id.imgArt);

        ArtifactView artifactView = this.object.get(groupPosition).getArtifactViews().get(childPosition);

        txtArtTitle.setText(artifactView.getTitle());
        txtArtTitle.setTextColor(Color.BLACK);

        String image = artifactView.getImage();

        Drawable drawable = null;
        try {
            InputStream inputStream = context.getAssets().open(image);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imgArt.setImageDrawable(drawable);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
