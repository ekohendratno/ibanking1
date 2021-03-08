package id.kopas.berkarya.said.ibanking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import id.kopas.berkarya.said.ibanking.adapters.AdapterBranch;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.CacheRequest;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.fun.TextViewCustom;
import id.kopas.berkarya.said.ibanking.models.Branch;

public class OptionalActivity extends AppCompatActivity {
    String TAG = "LoginActivity";

    static SharedPreferences sharedpreferences;

    static DataHelper dataHelper;
    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);


        context = OptionalActivity.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        requestStoragePermission();

        context = OptionalActivity.this;

        dataHelper = new DataHelper(getApplicationContext());


        final MaterialButton actSignIn = findViewById(R.id.actSignIn);
        final TextViewCustom actSignUp = findViewById(R.id.actSignUp);

        //txtAkun.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        //editPassword.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        actSignIn.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_bold));

        actSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(OptionalActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        actSignUp.setOnClickListener(v -> {

            Intent intent = new Intent(OptionalActivity.this, RegisterActivity.class);
            startActivity(intent);

        });
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


        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("link_api");
        editor.remove("branch");
        editor.remove("branch_nama");
        editor.remove("norek");
        editor.remove("nama");
        editor.remove("pinib");
        editor.apply();
    }

    @Override
    public void onBackPressed() {

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);

    }

    private void requestStoragePermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Log.i("izin", "Semua izin telah disetujui!");


                //TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                //String telephonyManagerSimSerialNumber = telephonyManager.getSimSerialNumber();
                //String sim = telephonyManager.getLine1Number();

                /**
                TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                String number = phoneMgr.getLine1Number();
                if(TextUtils.isEmpty(number)){
                    number = phoneMgr.getDeviceId();
                }
                Log.e("no_hp_detect", number);*/

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


}