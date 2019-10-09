package com.tmt.livechat.chat.clients.firestore.service.user_status;

import com.google.firebase.Timestamp;
import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;
import com.tmt.livechat.chat.clients.firestore.service.user_status.model.UserStatus;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class UserStatusService extends AbstractService implements UserStatusServiceInterface {

    private static UserStatusService userStatusService;

    @Override
    public void updateStatus(boolean online) {
        UserStatus userStatus = new UserStatus(online, Timestamp.now());
        getDb().document("users/" + getUserId() + "/data/public").set(userStatus);
    }

    @Override
    public void destroy() {
        userStatusService = null;
    }

    /**
     **/
    public static UserStatusService instance() {
        if (userStatusService == null)
            userStatusService = new UserStatusService();
        return userStatusService;
    }
}
