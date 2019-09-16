package com.tmt.livechat.screens.chat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.tmt.livechat.R;
import com.tmt.livechat.client.Livechat;
import com.tmt.livechat.model.Theme;

public class LiveChatActivity extends AppCompatActivity {

    private onBackPressedListener mOnBackPressedListener;
    private Toolbar toolbar;
    private TextView toolbarSubtitle;
    private Theme theme;
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);
        init();
    }

    private void init() {
        theme = getIntent().hasExtra("THEME") ? (Theme) getIntent().getExtras().getParcelable("THEME") : null;
        initToolBar();
        Fragment fragment = LiveChatFragment.newInstance();
        Bundle bundle = new Bundle();
        if (getIntent() != null && getIntent().getExtras() != null) {
            bundle.putAll(getIntent().getExtras());
        }
        fragment.setArguments(bundle);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_group_channel, fragment).commit();
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.my_toolbar);
        toolbarSubtitle = findViewById(R.id.toolbar_subtitle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(theme != null && theme.getToolbarTitle() != null ? theme.getToolbarTitle() : getString(R.string.message_center_toolbar_title));
        }
        toolbarSubtitle.setText(theme != null && theme.getToolbarSubtitle() != null ? theme.getToolbarSubtitle() : "");
        PACKAGE_NAME = getIntent().getStringExtra("PACKAGE_NAME");
    }

    public void setOnBackPressedListener(onBackPressedListener listener) {
        mOnBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        setResult(Livechat.OPEN_CHAT_VIEW_RESPONSE_CODE);
        if (mOnBackPressedListener != null && mOnBackPressedListener.onBack()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        setResult(Livechat.OPEN_CHAT_VIEW_RESPONSE_CODE);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface onBackPressedListener {
        boolean onBack();
    }
}