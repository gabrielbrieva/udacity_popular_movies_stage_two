package com.tuxan.udacity.popularmovies;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tuxan.udacity.popularmovies.sync.PopularMoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback, AdapterView.OnItemSelectedListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static String DETAILFRAGMENT_TAG = "MDFTAG";

    private Spinner mSpinner;
    private ArrayAdapter<CharSequence> mFilterAdapter;

    private Toolbar mToolbar;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            // two-pane using sw600dp layout
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

        mFilterAdapter = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(), R.array.pref_sort_options, android.R.layout.simple_spinner_dropdown_item);
        mFilterAdapter.setDropDownViewResource(R.layout.layout_drop_list);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String filterKey = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_value_popularity));
        int spinnerPos = getPosition(filterKey);

        mSpinner = (Spinner) findViewById(R.id.sp_filter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setAdapter(mFilterAdapter);
        mSpinner.setSelection(spinnerPos);

        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onItemSelected(Uri detailUri, View shareView) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI_KEY, detailUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(detailUri);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // transition between two poster images

                View view = shareView.findViewById(R.id.iv_movie_poster);

                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        view,
                        view.getTransitionName()
                ).toBundle();

                startActivity(intent, bundle);

            } else {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String option = getResources().getStringArray(R.array.pref_sort_values)[position];

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(getString(R.string.pref_sort_key), option);
        editor.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private int getPosition(String filterKey) {
        String[] filters = getResources().getStringArray(R.array.pref_sort_values);

        for (int i = 0; i < filters.length; i++) {
            if (filters[i].equals(filterKey))
                return i;
        }

        return 0;
    }
}
