package com.example.assignment1;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
public class ComingSoonFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coming_soon, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerComingSoon);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Movie> comingSoonMovies = new ArrayList<>();
        comingSoonMovies.add(new Movie("The Batman", "Action", 176, R.drawable.the_batman));
        comingSoonMovies.add(new Movie("Dune: Part Two", "Sci-Fi", 166, R.drawable.dune_part2));
        comingSoonMovies.add(new Movie("Oppenheimer", "Drama", 180, R.drawable.open_heimer));
        MovieAdapter adapter = new MovieAdapter(getContext(), comingSoonMovies, true);
        recyclerView.setAdapter(adapter);
    }
}
