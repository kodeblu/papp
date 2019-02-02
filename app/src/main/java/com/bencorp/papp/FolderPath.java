package com.bencorp.papp;

import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by hp-pc on 1/3/2019.
 */

public  class FolderPath {
    public static final String folder_name = "Papp";
    public static final String sub_folder_name = "profileImage";
    //public static final String sub_folder_name2 = "scanResults";
    private FolderPath(){}

    public static File filePath(String fileName){
        File filepath = Environment.getExternalStoragePublicDirectory(folder_name);
        File files = new File(filepath,sub_folder_name+"/"+fileName);
        return files;
    }

    public static boolean makeDir(){

        String state = Environment.getExternalStorageState();
        File filepath = Environment.getExternalStoragePublicDirectory(folder_name);
        File files = new File(filepath,sub_folder_name);

        if(Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            //Toast.makeText(get,"available",Toast.LENGTH_LONG).show();
            files.mkdirs();
            return true;
            //Toast.makeText(context,"available",Toast.LENGTH_LONG).show();
        }else{
            return false;
        }

    }
    public static boolean checkDir() {
        File filepath = Environment.getExternalStoragePublicDirectory("Papp");
        return filepath.isDirectory();
    }
    public static String getFileName(){
        String date = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
        String rand = UUID.randomUUID().toString().substring(0,8);
        return "papp-"+date+"-"+rand+".jpg";
    }
    /*public static Boolean isSpaceEnough(){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

        long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();

        long megAvailable = (long) bytesAvailable / (1024*1024);

        if(megAvailable < 20){
            return false;
        }
        return true;
    }*/

}
