package com.imagefinder.app.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.imagefinder.app.R;
import com.imagefinder.app.io.RestClient;
import com.imagefinder.app.model.FlickrPhotos;
import com.imagefinder.app.model.Photo;
import com.imagefinder.app.ui.adapter.ImageAdapter;
import com.imagefinder.app.util.SuggestionProvider;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private static final String API_KEY = "5f45c46eaf6e87b55c9f36fec03e3466";
    private static final String DEFAULT_SEARCH_QUERY = "cat";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        addBackButtton();

        listView = (ListView) findViewById(R.id.list);

        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction()) || intent.getStringExtra(SearchManager.QUERY) != null) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);

            suggestions.saveRecentQuery(query, null);

            getSupportActionBar().setSubtitle("results by \"" + query + "\"");

            searchPhotos(query);
        } else {
            searchPhotos(DEFAULT_SEARCH_QUERY);
        }
    }

    private void searchPhotos(String query) {
        Call<FlickrPhotos> imageList = RestClient.build().searchPhotos(API_KEY, query);

        imageList.enqueue(new Callback<FlickrPhotos>() {
            @Override
            public void onResponse(Response<FlickrPhotos> response, Retrofit retrofit) {
                List<Photo> photos = response.body().photos.photo;

                ImageAdapter imageAdapter = new ImageAdapter(SearchActivity.this, photos);
                listView.setAdapter(imageAdapter);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void addBackButtton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == R.id.menu_search) {
            Log.i(TAG, "selected");
        }

        return super.onOptionsItemSelected(item);
    }
}
