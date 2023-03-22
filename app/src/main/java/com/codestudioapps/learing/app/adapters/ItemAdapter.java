package com.codestudioapps.learing.app.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codestudioapps.learing.app.R;
import com.codestudioapps.learing.app.listeners.ListItemClickListener;
import com.codestudioapps.learing.app.models.content.Item;

import java.util.ArrayList;
import java.util.Random;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;

    private ArrayList<Item> mItemList;
    private ListItemClickListener mItemClickListener;

    public ItemAdapter(Context mContext, Activity mActivity, ArrayList<Item> mItemList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mItemList = mItemList;
    }

    public void setItemClickListener(ListItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }


    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_recycler, parent, false);
        return new ItemAdapter.ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView img_doc;
        private TextView tvTitleText;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            img_doc = (ImageView) itemView.findViewById(R.id.img_doc);
            tvTitleText = (TextView) itemView.findViewById(R.id.title_text);

            img_doc.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != mItemList ? mItemList.size() : 0);

    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder mainHolder, int position) {
        final Item model = mItemList.get(position);

        // setting data over views
        String title = model.getTagLine();
        mainHolder.tvTitleText.setText(Html.fromHtml(title));


        Random rand = new Random();
        int i = rand.nextInt(6) + 1;


        switch (i) {
            case 1:
                mainHolder.img_doc.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_blue));
                break;
            case 2:
                mainHolder.img_doc.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_red));
                break;

            case 3:
                mainHolder.img_doc.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_orange));
                break;

            case 4:
                mainHolder.img_doc.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_purple));
                break;
            case 5:
                mainHolder.img_doc.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_green));
                break;
            case 6:
                mainHolder.img_doc.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_deep_blue));
                break;
            default:
                break;
        }

    }
}
