package com.tmt.livechat.screens.test;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.tmt.livechat.R;
import com.tmt.livechat.client.Livechat;
import com.tmt.livechat.client.interfaces.ConnectionInterface;
import com.tmt.livechat.client.interfaces.OpenChatInterface;
import com.tmt.livechat.model.ConnectionRequest;
import com.tmt.livechat.model.OpenChatRequest;
import com.tmt.livechat.model.Theme;

public class TestActivity extends AppCompatActivity {

    int user = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final Theme theme = new Theme();
        theme.setAppName("PACE");
        theme.setToolbarTitle("HELLO");
        theme.setToolbarSubtitle("Rider");
        theme.setWelcomeMessage("Hello and welcome");

        ConnectionRequest connectionRequest = new ConnectionRequest("test_tmt_test_" + user, "password_"+ user, "tmt-test.m.in-app.io");
        Livechat.instance().connect(connectionRequest, new ConnectionInterface() {
            @Override
            public void onConnected() {
                OpenChatRequest openChatRequest = new OpenChatRequest(null, "test-room-nabil@conference.tmt-test.m.in-app.io");
                Livechat.instance().openChatView(TestActivity.this, openChatRequest, theme, new OpenChatInterface() {
                    @Override
                    public void screenWillLaunch() {

                    }

                    @Override
                    public void userNotConnected() {
                    }
                });
            }

            @Override
            public void onConnectionError(int code, Exception e) {

            }
        });
    }
}
