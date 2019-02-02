package com.bencorp.papp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CardActivity extends AppCompatActivity {
    ImageView picture;
    JSONObject providerObject;
    String reference;
    TextView p_name,p_phone1,p_phone2,p_address,p_services;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        loadDetails();
    }
    private void loadDetails(){
        Intent intent = getIntent();
        String provider = intent.getStringExtra("provider");
        try {
            providerObject = new JSONObject(provider);
            reference = providerObject.getString("id");
            String name = providerObject.getString("name");
            p_name.setText("Name: "+name);
            String address = providerObject.getString("street");
            p_address.setText("Stree address: "+address);
            String service = providerObject.getString("service");
            p_services.setText(service);
            String phone_1 = providerObject.getString("phone_1");
            p_phone1.setText("Phone number (1): "+phone_1);
            String phone_2 = providerObject.getString("phone_2");
            p_phone2.setText("Phone number (2): "+phone_2);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(reference);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Toast.makeText(getApplicationContext(),"found Url",Toast.LENGTH_LONG).show();
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .placeholder(R.drawable.angular)
                            .into(picture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
