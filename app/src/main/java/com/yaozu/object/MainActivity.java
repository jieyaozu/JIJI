package com.yaozu.object;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.fragment.FindFragment;
import com.yaozu.object.fragment.ForumFragment;
import com.yaozu.object.fragment.MineFragment;
import com.yaozu.object.utils.IntentUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Dialog dialog;
    private String currentContentFragmentTag = null;
    private RadioGroup mRadioGroup;
    private ActionBar mActionbar;
    private RadioButton rbForum, rbArticle, rbMine;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
        setSwipeBackEnable(false);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        mActionbar = actionBar;
        actionBar.setTitle("主页");
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_compass);
    }

    @Override
    protected void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.main_bottom_layout_group);
    }

    @Override
    protected void initData() {
        updateContent(0);
        ((RadioButton) findViewById(R.id.main_bottom_raido_forum)).setChecked(true);
        rbForum = (RadioButton) findViewById(R.id.main_bottom_raido_forum);
        rbArticle = (RadioButton) findViewById(R.id.main_bottom_raido_find);
        rbMine = (RadioButton) findViewById(R.id.main_bottom_raido_mine);

        rbForum.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void setListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_bottom_raido_forum:
                        mActionbar.setTitle("主页");
                        if (Build.VERSION.SDK_INT >= 21) {
                            mActionbar.setElevation(10);
                        }
                        updateContent(0);
                        rbForum.setTextColor(getResources().getColor(R.color.colorPrimary));
                        rbArticle.setTextColor(getResources().getColor(R.color.gray));
                        rbMine.setTextColor(getResources().getColor(R.color.gray));
                        break;
                    case R.id.main_bottom_raido_find:
                        if (!LoginInfo.getInstance(MainActivity.this).isLogining()) {
                            IntentUtil.toLoginActivity(MainActivity.this);
                            RadioButton radioButton = (RadioButton) mRadioGroup.findViewById(R.id.main_bottom_raido_forum);
                            radioButton.setChecked(true);
                        } else {
                            mActionbar.setTitle("发现");
                            if (Build.VERSION.SDK_INT >= 21) {
                                mActionbar.setElevation(10);
                            }
                            updateContent(1);
                            rbForum.setTextColor(getResources().getColor(R.color.gray));
                            rbArticle.setTextColor(getResources().getColor(R.color.colorPrimary));
                            rbMine.setTextColor(getResources().getColor(R.color.gray));
                        }
                        break;
                    case R.id.main_bottom_raido_mine:
                        if (!LoginInfo.getInstance(MainActivity.this).isLogining()) {
                            IntentUtil.toLoginActivity(MainActivity.this);
                            RadioButton radioButton = (RadioButton) mRadioGroup.findViewById(R.id.main_bottom_raido_forum);
                            radioButton.setChecked(true);
                        } else {
                            mActionbar.setTitle("我");
                            if (Build.VERSION.SDK_INT >= 21) {
                                mActionbar.setElevation(0);
                            }
                            updateContent(2);
                            rbForum.setTextColor(getResources().getColor(R.color.gray));
                            rbArticle.setTextColor(getResources().getColor(R.color.gray));
                            rbMine.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main_activity_actions, menu);

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//
//        // Configure the search info and add any event listeners...
//
//        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return false;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                return false;
//            }
//        };
//        MenuItem menuItem = menu.findItem(R.id.action_share);
//        MenuItemCompat.setOnActionExpandListener(menuItem, expandListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                return true;
            case R.id.action_login:
                IntentUtil.toLoginActivity(this);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void updateContent(int checkedId) {
        final Fragment fragment;
        final String tag;
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction tr = fm.beginTransaction();
        if (currentContentFragmentTag != null) {
            final Fragment currentFragment = fm.findFragmentByTag(currentContentFragmentTag);
            if (currentFragment != null)
                tr.hide(currentFragment);
        }
        if (checkedId == 0) {
            tag = ForumFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
            } else {
                fragment = new ForumFragment();
            }
        } else if (checkedId == 1) {
            tag = FindFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
            } else {
                fragment = new FindFragment();
            }
        } else if (checkedId == 2) {
            tag = MineFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
            } else {
                fragment = new MineFragment();
            }
        } else {
            return;
        }

        if (fragment.isAdded()) {
            tr.show(fragment);
        } else {
            tr.add(R.id.fragment_container, fragment, tag);
            // tr.replace(R.id.fragment_content, fragment);
        }
        tr.commit();
        currentContentFragmentTag = tag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private static String getSortkey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        } else
            return "#";   //获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
    }

    private void showObtainPermissionDialog() {
        dialog = new Dialog(this, R.style.NobackDialog);
        View view = View.inflate(this, R.layout.dialog_show_obtain_permission, null);
        TextView cancel = (TextView) view.findViewById(R.id.dialog_cancel);
        TextView obtain = (TextView) view.findViewById(R.id.dialog_obtain);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        obtain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = getAppDetailSettingIntent();
                startActivity(intent);
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        return localIntent;
    }
}
