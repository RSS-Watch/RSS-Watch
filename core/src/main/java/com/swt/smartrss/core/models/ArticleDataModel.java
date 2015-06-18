package com.swt.smartrss.core.models;

/**
 * Created by Dropsoft on 17.06.2015.
 */
public class ArticleDataModel implements java.io.Serializable {
    public String id;
    public String title;
    public String text;

    public ArticleDataModel(String id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }
}