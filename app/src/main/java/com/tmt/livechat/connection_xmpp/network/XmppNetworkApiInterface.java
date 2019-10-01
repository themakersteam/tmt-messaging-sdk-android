package com.tmt.livechat.connection_xmpp.network;

import com.tmt.livechat.connection_xmpp.network.requests.ProgressRequestBody;
import com.tmt.livechat.connection_xmpp.network.requests.SendNotificationBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * Created by mohammednabil on 2019-09-08.
 */
interface XmppNetworkApiInterface {

    @PUT
    Call<Void> uploadImage(@Header ("Content-MD5") String md5, @Url String url, @Body ProgressRequestBody image);

    @POST
    Call<Void> sendNotification(@Header("Authorization") String auth, @Url String url, @Body SendNotificationBody body);
}
