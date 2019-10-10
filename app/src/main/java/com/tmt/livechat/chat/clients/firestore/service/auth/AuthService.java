package com.tmt.livechat.chat.clients.firestore.service.auth;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.tmt.livechat.app_client.Livechat;
import com.tmt.livechat.app_client.interfaces.ConnectionInterface;
import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;
import com.tmt.livechat.chat.constants.ChatErrorCodes;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class AuthService extends AbstractService implements AuthServiceInterface {

    private static AuthService authService;


    @Override
    public void connect(String authToken, final ConnectionInterface connectionInterface) {
        getAuth().signInWithCustomToken(authToken).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    connectionInterface.onConnected();
                }
                else {
                    disconnect();
                    connectionInterface.onConnectionError(ChatErrorCodes.CONNECTION_ERROR, task.getException());
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return Livechat.instance().getFirebaseApp() != null && getAuth().getCurrentUser() != null;
    }

    @Override
    public void disconnect() {
        if (isConnected())
            getAuth().signOut();
    }

    @Override
    public void destroy() {
    }

    /**
     **/
    public static AuthService instance() {
        if (authService == null)
            authService = new AuthService();
        return authService;
    }
}
