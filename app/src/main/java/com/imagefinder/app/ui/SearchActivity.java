package com.imagefinder.app.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import com.imagefinder.app.R;
import com.imagefinder.app.db.RecordsDbHelper;
import com.imagefinder.app.util.SuggestionProvider;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private RecordsDbHelper mDbHelper;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView) findViewById(R.id.list);

        //Создаем экземпляр БД
        mDbHelper = new RecordsDbHelper(this);
        //Открываем БД для записи
        mDbHelper.open();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            saveTask(query);

            //Создаем экземпляр SearchRecentSuggestions
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            //Сохраняем запрос
            suggestions.saveRecentQuery(query, null);

            Log.i(TAG, query);

            showResults(query);
        }
    }

    private void showResults(String query) {
        //Ищем совпадения
        Cursor cursor = mDbHelper.fetchRecordsByQuery(query);
        startManagingCursor(cursor);
        String[] from = new String[] { RecordsDbHelper.KEY_DATA };
        int[] to = new int[] { R.id.textItem };

        SimpleCursorAdapter records = new SimpleCursorAdapter(this,
                R.layout.list_item, cursor, from, to);
        //Обновляем адаптер
        listView.setAdapter(records);
    }

    private void saveTask(String data) {
        mDbHelper.createRecord(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
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
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.menu_search) {
//            onSearchRequested()
            Log.i(TAG, "selected");
        }

        return super.onOptionsItemSelected(item);
    }
}
