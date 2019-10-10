package com.tmt.livechat.chat.clients.firestore.service.notification;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.InstanceIdResult;
import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class NotificationService extends AbstractService implements NotificationServiceInterface {

    private final String ADD_USER_TOKEN_FUCNTION = "addUserFcmToken";
    private final String REMOVE_USER_TOKEN_FUNCTION = "removeUserFcmToken";


    private static NotificationService notificationService;

    @Override
    public void registerNotificationToken() {
        getToken(new TokenCallbacks() {
            @Override
            public void onToken(String fcmToken) {
                Map<String, Object> data = new HashMap<>();
                data.put("token", fcmToken);
                getFunctions().getHttpsCallable(ADD_USER_TOKEN_FUCNTION).call(data);
            }
        });
    }

    @Override
    public void getToken(final TokenCallbacks tokenCallbacks) {
        getInstanceId().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                tokenCallbacks.onToken(instanceIdResult.getToken());
            }
        });
    }

    @Override
    public void unregisterNotificationToken() {
        getToken(new TokenCallbacks() {
            @Override
            public void onToken(String token) {
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                getFunctions().getHttpsCallable(REMOVE_USER_TOKEN_FUNCTION).call(data);
            }
        });
    }

    @Override
    public void destroy() {
        notificationService = null;
    }

    /**
     **/
    public static NotificationService instance() {
        if (notificationService == null)
            notificationService = new NotificationService();
        return notificationService;
    }
}
