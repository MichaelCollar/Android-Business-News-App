package com.example.android.businessnews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

// Helper methods related to requesting and receiving business news data from The Guardian API.
public final class QueryUtils {

    // Constants that contain keys of JSON objects
    public static final String RESPONSE = "response";
    public static final String RESULTS = "results";
    public static final String WEB_TITLE = "webTitle";
    public static final String SECTION_NAME = "sectionName";
    public static final String WEB_URL = "webUrl";
    public static final String PUBLICATION_DATE = "webPublicationDate";
    public static final String TAGS = "tags";

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods.
     */
    private QueryUtils() {
    }

    /**
     * Query the dataset and return a list of {@link BusinessNews} objects.
     */
    public static List<BusinessNews> fetchBusinessNewsData(String requestUrl) {
        // Creates URL object
        URL url = createUrl(requestUrl);

        // Performs HTTP request to the URL and receives a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extracts relevant fields from the JSON response and returns them
        return extractFeatureFromJson(jsonResponse);
    }

    /*
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /*
     * Makes a HTTP request to the given URL and returns a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the business news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns a list of {@link BusinessNews} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<BusinessNews> extractFeatureFromJson(String response) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(response)) {
            return null;
        }

        // Creates an empty ArrayList that we can start adding business articles to
        List<BusinessNews> businessNewsList = new ArrayList<>();

        // Creates a JSONObject from the JSON response string
        JSONObject baseJsonResponse = null;
        try {
            baseJsonResponse = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

       /*
        * Extracts the JSONArray associated with the key called "features",
        * which represents a list of features (or business news).
        */
        JSONObject jsonResponse = baseJsonResponse.optJSONObject(RESPONSE);
        JSONArray newsArray = jsonResponse.optJSONArray(RESULTS);

        // For each business article in the newsArray, create a {@link BusinessNews} object
        for (int i = 0; i < newsArray.length(); i++) {

            // Get a single business article at position i within the list of business articles
            JSONObject currentBusinessNews = newsArray.optJSONObject(i);

            String title;
            String sectionName;
            String url;
            String date;

            title = currentBusinessNews.optString(WEB_TITLE);
            sectionName = currentBusinessNews.optString(SECTION_NAME);
            url = currentBusinessNews.optString(WEB_URL);
            date = currentBusinessNews.optString(PUBLICATION_DATE);
            date = dateFormatter(date);

            JSONArray tagsArray = currentBusinessNews.optJSONArray(TAGS);
            String author = "by ";
            if (tagsArray.length() == 0) {
                author = null;
            } else {
                for (int j = 0; j < tagsArray.length(); j++) {
                    JSONObject contributor = tagsArray.optJSONObject(j);
                    author += contributor.optString(WEB_TITLE);
                    // shows maximum 4 contributors
                    if (j == 4 && j < tagsArray.length() - 1) {
                        author += ", (...)";
                        break;
                    }
                    if (j < tagsArray.length() - 1) {
                        author += ", ";
                    }

                }
            }

            // Add the new {@link BusinessNews} to the list of business articles.
            businessNewsList.add(new BusinessNews(title, sectionName, author, date, url));
        }

        // Returns the list of business articles
        return businessNewsList;
    }

    // Date formatter
    private static String dateFormatter(String initialDate) {
        DateTimeFormatter fmtIn = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);
        TemporalAccessor date = fmtIn.parse(initialDate);
        Instant time = Instant.from(date);
        DateTimeFormatter fmtOut = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneOffset.UTC);
        return fmtOut.format(time);
    }
}