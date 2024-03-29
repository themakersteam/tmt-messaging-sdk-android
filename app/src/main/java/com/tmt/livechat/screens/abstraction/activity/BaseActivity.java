package com.tmt.livechat.screens.abstraction.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class BaseActivity extends AppCompatActivity {


    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_GALLERY_CAPTURE = 2;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static boolean IS_PRESENTED = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_PRESENTED = true;
    }

    protected void setupToolbarWithHome(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (title != null)
                getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        IS_PRESENTED = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IS_PRESENTED = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IS_PRESENTED = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
