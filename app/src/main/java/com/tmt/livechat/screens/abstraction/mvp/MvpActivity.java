package com.tmt.livechat.screens.abstraction.mvp;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.tmt.livechat.screens.abstraction.activity.BaseActivity;

/**
 * Created by mohammednabil on 2019-06-26.
 */
public abstract class MvpActivity extends BaseActivity implements MvpInterface.View {

    @Override
    public Activity activity() {
        return this;
    }

    @Override
    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startActivity(Intent intent, Integer requestCode) {
        if (requestCode != null)
            startActivityForResult(intent, requestCode);
        else
            startActivity(intent);
    }

    @Override
    public void finishActivityWithResult(Intent intent, int result) {
        setResult(result, intent);
        finish();
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
