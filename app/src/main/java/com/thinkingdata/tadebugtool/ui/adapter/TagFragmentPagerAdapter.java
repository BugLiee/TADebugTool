/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.thinkingdata.tadebugtool.ui.fragments.EventListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * < viewPager Adapter >.
 *
 * @author bugliee
 * @create 2022/7/5
 * @since 1.0.0
 */
public class TagFragmentPagerAdapter extends FragmentStateAdapter {

    private List<EventListFragment> fragments = new ArrayList<>();

    public TagFragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public TagFragmentPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public TagFragmentPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<EventListFragment> fragments) {
        super(fragmentManager, lifecycle);
        this.fragments.addAll(fragments);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
