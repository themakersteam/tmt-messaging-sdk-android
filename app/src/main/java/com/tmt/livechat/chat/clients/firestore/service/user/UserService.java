package com.tmt.livechat.chat.clients.firestore.service.user;

import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class UserService extends AbstractService implements UserServiceInterface {

    private static UserService userService;

    @Override
    public boolean isMyMessage(String user_id) {
        return getUserId().equals(user_id);
    }

    @Override
    public void destroy() {
        userService = null;
    }

    /**
     **/
    public static UserService instance() {
        if (userService == null)
            userService = new UserService();
        return userService;
    }
}
