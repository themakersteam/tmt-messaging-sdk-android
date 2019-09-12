package com.tmt.messagecenter.screens.test;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.tmt.messagecenter.R;
import com.tmt.messagecenter.client.MessageCenter;
import com.tmt.messagecenter.client.interfaces.ConnectionInterface;
import com.tmt.messagecenter.client.interfaces.OpenChatInterface;
import com.tmt.messagecenter.model.ConnectionRequest;
import com.tmt.messagecenter.model.Theme;

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
        MessageCenter.instance().connect(connectionRequest, new ConnectionInterface() {
            @Override
            public void onConnected() {
                MessageCenter.instance().openChatView(TestActivity.this, "test-room-nabil@conference.tmt-test.m.in-app.io", theme, new OpenChatInterface() {
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
