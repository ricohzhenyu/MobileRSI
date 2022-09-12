package com.ricoh.mobilersi;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationBarView;
import com.ricoh.mobilersi.databinding.ActivityMainBinding;
import com.ricoh.mobilersi.ui.help.HelpFragment;
import com.ricoh.mobilersi.ui.home.HomeFragment;
import com.ricoh.mobilersi.ui.rsi.RsiFragment;
import com.ricoh.mobilersi.ui.status.StatusFragment;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private HomeFragment homeFragment = new HomeFragment();
    private RsiFragment rsiFragment = new RsiFragment();
    private StatusFragment statusFragment = new StatusFragment();
    private HelpFragment helpFragment = new HelpFragment();
    private Fragment[] allFragments = null;

    BottomNavigationView navView;
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;
    boolean doubleBackToExitPressedOnce = false;

    View rootView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);

        navView = (BottomNavigationView) findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        navView.setItemIconSize(80);
        navView.setMinimumHeight(170);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_rsi, R.id.navigation_status, R.id.navigation_help)
                .build();

        NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, controller, appBarConfiguration);
        //NavigationUI.setupWithNavController(binding.navView, controller);
    }

    @Override
    protected void onStart() {
        setFragment(homeFragment, "Home", 0);
        super.onStart();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(homeFragment, "Home", 0);
                    getSupportActionBar().setTitle("Welcome");
                    return true;
                case R.id.navigation_rsi:
                    setFragment(rsiFragment, "RSI", 1);
                    getSupportActionBar().setTitle("RSI");
                    return true;
                case R.id.navigation_status:
                    setFragment(statusFragment, "Status", 2);
                    getSupportActionBar().setTitle("RSI Status");
                    return true;
                case R.id.navigation_help:
                    setFragment(helpFragment, "Help", 3);
                    getSupportActionBar().setTitle("RSI Help");
                    return true;
            }
            return false;
        }
    };


    public void setFragment(Fragment fragment, String tag, int position) {
        if(allFragments==null) {
            allFragments = new Fragment[]{homeFragment, rsiFragment, statusFragment, helpFragment};
        }

        //show destination Fragment, and hide others
        for(int i=0;i<allFragments.length;i++) {
            if(allFragments[i]==fragment) {
                if (fragment.isAdded()) {
                    fm.beginTransaction().hide(active).commit();
                    fm.beginTransaction().show(fragment).commit();
                } else {
                    fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, fragment, tag).commit();
                }
            }else {
                if (allFragments[i].isAdded()) {
                    fm.beginTransaction().hide(allFragments[i]).commit();
                }
            }
        }
        navView.getMenu().getItem(position).setChecked(true);
        active = fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.navigation_home:
                setFragment(homeFragment, "Home", 0);
                getSupportActionBar().setTitle("Welcome");
                return true;
            case R.id.navigation_rsi:
                setFragment(rsiFragment, "RSI", 1);
                getSupportActionBar().setTitle("RSI");
                return true;
            case R.id.navigation_status:
                setFragment(statusFragment, "Status", 2);
                getSupportActionBar().setTitle("RSI Status");
                return true;
            case R.id.navigation_help:
                setFragment(helpFragment, "Help", 3);
                getSupportActionBar().setTitle("RSI Help");
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (active == homeFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        } else {
            setFragment(homeFragment, "Home", 0);
        }
    }

}