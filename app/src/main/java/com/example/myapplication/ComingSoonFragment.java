package com.example.myapplication;

import com.example.myapplication.movie.MovieListFragment;
import com.example.myapplication.movie.MovieRepository;

public class ComingSoonFragment extends MovieListFragment {
    @Override
    protected String getCategory() {
        return MovieRepository.CATEGORY_COMING_SOON;
    }
}
