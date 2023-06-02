package com.example.theguardian_final;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.theguardian_final.MainActivity;
import com.example.theguardian_final.my_favourite;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class LanguageSettings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private Spinner languageSpinner;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_settings);

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button saveButton = findViewById(R.id.button_save);
        languageSpinner = findViewById(R.id.spinner_language);

        // Set up the spinner with language options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.language_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Retrieve the saved language preference and set it as the selected option
        String savedLanguage = sharedPreferences.getString("language", "");
        int selectedLanguageIndex = adapter.getPosition(savedLanguage);
        languageSpinner.setSelection(selectedLanguageIndex);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedLanguage = languageSpinner.getSelectedItem().toString();

                // Save the selected language to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("language", selectedLanguage);
                editor.apply();

                // Update the locale and restart the application
                updateLocale(selectedLanguage);
            }
        });
    }

    private void updateLocale(String language) {
        Locale newLocale = new Locale(language);
        Locale.setDefault(newLocale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(newLocale);
        configuration.setLayoutDirection(newLocale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Log the updated locale
        Log.d("LanguageSettings", "Updated locale: " + Locale.getDefault().toString());

        // Restart the application by starting the main activity
        restartApplication();
    }

    private void restartApplication() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_search:
                // Open MainActivity
                startActivity(new Intent(LanguageSettings.this, MainActivity.class));
                break;
            case R.id.menu_favorite:
                // Open MyFavoritesActivity
                startActivity(new Intent(LanguageSettings.this, my_favourite.class));
                break;
            case R.id.Language:
                // Open LanguageSettingsActivity (already in LanguageSettings)
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
