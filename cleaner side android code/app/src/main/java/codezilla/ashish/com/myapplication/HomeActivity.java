package codezilla.ashish.com.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements AppRequestListener {

    ProgressDialog progressDialog;

    private static final int CAMERA_PIC_REQUEST = 50;
    LinearLayout cam1, cam2;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cleaner Side");

        cam1 = (LinearLayout) findViewById(R.id.cam1);
        cam2 = (LinearLayout) findViewById(R.id.cam2);

        cam1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        cam2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap image1 = (Bitmap) data.getExtras().get("data");
                image = getStringImage(image1);

                uploadData(image);
            }
        }
    }

    private void uploadData(String imageText) {
        progressDialog = ProgressDialog.show(this, "Fetching Location..Please Wait", null);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendData();
                    }
                });
            }
        }, 7000);
    }

    private void sendData() {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("photo", image);
        hash.put("comment", "hello");
        hash.put("lat", "28.6168129");
        hash.put("lon", "77.1908167");
        hash.put("staff_id", Integer.toString(3));
        hash.put("toilet_id", Integer.toString(1));
        CustomStringRequest req = new CustomStringRequest(Request.Method.POST, "http://192.168.1.132:8000/submit-photo/", "", this, hash);
        req.setShouldCache(false);
        req.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ZApplication.getInstance().addToRequestQueue(req, "");
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onRequestStarted(String requestTag) {
        progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "Sending data to server", "Please wait..");
    }

    @Override
    public void onRequestFailed(String requestTag, VolleyError error) {
        progressDialog.dismiss();
        Toast.makeText(this, "Data Sent", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, ThankyouActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestCompleted(String requestTag, String response) {
        progressDialog.dismiss();
        Toast.makeText(this, "Thankyou. Lets have a Swachh Bharat", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, ThankyouActivity.class);
        startActivity(i);
    }
}
