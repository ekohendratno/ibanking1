package id.kopas.berkarya.said.ibanking;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

import id.kopas.berkarya.said.ibanking.fun.TextViewCustom;

public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivity";

    String akun, password;

    ProgressDialog progressDialog;

    static SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        requestStoragePermission();

        /**
         * Loading
         */

        final TextInputEditText editAkun = findViewById(R.id.editAkun);
        final TextInputEditText editPassword = findViewById(R.id.editPassword);

        final MaterialButton actSignIn = findViewById(R.id.actSignIn);
        final TextViewCustom actSignUp = findViewById(R.id.actSignUp);

        //txtAkun.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        //editPassword.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        actSignIn.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_bold));

        actSignIn.setOnClickListener(v -> {
            akun = editAkun.getText().toString();
            password = editPassword.getText().toString();

            if(TextUtils.isEmpty(akun) || TextUtils.isEmpty(password)){
                Toast.makeText(v.getContext(),"Akun atau Sandi belum diisi!", Toast.LENGTH_LONG).show();
            }else{

                getSignIn();
            }
        });

        actSignUp.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

            startActivity(intent);
        });


    }

    private void getSignIn(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        startActivity(intent);
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onStart() {
        super.onStart();
    }

    private void updateUI() {
    }



    private void requestStoragePermission() {
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Log.i("izin", "Semua izin telah disetujui!");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                showSettingsDialog();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Membutuhkan Izin");
        builder.setMessage("Beberapa fitur diperlukan untuk aplikasi ini. Kamu Setujui di pengaturan.");
        builder.setPositiveButton("KE PENGATURAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    @Override
    public void onBackPressed() {


    }
}