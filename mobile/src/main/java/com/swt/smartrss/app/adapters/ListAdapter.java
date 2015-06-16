package com.swt.smartrss.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.Picasso;
import com.swt.smartrss.app.R;
import com.swt.smartrss.app.helper.ArticleData;
import com.swt.smartrss.app.helper.DpPixelConverter;

import java.util.ArrayList;

/**
 * Created by Florian on 05.06.2015.
 */
public class ListAdapter extends ArrayAdapter<ArticleData> {
    private Context context;
    private ArrayList<ArticleData> values;

    public ListAdapter(Context context, int layout, ArrayList<ArticleData> values) {
        super(context, layout, values);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the data of the current item
        ArticleData a = getItem(position);


        if (convertView == null) {
            // Check if an existing view is being reused, otherwise inflate the view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_entry, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView tvSource = (TextView) convertView.findViewById(R.id.textViewSource);
        ImageView ivThumb = (ImageView) convertView.findViewById(R.id.imageView);

        tvTitle.setText(a.getTitle());
        if (a.getSource() != null && a.getSource() != "")
            tvSource.setText("- " + a.getSource());
        if (a.getPictureUrl() != "" && a.getPictureUrl() != null) {
            Picasso.with(context).load(a.getPictureUrl())
                    .error(android.R.drawable.ic_delete).resize((int) DpPixelConverter.convertDpToPixel(78, context), (int) DpPixelConverter.convertDpToPixel(60, context)).centerInside().into(ivThumb);
        }

        RelativeTimeTextView v = (RelativeTimeTextView) convertView.findViewById(R.id.timestamp);
        v.setReferenceTime(a.getPublished().getTime().getTime());

        return convertView;
    }
}
