package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.v2;

import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.v2.RouteRequestManagerFragment;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import cn.bingoogolapple.titlebar.BGATitleBar;


public class DriverActivity extends AppCompatActivity
    implements RouteRequestManagerFragment.OnRouteRequestManagerFragmentListener{

    BGATitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Your routes", RouteRequestManagerFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        Init();
        Event();
    }

    private void Init() {
        titleBar = (BGATitleBar) findViewById(R.id.titlebar);
    }

    private void Event() {
        titleBar.setDelegate(new BGATitleBar.Delegate() {
            @Override
            public void onClickLeftCtv() {

            }

            @Override
            public void onClickTitleCtv() {

            }

            @Override
            public void onClickRightCtv() {

            }

            @Override
            public void onClickRightSecondaryCtv() {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
