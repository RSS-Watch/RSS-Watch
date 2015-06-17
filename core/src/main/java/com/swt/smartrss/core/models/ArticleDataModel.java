package com.swt.smartrss.core.models;

/**
 * Created by Dropsoft on 17.06.2015.
 */
public class ArticleDataModel implements java.io.Serializable {
    public String title;
    public String text;

    public ArticleDataModel(String title, String text) {
        this.title = title;
        this.text = text;
    }
}