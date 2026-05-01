package com.example.myapplication.movie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.SeatSelectionFragment;

public abstract class MovieListFragment extends Fragment implements MovieAdapter.MovieActionListener {
    private MovieAdapter movieAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.submitList(MovieRepository.getMovies(requireContext(), getCategory()));
    }

    protected abstract String getCategory();

    @Override
    public void onTrailerClicked(Movie movie) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getTrailerUrl()));
        try {
            startActivity(intent);
        } catch (Exception exception) {
            Toast.makeText(requireContext(), R.string.home_trailer_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBookSeatsClicked(Movie movie) {
        SeatSelectionFragment seatSelectionFragment = SeatSelectionFragment.newInstance(movie);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.main_fragment_container, seatSelectionFragment)
                .addToBackStack(null)
                .commit();
    }
}
