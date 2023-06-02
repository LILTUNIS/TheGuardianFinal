package com.example.theguardian_final;

import android.net.Uri;



import java.util.ArrayList;
import java.util.List;

public class GuardianAPI {

    private static final String BASE_URL = "https://content.guardianapis.com/";
    private static final String API_KEY = "19e9f528-9f22-425b-8a56-80aef4462439";

    public static void searchArticles(String searchQuery, NetworkUtils.OnResponseListener listener) {
        String apiUrl = buildSearchUrl(searchQuery);
        NetworkUtils.searchArticles(apiUrl, new NetworkUtils.OnResponseListener() {
            @Override
            public void onResponse(List<ArticleModel> articles) {
                List<ArticleModel> simplifiedArticles = simplifyArticles(articles);
                listener.onResponse(simplifiedArticles);
            }
        });
    }

    private static String buildSearchUrl(String searchQuery) {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath("search")
                .appendQueryParameter("q", searchQuery)
                .appendQueryParameter("api-key", API_KEY);
        return builder.build().toString();
    }

    private static List<ArticleModel> simplifyArticles(List<ArticleModel> articles) {
        List<ArticleModel> simplifiedArticles = new ArrayList<>();
        for (ArticleModel article : articles) {
            ArticleModel simplifiedArticle = new ArticleModel(article.getTitle(), article.getUrl(), article.getSectionName());
            simplifiedArticles.add(simplifiedArticle);
        }
        return simplifiedArticles;
    }
}
