package com.abhay.universityapplication.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.ui.active_users.ActiveUsersFragment;
import com.abhay.universityapplication.ui.message.MessageFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private int[] tabIcons = {
            R.drawable.ic_baseline_comment_24,
            R.drawable.ic_baseline_person_24,
    };
    TabLayout tabLayout;
    ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        viewPager= view.findViewById(R.id.viewPager);
        tabLayout= view.findViewById(R.id.tabLayout);
        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(getChildFragmentManager());
        viewpagerAdapter.addFragments(new MessageFragment(),"Chat");
        viewpagerAdapter.addFragments(new ActiveUsersFragment(),"Friend list");
        viewPager.setAdapter(viewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();




        return view;
    }
    class ViewpagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private  ArrayList<String> titles;
        ViewpagerAdapter(FragmentManager fm )
        {
            super( fm);
            this.fragments=new ArrayList<>();
            this.titles= new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragments(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
}