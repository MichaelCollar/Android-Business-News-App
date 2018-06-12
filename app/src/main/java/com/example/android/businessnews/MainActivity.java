package com.example.android.businessnews;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<BusinessNews>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    // URL for business news data
    private static final String REQUEST_URL = "https://content.guardianapis.com/search";

    // Constant value for the loader ID.
    private static final int NEWS_LOADER_ID = 1;

    // Adapter for the list of business articles
    private NewsAdapter adapter;

    // TextView that is displayed when the list is empty
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView businessNewsListView = (ListView) findViewById(R.id.list);

        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        businessNewsListView.setEmptyView(emptyStateTextView);

        // Creates a new adapter that takes an empty list of business articles as input
        adapter = new NewsAdapter(this, new ArrayList<BusinessNews>());

        // Set the adapter on the {@link ListView} so the list can be populated in the UI
        businessNewsListView.setAdapter(adapter);

        /*
         * Set an item click listener on the ListView, which sends an intent to a web browser
         * to open a website with more information about the selected business article.
         */
        businessNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current business article that was clicked on
                BusinessNews currentBusinessNews = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentBusinessNews.getUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            /*
            * Initialize the loader. Pass in the int ID constant defined above and pass in null for
            * the bundle. Pass in this activity for the LoaderCallbacks parameter.
            */
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    // onCreateLoader instantiates and returns a new Loader for the given ID
    @Override
    public Loader<List<BusinessNews>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

       /*
        * getString retrieves a String value from the preferences.
        * The second parameter is the default value for this preference.
        */
        String topic = sharedPrefs.getString(getString(R.string.settings_topic), getString(R.string.settings_topic_default));

        // Get the information from SharedPreferences and check for the value associated with the key
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default)
        );

        // Parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("q", topic);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("order-date", "published");
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "test");

        // Return the completed uri
        return new BusinessNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<BusinessNews>> loader, List<BusinessNews> businessNews) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No business news found."
        emptyStateTextView.setText(R.string.no_news);

        // If there is a valid list of {@link BusinessNews}s, then add them to the adapter
        if (businessNews != null && !businessNews.isEmpty()) {
            adapter.addAll(businessNews);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BusinessNews>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }

    @Override
    // This method initializes the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}