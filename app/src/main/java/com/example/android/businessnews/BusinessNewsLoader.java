package com.example.android.businessnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of the articles by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class BusinessNewsLoader extends AsyncTaskLoader<List<BusinessNews>> {

    // Tag for log messages
    private static final String LOG_TAG = BusinessNewsLoader.class.getName();

    // Query URL
    private String url;

    /**
     * Constructs a new {@link BusinessNewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public BusinessNewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // On a background thread.
    @Override
    public List<BusinessNews> loadInBackground() {
        if (this.url == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of business articles.
        List<BusinessNews> businessNews = QueryUtils.fetchBusinessNewsData(this.url);
        return businessNews;
    }
}
