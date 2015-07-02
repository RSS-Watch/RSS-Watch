package com.swt.smartrss.app.helper;

import org.feedlyapi.model.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class is our own class holding the data of each article.
 * It is very similar to the Article class from the Feedly API but provides additional functionality.
 * We did not inherit the Article class to reduce overhead.
 *
 * @author Florian Lüdiger
 */
public class ArticleData {
    private String id;
    private String title;
    private String text;
    private Calendar published;
    private String pictureUrl;
    private String source;
    private String url;
    private boolean unread;

    /**
     * The default constructor just fills the fields with empty strings.
     */
    public ArticleData() {
        title = "";
        text = "";
        published = new GregorianCalendar();
        pictureUrl = "";
        source = "";
        unread = false;
    }

    /**
     * This constructor converts an Article object into an ArticleData object
     * @param a The Article object containing the relevant data
     */
    public ArticleData(Article a) {
        this.id = a.getId();
        this.unread = a.isUnread();
        this.title = a.getTitle();
        this.url = a.getOriginId();
        this.source = formatSource(a.getOriginId());
        // Some feeds have an empty content field but a summary.
        // We want to display at least the summary if no content is available.
        if (a.getContent() != null && !a.getContent().isEmpty())
            this.text = a.getContent();
        else
            this.text = a.getSummary();
        this.published = a.getPublished();
        this.pictureUrl = this.extractPictureUrl();
    }

    public boolean isUnread() {
        return unread;
    }

    public String getId() {
        return id;
    }

    /**
     * This method extracts the url of the first image in the text of the feed.
     * @return the image url of the first picture
     */
    private String extractPictureUrl() {
        if (text != null) {
            Document doc = Jsoup.parse(text);
            Elements imgUrl = doc.select("img");
            String test = imgUrl.attr("src");
            return test;
        } else
            return "";
    }

    /**
     * This method removes all the unnessecary text in the source url.
     * This ensures a clean looking source field in the ListView.
     * @param src The source url with all the junk
     * @return Only the domain of the source without all the junk
     */
    private String formatSource(String src) {
        //remove http://www. from the url
        if (src.startsWith("http://www."))
            src = src.substring(11);
        else if (src.startsWith("http://"))
            src = src.substring(7);
        else if (src.startsWith("www.")) {
            src = src.substring(4);
        }

        //remove everything after the /
        src = src.substring(0, src.indexOf("/"));

        return src;
    }

    //Getters and setters following.

    public Calendar getPublished() {
        return published;
    }

    public void setPublished(Calendar published) {
        this.published = published;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }
}
