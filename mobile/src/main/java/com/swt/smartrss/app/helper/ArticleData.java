package com.swt.smartrss.app.helper;

import org.feedlyapi.model.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Florian on 09.06.2015.
 */
public class ArticleData {
    private String title;
    private String text;
    private Calendar published;
    private String pictureUrl;
    private String source;

    public ArticleData() {
        title = "";
        text = "";
        published = new GregorianCalendar();
        pictureUrl = "";
        source = "";
    }

    public ArticleData(Article a) {
        this.title = a.getTitle();
        this.source = formatSource(a.getOriginId());
        if (a.getContent() != null && !a.getContent().isEmpty())
            this.text = a.getContent();
        else
            this.text = a.getSummary();
        this.published = a.getPublished();
        this.pictureUrl = this.extractPictureUrl();
    }

    private String extractPictureUrl() {
        Document doc = Jsoup.parse(text);
        Elements imgUrl = doc.select("img");
        String test =  imgUrl.attr("src");
        return test;
    }

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
        src = src.substring(0,src.indexOf("/"));

        return src;
    }

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
}
