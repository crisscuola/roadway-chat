package com.roadway.capslabs.roadway_chat.share;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by konstantin on 28.11.16.
 */
public class ShareVk extends AppCompatActivity {
    private String[] scope = new String[]{VKScope.WALL};
    private Activity context = this;
    private Button share, logout;
    private String url, title;
    private Toolbar toolbar;
    private TextView name;

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
        logout = (Button) findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.logout();
                onBackPressed();
            }
        });

        if (VKAccessToken.currentToken() == null) {
        VKSdk.login(context, scope);
        } else {
           // nothing

            final int id = Integer.parseInt(VKAccessToken.currentToken().userId);

            VKRequest request = new VKRequest("users.get", VKParameters.from(VKApiConst.USER_IDS,id,
                    VKApiConst.FIELDS, "photo_100","first_name, last_name"));

            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {

                    String first_name = " ";
                    String last_name = " ";
                    String photo_url = " ";

                    try {
                        JSONArray array = response.json.getJSONArray("response");
                        first_name = array.getJSONObject(0).getString("first_name");
                        last_name = array.getJSONObject(0).getString("last_name");
                        photo_url = array.getJSONObject(0).getString("photo_100");

                        Log.i("PHOTO", photo_url);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    name = (TextView) findViewById(R.id.username);
                    ImageView image = (ImageView) findViewById(R.id.imageView);
                    name.setText(first_name + " " + last_name);


                    Picasso.with(context).load(photo_url).transform(new CircleTransform())
                            //.placeholder(R.drawable.placeholder_dark)
                            .into(image);

                    super.onComplete(response);
                }
            });


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
