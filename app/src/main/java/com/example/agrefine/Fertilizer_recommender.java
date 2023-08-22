package com.example.agrefine;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fertilizer_recommender extends AppCompatActivity {
    EditText nitrogen,potassium,phosphorus;

    AutoCompleteTextView cropName;
    ArrayList<String> cropNamesArray = new ArrayList<>();
    Button fertSuggest,helpOKbtn, FertOkBtn;
    ImageView fertHelpBtn;
    TextView fertResult;
    LinearLayout helpLayout,fertResultLayout;
    String url = "https://koshish-gusmang525.b4a.run/fertilizer";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fertilizer_recommender);

        nitrogen = findViewById(R.id.fertNitrogen);
        potassium = findViewById(R.id.fertPotassium);
        phosphorus = findViewById(R.id.fertPhosphorus);
        cropName = findViewById(R.id.fertCrop);

        fertSuggest =findViewById(R.id.fetrilizerBtn);
        helpOKbtn = findViewById(R.id.helpOKBtn);
        fertHelpBtn = findViewById(R.id.fertHelpBtn);
        helpLayout = findViewById(R.id.helpLayout);

        fertResultLayout = findViewById(R.id.fertResultLayout);
        FertOkBtn = findViewById(R.id.fertOKBtn);
        fertResult=findViewById(R.id.fertResultTxtView);

        FertOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fertResultLayout.setVisibility(View.GONE);
                nitrogen.setText(" ");
                potassium.setText(" ");
                phosphorus.setText(" ");
                cropName.setText(" ");
            }
        });

        fertSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringRequest fertStringRequest  = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {


                                    JSONObject jsonObject= new JSONObject(response);
                                    String result = (jsonObject.getString("suggested_fertilizer")).toString();

                                    if(result.equals("NLow") ){
                                        fertResult.setText(getResources().getString(R.string.NLOW));
                                    }else if(result.equals("PLow")){
                                        fertResult.setText(getResources().getString(R.string.PLOW));
                                    }else if(result.equals("KLow")){
                                        fertResult.setText(getResources().getString(R.string.KLOW));
                                    }else if(result.equals("NHigh")){
                                        fertResult.setText(getResources().getString(R.string.NHIGH));
                                    }else if(result.equals("PHigh")){
                                        fertResult.setText(getResources().getString(R.string.PHIGH));
                                    }else if(result.equals("KHigh")){
                                        fertResult.setText(getResources().getString(R.string.KHIGH));
                                    }else if(result.equals("all good")){
                                        fertResult.setText(getResources().getString(R.string.NLOW));
                                    }else{
                                        fertResult.setText("Failed to get recommendation");
                                    }

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
                        params.put("fertNitrogen",nitrogen.getText().toString());
                        params.put("fertPhosphorus",phosphorus.getText().toString());
                        params.put("fertPotassium",potassium.getText().toString());
                        params.put("cropName",cropName.getText().toString());
                        return params;

                    }
                };
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(fertStringRequest);

                fertResultLayout.setVisibility(View.VISIBLE);



            }
        });

        fertHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpLayout.setVisibility(View.VISIBLE);
            }
        });
        helpOKbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpLayout.setVisibility(View.GONE);
            }
        });


        cropNamesArray.add("rice");
        cropNamesArray.add("maize");
        cropNamesArray.add("chickpea");
        cropNamesArray.add("kidneybeans");
        cropNamesArray.add("pigeonpeas");
        cropNamesArray.add("mothbeans");
        cropNamesArray.add("mungbean");
        cropNamesArray.add("blackgram");
        cropNamesArray.add("lentil");
        cropNamesArray.add("pomegranate");
        cropNamesArray.add("banana");
        cropNamesArray.add("mango");
        cropNamesArray.add("grapes");
        cropNamesArray.add("watermelon");
        cropNamesArray.add("muskmelon");
        cropNamesArray.add("apple");
        cropNamesArray.add("orange");
        cropNamesArray.add("papaya");
        cropNamesArray.add("coconut");
        cropNamesArray.add("cotton");
        cropNamesArray.add("jute");
        cropNamesArray.add("coffee");


        ArrayAdapter<String> cropNameAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,cropNamesArray);
        cropName.setAdapter(cropNameAdapter);
        cropName.setThreshold(1);

    }

}
