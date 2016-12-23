package pl.com.czechorowski.benchmkaranyway;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowResults extends AppCompatActivity {
    public String token = "";
    public ArrayList<HashMap<String, String>> dataResponse = new ArrayList<>();
    public ArrayList<HashMap<String, String>> dataResponse2 = new ArrayList<>();
    ArrayList<HashMap<Integer,ArrayList<HashMap<String,String>>>> map = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        //Get token
        token = intent.getStringExtra("token");

        getData();
    }
    public void getData() {
        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Config.URL_RESULTS;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getSpecificResult(response,0,dataResponse);
                        getSpecificResult(response,1,dataResponse2);
                        plotData();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", "That didn't work!");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
//Render plots
public void plotData(){
    GraphView graph = (GraphView) findViewById(R.id.graph);
    GraphView graph2 = (GraphView) findViewById(R.id.graph2);
    DataPoint[] values = new DataPoint[dataResponse.size()];
    String [] xAxis= new String [dataResponse.size()];
    DataPoint[] values2 = new DataPoint[dataResponse.size()];
    String [] xAxis2= new String [dataResponse.size()];
    //Log.d("RESOPNE", String.valueOf(dataResponse.size()));
    for (int i = 0; i< dataResponse.size(); i++) {
            Integer xi = i+1;
            xAxis[i] = dataResponse.get(i).get("deviceBrand") + " " + dataResponse.get(i).get("deviceModel");
            Double yi = Double.valueOf(dataResponse.get(i).get("average"));
            DataPoint v = new DataPoint(xi, yi);
            values[i] = v;
    }
    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(values);
    graph.addSeries(series);
    // styling
    series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
        @Override
        public int get(DataPoint data) {
            return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
        }
    });
    graph.getViewport().setXAxisBoundsManual(true);
    //Set legend
    TextView labels = (TextView)findViewById(R.id.textView4);
    for(int i=0;i<xAxis.length;i++){
        labels.setText(labels.getText()+ String.valueOf(i)+"- "+xAxis[i]+": "+"\n");
    }
    series.setSpacing(30);
    series.setDrawValuesOnTop(true);
    series.setValuesOnTopColor(Color.BLACK);
    //Log.d("TEST",String.valueOf(dataResponse2.size()));
    for (int i = 0; i< dataResponse2.size(); i++) {
        Integer xi = i+1;
        xAxis2[i] = dataResponse2.get(i).get("deviceBrand") + " " + dataResponse2.get(i).get("deviceModel");
        Double yi = Double.valueOf(dataResponse2.get(i).get("average"));
        DataPoint v = new DataPoint(xi, yi);
        values2[i] = v;
    }
    BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(values2);
    graph2.addSeries(series2);
    TextView labels2 = (TextView)findViewById(R.id.textView5);
    for(int i=0;i<xAxis2.length;i++){
        labels2.setText(labels2.getText()+ String.valueOf(i)+"- "+xAxis2[i]+"\n");
    }
    // styling
    series2.setValueDependentColor(new ValueDependentColor<DataPoint>() {
        @Override
        public int get(DataPoint data) {
            return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
        }
    });
    series2.setSpacing(30);
    series2.setDrawValuesOnTop(true);
    series2.setValuesOnTopColor(Color.BLACK);
}
    //Get specific test restuls
    public void getSpecificResult(String response,int i,ArrayList<HashMap<String, String>> array){
        try {
            JSONObject jsonObj = null;
            jsonObj = new JSONObject(response);
            JSONArray contacts = jsonObj.getJSONArray("challengesResults");
            // looping through All Contacts
                JSONObject c = contacts.getJSONObject(i);
                JSONObject challange = c.getJSONObject("challenge");
                String name = challange.getString("name");
                JSONArray devices = c.getJSONArray("devicesResults");
                for (int h = 0; h < devices.length(); h++) {
                    HashMap<String, String> contact = new HashMap<>();
                    JSONObject device = devices.getJSONObject(h);
                    JSONObject deviceDescr = device.getJSONObject("device");
                    String deviceBrand = deviceDescr.getString("deviceBrand");
                    String deviceModel = deviceDescr.getString("deviceModel");
                    JSONObject deviceRes = device.getJSONObject("result");
                    String min = deviceRes.getString("min");
                    String max = deviceRes.getString("max");
                    String average = deviceRes.getString("average");
                    String standardDeviation = deviceRes.getString("standardDeviation");
                    contact.put("deviceBrand", deviceBrand);
                    contact.put("deviceModel", deviceModel);
                    contact.put("name", name);
                    contact.put("min", min);
                    contact.put("max", max);
                    contact.put("average", average);
                    contact.put("standardDeviation", standardDeviation);
                    array.add(contact);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}