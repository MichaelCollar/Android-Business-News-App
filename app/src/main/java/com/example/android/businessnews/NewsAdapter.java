package com.example.android.businessnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * An {@link NewsAdapter} knows how to create a list item layout for each business article
 * in the data source (a list of {@link BusinessNews} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsAdapter extends ArrayAdapter<BusinessNews> {

    // Use of ViewHolder pattern
    ViewHolder holder = new ViewHolder();

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context          of the app
     * @param businessNewsList is the list of business articles, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<BusinessNews> businessNewsList) {
        super(context, 0, businessNewsList);
    }

    /**
     * Returns a list item view that displays information about the business article at the given position
     * in the list of business articles.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.business_news_list_item, parent, false);

            holder.titleTextView = convertView.findViewById(R.id.title);
            holder.sectionNameTextView = convertView.findViewById(R.id.sectionName);
            holder.authorTextView = convertView.findViewById(R.id.author);
            holder.dateTextView = convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Find the business article at the given position in the list of business articles
        BusinessNews currentBusinessNews = getItem(position);

        // Display the title of the current business article in the given TextView
        holder.titleTextView.setText(currentBusinessNews.getTitle());

        // Display the section name of the current business article in the given TextView
        holder.sectionNameTextView.setText(currentBusinessNews.getSectionName());

        // Display the author of the current business article in the given TextView
        holder.authorTextView.setText(currentBusinessNews.getAuthor());

        // Display the date of the current business article in the given TextView
        holder.dateTextView.setText(currentBusinessNews.getDate());

        return convertView;
    }

   /*
    * Private static inner class used for ViewHolder pattern.
    * Its aim is to increase the speed at which ListView renders data.
    */
    private static class ViewHolder {
        TextView titleTextView;
        TextView sectionNameTextView;
        TextView authorTextView;
        TextView dateTextView;
    }
}