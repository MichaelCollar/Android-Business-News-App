package com.example.android.businessnews;

/**
 * A {@link BusinessNews} object contains information related to a single article.
 */
public class BusinessNews {

    /**
     * Title of the article
     */
    private String title;

    /**
     * Section name of the article
     */
    private String sectionName;

    /**
     * Author of the article
     */
    private String author;

    /**
     * Date of the article
     */
    private String date;

    /**
     * Website URL of the article
     */
    private String url;

    /**
     * Constructs a new {@link BusinessNews} object.
     *
     * @param title       is the title of the article
     * @param sectionName is the section name of the article
     * @param author      is the name and surname of the author
     * @param date        is the date of the article
     * @param url         is the website URL to read more
     */
    public BusinessNews(String title, String sectionName, String author, String date, String url) {
        this.title = title;
        this.sectionName = sectionName;
        this.author = author;
        this.date = date;
        this.url = url;
    }

    /**
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the section name.
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * @return the author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return the date.
     */
    public String getDate() {
        return date;
    }

    /**
     * @return the website URL.
     */
    public String getUrl() {
        return url;
    }
}