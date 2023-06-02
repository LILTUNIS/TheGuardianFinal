package com.example.theguardian_final;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {

    private static final String BASE_URL = "https://content.guardianapis.com/search";
    private static final String API_KEY = "19e9f528-9f22-425b-8a56-80aef4462439";

    public static void searchArticles(String query, OnResponseListener listener) {
        String url = buildURL(query);
        new NetworkTask(listener).execute(url);
    }

    private static String buildURL(String query) {
        return BASE_URL + "?q=" + query + "&api-key=" + API_KEY;
    }

    private static List<ArticleModel> parseArticles(String response) {
        List<ArticleModel> articles = new ArrayList<>();
        try {
            if (response != null && !response.isEmpty()) {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.has("response")) {
                    JSONObject responseObj = responseObject.getJSONObject("response");
                    if (responseObj.has("results")) {
                        JSONArray resultsArray = responseObj.getJSONArray("results");
                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject articleObject = resultsArray.getJSONObject(i);
                            String title = articleObject.getString("webTitle");
                            String url = articleObject.getString("webUrl");
                            String sectionName = articleObject.getString("sectionName");
                            ArticleModel article = new ArticleModel(title, url, sectionName);
                            articles.add(article);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return articles;
    }


    private static class NetworkTask extends AsyncTask<String, Void, String> {

        private OnResponseListener listener;

        public NetworkTask(OnResponseListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    response = stringBuilder.toString();
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (listener != null) {
                List<ArticleModel> articles = parseArticles(response);
                listener.onResponse(articles);
            }
        }
    }

    public interface OnResponseListener {
        void onResponse(List<ArticleModel> articles);
    }
}
