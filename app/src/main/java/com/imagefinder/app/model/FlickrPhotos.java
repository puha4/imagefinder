package com.imagefinder.app.model;

import java.util.List;

public class FlickrPhotos {

    public Photos photos;

    public static class Photos {

        public int page;
        public int pages;
        public int perpage;
        public int total;
        public List<Photo> photo;
    }
}
