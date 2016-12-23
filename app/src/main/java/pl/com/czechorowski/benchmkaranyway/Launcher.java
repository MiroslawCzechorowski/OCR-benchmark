package pl.com.czechorowski.benchmkaranyway;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static pl.com.czechorowski.benchmkaranyway.R.id.imageView;

public class Launcher extends AppCompatActivity {
    ArrayList<HashMap<String,String>> challanges = new ArrayList<>();
    Bitmap image;
    private TessBaseAPI mTess;
    String datapath="";
    public String token="";
    private Target loadtarget;
    //Get hardware/software information
    String url="";
    String deviceModel = android.os.Build.MODEL;
    String deviceBrand = android.os.Build.MANUFACTURER;
    String deviceOsVersion = String.valueOf(Build.VERSION.SDK_INT);
    String deviceOS = "Android";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //Get token
        token = intent.getStringExtra("token");
        //Log.d("TOKEN",token);

        setContentView(R.layout.activity_launcher);
        getSupportActionBar().hide();
        //Configure tesseract library
        datapath = getFilesDir()+ "/tesseract/";
        checkFile(new File(datapath + "tessdata/"));
        //init Tesseract API
        String language = "eng";
        mTess = new TessBaseAPI();
        mTess.init(datapath, language);
        getData();
    }
    //Load bitmap from server
    public void loadBitmap(String url) {
        if (loadtarget == null) loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                //Set image for tesseract api
                handleLoadedBitmap(bitmap);
                image=bitmap;
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.with(this).load(url).into(loadtarget);
    }
    public void handleLoadedBitmap(Bitmap b) {
        // do something here
    }
    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";
            //get access to AssetManager
            AssetManager assetManager = getAssets();
            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void getData(){
    // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url =Config.URL_CHALLANGES;
    // Request a string response from the provided URL.
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("VOLLEY",response);
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(response);
                            JSONArray challange = jsonObj.getJSONArray("data");
                            // looping through All data
                            //Log.d("Challange data",String.valueOf(challange.length()));
                            for (int i = 0; i < challange.length(); i++) {
                                JSONObject c = challange.getJSONObject(i);
                                String id = c.getString("id");
                                String name= c.getString("name");
                                JSONObject imagePath = c.getJSONObject("image");
                                String image=imagePath.getString("url");
                                HashMap<String, String> contact = new HashMap<>();
                                contact.put("id", id);
                                contact.put("name", name);
                                contact.put("url", image);
                                challanges.add(contact);
                            }
                        } catch (JSONException e) {
                        e.printStackTrace();
                    }
                        //Log.d("CHALLANGES", challanges.toString());
                        //Set received data to UI
                        ImageView mImageView = (ImageView)findViewById(imageView);
                        ImageView mImageView2= (ImageView)findViewById(R.id.imageView2);
                        ImageView mImageView3= (ImageView)findViewById(R.id.imageView3);
                        loadImageWithPicasso(0,mImageView);
                        loadImageWithPicasso(1,mImageView2);
                        loadImageWithPicasso(2,mImageView3);
                        RelativeLayout ocrView=(RelativeLayout)findViewById(R.id.OCRButtonContainer);
                        ocrView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startTest();
                            }
                        });
                        TextView ocrButton = (TextView)findViewById(R.id.OCRbutton);
                        ocrButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            startTest();
                            }
                        });
                }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY","That didn't work!");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization","Bearer "+token);
                return params;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    //Show downloaded images from server at UI
    public void loadImageWithPicasso(int i,ImageView image){
        Picasso.with(getApplicationContext())
                .load(challanges.get(i).get("url"))
                .into(image);
    }
    //RUN OCR
    public void processImage(int i,TextView text){
        String OCRresult = null;
        url=challanges.get(i).get("url");
        loadBitmap(url);
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        text.setText(OCRresult);
    }
    public void sendResults(Context context, final String content, final String challangeID, final String deviceBrand, final String deviceModel, final String deviceOS, final String deviceOSVersion, final String seconds){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST,Config.URL_ANSWERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getApplicationContext(),"Test finished. Now you can check results",Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("EROOORRR SENDING", error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("content", content);
                params.put("challenge", challangeID);
                params.put("deviceBrand",deviceBrand);
                params.put("deviceModel",deviceModel);
                params.put("deviceOS",deviceOS);
                params.put("deviceOSVersion",deviceOSVersion);
                params.put("timeResult",String.valueOf(seconds));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(sr);
    }
    public void startTest(){
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        TextView OCRTextView2 = (TextView) findViewById(R.id.OCRTextView2);
        TextView OCRTextView3 = (TextView) findViewById(R.id.OCRTextView3);
        long startTime = System.currentTimeMillis();
        processImage(0, OCRTextView);
        long endTime = System.currentTimeMillis();
        double seconds = (endTime - startTime)/1000.0;
        startTime = System.currentTimeMillis();
        processImage(1, OCRTextView2);
        endTime = System.currentTimeMillis();
        double seconds1 = (endTime - startTime)/1000.0;
        startTime = System.currentTimeMillis();
        processImage(2, OCRTextView3);
        endTime = System.currentTimeMillis();
        double seconds2 = (endTime - startTime)/1000.0;

        //SEND JSON TO SERVER
        sendResults(getApplicationContext(),OCRTextView.getText().toString().replaceAll("\\n", ""),"53c48bf6c4dcbf5a692e72bbfd0ad59b6cf918138873435b9a0a10b751679280e7c74e2cba35820edc79df87f9de385e977cb51b0cf4f207f153987adefbec15",deviceBrand,deviceModel,deviceOS,deviceOsVersion,String.valueOf(seconds));
        sendResults(getApplicationContext(),OCRTextView3.getText().toString().replaceAll("\\n", ""),"23395307d338adacd20ac4fe8438a0000097a4d0a8e8a54ef038875aeda7077708cb625d1436e2555335f25e5396f42bd896705e678acc08ff7d46ba2212045a",deviceBrand,deviceModel,deviceOS,deviceOsVersion,String.valueOf(seconds2));

        //Show results and ask user about checking for global results
        new AlertDialog.Builder(Launcher.this)
                .setTitle("Test result")
                .setMessage("Test1: "+seconds+"\nTest2: "+seconds1+"\nTest3: "+seconds2+"\n Would you like to see all results?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ShowResults.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
