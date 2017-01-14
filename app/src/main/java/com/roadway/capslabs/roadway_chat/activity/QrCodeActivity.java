package com.roadway.capslabs.roadway_chat.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;

/**
 * Created by konstantin on 14.10.16
 */
public class QrCodeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;
    private ImageView imageQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        initToolbar(getString(R.string.qr_code_title));

        if (!ConnectionChecker.isOnline(this)) {
            ConnectionChecker.showNoInternetMessage(this);
            return;
        }

        imageQr = (ImageView) findViewById(R.id.qr_image);
        Bitmap bitmap = (Bitmap) getIntent().getExtras().get("bitmap");
        imageQr.setImageBitmap(bitmap);
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_qr_code);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);

        drawer =  drawerFactory.getDrawerBuilderWithout(this)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        onBackPressed();
                        return false;
                    }
                })
                .build();

        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
