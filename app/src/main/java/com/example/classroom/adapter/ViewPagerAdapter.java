package com.example.classroom.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.classroom.ui.AssigmentFragment;
import com.example.classroom.ui.FeedFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {

        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new FeedFragment();
        }else {
            return new AssigmentFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
