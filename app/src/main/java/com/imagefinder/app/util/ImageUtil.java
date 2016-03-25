package com.imagefinder.app.util;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.imagefinder.app.R;
import com.imagefinder.app.model.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageUtil {

    public static void loadImages(View rootView, Context context, List<Photo> photos) {
        loadOneImage(rootView, R.id.thumb1, context, photos.get(0));
        loadOneImage(rootView, R.id.thumb2, context, photos.get(1));
        loadOneImage(rootView, R.id.thumb3, context, photos.get(2));
        loadOneImage(rootView, R.id.thumb4, context, photos.get(3));
    }

    public static void loadTwoImages(View rootView, Context context, List<Photo> photos) {
        loadOneImage(rootView, R.id.thumb1, context, photos.get(0));
        loadOneImage(rootView, R.id.thumb2, context, photos.get(1));
    }

    public static void loadThreeImages(View rootView, Context context, List<Photo> photos) {
        loadOneImage(rootView, R.id.thumb1, context, photos.get(0));
        loadOneImage(rootView, R.id.thumb2, context, photos.get(1));
        loadOneImage(rootView, R.id.thumb3, context, photos.get(2));
    }

    private static void loadOneImage(View rootView, int resource,  Context context, Photo photo) {
        ImageView column = (ImageView) rootView.findViewById(resource);

        Picasso.with(context)
                .load(getFormattedImageString(context, photo))
                .fit()
                .centerCrop()
                .into(column);
    }

    private static String getFormattedImageString(Context context, Photo photo) {
        return context
                .getString(R.string.image_url, photo.farm, photo.server, photo.id, photo.secret);
    }

    public static void displayImage(Context context, Photo photo, ImageView view) {
        Picasso.with(context)
                .load(getFormattedImageString(context, photo))
                .fit()
                .centerCrop()
                .into(view);
    }

}
