package com.example.dell.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.dell.museumguide.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageFragment extends Fragment {
    private Animation loadingAnimation;
    private ImageView imgFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        imgFragment = (ImageView) view.findViewById(R.id.imgFragment);

        Context context = getActivity();
        loadingAnimation = AnimationUtils.loadAnimation(context, R.anim.loading);

        if (getArguments().getString("url") != null) {
            GetImageAsyncTask getImageAsyncTask = new GetImageAsyncTask(getArguments().getString("url"));
            getImageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return view;
    }

    public static ImageFragment newInstance(String url, boolean dark) {

        ImageFragment f = new ImageFragment();
        Bundle b = new Bundle();
        b.putString("url", url);
        b.putBoolean("dark", dark);

        f.setArguments(b);

        return f;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        String url = "";
        GetImageAsyncTask(String urlPass){
            url = urlPass;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (getArguments().getBoolean("dark")) {
                imgFragment.setImageResource(R.drawable.loading_white);
            }
            else {
                imgFragment.setImageResource(R.drawable.loading_black);
            }

            imgFragment.startAnimation(loadingAnimation);
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;

            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpEntity);

                InputStream inputStream = bufferedHttpEntity.getContent();

                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            imgFragment.clearAnimation();
            imgFragment.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgFragment.setImageBitmap(result);
        }
    }
}
