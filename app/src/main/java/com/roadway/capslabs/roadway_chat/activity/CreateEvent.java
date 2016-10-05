package com.roadway.capslabs.roadway_chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.DateRange;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by konstantin on 02.10.16.
 */
public class CreateEvent extends AppCompatActivity {


    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL = "http://simplifiedcoding.16mb.com/VolleyUpload/upload.php";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";

    private Drawer drawer;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory(new HttpConnectionHandler());

    private final Activity context = this;


    private Button buttonCreate, buttonChoose;
    private ImageView imageView;
    private EditText title, description, address;

    private String titleString, descriptionString;
    private Bitmap bitmap;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initToolbar("CreateEvent");
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

        buttonChoose = (Button) findViewById(R.id.btn_choose);
        buttonCreate = (Button) findViewById(R.id.btn_create);

        imageView = (ImageView) findViewById(R.id.imageView);

        title = (EditText) findViewById(R.id.event_title);
        description = (EditText) findViewById(R.id.event_desciption);
        address = (EditText) findViewById(R.id.event_address);


        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleString = title.getText().toString();
                descriptionString = description.getText().toString();
                event = new Event(titleString, descriptionString, getBytesImage(bitmap), new DateRange("10/1/2016 18:00:00", "20/1/2018 18:00:00"));
                Log.d("lolo_title", event.getTitle());
                new EventCreator().execute(new EventRequestHandler());
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
                Log.d("lolo", "lolo");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public byte[] getBytesImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        toolbar.setTitle(title);
    }

    private final class EventCreator extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            Log.d("lolo_title", titleString);
            EventRequestHandler handler = (EventRequestHandler) params[0];
            return handler.createEvent(context, new Event(titleString, descriptionString,
                    getBytesImage(bitmap), new DateRange("10/1/2016 17:00:00", "10/1/2016 18:00:00")));
        }


        @Override
        protected void onPostExecute(String result) {
            Log.d("lolo", result);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Event Created!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            JSONObject object = HttpConnectionHandler.parseJSON(result);
        }
    }


}


