package com.tmt.messagecenter.connection_xmpp.network;

import com.tmt.messagecenter.connection_xmpp.network.requests.ProgressRequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * Created by mohammednabil on 2019-09-08.
 */
interface XmppNetworkApiInterface {

    @PUT
    Call<Void> uploadImage(@Header ("Content-MD5") String md5, @Url String url, @Body ProgressRequestBody image);

}
