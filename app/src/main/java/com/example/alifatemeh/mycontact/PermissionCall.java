package com.example.alifatemeh.mycontact;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class PermissionCall extends Activity {

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    private Context context;

    public PermissionCall(Context context) {
        this.context = context;
    }
    public void checkWriteExternalStoragePermission() {
        final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 1;
        ContextCompat.checkSelfPermission(context,      Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
