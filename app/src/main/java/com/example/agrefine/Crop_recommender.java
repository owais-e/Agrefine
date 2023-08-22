package com.example.agrefine;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Crop_recommender extends AppCompatActivity {
    EditText nitrogen,potassium,phosphorus,temperature,ph,rainfall;
    Button suggest,crophelpOKbtn,cropOKBtn;
    ImageView cropHelpBtn;
    TextView resultTxtView;
    LinearLayout crophelpLayout,cropResultLayout;
    String url = "https://koshish-gusmang525.b4a.run/crop";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_recommender);

        nitrogen = findViewById(R.id.nitrogen);
        potassium = findViewById(R.id.potassium);
        phosphorus = findViewById(R.id.phosphorus);
        temperature = findViewById(R.id.temperature);
        ph = findViewById(R.id.ph);
        rainfall = findViewById(R.id.rainfall);

        cropHelpBtn = findViewById(R.id.cropHelpBtn);
        crophelpOKbtn = findViewById(R.id.crophelpOKBtn);
        crophelpLayout = findViewById(R.id.crophelpLayout);

        cropResultLayout = findViewById(R.id.ResultLayout);
        cropOKBtn = findViewById(R.id.resultOKBtn);

        suggest = findViewById(R.id.cropBtn);
        resultTxtView = findViewById(R.id.resultTxtView);


        cropOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropResultLayout.setVisibility(View.GONE);
                nitrogen.setText(" ");
                potassium.setText(" ");
                phosphorus.setText(" ");
                temperature.setText(" ");
                ph.setText(" ");
                rainfall.setText(" ");
            }
        });

        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hit the API
                StringRequest stringRequest  = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {


                                    JSONObject jsonObject= new JSONObject(response);
                                    String result = jsonObject.getString("suggested_crop");

                                    resultTxtView.setText("According to the parameters you provided, Agrefine ML model suggests you to grow  '"+result+"'  in your soil.");


                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String,String>();
                        params.put("nitrogen",nitrogen.getText().toString());
                        params.put("phosphorus",phosphorus.getText().toString());
                        params.put("potassium",potassium.getText().toString());
                        params.put("temperature",temperature.getText().toString());
                        params.put("ph",ph.getText().toString());
                        params.put("rainfall",rainfall.getText().toString());
                        return params;

                    }
                };
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(stringRequest);

                cropResultLayout.setVisibility(View.VISIBLE);



            }
        });


        cropHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crophelpLayout.setVisibility(View.VISIBLE);
            }
        });


        crophelpOKbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crophelpLayout.setVisibility(View.GONE);
            }
        });




    }
}