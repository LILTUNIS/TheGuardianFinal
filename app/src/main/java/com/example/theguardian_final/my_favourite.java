package com.example.theguardian_final;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class my_favourite extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listViewFavorites;
    private ArticleAdapter articleAdapter;
    private DatabaseHelper databaseHelper;
    private Button deleteButton;
    private TextView emptyMessage;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourite);

        listViewFavorites = findViewById(R.id.listViewFavorites);
        deleteButton = findViewById(R.id.button_delete);
        emptyMessage = findViewById(R.id.empty_message);
        databaseHelper = new DatabaseHelper(this);
        drawerLayout = findViewById(R.id.drawer_layout);

        final List<ArticleModel> favoriteArticles = databaseHelper.getAllArticles();
        articleAdapter = new ArticleAdapter(this, favoriteArticles);
        listViewFavorites.setAdapter(articleAdapter);

        listViewFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                articleAdapter.toggleSelection(position);
                updateDeleteButton();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray selectedPositions = articleAdapter.getSelectedPositions();

                for (int i = selectedPositions.size() - 1; i >= 0; i--) {
                    if (selectedPositions.valueAt(i)) {
                        int position = selectedPositions.keyAt(i);
                        ArticleModel article = favoriteArticles.get(position);
                        databaseHelper.deleteArticle(article.getId());
                        favoriteArticles.remove(position);
                    }
                }

                articleAdapter.clearSelections();
                articleAdapter.notifyDataSetChanged();
                updateDeleteButton();
                updateEmptyMessage();
                showSnackbar(deleteButton, getString(R.string.snackbar_message));
            }
        });


        updateDeleteButton();
        updateEmptyMessage();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_search:
                // Open MainActivity
                startActivity(new Intent(my_favourite.this, MainActivity.class));
                break;
            case R.id.menu_favorite:
                // Open MyFavoritesActivity
                startActivity(new Intent(my_favourite.this, my_favourite.class));
                break;
            case R.id.Language:
                // Open LanguageSettingsActivity
                startActivity(new Intent(my_favourite.this, LanguageSettings.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void updateDeleteButton() {
        int selectedItemCount = articleAdapter.getSelectedItemCount();
        if (selectedItemCount > 0) {
            deleteButton.setEnabled(true);
            deleteButton.setText(getString(R.string.delete_selected, selectedItemCount));
        } else {
            deleteButton.setEnabled(false);
            deleteButton.setText(getString(R.string.delete));
        }
    }

    private void updateEmptyMessage() {
        if (articleAdapter.getCount() == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
            listViewFavorites.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            listViewFavorites.setVisibility(View.VISIBLE);
        }
    }
    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
