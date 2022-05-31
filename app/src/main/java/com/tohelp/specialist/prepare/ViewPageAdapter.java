package com.tohelp.specialist.prepare;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.tohelp.specialist.FragmentProfile;
import com.tohelp.specialist.FragmentQuestionary;

public class ViewPageAdapter extends FragmentStateAdapter
{
    public ViewPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 1: return new FragmentQuestionary();
            default: return new FragmentProfile();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
