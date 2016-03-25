package com.imagefinder.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.imagefinder.app.R;
import com.imagefinder.app.model.Photo;
import com.imagefinder.app.util.ImageUtil;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private List<Photo> photos;
    private LayoutInflater layoutInflater;
    private Context context;

    public ImageAdapter(Context context, List<Photo> photos) {
        this.photos = photos;
        this.context = context;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_item, parent, false);
        }

        Photo photo = getPhoto(position);

        ImageView imageView = (ImageView) view.findViewById(R.id.messagePicture);

        ImageUtil.displayImage(context, photo, imageView);

        return view;
    }

    private Photo getPhoto(int position) {
        return (Photo) getItem(position);
    }
}
