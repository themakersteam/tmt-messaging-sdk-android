package com.tmt.messagecenter.connection_xmpp.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmt.messagecenter.connection_xmpp.network.requests.ProgressRequestBody;
import java.io.File;
import java.util.concurrent.TimeUnit;
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

    public XmppNetwork() {
        xmppNetworkApiInterface = getClient().create(XmppNetworkApiInterface.class);
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
