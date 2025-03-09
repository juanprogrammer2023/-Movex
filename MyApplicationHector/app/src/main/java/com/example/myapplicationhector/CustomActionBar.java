package com.example.myapplicationhector;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CustomActionBar extends LinearLayout {

    private static final String TAG = "CustomActionBar";

    public CustomActionBar(Context context) {
        super(context);
        init(context);
    }

    public CustomActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Log.d(TAG, "Init method called");

        // Infla el layout custom_action_bar.xml
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_action_bar, this, true);

        if (view != null) {
            Log.d(TAG, "Layout inflated successfully");
        } else {
            Log.e(TAG, "Error inflating layout");
        }

        // Verificar si las vistas se encuentran correctamente
        ImageView leftImageButton = findViewById(R.id.leftImageButton);
        ImageView rightImageButton = findViewById(R.id.rightImageButton);

        if (leftImageButton != null && rightImageButton != null) {
            Log.d(TAG, "ImageButtons found successfully");
        } else {
            Log.e(TAG, "One or both ImageButtons not found");
        }
    }
}
