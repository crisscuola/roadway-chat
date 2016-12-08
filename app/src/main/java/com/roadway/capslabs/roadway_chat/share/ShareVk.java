package com.roadway.capslabs.roadway_chat.share;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

/**
 * Created by konstantin on 28.11.16.
 */
public class ShareVk extends AppCompatActivity {
    private String[] scope = new String[]{VKScope.WALL};
    private Activity context = this;
    private Button share;
    private String url, title;
    private Toolbar toolbar;

    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk_share);
        url = getIntent().getExtras().getString("url");
        title = getIntent().getExtras().getString("title");

        initToolbar(getString(R.string.vk_activity_title));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

        share = (Button) findViewById(R.id.sharePost);

        if (VKAccessToken.currentToken() == null) {
        VKSdk.login(context, scope);
        } else {
           // nothing

            final int id = Integer.parseInt(VKAccessToken.currentToken().userId);


            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    VKRequest request = new VKRequest("wall.post", VKParameters.from(VKApiConst.OWNER_ID, id,
                            VKApiConst.MESSAGE, title, VKApiConst.ATTACHMENTS, url));

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            alertShow();
                        }
                    });

                }
            });
        }
    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_share_vk);
        toolbar.setTitle(title);

    }

//    @Override
//    public void onBackPressed() {
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (VKAccessToken.currentToken() == null) {
            onBackPressed();
        }
    }

    public void alertShow () {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShareVk.this);
        builder.setTitle("Важное сообщение!")
                .setMessage("Репост в VK успешно добавлен!")
                .setIcon(R.drawable.logo2)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                onBackPressed();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
