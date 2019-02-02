package com.bencorp.papp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LockActivity extends AppCompatActivity {
    Button saveBtn;
    private ArrayList<String> permissions;
    private static final int REQUEST_PERMISSION = 999;
    int welcomeMsgCount = 0;
    int msgCount = 0;
    TextView slideMsgTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock);
        if(FolderPath.checkDir()){
            startActivity(new Intent(LockActivity.this,DashboardActivity.class));
            finish();
        }
        saveBtn = (Button) findViewById(R.id.save);
        if(shouldAskPermission()){
            requestAllPermissions();
        }else{
            FolderPath.makeDir();
            setBtnClick();
        }


    }
    public Boolean shouldAskPermission(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public void setBtnClick() {
        welcomeMsgCount = welcomeMsg().length;
        slideMsgTxt = (TextView) findViewById(R.id.slideMsg);
        slideMsg();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtn = (Button) findViewById(R.id.save);
                    //Toast.makeText(LockActivity.this, "Pressing", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LockActivity.this,DashboardActivity.class));
                    finish();

                }
        });
    }

    public void requestAllPermissions(){
        permissions = new ArrayList<>();
        if(ActivityCompat.checkSelfPermission(LockActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.CALL_PHONE);
        }
        if(ActivityCompat.checkSelfPermission(LockActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(LockActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(LockActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.INTERNET);
        }
        if(permissions.size() > 0){
            ActivityCompat.requestPermissions(LockActivity.this,permissions.toArray(new String[permissions.size()]),REQUEST_PERMISSION);
        }else{
            FolderPath.makeDir();
            setBtnClick();
        }
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
        FolderPath.makeDir();
        setBtnClick();
    }

    public String[] welcomeMsg(){
        return new String[]{"Hello champ, welcome to Papp",
                "Connecting you to your solutions",
                "Solutions at your door step",
                "Finding help just got simpler"};
    }
    public void slideMsg(){
        final Handler handler =  new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(msgCount == welcomeMsgCount){
                    msgCount = 0;
                }
                //Toast.makeText(getApplicationContext(),welcomeMsg()[msgCount],Toast.LENGTH_SHORT).show();
                slideMsgTxt.setText(welcomeMsg()[msgCount]);
                handler.postDelayed(this,3000);
                msgCount++;
            }
        };
        handler.post(runnable);
    }

}
