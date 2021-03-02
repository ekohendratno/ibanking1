package id.kopas.berkarya.said.ibanking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.kopas.berkarya.said.ibanking.adapters.AdapterBranch;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.CacheRequest;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.fun.TextViewCustom;
import id.kopas.berkarya.said.ibanking.models.Branch;

public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivity";

    String akun, password;

    static RelativeLayout progressBar;

    static SharedPreferences sharedpreferences;

    static AppCompatEditText editKeyBranch;
    static AppCompatEditText editKeyBranchLinkApi;
    static AutoCompleteTextView editBranch;

    static DataHelper dataHelper;
    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        context = LoginActivity.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        requestStoragePermission();

        context = LoginActivity.this;

        dataHelper = new DataHelper(getApplicationContext());

        /**
         * Loading
         */
        progressBar = (RelativeLayout) findViewById(R.id.progressBar);

        editKeyBranch = findViewById(R.id.editKeyBranch);
        editKeyBranchLinkApi = findViewById(R.id.editKeyBranchLinkApi);
        editBranch = findViewById(R.id.editBranch);

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

            if (!TextUtils.isEmpty(akun) && !TextUtils.isEmpty(password)) {
                String link_api = editKeyBranchLinkApi.getText().toString();
                String branch = editKeyBranch.getText().toString();

                if (!TextUtils.isEmpty(branch) && !TextUtils.isEmpty(link_api) && Patterns.WEB_URL.matcher(link_api ).matches()) {
                    cekDataBranchIbankingAsyncTask(link_api, branch, akun,password);
                }else{
                    Toast.makeText(v.getContext(), "Akun atau Sandi belum diisi!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(v.getContext(), "Akun atau Sandi belum diisi!", Toast.LENGTH_LONG).show();
            }
        });

        actSignUp.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);

        });

        /**
         * INIT
         */
        initBranchAsyncTask();
    }


    /**
     * CEK DATA IBANK
     */
    @SuppressLint("NewApi")
    private void cekDataBranchIbankingAsyncTask(String branchLink, String branch, String norek, String pin) {
        if(!progressBar.isShown()) progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        CacheRequest serverRequest = new CacheRequest(Request.Method.GET,branchLink +"banking.php?"+
                "ib=validasi_login_ibanking"+
                "&branch="+branch+
                "&norek="+norek+
                "&pin_ib="+pin,
                req -> {
                    if(progressBar.isShown()) progressBar.setVisibility(View.GONE);
                    JSONObject response_json2 = null;
                    try {
                        final String jsonString = new String(req.data, HttpHeaderParser.parseCharset(req.headers));

                        response_json2 = new JSONObject(jsonString);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        if(jsonObject2.getString("status_aktif_rek").equalsIgnoreCase("A")){
                            //dibuat jadi error saja nanti

                            //For apps targeting SDK higher than Build.VERSION_CODES.O_MR1 this field is set to UNKNOWN.
                            final String security_phone = dataHelper.getIdUnique();
                            final String phone_model = Build.MODEL;
                            //String serial = Build.SERIAL;
                            //String android_id = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);

                            //String myKey = serial +android_id;

                            String gpsCoords = dataHelper.getGPSCoordinates(0, false, "");






                            //if(jsonObject2.getString("security_phone").equalsIgnoreCase(myKey)){
                            postStatusLoginAsyncTask(branchLink,branch,norek,gpsCoords,phone_model,pin);
                            /**}else{
                             new AlertDialog.Builder(context)
                             .setTitle("Info")
                             .setMessage("Maaf, Anda mendaftar i-banking bukan dengan device ini!")
                             .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {
                             // Continue with delete operation
                             dialog.dismiss();

                             }
                             })
                             .show();

                             }*/

                        }else{
                            new AlertDialog.Builder(context)
                                    .setTitle("Info")
                                    .setMessage("Rekening Anda Diblokir!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            dialog.dismiss();

                                        }
                                    })
                                    .show();
                        }
                    } catch (JSONException e) {

                        new AlertDialog.Builder(context)
                                .setTitle("Perhatian")
                                .setMessage("Akun dan Password salah!")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            if(progressBar.isShown())  progressBar.setVisibility(View.GONE);
            Log.w(DataHelper.getTAG(), "JSONException @ cekDataBranchIbankingAsyncTask()\n" + error.getMessage());
        }
        );


        serverRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(serverRequest);
    }

    /**
     * UPDATE STATUS LOGIN
     */
    @SuppressLint("NewApi")
    private void postStatusLoginAsyncTask(String branchLink, String branch, String norek,String lokasi,String merek_hp,String pinib) {
        if(!progressBar.isShown()) progressBar.setVisibility(View.VISIBLE);

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET, branchLink +"banking.php?"+
                "ib=update_status_login"+
                "&branch="+branch+
                "&norek="+norek,

                response -> {
                    if(progressBar.isShown()) progressBar.setVisibility(View.GONE);

                    try {
                        JSONObject response_json2 = new JSONObject(response);
                        JSONObject jsonObject2 = response_json2.getJSONObject("status");


                        if(jsonObject2.getString("pesan").equalsIgnoreCase("SUKSES")){

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("link_api",branchLink);
                            editor.putString("branch",branch);
                            editor.putString("norek",norek);
                            editor.putString("pinib",pinib);
                            editor.apply();

                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(context,"Berhasil masuk",Toast.LENGTH_SHORT).show();

                        }else{
                            new AlertDialog.Builder(context)
                                    .setTitle("Perhatian")
                                    .setMessage("Gagal masuk, coba kembali!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }

                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }

                }, error -> {
                    if(progressBar.isShown()) progressBar.setVisibility(View.GONE);
                    Log.w(DataHelper.getTAG(), "JSONException @ postStatusLoginAsyncTask()\n" + error.getMessage());
                }
        );


        StringRequest serverRequest2 = new StringRequest(Request.Method.GET, branchLink +"banking.php?"+
                "ib=insert_history_login_banking"+
                "&norek="+norek+
                "&lokasi="+lokasi+
                "&merek_hp="+merek_hp,
                response -> {}, error -> {Log.w(DataHelper.getTAG(), "JSONException @ postStatusLoginAsyncTask()\n" + error.getMessage());}
        );



        serverRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        serverRequest2.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(serverRequest);
        requestQueue.add(serverRequest2);
    }



    /**
     * INIT BRANCH
     */

    @SuppressLint("NewApi")
    private static void initBranchAsyncTask() {
        if(!progressBar.isShown()) progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        CacheRequest serverRequest = new CacheRequest(Request.Method.GET,DataHelper.getAlamatServer() + "/informasi_bank",
                req -> {
                    if(progressBar.isShown()) progressBar.setVisibility(View.GONE);
                    try {
                        final String jsonString = new String(req.data, HttpHeaderParser.parseCharset(req.headers));
                        if (!req.equals("")) {
                            JSONArray json = new JSONArray(jsonString);

                            ArrayList<Branch> branchArrayList = new ArrayList<>();

                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                if(jsonObject.getString("status_view").equalsIgnoreCase("I")){
                                    //items.add( jsonObject.getString("nama") );
                                    branchArrayList.add(new Branch(
                                            jsonObject.getString("branch"),
                                            jsonObject.getString("nama"),
                                            jsonObject.getString("link_logo_bank"),
                                            jsonObject.getString("status_view"),
                                            jsonObject.getString("status_izin"),
                                            jsonObject.getString("link_api")
                                    ));
                                }
                            }

                            if(branchArrayList.size() > 0){

                                ArrayAdapter arrayAdapter = new AdapterBranch(context, R.layout.item_branch, branchArrayList);
                                editBranch.setAdapter(arrayAdapter);
                                editBranch.setOnItemClickListener((parent, view, position, id) -> {
                                    String selected = branchArrayList.get(position).nama;
                                    editBranch.setText(selected,false);
                                    editKeyBranch.setText(branchArrayList.get(position).branch);
                                    editKeyBranchLinkApi.setText(branchArrayList.get(position).link_api);

                                });

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    if(progressBar.isShown()) progressBar.setVisibility(View.GONE);
                    Log.w(DataHelper.getTAG(), "JSONException @ initBranchAsyncTask()\n" + error.getMessage());
                }
        );


        serverRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(serverRequest);
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
        editor.remove("norek");
        editor.apply();
    }

    private void updateUI() {
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


    public void onBackPressed() {
        super.onBackPressed();
    }
}