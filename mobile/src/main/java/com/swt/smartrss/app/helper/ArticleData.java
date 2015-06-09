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

    public ArticleData() {
        title = "";
        text = "";
        published = new GregorianCalendar();
        pictureUrl = "";
    }

    public ArticleData(Article a) {
        this.title = a.getTitle();
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
}
