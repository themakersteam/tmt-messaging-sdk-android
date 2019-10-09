package com.tmt.livechat.screens.test;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.FirebaseApp;
import com.tmt.livechat.R;
import com.tmt.livechat.app_client.Livechat;
import com.tmt.livechat.app_client.interfaces.ConnectionInterface;
import com.tmt.livechat.app_client.interfaces.OpenChatInterface;
import com.tmt.livechat.app_client.interfaces.UnreadCountInterface;
import com.tmt.livechat.app_client.model.ConnectionRequest;
import com.tmt.livechat.app_client.model.Theme;

public class TestActivity extends AppCompatActivity {

    private String senToekn = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImlhdCI6MTU3MDYyMzQyNSwiZXhwIjoxNTcwNjI3MDI1LCJpc3MiOiJ0bXQtbWVzc2FnaW5nQGFwcHNwb3QuZ3NlcnZpY2VhY2NvdW50LmNvbSIsInN1YiI6InRtdC1tZXNzYWdpbmdAYXBwc3BvdC5nc2VydmljZWFjY291bnQuY29tIiwidWlkIjoibW9oYW1tZWQiLCJjbGFpbXMiOnsibmFtZSI6Im1vaGFtbWVkIn19.KIxUzEP_rJmSj4-gpBnPsmJTP0zVMfoW6AzHU0aunRrOvuPK4EN34f9KCJQ29muDWNQano21SGtIXtwOJgcMDRvM0kcFYQE3jVBRNPjKwUP8EugOSMo-1qcIlOywXLR6AuCyZqY8VXcdWKKAFhHZC5w159RgLf0PZNw3zAFtgAaVDVwDGbmoccJQRuX6SwgFRkzOGVZFybCLnsSRWoaQnOqsDlMQVgTJeCHspSAVrLHduyxLt8eFr4_qY6EhQwbfKfMBzK5NAdsDeBs87kbv2zyEFWE9WE1L7o4xe_ZuiUlg3wUanzUdTmErE-rFzInUFuPKug33Mp3vskhFhg3VWQ";
    private String userToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImlhdCI6MTU3MDYyNTUxOSwiZXhwIjoxNTcwNjI5MTE5LCJpc3MiOiJ0bXQtbWVzc2FnaW5nQGFwcHNwb3QuZ3NlcnZpY2VhY2NvdW50LmNvbSIsInN1YiI6InRtdC1tZXNzYWdpbmdAYXBwc3BvdC5nc2VydmljZWFjY291bnQuY29tIiwidWlkIjoibmFiaWwiLCJjbGFpbXMiOnsibmFtZSI6Im5hYmlsIn19.DuZUWgD0SNcc5krGqG99ubLTeBVUIEMAiyg8dCeRTMmjdqWQdg8uS8UR6XN0Tuu75u2BBVG53C179i9d8qpIm46llAAVg2eCDcOP_YYQeuM92QrKLSGHE3-fXVzrt6oau1DWhkILK8LkPV6CNhT4G3vjRgTmmbK0Qzm9yIk0m4QCUQUVuhkgWYeUNIx_cN4DTgocw-rX-FRYt-FZgzNDsdOAgekpaz7E6HPP4Cq3EchK5Z5flAyecEpUYzKABJZKIaaCv-fsomwNaZqDwvqF2e89MG_SnWx_rRb4dfx0aLrPwHzfNORqEMC_dbrXInwjPLfmG08L4DCuBhHJeJ-0WA";
    private String channel_id = "4KDqAkKFNRmgq9TJi2pi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final Theme theme = new Theme();
        theme.setAppName("PACE");
        theme.setToolbarTitle("HELLO");
        theme.setToolbarSubtitle("Rider");
        theme.setWelcomeMessage("Hello and welcome");

        FirebaseApp.initializeApp(this);

        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Livechat.instance().openChatView(TestActivity.this, channel_id, theme, new OpenChatInterface() {
                    @Override
                    public void screenWillLaunch() {
                        ((TextView)findViewById(R.id.tv_unread)).setText(0 +  "");
                    }

                    @Override
                    public void userNotConnected() {

                    }
                });
            }
        });

        Livechat.instance().connect(FirebaseApp.getInstance(), new ConnectionRequest(userToken), new ConnectionInterface() {
            @Override
            public void onConnected() {
                ((Button)findViewById(R.id.btn_connect)).setText("Connected");
                Livechat.instance().getUnreadMessageCount(channel_id, new UnreadCountInterface() {
                    @Override
                    public void onUnreadMessageCount(int count) {
                        ((TextView)findViewById(R.id.tv_unread)).setText(count +  "");
                    }
                });
            }

            @Override
            public void onConnectionError(int code, Exception e) {
                ((Button)findViewById(R.id.btn_connect)).setText(e.getMessage());
            }
        });
    }
}
