package task.test.kataryna.dmytro.match.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.services.UpdateService;

import task.test.kataryna.dmytro.match.R;
import task.test.kataryna.dmytro.match.adapters.PagerAdapter;
import task.test.kataryna.dmytro.match.db.DataBaseHelper;
import task.test.kataryna.dmytro.match.helpers.Utils;
import task.test.kataryna.dmytro.match.otto.BusProvider;
import task.test.kataryna.dmytro.match.otto.PersonChangedEvent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button subscribeButton;
    private boolean isSubscribed;

    public static void open(Context context) {
        context.startActivity(new Intent(context, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initViewPagerAndTabs();
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unSubscribe();
        BusProvider.getInstance().unregister(this);
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(PersonsFragment.createInstance(), "People");
        pagerAdapter.addFragment(MapFragment.createInstance(), "Places");
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(onTabSelectedListener(viewPager));
    }

    /**
     * Find and initialize toolbar button view
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.subscribe) {
                View view = MenuItemCompat.getActionView(item).findViewById(R.id.subscribeButtonToolbar);
                subscribeButton = (Button) view;
                subscribeButton.setOnClickListener(this);
                changeSubscribeButtonView(isSubscribed);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void changeSubscribeButtonView(boolean isSubscribed) {
        if (subscribeButton == null) return;
        subscribeButton.setBackgroundResource(isSubscribed ? R.drawable.button_white_background : R.drawable.button_background_with_white_frame);
        subscribeButton.setText(getResources().getString(isSubscribed ? R.string.unsubscribe : R.string.subscribe));
        subscribeButton.setTextColor(getResources().getColor(isSubscribed ? R.color.black : R.color.white));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.subscribeButtonToolbar) {
            if (!isSubscribed)
                subscribeForUpdates();
            else
                unSubscribe();
        }
    }

    private void subscribeForUpdates() {
        changeSubscribeButtonView(isSubscribed = true);
        API.INSTANCE.subscribeUpdates(new UpdateService.UpdateServiceListener() {
            @Override
            public void onChanges(final String personJSON) {
                BusProvider.getInstance().post(new PersonChangedEvent(Utils.getPersonFromJSON(personJSON)));
            }
        });
    }

    private void unSubscribe() {
        API.INSTANCE.unSubscribeUpdates();
        changeSubscribeButtonView(isSubscribed = false);
    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager pager) {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };
    }
}
