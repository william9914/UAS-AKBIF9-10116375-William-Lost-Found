package com.williamlostfoundappnocopyright;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {


    View view;

    public ViewHolder(View itemView) {
        super(itemView);
        view=itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return false;
            }
        });

    }

    public void setDetails(Context ctx, String title, String description, String image){
        TextView mTitleView = view.findViewById(R.id.rTitleTv);
        TextView mDetailTv = view.findViewById(R.id.rDescriptionTv);
        ImageView mImageTv = view.findViewById(R.id.rImageViewTv);
        mTitleView.setText(title);
        mDetailTv.setText(description);
        Picasso.get().load(image).into(mImageTv);
    }

    private ViewHolder.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setonClickListener(ViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
