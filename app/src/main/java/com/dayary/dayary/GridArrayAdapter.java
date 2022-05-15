package com.dayary.dayary;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.LogRecord;

public class GridArrayAdapter extends ArrayAdapter<GridItem> {
    private static final int LAYOUT_ID = R.layout.sub3;

    private Bitmap bitmap;
    private Bitmap smallBitmap;
    private Context mContext;
    private List<GridItem> mItemList;

    public GridArrayAdapter(@NonNull Context context, @NonNull List<GridItem> itemList) {
        super(context, LAYOUT_ID, itemList);

        mContext = context;
        mItemList = itemList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        GridItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(LAYOUT_ID, parent, false);
            viewHolder = new GridItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (GridItemViewHolder) convertView.getTag();

        final GridItem item = mItemList.get(position);
        viewHolder.textView.setText(item.getYear() + "/" + item.getMonth() + "/" + item.getDay());
        drawImage(item.getURL(), viewHolder.imageView);
        Handler handler = new Handler();

        return convertView;
    }

    public void drawImage(String imgURL, ImageView view) {
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imgURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    smallBitmap = Bitmap.createScaledBitmap(bitmap, 160, 160, false);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try {
            mThread.join();
            view.setImageBitmap(smallBitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
