package com.bencorp.papp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SearchActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener, View.OnClickListener {
    EditText service;
    Spinner country;
    Spinner state;
    EditText street;
    ProgressBar countryProgress;
    ProgressBar stateProgress;
    TextView refresh;
    TextView refreshState;
    Button searchService;
    private DatabaseReference mDatabase;
    HashMap<String ,String> countryCode = new HashMap<String,String>();
    ArrayList<User> arrayList;
    JSONArray packageResult;
    JSONObject packageObject;
    Boolean serverStatus = false;
    LinearLayout errorMsgHolder;
    TextView errorMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initElements();
        country.setOnItemSelectedListener(this);
        populateCountries();

    }


    private void initElements(){
        service = (EditText) findViewById(R.id.keyword);
        street = (EditText) findViewById(R.id.street);
        country = (Spinner) findViewById(R.id.countries);
        state = (Spinner) findViewById(R.id.state);
        refresh = (TextView) findViewById(R.id.countryBtn);
        refresh.setOnClickListener(this);
        refreshState = (TextView) findViewById(R.id.stateBtn);
        refreshState.setOnClickListener(this);

        countryProgress = (ProgressBar) findViewById(R.id.country_progress);
        stateProgress = (ProgressBar) findViewById(R.id.state_progress);

        searchService = (Button) findViewById(R.id.search_service);
        searchService.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        errorMsgHolder = (LinearLayout) findViewById(R.id.error_msg_holder);
        errorMsg = (TextView) findViewById(R.id.error_msg);

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
        String selectedCountry = parent.getItemAtPosition(position).toString();
        //if(!LoadedNow){
        populateState(countryCode.get(selectedCountry));
        //}

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent){

    }
    public void populateCountries(){
        if(countryCode.size() > 0){
            return;
        }
        countryProgress.setVisibility(View.VISIBLE);
        Request request = new Request.Builder()
                .url("https://restcountries.eu/rest/v2/all")
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(),"Failed to load countries, check internet connection",Toast.LENGTH_LONG).show();
                        countryProgress.setVisibility(View.INVISIBLE);

                    }
                });

                //populateCountries();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String countries =  response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //final String countries =  countriesResponse;
                            try {
                                JSONArray countryArr = (JSONArray) new JSONArray(countries);
                                List<String> spinnerArray = new ArrayList<String>();
                                for(int i = 0; i < countryArr.length();i++){
                                    JSONObject countryObj = (JSONObject) countryArr.getJSONObject(i);
                                    String countryName = countryObj.getString("name");
                                    String countryId = countryObj.getString("alpha2Code");

                                    spinnerArray.add(countryName);
                                    countryCode.put(countryName,countryId);
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_spinner_item,spinnerArray);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                country.setAdapter(adapter);
                                countryProgress.setVisibility(View.INVISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });
    }
    public void populateState(String country){
        stateProgress.setVisibility(View.VISIBLE);
        final String countryfinal =  country;
        String urlString =
                "https://battuta.medunes.net/api/region/"+countryfinal+"/all/?key=19b63e713812dfae23c3d6a1cdb824bf";
        Request request = new Request.Builder()
                .url(urlString)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Failed to load cities, check internet connection",Toast.LENGTH_LONG).show();
                        stateProgress.setVisibility(View.INVISIBLE);
                    }
                });
                //Toast.makeText(getApplicationContext(),"Failed to load citie, check internet connection",Toast.LENGTH_LONG).show();

                //populateState(countryfinal);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String countries =  response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //final String countries =  countriesResponse;
                            try {
                                JSONArray stateyArr = (JSONArray) new JSONArray(countries);
                                List<String> spinnerArray = new ArrayList<String>();
                                for(int i = 0; i < stateyArr.length();i++){
                                    JSONObject stateObj = (JSONObject) stateyArr.getJSONObject(i);
                                    String stateName = stateObj.getString("region");
                                    ///String countryId = countryObj.getString("alpha2Code");

                                    spinnerArray.add(stateName);
                                    //countryCode.put(countryName,countryId);
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_spinner_item,spinnerArray);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                state.setAdapter(adapter);
                                stateProgress.setVisibility(View.INVISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.search_service:
                validateSearchParams();
                break;
            case R.id.stateBtn:
                if(country.getSelectedItem() == null){
                    Toast.makeText(this,"No country selected",Toast.LENGTH_LONG).show();
                }else{
                    populateState(countryCode.get(country.getSelectedItem().toString().trim()));
                }
                break;
            case R.id.countryBtn:
                populateCountries();
                break;
                //Toast.makeText(this,"get c",Toast.LENGTH_LONG).show();
        }
    }
    private void validateSearchParams(){
        String serviceValue = service.getText().toString().trim();
        String streetValue = street.getText().toString().trim();
        String countryValue = country.getSelectedItem().toString().trim();
        String stateValue = state.getSelectedItem().toString().trim();

        if(serviceValue.isEmpty() || countryValue.isEmpty() || stateValue.isEmpty()){
            Toast.makeText(this,
                    "Please a service, country and state is needed to do search",
                    Toast.LENGTH_LONG).show();
            return;
        }
        doSearch(serviceValue,streetValue,countryValue,stateValue);

    }
    private void doSearch(final String service, String streetValue, String country, String state){
        errorMsgHolder.setVisibility(View.INVISIBLE);

        Query query = mDatabase.child("workers").orderByChild("country").equalTo(country);
        final String clientState = state;
        final String clientService = service.toLowerCase();
        final String clientStreet= streetValue;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Searching for service please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if(dataSnapshot.exists()){
                    packageResult = new JSONArray();

                    for(DataSnapshot workers: dataSnapshot.getChildren()){
                        String Serverstate = workers.child("state").getValue().toString().trim();
                        String Serverservice = workers.child("services").getValue().toString().trim().toLowerCase();
                        String Serverstreet = workers.child("address").getValue().toString().trim();
                        String Serverid = workers.child("id").getValue().toString().trim();
                        String Servername = workers.child("name").getValue().toString().trim();
                        String Serverphone1 = workers.child("phone_1").getValue().toString().trim();
                        String Serverphone2 = workers.child("phone_2").getValue().toString().trim();
                        packageObject = new JSONObject();
                        if(clientStreet.isEmpty()){
                            if(Serverstate.equals(clientState) && Serverservice.contains(clientService)){
                                errorMsgHolder.setVisibility(View.INVISIBLE);
                                try {
                                    packageObject.put("name",Servername);
                                    packageObject.put("id",Serverid);
                                    packageObject.put("phone_1",Serverphone1);
                                    packageObject.put("phone_2",Serverphone2);
                                    packageObject.put("street",Serverstreet);
                                    packageObject.put("service",Serverservice);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //User user = new User(Servername,Serverid,Serverphone1,Serverphone2,Serverstreet);
                                packageResult.put(packageObject);

                                Intent intent = new Intent(SearchActivity.this,FindActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.putExtra("results",packageResult.toString());
                                startActivity(intent);
                            }else {
                                errorMsgHolder.setVisibility(View.VISIBLE);
                                errorMsg.setText("No nearby service found, try including street");
                                revert();
                            }
                        }else{
                            if(Serverstate.equals(clientState) && Serverservice.contains(clientService) &&
                                    Serverstreet.toLowerCase().contains(clientStreet)){
                                errorMsgHolder.setVisibility(View.INVISIBLE);

                                try {
                                    packageObject.put("name",Servername);
                                    packageObject.put("id",Serverid);
                                    packageObject.put("phone_1",Serverphone1);
                                    packageObject.put("phone_2",Serverphone2);
                                    packageObject.put("street",Serverstreet);
                                    packageObject.put("service",Serverservice);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //User user = new User(Servername,Serverid,Serverphone1,Serverphone2,Serverstreet);
                                packageResult.put(packageObject);
                                    Intent intent = new Intent(SearchActivity.this,FindActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.putExtra("results",packageResult.toString());
                                    startActivity(intent);
                            }else {
                                errorMsgHolder.setVisibility(View.VISIBLE);
                                errorMsg.setText("No nearby service found, re-check street");
                                revert();
                            }
                        }


                    }
                }else {
                    //Toast.makeText(getApplicationContext(),"Nothing found333",Toast.LENGTH_LONG).show();
                    errorMsgHolder.setVisibility(View.VISIBLE);
                    errorMsg.setText("No nearby service found");
                    revert();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }
    private void revert(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                errorMsgHolder.setVisibility(View.INVISIBLE);
            }
        },3000);
    }
}
