package com.jakebergamin.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    Movie mMovie;

    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        Intent intent = getActivity().getIntent();
        mMovie = intent.getParcelableExtra("movie");

        // load poster
        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        String picUrl = getContext().getString(R.string.tmdb_image_base_url) +
                getContext().getString(R.string.tmdb_image_size) + mMovie.posterPath;
        Picasso.with(getContext()).load(picUrl).placeholder(R.drawable.poster_placeholder).into(imageView);

        // load title
        TextView titleTextView = (TextView) rootView.findViewById(R.id.textViewTitle);
        titleTextView.setText(mMovie.originalTitle);

        // load overview
        TextView overviewTextView = (TextView) rootView.findViewById(R.id.textViewOverview);
        overviewTextView.setText(mMovie.overview);

        // load release date
        TextView dateTextView = (TextView) rootView.findViewById(R.id.textViewDate);
        dateTextView.setText("" + mMovie.releaseDate.get(GregorianCalendar.YEAR));

        // load rating
        TextView ratingTextView = (TextView) rootView.findViewById(R.id.textViewRating);
        ratingTextView.setText(mMovie.voteAverage + "/10");

        return rootView;
    }
}
