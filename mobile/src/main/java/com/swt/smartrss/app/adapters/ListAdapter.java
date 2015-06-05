package com.swt.smartrss.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.swt.smartrss.app.R;
import org.feedlyapi.model.Article;

import java.util.ArrayList;

/**
 * Created by Florian on 05.06.2015.
 */
public class ListAdapter extends ArrayAdapter<Article> {
    private Context context;
    private ArrayList<Article> values;

    public ListAdapter(Context context, int layout, ArrayList<Article> values) {
        super(context, layout, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the data of the current item
        Article a = getItem(position);


        if (convertView == null) {
            // Check if an existing view is being reused, otherwise inflate the view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_entry, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.textView);

        tv.setText(a.getTitle());

        return convertView;
    }
}
