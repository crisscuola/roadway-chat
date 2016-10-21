package com.roadway.capslabs.roadway_chat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;

/**
 * Created by konstantin on 14.10.16
 */
public class QrCodeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        initToolbar("QR code");
        drawerFactory.getDrawerBuilder(this, toolbar).build();
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_qr_code);
        toolbar.setTitle(title);
    }
}
