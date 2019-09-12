package com.tmt.messagecenter.screens.chat;

import android.app.Dialog;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tmt.messagecenter.R;

/**
 * Created by MohammedNabil on 8/27/17.
 */

public class FileOptionsBottomSheetAdapter extends BottomSheetDialogFragment implements View.OnClickListener{

    private ButtonClickListener listener;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View v = View.inflate(getContext(), R.layout.bottom_sheet_file_options_buttons, null);
        dialog.setContentView(v);

        renderDialog(v);
        RelativeLayout camera = v.findViewById(R.id.action_camera);
        RelativeLayout gallery = v.findViewById(R.id.action_gallery);
        RelativeLayout location = v.findViewById(R.id.action_location);
        RelativeLayout dismissShaddow = v.findViewById(R.id.dismiss_shadow);
        dismissShaddow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        location.setOnClickListener(this);

    }

    protected void renderDialog(View v) {
        try {
            ((View) v.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
            View parent = (View) v.getParent();
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
            layoutParams.setMargins(
                    getResources().getDimensionPixelSize(R.dimen.bottom_sheet_margin_left_right),
                    0,
                    getResources().getDimensionPixelSize(R.dimen.bottom_sheet_margin_left_right),
                    getResources().getDimensionPixelSize(R.dimen.bottom_sheet_margin_bottom)
            );
            parent.setLayoutParams(layoutParams);
        }
        catch (Exception e){}
    }

    public void setListener(ButtonClickListener listener) {
        this.listener = listener;
    }

    public static FileOptionsBottomSheetAdapter newInstance() {
        FileOptionsBottomSheetAdapter fragment = new FileOptionsBottomSheetAdapter();
        return fragment;
    }

    public interface ButtonClickListener {
        void onCameraClicked();
        void onGalleryClicked();
        void onLocationClicked();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_camera)
            listener.onCameraClicked();
        else if (v.getId() == R.id.action_gallery)
            listener.onGalleryClicked();
        else
            listener.onLocationClicked();
    }
}
