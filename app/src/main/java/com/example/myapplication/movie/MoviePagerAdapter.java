package com.example.myapplication.movie;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.ComingSoonFragment;
import com.example.myapplication.NowShowingFragment;

public class MoviePagerAdapter extends FragmentStateAdapter {
    public MoviePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new NowShowingFragment();
        }
        return new ComingSoonFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public String getPageTitle(int position) {
        return position == 0 ? "Now Showing" : "Coming Soon";
    }
}
