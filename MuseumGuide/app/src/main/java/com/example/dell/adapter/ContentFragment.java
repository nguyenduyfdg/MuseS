package com.example.dell.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.museumguide.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ContentFragment extends Fragment {
    private TextView txtFragment;
    private String language;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        txtFragment = (TextView) view.findViewById(R.id.txtFragment);

        if (getArguments().getString("url") != null) {
            language = getArguments().getString("language");

            if (getArguments().getBoolean("dark")) {
                txtFragment.setTextColor(Color.WHITE);
            }
            else {
                txtFragment.setTextColor(Color.BLACK);
            }

            GetTextAsyncTask getTextAsyncTask = new GetTextAsyncTask(getArguments().getString("url"));
            getTextAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return view;
    }

    public static ContentFragment newInstance(String url, boolean dark, String language) {

        ContentFragment f = new ContentFragment();
        Bundle b = new Bundle();
        b.putString("url", url);
        b.putBoolean("dark", dark);
        b.putString("language", language);

        f.setArguments(b);

        return f;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetTextAsyncTask extends AsyncTask<Void, Void, String> {
        String url;
        GetTextAsyncTask(String urlPass){
            url = urlPass;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            switch (language) {
                case "english":
                    txtFragment.setText(R.string.loading_content_en);
                    break;
                case "vietnamese":
                    txtFragment.setText(R.string.loading_content_vi);
                    break;
                default:
                    txtFragment.setText(R.string.loading_content_en);
                    break;
            }
        }
        @Override
        protected String doInBackground(Void... params) {
            String string = null;

            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpEntity);

                InputStream inputStream = bufferedHttpEntity.getContent();

                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append("\n");
                }
                string = total.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return string;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            txtFragment.setText(result);
        }
    }
}
