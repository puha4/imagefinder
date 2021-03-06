package com.imagefinder.app.ui;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.imagefinder.app.R;
import com.imagefinder.app.io.RestClient;
import com.imagefinder.app.model.AuthUser;
import com.imagefinder.app.model.FlickrPhotos;
import com.imagefinder.app.model.Photo;
import com.imagefinder.app.ui.fragment.GoogleMapFragment;
import com.imagefinder.app.user.User;
import com.imagefinder.app.util.ImageUtil;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private final int REQ_CODE_SPEECH = 100;

    private GoogleMapFragment googleMapFragment;
    private FragmentManager manager;

    private double latitude;
    private double longitude;
    private String frob;

    private AuthUser user;

    private static final String API_KEY = "5f45c46eaf6e87b55c9f36fec03e3466";
    private static final String API_SECRET = "6e732ab8487b05e4";
    private static final String AUTH_ACTION = "auth";
    private static final String GET_TOKEN_ACTION = "gettoken";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        attachMapFragment(savedInstanceState);

        setContentView(R.layout.activity_home);

        ImageButton micButton = (ImageButton) findViewById(R.id.micBtn);

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        Uri uri = getIntent().getData();

        if (uri != null) {
            frob = uri.getQueryParameter("frob");
            Log.i(TAG, frob);
            sendRequestForAuthToken();
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.say_smth));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, R.string.doesnt_support, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.get(0) != null) {
                        onStartSearch(result.get(0));
                    }
                    for (String res : result) {
                        Log.i(TAG, res);
                    }
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setActionBarSubtitle();
    }

    private void setActionBarSubtitle() {
        if (User.getInstance().getFullname() != null) {
            getSupportActionBar().setSubtitle("Hello " + User.getInstance().getFullname() + "!");
        }
    }

    private void attachMapFragment(Bundle savedInstanceState) {
        manager = getSupportFragmentManager();
        googleMapFragment = new GoogleMapFragment();

        if (savedInstanceState == null) {
            manager.beginTransaction().add(R.id.mapContainer, googleMapFragment).commit();
        }
    }

    public void sendRequestForImageByGeo() {
        this.latitude = googleMapFragment.getLatitude();
        this.longitude = googleMapFragment.getLongitude();

        Call<FlickrPhotos> imageList = RestClient.build().getPhotosByGeo(API_KEY, latitude, longitude);

        imageList.enqueue(new Callback<FlickrPhotos>() {
            @Override
            public void onResponse(Response<FlickrPhotos> response, Retrofit retrofit) {
                List<Photo> photos = response.body().photos.photo;
                Collections.shuffle(photos);

                ImageUtil.loadTwoImages(findViewById(R.id.first_row), HomeActivity.this, response.body().photos.photo.subList(0, 2));
                ImageUtil.loadThreeImages(findViewById(R.id.second_row), HomeActivity.this, response.body().photos.photo.subList(2, 5));
                ImageUtil.loadImages(findViewById(R.id.third_row), HomeActivity.this, response.body().photos.photo.subList(5, 9));
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(HomeActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendRequestForAuthToken() {
        if (frob != null) {
            Call<AuthUser> getToken = RestClient.build().getToken(API_KEY, frob, getApiSig(GET_TOKEN_ACTION));

            getToken.enqueue(new Callback<AuthUser>() {
                @Override
                public void onResponse(Response<AuthUser> response, Retrofit retrofit) {
                    user = response.body();

                    User.getInstance().setUsername(user.auth.user.username);
                    User.getInstance().setFullname(user.auth.user.fullname);
                    User.getInstance().setToken(user.auth.token._content);
                    User.getInstance().setNsid(user.auth.user.nsid);

                    setActionBarSubtitle();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(HomeActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void onStartSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void onStartSearch(String seachQuery) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchManager.QUERY, seachQuery);
        startActivity(intent);
    }

    public void onAuth(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flickr.com/services/auth/?api_key=" + API_KEY + "&perms=write&api_sig=" + getApiSig(AUTH_ACTION))));
    }

    private String getApiSig(String requestType) {
        String apiSig = "";

        if (requestType.equals(GET_TOKEN_ACTION)) {
            apiSig = new String(Hex.encodeHex(DigestUtils.md5(API_SECRET + "api_key" + API_KEY + "formatjsonfrob" + frob + "methodflickr.auth.getTokennojsoncallback1")));
        } else if (requestType.equals(AUTH_ACTION)) {
            apiSig = new String(Hex.encodeHex(DigestUtils.md5(API_SECRET + "api_key" + API_KEY + "permswrite")));
        }

        return apiSig;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
