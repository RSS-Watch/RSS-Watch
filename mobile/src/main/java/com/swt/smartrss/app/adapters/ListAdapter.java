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
import com.swt.smartrss.app.EndlessArticlesTask;
import com.swt.smartrss.app.GlobalApplication;
import com.swt.smartrss.app.R;
import com.swt.smartrss.app.helper.ArticleData;
import com.swt.smartrss.app.helper.DpPixelConverter;
import com.swt.smartrss.app.helper.FeedlyCache;

import java.util.ArrayList;

/**
 * This class provides an ArrayAdapter filled with ArticleData objects for the MainActivity.
 *
 * @author Florian Lüdiger
 */
public class ListAdapter extends ArrayAdapter<ArticleData> {
    private Context context;

    public ListAdapter(Context context, int layout, ArrayList<ArticleData> values) {
        super(context, layout, values);
        this.context = context;
    }

    /**
     * The data from ArticleData is mapped to the positions in the layout file.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the data of the current item
        ArticleData a = getItem(position);


        if (convertView == null) {
            // Check if an existing view is being reused, otherwise inflate the view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_entry, parent, false);
        }

        //reference the items of the layout file.
        TextView tvTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView tvSource = (TextView) convertView.findViewById(R.id.textViewSource);
        ImageView ivThumb = (ImageView) convertView.findViewById(R.id.imageView);

        //displays the title of the article
        tvTitle.setText(a.getTitle());

        //changes the color of the title based on the article being read or unread
        tvTitle.setTextColor(context.getResources().getColor(a.isUnread() ? R.color.titleColor : R.color.titleColorRead));
        if (a.isUnread())
            if (a.getSource() != null && a.getSource() != "")
                tvSource.setText("- " + a.getSource());

        //displays the picture
        if (a.getPictureUrl() != "" && a.getPictureUrl() != null) {
            Picasso.with(context).load(a.getPictureUrl())
                    .error(android.R.drawable.ic_delete).resize((int) DpPixelConverter.convertDpToPixel(78, context), (int) DpPixelConverter.convertDpToPixel(60, context)).centerInside().into(ivThumb);
        }

        //displays a small annotation about the time that has passed since the article was published
        //e.g. "5 minutes ago"
        RelativeTimeTextView v = (RelativeTimeTextView) convertView.findViewById(R.id.timestamp);
        v.setReferenceTime(a.getPublished().getTime().getTime());

        FeedlyCache feedlyCache = ((GlobalApplication)context.getApplicationContext()).getStateManager().getFeedlyCache();

        //when the list is scrolled to the end new articles are loaded
        if(position == getCount() - 1) {
            new EndlessArticlesTask(feedlyCache).execute();
        }

        return convertView;
    }
}
