package com.bencorp.papp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

/**
 * Created by hp-pc on 1/18/2019.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    Context context;
    JSONArray resultList;
    Activity activity;

    RecyclerAdapter(JSONArray resultList, Context context, Activity activity) {
        this.resultList = resultList;
        this.context = context;
        this.activity = activity;
    }
    private static final int REQUEST_CALL_PERMISSION = 999;
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_results, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        try {
            final JSONObject eachPerson = resultList.getJSONObject(position);
            holder.name.setText("Name: " + eachPerson.getString("name"));
            holder.address.setText("Address: " + eachPerson.getString("street"));
            holder.service.setText("I provide: " + eachPerson.getString("service"));
            final String telephone1 = eachPerson.getString("phone_1");
            final String telephone2 = eachPerson.getString("phone_2");
            final String reference = eachPerson.getString("id");
            final String name = eachPerson.getString("name");
            final String services = eachPerson.getString("service");
            final String address = eachPerson.getString("street");
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + telephone1));
                    if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity
                                ,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_PERMISSION);
                        return;
                    }
                    v.getContext().startActivity(intent);
                }
            });
            holder.call2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + telephone2));
                    if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity
                                ,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_PERMISSION);
                        return;
                    }
                    v.getContext().startActivity(intent);
                }
            });
            holder.infoCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert(reference,telephone1,telephone2,services,address,name);
                    //Toast.makeText(v.getContext(),"No applictaion found for that",Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void alert(String id,String phone1, String phone2, String services, String street,String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View inflater = LayoutInflater.from(context).inflate(R.layout.activity_card, null);

        TextView p_name = (TextView) inflater.findViewById(R.id.provider_name);
        p_name.setText(name);
        TextView p_phone1 = (TextView) inflater.findViewById(R.id.provider_phone1);
        p_phone1.setText(phone1);
        TextView p_phone2 = (TextView) inflater.findViewById(R.id.provider_phone2);
        p_phone2.setText(phone2);
        TextView p_address = (TextView) inflater.findViewById(R.id.provider_address);
        p_address.setText(street);
        TextView p_services = (TextView) inflater.findViewById(R.id.provider_services);
        p_services.setText(services);

        final ImageView picture = (ImageView) inflater.findViewById(R.id.helper);
        builder.setView(inflater);
        final AlertDialog dialog = builder.create();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(id);
        dialog.show();
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.angular)
                        .into(picture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return resultList.length();
    }



    public  static  class RecyclerViewHolder extends  RecyclerView.ViewHolder{
        TextView name,address,service;
        ImageView call,call2;
        LinearLayout infoCard;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.result_name);
            address = (TextView) itemView.findViewById(R.id.result_address);
            service = (TextView) itemView.findViewById(R.id.result_service);
            call = (ImageView) itemView.findViewById(R.id.call);
            call2 = (ImageView) itemView.findViewById(R.id.call2);
            infoCard = (LinearLayout) itemView.findViewById(R.id.info_card);
        }

    }
}
