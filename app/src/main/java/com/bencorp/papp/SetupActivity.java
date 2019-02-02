package com.bencorp.papp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SetupActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener,OnClickListener {

    Spinner countrySpinner;
    Spinner stateSpinner;
    TextView countryBtn;
    TextView stateBtn;
    private Boolean LoadedNow = false;
    ProgressBar countryProgress;
    ProgressBar stateProgress;
    HashMap<String ,String> countryCode = new HashMap<String,String>();
    private EditText mPasswordView;
    private EditText services;
    private FloatingActionButton selectImage;
    private ImageView displayImage;
    private Button submitButton;
    private Uri fileUri = null;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;
    NavigationView navigationView;
    final int REQUEST_CODE_GALLERY = 999;
    final int REQUEST_PERMISSION = 888;
    private int[] editViewIds = new int[]{R.id.name,R.id.phone_1,R.id.phone_2,R.id.services,R.id.address};
    private String[] editViewValues;
    private String[] editViewLocation;
    private int[] spinnerViewIds = new int[]{R.id.countries,R.id.state};
    SqliteHandler sqliteHandler;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        // Set up the login form.
        /*SqliteHandler sqliteHandler = new SqliteHandler(this);
        if(sqliteHandler.getUser() > 0){
            finish();
        }*/
        initElements();
        populateCountries();
    }
    public void initElements(){
        services = (EditText) findViewById(R.id.services);
        selectImage = (FloatingActionButton) findViewById(R.id.selectPicture);
        submitButton = (Button) findViewById(R.id.setup);
        displayImage = (ImageView) findViewById(R.id.displayPicture);
        mDatabase = FirebaseDatabase.getInstance().getReference("workers");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        sqliteHandler = new SqliteHandler(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        selectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SetupActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION);
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent,REQUEST_CODE_GALLERY);
                }
            }
        });
        services.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        countrySpinner = (Spinner) findViewById(R.id.countries);
        stateSpinner = (Spinner) findViewById(R.id.state);
        countrySpinner.setOnItemSelectedListener(this);
        countryProgress = (ProgressBar) findViewById(R.id.country_progress);
        stateProgress = (ProgressBar) findViewById(R.id.state_progress);
        countryBtn = (TextView) findViewById(R.id.countryBtn);
        stateBtn = (TextView) findViewById(R.id.stateBtn);
        countryBtn.setOnClickListener(this);
        stateBtn.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        //populateAutoComplete();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION:{
                for (int i = 0; i < grantResults.length;i++){
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this,"You have to accept all permissions to use this application",Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    }
                }

            }

        }

    }
    @Override

    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                displayImage.setImageBitmap(bitmap);
                //style.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {

            }
        }else{

        }
        super.onActivityResult(requestCode,resultCode,data);
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
                                   LoadedNow = true;
                                   ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                           android.R.layout.simple_spinner_item,spinnerArray);
                                   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                   countrySpinner.setAdapter(adapter);
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
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
        String selectedCountry = parent.getItemAtPosition(position).toString();
        //if(!LoadedNow){
        populateState(countryCode.get(selectedCountry));
        //}
        LoadedNow = false;
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent){

    }
    public void populateState(String country){
        final String countryfinal =  country;
        stateProgress.setVisibility(View.VISIBLE);
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
                                stateSpinner.setAdapter(adapter);
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
            case R.id.stateBtn:
                if(countrySpinner.getSelectedItem() == null){
                    Toast.makeText(this,"No country selected",Toast.LENGTH_LONG).show();
                }else{
                    populateState(countryCode.get(countrySpinner.getSelectedItem().toString().trim()));
                }
                break;
            case  R.id.countryBtn:
                populateCountries();
                //Toast.makeText(this,"Phfyd",Toast.LENGTH_LONG);
                break;
            case R.id.setup:
                submitButton.setEnabled(false);
                prepareSendData();
                break;
        }

    }
    public void prepareSendData(){
        boolean allClear = true;
        editViewValues = new String[6];
        editViewLocation = new String[2];
        for(int i = 0; i < editViewIds.length;i++){
            EditText eachField = (EditText) findViewById(editViewIds[i]);
            if(eachField.getText().toString().trim().isEmpty()){
                Toast.makeText(this,"Please all fields are compulsory",Toast.LENGTH_LONG).show();
                submitButton.setEnabled(true);
                allClear = false;
                break;
            }else{
                editViewValues[i] = eachField.getText().toString().trim();
            }

        }

        for(int s =0; s < spinnerViewIds.length;s++){
            Spinner eachSpinner = (Spinner) findViewById(spinnerViewIds[s]);
            if(eachSpinner.getSelectedItem().toString().trim().isEmpty()){
                Toast.makeText(this,"Please all fields are compulsory",Toast.LENGTH_LONG).show();
                submitButton.setEnabled(true);
                allClear = false;
                break;
            }else{
                editViewLocation[s] = eachSpinner.getSelectedItem().toString().trim();
            }
        }
        if(allClear){
            validateImageIntegrity();
        }

    }
    public void validateImageIntegrity(){
        if(fileUri == null){
            Toast.makeText(this,"Please select an image",Toast.LENGTH_LONG).show();
            submitButton.setEnabled(true);
            return;
        }
        if(!checkImageType()){
            Toast.makeText(this,"File is not an image",Toast.LENGTH_LONG).show();
            submitButton.setEnabled(true);
            return;
        }
        if(!fileSize()){
            Toast.makeText(this,"File must not be bigger than 2mb",Toast.LENGTH_LONG).show();
            submitButton.setEnabled(true);
            return;
        }
        //imageUpload(FolderPath.getFileName());
        sendDataToServer();
        //Toast.makeText(this,editViewLocation[0],Toast.LENGTH_LONG).show();
    }

    public Boolean checkImageType(){
        String mimeType = getContentResolver().getType(fileUri);
        if(mimeType.contains("image")){
            return true;
        }
        return false;
    }
    private Boolean fileSize(){
       Cursor returnCursor = getContentResolver().query(fileUri,null,null,null,null);

        int fileSizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        long fileSize = returnCursor.getLong(fileSizeIndex);

        if(fileSize > 2097152){
            return false;
        }else{
            //Toast.makeText(this,String.valueOf(fileSize),Toast.LENGTH_LONG).show();
            return true;
        }


    }
    public void imageUpload(String fileName){
        Bitmap bitmap = imageViewToBitmap(displayImage);
        try{
            OutputStream outputStream = new FileOutputStream(FolderPath.filePath(fileName));
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public Bitmap imageViewToBitmap(ImageView img){
        Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        return bitmap;

    }

    public void sendDataToServer(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating your account...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        EditText phone = (EditText)findViewById(R.id.phone_1);
        final String boom = phone.getText().toString().trim();
        Query query = mDatabase.orderByChild("phone_1").equalTo(boom);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(getApplicationContext(),"Phone number (1) already exists",Toast.LENGTH_LONG).show();
                }else{
                    moveDataToServer();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void moveDataToServer(){
        final String id = mDatabase.push().getKey();
        final String date = new SimpleDateFormat("dd MMMM, yyyy | EEEE", Locale.getDefault()).format(new Date());

        StorageReference ref = storageReference.child(id);
        ref.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        User user = new User(id,editViewValues,editViewLocation,date);
                        mDatabase.child(id).setValue(user);
                        sqliteHandler.addUser(id,editViewValues,editViewLocation,date);
                        progressDialog.dismiss();
                        Toast.makeText(SetupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SetupActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        submitButton.setEnabled(true);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        //progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }
}

