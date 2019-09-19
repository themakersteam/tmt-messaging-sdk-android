package com.tmt.livechat.screens.chat.activity.presenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.tmt.livechat.R;
import com.tmt.livechat.model.Theme;
import com.tmt.livechat.screens.abstraction.mvp.MvpPresenter;
import com.tmt.livechat.screens.chat.LiveChatFragment;
import com.tmt.livechat.screens.chat.activity.LiveChatActivityInterface;
import java.util.HashMap;

/**
 * Created by mohammednabil on 2019-09-18.
 */
public class LiveChatActivityPresenter extends MvpPresenter<LiveChatActivityInterface.View> implements LiveChatActivityInterface.Presenter{

    private Theme theme;
    private HashMap<String, Boolean> inProgressMessages;

    @Override
    public void init() {
        inProgressMessages = new HashMap<>();
        theme = view().activity().getIntent().hasExtra("THEME") ? (Theme) view().activity().getIntent().getExtras().getParcelable("THEME") : null;
        view().setupViews();
        String title = theme != null && theme.getToolbarTitle() != null ? theme.getToolbarTitle() : view().activity().getString(R.string.message_center_toolbar_title);
        String subtitle = theme != null && theme.getToolbarSubtitle() != null ? theme.getToolbarSubtitle() : "";
        view().setupToolbar(title, subtitle);
        Fragment fragment = LiveChatFragment.newInstance();
        Bundle bundle = new Bundle();
        if (view().activity().getIntent() != null && view().activity().getIntent().getExtras() != null) {
            bundle.putAll(view().activity().getIntent().getExtras());
        }
        fragment.setArguments(bundle);
        view().onFragment(fragment);
    }

    @Override
    public void addProgress(String name, boolean val) {
        inProgressMessages.put(name, val);
    }

    @Override
    public void removeProgress(String name) {
        inProgressMessages.remove(name);
    }

    @Override
    public void onBackPressed() {
        if (inProgressMessages != null && inProgressMessages.size() > 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(view().activity()).setMessage(view().activity().getString(R.string.ms_message_file_in_progress))
                    .setNegativeButton(view().activity().getString(R.string.ms_message_failed_no), null)
                    .setPositiveButton(view().activity().getString(R.string.ms_message_failed_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (inProgressMessages != null) {
                                inProgressMessages.clear();
                            }
                            view().close();
                        }
                    });
            view().showInProgressMessageAlert(dialog);
        }
        else {
           view().close();
        }
    }
}
