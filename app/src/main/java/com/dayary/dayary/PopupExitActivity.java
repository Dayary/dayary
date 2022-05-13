package com.dayary.dayary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;

public class PopupExitActivity extends Activity {
    private View ok;
    private View not_ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.exit);

        ok = findViewById(R.id.ok);





    }
}
