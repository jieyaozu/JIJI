package com.yaozu.object;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.adapter.ContactsAdapter;
import com.yaozu.object.entity.ContactsInfo;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Button btShowContacts;
    private ListView listView;
    private List<ContactsInfo> list = new ArrayList<ContactsInfo>();
    private ContactsAdapter adapter;

    private Dialog dialog;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
        setSwipeBackEnable(false);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("主页");
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_compass);
    }

    @Override
    protected void initView() {
        btShowContacts = (Button) findViewById(R.id.activity_main_showcontacts);
        listView = (ListView) findViewById(R.id.activity_main_listview);
    }

    @Override
    protected void initData() {
        adapter = new ContactsAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        btShowContacts.setOnClickListener(this);
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
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_showcontacts:
                boolean status = Utils.selfPermissionGranted(MainActivity.this, android.Manifest.permission.READ_CONTACTS);
                boolean isShould = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_CONTACTS);
                getContacts();
                break;
        }
    }

    private void getContacts() {
        list.clear();
        try {
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor = this.getContentResolver().query(contactUri,
                    new String[]{"display_name", "sort_key", "contact_id", "data1"},
                    null, null, "sort_key");
            String contactName;
            String contactNumber;
            String contactSortKey;
            int contactId;
            while (cursor.moveToNext()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                contactSortKey = getSortkey(cursor.getString(1));
                ContactsInfo contactsInfo = new ContactsInfo(contactName, contactNumber, contactSortKey, contactId);
                if (contactName != null)
                    list.add(contactsInfo);
            }

            if (list.size() == 0) {
                showDeleteConfirmDialog();
            } else {
                adapter.clearData();
                adapter.setAddDataList(list);
            }

            cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    private static String getSortkey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        } else
            return "#";   //获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
    }

    private void showDeleteConfirmDialog() {
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
