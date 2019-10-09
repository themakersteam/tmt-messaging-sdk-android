package com.tmt.livechat.app_client;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tmt.livechat.chat.clients.firestore.service.filedownload.FileDownloadService;
import com.tmt.livechat.chat.clients.firestore.service.unread.UnreadService;
import com.tmt.livechat.chat.constants.ChatErrorCodes;
import com.tmt.livechat.app_client.interfaces.ConnectionInterface;
import com.tmt.livechat.app_client.interfaces.OpenChatInterface;
import com.tmt.livechat.app_client.interfaces.UnreadCountInterface;
import com.tmt.livechat.app_client.model.ConnectionRequest;
import com.tmt.livechat.app_client.model.Theme;
import com.tmt.livechat.screens.abstraction.activity.BaseActivity;
import com.tmt.livechat.screens.chat.activity.view.LiveChatActivity;

/**
 * Created by mohammednabil on 2019-09-01.
 */
public class Livechat extends LivechatInterface {

    private static Livechat livechat;
    public static final Integer OPEN_CHAT_VIEW_REQUEST_CODE = 234;
    public static final Integer OPEN_CHAT_VIEW_RESPONSE_CODE = 232;

    private FirebaseApp firebaseApp;

    @Override
    public void connect(FirebaseApp firebaseApp, ConnectionRequest connectionRequest, final ConnectionInterface connectionInterface) {
        this.firebaseApp = firebaseApp;
        FirebaseAuth.getInstance(firebaseApp).signInWithCustomToken(connectionRequest.getAuthToken()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    connectionInterface.onConnected();
                }
                else {
                    connectionInterface.onConnectionError(ChatErrorCodes.CONNECTION_ERROR, task.getException());
                }
            }
        });
    }

    @Override
    public FirebaseApp getFirebaseApp() {
        return firebaseApp;
    }

    @Override
    public boolean isConnected() {
        return firebaseApp != null && FirebaseAuth.getInstance(firebaseApp).getCurrentUser() != null;
    }

    @Override
    public boolean isPresented() {
        return BaseActivity.IS_PRESENTED;
    }

    @Override
    public void openChatView(Activity context, String channel_id, Theme theme, OpenChatInterface openChatInterface) {
        //Clearing the last channel files Cache
        FileDownloadService.instance().clearFilesCache(context);
        if (isConnected()) {
            openChatActivity(context, channel_id, theme);
            if (openChatInterface != null)
                openChatInterface.screenWillLaunch();
        }
        else {
            if (openChatInterface != null)
                openChatInterface.userNotConnected();
        }
    }

    @Override
    public void getUnreadMessageCount(String chat_id, UnreadCountInterface unreadCountInterface) {
        if (unreadCountInterface == null || chat_id == null)
            return;
        try {
            UnreadService.instance(chat_id).getUnreadMessageCount(unreadCountInterface);
        }
        catch (Exception e){
            unreadCountInterface.onUnreadMessageCount(0);
        }
    }

    @Override
    public void disconnect() {
        if (isConnected() && firebaseApp != null)
            FirebaseAuth.getInstance(firebaseApp).signOut();
    }

    /**
     *
     * @param context
     * @param channel_id
     * @param theme
     */
    private void openChatActivity(Activity context, String channel_id, Theme theme) {
        Intent a1 = new Intent(context, LiveChatActivity.class);
        if (theme != null) {
            a1.putExtra("THEME", theme);
        }
        a1.putExtra("CHANNEL_ID", channel_id);
        a1.putExtra("PACKAGE_NAME", context.getPackageName());
        context.startActivityForResult(a1, Livechat.OPEN_CHAT_VIEW_REQUEST_CODE);
    }
    /**
     **/
    public static Livechat instance() {
        if (livechat == null)
            livechat = new Livechat();
        return livechat;
    }
}
