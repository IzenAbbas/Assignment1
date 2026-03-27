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
public class NowShowingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_showing, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerNowShowing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Movie> nowShowingMovies = new ArrayList<>();
        nowShowingMovies.add(new Movie("The Dark Knight", "Action", 152, R.drawable.the_dark_knight));
        nowShowingMovies.add(new Movie("Inception", "Sci-Fi", 148, R.drawable.inception));
        nowShowingMovies.add(new Movie("Interstellar", "Sci-Fi", 169, R.drawable.intersteller));
        nowShowingMovies.add(new Movie("The Shawshank Redemption", "Drama", 142, R.drawable.shawshank_redemption));
        MovieAdapter adapter = new MovieAdapter(getContext(), nowShowingMovies);
        recyclerView.setAdapter(adapter);
    }
}
