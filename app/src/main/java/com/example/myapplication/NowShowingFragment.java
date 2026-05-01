package com.example.myapplication;

import com.example.myapplication.movie.MovieListFragment;
import com.example.myapplication.movie.MovieRepository;

public class NowShowingFragment extends MovieListFragment {
    @Override
    protected String getCategory() {
        return MovieRepository.CATEGORY_NOW_SHOWING;
    }
}
