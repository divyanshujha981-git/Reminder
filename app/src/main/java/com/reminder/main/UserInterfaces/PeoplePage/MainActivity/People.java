package com.reminder.main.UserInterfaces.PeoplePage.MainActivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.navigation.NavigationBarView;
import com.reminder.main.R;

public class People extends AppCompatActivity implements
        NavigationBarView.OnItemSelectedListener {
    private NavigationBarView bottomNavigationView;
    private ViewPager2 viewPager2;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_page);

        declare();
        setActions();

    }


    private void declare() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager2 = findViewById(R.id.viewPager);
    }


    private void setActions() {

        getSupportActionBar().setTitle(getString(R.string.people));
        viewPager2.setAdapter(new PeoplePagerAdapter(this));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.setSelectedItemId(position == 0 ? R.id.menu_search : position == 1 ? R.id.menu_accepted : R.id.menu_pending);
            }
        });
        bottomNavigationView.setOnItemSelectedListener(People.this);

        uiCorrection();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.menu_search) {
            viewPager2.setCurrentItem(0);
        }
        else if (id == R.id.menu_accepted) {
            viewPager2.setCurrentItem(1);
        }
        else if (id == R.id.menu_pending) {
            viewPager2.setCurrentItem(2);
        }
        else {
            viewPager2.setCurrentItem(0);
        }
        return true;
    }


    private void uiCorrection() {

        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0,0,0,0);

        findViewById(R.id.bottomAppBar).setBackgroundColor(getColor(R.color.windowBackground));

        ((BottomAppBar) findViewById(R.id.bottomAppBar)).setElevation(5f);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomAppBar), (v, insets) -> {
            // Apply only bottom inset (system navigation bar)

            v.setPadding(
                    0,
                    0,
                    0,
                    0
            );

            return insets;

        });

    }



}
