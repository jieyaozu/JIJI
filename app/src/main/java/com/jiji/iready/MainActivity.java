package com.jiji.iready;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.jiji.iready.activity.BaseActivity;
import com.jiji.iready.utils.IntentUtil;

public class MainActivity extends BaseActivity {

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
        setSwipeBackEnable(false);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("主页");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_compass);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...

        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                System.out.println("===========onMenuItemActionExpand============>" + item.getItemId());
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                System.out.println("===========onMenuItemActionCollapse============>" + item.getItemId());
                return false;
            }
        };
        MenuItem menuItem = menu.findItem(R.id.action_share);
        MenuItemCompat.setOnActionExpandListener(menuItem, expandListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                IntentUtil.toSettingActivity(this);
                return true;
            case android.R.id.home:

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
