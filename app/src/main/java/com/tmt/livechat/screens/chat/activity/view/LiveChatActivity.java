package com.tmt.livechat.screens.chat.activity.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.tmt.livechat.R;
import com.tmt.livechat.client.Livechat;
import com.tmt.livechat.screens.abstraction.mvp.MvpActivity;
import com.tmt.livechat.screens.chat.activity.LiveChatActivityInterface;
import com.tmt.livechat.screens.chat.activity.presenter.LiveChatActivityPresenter;

public class LiveChatActivity extends MvpActivity implements LiveChatActivityInterface.View {

    private Toolbar toolbar;
    private TextView toolbarSubtitle;
    public static String PACKAGE_NAME;

    private LiveChatActivityInterface.Presenter presenter = new LiveChatActivityPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);
        ((LiveChatActivityPresenter)presenter).setView(this);
        presenter.init();
    }

    @Override
    public void setupViews() {
        toolbar = findViewById(R.id.my_toolbar);
        toolbarSubtitle = findViewById(R.id.toolbar_subtitle);
        PACKAGE_NAME = getIntent().getStringExtra("PACKAGE_NAME");
    }

    @Override
    public void setupToolbar(String title, String subtitle) {
        setupToolbarWithHome(toolbar, title);
        toolbarSubtitle.setText(subtitle);
    }

    @Override
    public void onFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_group_channel, fragment).commit();
    }

    @Override
    public void showInProgressMessageAlert(AlertDialog.Builder dialog) {
        dialog.create().show();
    }

    @Override
    public void addProgress(String name, boolean val) {
        presenter.addProgress(name, val);
    }

    @Override
    public void removeProgress(String name) {
        presenter.removeProgress(name);
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void close() {
        setResult(Livechat.OPEN_CHAT_VIEW_RESPONSE_CODE);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        setResult(Livechat.OPEN_CHAT_VIEW_RESPONSE_CODE);
        super.onDestroy();
    }

}