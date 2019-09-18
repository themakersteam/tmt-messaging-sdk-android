package com.tmt.livechat.screens.mylocation.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.tmt.livechat.R;
import com.tmt.livechat.screens.abstraction.activity.MapsActivity;
import com.tmt.livechat.screens.mylocation.MyLocationInterface;
import com.tmt.livechat.screens.mylocation.presenter.MyLocationPresenter;

public class MyLocationActivity extends MapsActivity implements MyLocationInterface.View {

    private LinearLayout layout_send_location;
    private ImageButton button_get_current_location;
    private ImageButton button_send_location;
    private TextView tv_send_location;
    private RadioGroup toggle_map_view;
    private RadioButton rb_map;
    private RadioButton rb_satellite;

    private View.OnClickListener locationClickedListener;
    private MyLocationInterface.Presenter presenter = new MyLocationPresenter();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_my_location);
        setup();
        ((MyLocationPresenter)presenter).setView(this);
        presenter.init();
    }

    @Override
    public void setupViews() {
        setupToolbarWithHome((Toolbar)findViewById(R.id.my_toolbar), getString(R.string.message_center_send_location_title));
        layout_send_location = findViewById(R.id.layout_send_location);
        button_get_current_location = findViewById(R.id.button_get_current_location);
        button_send_location = findViewById(R.id.button_send_location);
        toggle_map_view = findViewById(R.id.toggle_map_view);
        tv_send_location = findViewById(R.id.tv_send_location);
        rb_map = findViewById(R.id.rb_map);
        rb_satellite = findViewById(R.id.rb_satellite);
        locationClickedListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.sendLocationClicked(geocodeLocation, latitude, longitude);
            }
        };
        layout_send_location.setOnClickListener(locationClickedListener);
        button_send_location.setOnClickListener(locationClickedListener);
        button_get_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildGoogleApiClient();
            }
        });
        toggle_map_view.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                presenter.onCheckedChanged(group, checkedId);
            }
        });
    }

    @Override
    public void updateRadio(int map_style, int checked_color, int color) {
        mGoogleMap.setMapType(map_style);
        rb_map.setTextColor(checked_color);
        rb_satellite.setTextColor(color);
    }

    @Override
    public void onLocationText(String text) {
        tv_send_location.setText(text);
    }

}