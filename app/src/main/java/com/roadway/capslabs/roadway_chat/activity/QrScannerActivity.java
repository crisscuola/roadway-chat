package com.roadway.capslabs.roadway_chat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by konstantin on 14.10.16
 */
public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private Toolbar toolbar;
    private final static DrawerFactory drawerFactory = new DrawerFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        initToolbar(getString(R.string.qr_activity_title));
        drawerFactory.getDrawerBuilder(this, toolbar).build();
    }

    public void QrScanner(View view) {

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_qr_scan);
        toolbar.setTitle(title);
    }

    @Override
    public void handleResult(Result rawResult) {


        Log.e("handler", rawResult.getText());
        Log.e("handler", rawResult.getBarcodeFormat().toString());

        Toast toast = Toast.makeText(getApplicationContext(),
                rawResult.getText(),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        mScannerView.resumeCameraPreview(this);

    }
}
