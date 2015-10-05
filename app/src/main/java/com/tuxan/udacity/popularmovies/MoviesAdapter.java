package com.tuxan.udacity.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tuxan.udacity.popularmovies.picasso.PicassoBigCache;

public class MoviesAdapter extends CursorAdapter {

    // default image size, all image result must have the same size
    private static String DEFAULT_IMAGE_SCALE = "w342";

    Picasso p;

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

        p = PicassoBigCache.INSTANCE.getPicassoBigCache(mContext);
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

        String posterPath = cursor.getString(MoviesFragment.COL_MOVIE_POSTER);

        if (posterPath != null) {

            // load the poster image
            p.load(Utils.IMG_END_POINT + DEFAULT_IMAGE_SCALE + posterPath)
                    .error(R.drawable.poster_missing) // if the image don't exist we use a default drawable
                    .placeholder(R.drawable.poster_missing)
                    .into(posterViewHolder.poster); // put the result image in poster ImageView
        }
    }
}
