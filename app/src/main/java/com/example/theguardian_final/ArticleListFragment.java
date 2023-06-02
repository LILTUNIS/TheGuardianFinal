package com.example.theguardian_final;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
public class ArticleListFragment extends Fragment {

    private ListView listView;
    private ArticleAdapter articleAdapter;
    private List<ArticleModel> articleList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_list, container, false);

        // Initialize the ListView and adapter
        listView = rootView.findViewById(R.id.listViewSearchResults);
        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(getActivity(), articleList);
        listView.setAdapter(articleAdapter);

        // Get the FrameLayout from the main activity
        FrameLayout frameLayout = getActivity().findViewById(R.id.fragmentContainer);

        // Get the reference to the parent view of the FrameLayout
        ViewGroup parentView = (ViewGroup) frameLayout.getParent();

        // Remove the FrameLayout from its parent view
        parentView.removeView(frameLayout);

        // Create a new RelativeLayout to hold the FrameLayout and ListView
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());

        // Set the layout parameters of the RelativeLayout to match_parent
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        relativeLayout.setLayoutParams(layoutParams);

        // Add the FrameLayout to the RelativeLayout
        relativeLayout.addView(frameLayout);

        // Set the layout parameters of the ListView
        RelativeLayout.LayoutParams listViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        listViewParams.addRule(RelativeLayout.BELOW, R.id.edit_text_search); // Adjust this rule as needed
        listView.setLayoutParams(listViewParams);

        // Add the ListView to the RelativeLayout
        relativeLayout.addView(listView);

        // Add the RelativeLayout to the parent view
        parentView.addView(relativeLayout);

        return rootView;
    }
}
