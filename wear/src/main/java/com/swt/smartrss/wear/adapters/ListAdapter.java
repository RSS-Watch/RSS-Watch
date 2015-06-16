package com.swt.smartrss.wear.adapters;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.swt.smartrss.wear.R;

/**
 * Created by Florian on 19.05.2015.
 */
public class ListAdapter extends WearableListView.Adapter {
    private String[] mDataset;
    private Context mContext;
    private LayoutInflater mInflater;

    public ListAdapter(Context context, String[] dataset) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataset = dataset;
    }

    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ItemViewHolder(mInflater.inflate(R.layout.listview_entry, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView view = itemHolder.textView;

        view.setText(mDataset[position]);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public class ItemViewHolder extends WearableListView.ViewHolder {
        TextView textView;
        //ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            //imageView = (ImageView) imageView.findViewById(R.id.imageView);
        }
    }
}
