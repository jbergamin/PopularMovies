package com.jakebergamin.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jake on 11/11/2015.
 */
public class MoviesArrayAdapter extends ArrayAdapter<Movie> {
    private final String LOG_TAG = MoviesArrayAdapter.class.getSimpleName();

    public MoviesArrayAdapter(Context context, List<Movie> movies){
        super(context, 0, movies);
    }

    /**
     * overide method in super class to return custom view to display movie object
     * @param position position of movie object to be displayed with view
     * @param convertView the recycled view to populate
     * @param parent parent viewgroup
     * @return view for that position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Movie movie = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_poster_item, parent, false);
        }

        String picUrl = getContext().getString(R.string.tmdb_image_base_url) +
                getContext().getString(R.string.tmdb_image_size) + movie.posterPath;

        Picasso.with(getContext()).load(picUrl)
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.poster_placeholder)
                .into((ImageView)convertView);

        return convertView;
    }
}
