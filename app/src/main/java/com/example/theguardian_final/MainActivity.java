package com.example.theguardian_final;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NetworkUtils.OnResponseListener, NavigationView.OnNavigationItemSelectedListener {

    private List<ArticleModel> articleList;
    private ArticleAdapter articleAdapter;
    private ListView listView;
    private EditText searchInput;
    private DatabaseHelper databaseHelper;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articleList);
        listView = findViewById(R.id.listViewSearchResults);
        listView.setAdapter(articleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArticleModel article = articleList.get(position);
                showArticleDetails(article);
            }
        });

        searchInput = findViewById(R.id.edit_text_search);

        Button searchButton = findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchInput.getText().toString();
                fetchArticles(searchQuery);
            }
        });
        progressBar = findViewById(R.id.progress_Bar);
        databaseHelper = new DatabaseHelper(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fetchArticles("world"); // Default search query

        // Load articles from the local database
        loadArticlesFromDatabase();
    }

    private void loadArticlesFromDatabase() {
        List<ArticleModel> savedArticles = databaseHelper.getAllArticles();
        articleList.clear();
        articleList.addAll(savedArticles);
        articleAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Language) {
            // Open LanguageSettingsActivity
            startActivity(new Intent(MainActivity.this, LanguageSettings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchArticles(String searchQuery) {
        progressBar.setVisibility(View.VISIBLE);
        NetworkUtils.searchArticles(searchQuery, this);
    }

    @Override
    public void onResponse(List<ArticleModel> articles) {
        progressBar.setVisibility(View.GONE);
        articleList.clear();
        articleList.addAll(articles);
        articleAdapter.notifyDataSetChanged();
    }

    private void showArticleDetails(ArticleModel article) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(article.getTitle())
                .setMessage("Title: " + article.getTitle() + "\n\nSection: " + article.getSectionName() + "\n\nURL: " + article.getUrl())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Open Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open the URL in a web browser
                        String url = article.getUrl();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_search:
                // Open MainActivity
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
            case R.id.menu_favorite:
                // Open MyFavoritesActivity
                startActivity(new Intent(MainActivity.this, my_favourite.class));
                break;
            case R.id.Language:
                // Open LanguageSettingsActivity
                startActivity(new Intent(MainActivity.this, LanguageSettings.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
