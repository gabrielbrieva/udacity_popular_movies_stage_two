package com.tuxan.udacity.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MoviesAdapter extends CursorAdapter {

    // default image size, all image result must have the same size
    private static int DEFAULT_IMG_WIDTH = 185;
    private static int DEFAULT_IMG_HEIGHT = 278;

    public static class PosterViewHolder {
        public final ImageView poster;
        public final TextView title;

        public PosterViewHolder(View view) {
            poster = (ImageView) view.findViewById(R.id.iv_movie_poster);
            title = (TextView) view.findViewById(R.id.tv_movie_title);
        }
    }

    public MoviesAdapter(Context context, Cursor c, int flag) {
        super(context, c, flag);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.movie_poster, parent, false);

        PosterViewHolder posterViewHolder = new PosterViewHolder(view);
        view.setTag(posterViewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final PosterViewHolder posterViewHolder = (PosterViewHolder) view.getTag();

        String title = cursor.getString(MoviesFragment.COL_MOVIE_TITLE);
        posterViewHolder.title.setText(title);

        // TODO: Add image resource

        String posterPath = cursor.getString(MoviesFragment.COL_MOVIE_POSTER);

        if (posterPath != null) {

            // create an instance of Picasso using the context
            Picasso p = Picasso.with(mContext);

            // debugging purpose
            p.setLoggingEnabled(true);

            // load the poster image
            p.load(Utils.IMG_END_POINT + "w185" + posterPath)
                    // if the image don't exist we use a default drawable
                    .error(R.drawable.poster_missing)
                    .resize(DEFAULT_IMG_WIDTH, DEFAULT_IMG_HEIGHT)
                    // put the result image in poster ImageView
                    .into(posterViewHolder.poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            posterViewHolder.title.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            posterViewHolder.title.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }


    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.movie_poster, parent, false);
        }

        // we set the title to TextView
        final TextView title = (TextView) convertView.findViewById(R.id.tv_movie_title);
        title.setVisibility(View.GONE);
        title.setText(movie.getOriginal_title());

        ImageView poster = (ImageView) convertView.findViewById(R.id.iv_movie_poster);

        if (movie.getPoster_path() != null) {

            // create an instance of Picasso using the context
            Picasso p = Picasso.with(getContext());

            // debugging purpose
            //p.setLoggingEnabled(true);

            // load the poster image
            p.load(Utils.IMG_END_POINT + "w185" + movie.getPoster_path())
                    // if the image don't exist we use a default drawable
                    .error(R.drawable.poster_missing)
                    .resize(DEFAULT_IMG_WIDTH, DEFAULT_IMG_HEIGHT)

                    // put the result image in poster ImageView
                    .into(poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            title.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            title.setVisibility(View.VISIBLE);
                        }
                    });

        } else {
            // Default without poster image
            poster.setImageResource(R.drawable.poster_missing);
            title.setVisibility(View.VISIBLE);
        }

        return convertView;
    }*/
}
