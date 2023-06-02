package com.example.theguardian_final;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<ArticleModel> {

    private Context context;
    private List<ArticleModel> articleList;
    private DatabaseHelper databaseHelper;
    private SparseBooleanArray selectedPositions;

    public ArticleAdapter(Context context, List<ArticleModel> articleList) {
        super(context, 0, articleList);
        this.context = context;
        this.articleList = articleList;
        databaseHelper = new DatabaseHelper(context);
        selectedPositions = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.text_item_article, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.titleTextView = convertView.findViewById(R.id.textViewTitle);
            viewHolder.sectionTextView = convertView.findViewById(R.id.textViewSection);
            viewHolder.favoriteIcon = convertView.findViewById(R.id.favoriteIcon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ArticleModel article = articleList.get(position);

        viewHolder.titleTextView.setText(article.getTitle());
        viewHolder.sectionTextView.setText(article.getSectionName());

        if (article.isFavorite()) {
            viewHolder.favoriteIcon.setImageResource(R.drawable.favourite_filled);
        } else {
            viewHolder.favoriteIcon.setImageResource(R.drawable.favourite_icon);
        }

        // Set the background color of the item based on its selection
        if (selectedPositions.get(position)) {
            // Selected item
            // Set the background color to indicate selection
            convertView.setBackgroundColor(context.getResources().getColor(R.color.selectedItemColor));
        } else {
            // Not selected item
            // Set the background color to the default color
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        viewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteStatus(article);
            }
        });

        return convertView;
    }

    public void toggleSelection(int position) {
        boolean isSelected = selectedPositions.get(position);
        selectedPositions.put(position, !isSelected);
        notifyDataSetChanged();
    }

    public void clearSelections() {
        selectedPositions.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedPositions.size();
    }

    public SparseBooleanArray getSelectedPositions() {
        return selectedPositions;
    }

    private void toggleFavoriteStatus(ArticleModel article) {
        if (article.isFavorite()) {
            removeArticleFromFavorites(article);
            Toast.makeText(context, "Article removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            addArticleToFavorites(article);
            Toast.makeText(context, "Article added to favorites", Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

    private void addArticleToFavorites(ArticleModel article) {
        long insertedId = databaseHelper.insertArticle(article.getTitle(), article.getUrl(), article.getSectionName());
        if (insertedId != -1) {
            article.setFavorite(true);
            article.setId((int) insertedId);
        }
    }

    private void removeArticleFromFavorites(ArticleModel article) {
        int deletedRows = databaseHelper.deleteArticle(article.getId());
        if (deletedRows > 0) {
            article.setFavorite(false);
            article.setId(0);
        }
    }

    private static class ViewHolder {
        TextView titleTextView;
        TextView sectionTextView;
        ImageView favoriteIcon;
    }
}
