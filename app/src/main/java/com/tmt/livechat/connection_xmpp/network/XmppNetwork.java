package com.tmt.livechat.connection_xmpp.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmt.livechat.connection_xmpp.network.networkCallbacks.EmptyCallback;
import com.tmt.livechat.connection_xmpp.network.requests.ProgressRequestBody;
import com.tmt.livechat.model.UserMessage;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mohammednabil on 2019-09-08.
 */
public class XmppNetwork {

    private final long request_time_out = 60;

    private XmppNetworkApiInterface xmppNetworkApiInterface;
    private Retrofit retrofit = null;

    private static XmppNetwork xmppNetwork;

    private String username;
    private String password;

    public XmppNetwork() {
        xmppNetworkApiInterface = getClient().create(XmppNetworkApiInterface.class);
    }

    /**
     *
     * @param username
     * @param password
     */
    public void setUsernameAndPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     *
     * @param file
     * @param md5
     * @param url
     * @param callback
     */
    public void uploadImage(File file,String md5,  String url, Callback<Void> callback, ProgressRequestBody.UploadCallbacks callbacks) {
        ProgressRequestBody requestFile = new ProgressRequestBody(file, callbacks);
        xmppNetworkApiInterface.uploadImage(md5, url, requestFile).enqueue(callback);
    }

    /**
     *
     * @param url
     * @param room_id
     * @param userMessage
     */
    public void sendNotification(String url, String room_id,  UserMessage userMessage) {
        if (url != null && !url.isEmpty()) {
            String auth = Credentials.basic(username, password);
            xmppNetworkApiInterface.sendNotification(auth, url, userMessage.getNotificationRequest(room_id)).enqueue(new EmptyCallback<Void>());
        }
    }

    /**
     * @return RetroFit Client Object For creating Api Requests
     */
    private Retrofit getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(request_time_out, TimeUnit.SECONDS)
                .writeTimeout(request_time_out, TimeUnit.SECONDS)
                .connectTimeout(request_time_out, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.dummy.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        return retrofit;
    }

    /**
     * Instance for Api
     **/
    public static XmppNetwork instance() {
        if (xmppNetwork == null)
            xmppNetwork = new XmppNetwork();
        return xmppNetwork;
    }

}
