package com.dnk.xinfeng902.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnk.xinfeng902.R;
import com.dnk.xinfeng902.utils.Config;
import com.dnk.xinfeng902.utils.DevStateValue;

public class WarningActivity extends Activity {

    LinearLayout linearView;
    TextView tv;

    int length =0;
    String warninginfo[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((DevStateValue.warningData[i] & (1 << j)) > 0) length++;
            }
        }

        warninginfo = new String[length];
        length =0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((DevStateValue.warningData[i] & (1 << j)) > 0){
                    warninginfo[length] = Config.warninginfo[i*8+j];
                    length++;
                }
            }
        }

        linearView =findViewById(R.id.linearView);

        for (int i = 0; i < warninginfo.length; i++) {
            tv = new TextView(this);
            tv.setBackgroundColor(Color.WHITE);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(20);
            tv.setText(warninginfo[i]);
            linearView.addView(tv);
        }
    }
}
