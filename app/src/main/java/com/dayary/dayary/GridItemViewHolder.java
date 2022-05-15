package com.dayary.dayary;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GridItemViewHolder {
    public TextView textView;
    public ImageView imageView;

    public GridItemViewHolder(View view){
        textView = view.findViewById(R.id.layout_sub3_text);
        imageView = view.findViewById(R.id.layout_sub3_img);
    }
}
