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


    static RelativeLayout progressBar;

    static SharedPreferences sharedpreferences;

    static AppCompatEditText editKeyBranch;
    static AppCompatEditText editKeyBranchLinkApi;
    static AppCompatEditText editKeyBranchLinkApiLogo;
    static AutoCompleteTextView editBranch;

    static DataHelper dataHelper;
    static Context context;

    String akun, password;
    String link_api, link_api_logo, branch, branch_nama;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        context = LoginActivity.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        context = LoginActivity.this;

        dataHelper = new DataHelper(getApplicationContext());

        /**
         * Loading
         */
        progressBar = (RelativeLayout) findViewById(R.id.progressBar);

        editKeyBranch = findViewById(R.id.editKeyBranch);
        editKeyBranchLinkApi = findViewById(R.id.editKeyBranchLinkApi);
        editKeyBranchLinkApiLogo = findViewById(R.id.editKeyBranchLinkApiLogo);
        editBranch = findViewById(R.id.editBranch);

        final TextInputEditText editAkun = findViewById(R.id.editAkun);
        final TextInputEditText editPassword = findViewById(R.id.editPassword);

        final MaterialButton actSignIn = findViewById(R.id.actSignIn);
        final TextViewCustom actLostPassword = findViewById(R.id.actLostPassword);

        //txtAkun.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        //editPassword.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        actSignIn.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_bold));

        actSignIn.setOnClickListener(v -> {
            akun = editAkun.getText().toString();
            password = editPassword.getText().toString();

            if (!TextUtils.isEmpty(akun) && !TextUtils.isEmpty(password)) {
                link_api = editKeyBranchLinkApi.getText().toString();
                link_api_logo = editKeyBranchLinkApiLogo.getText().toString();
                branch = editKeyBranch.getText().toString();
                branch_nama = editBranch.getText().toString();

                if (!TextUtils.isEmpty(branch) && !TextUtils.isEmpty(link_api) && Patterns.WEB_URL.matcher(link_api ).matches()) {
                    cekDataBranchIbankingAsyncTask();
                }else{
                    Toast.makeText(v.getContext(), "Akun atau Sandi belum diisi!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(v.getContext(), "Akun atau Sandi belum diisi!", Toast.LENGTH_LONG).show();
            }
        });

        actLostPassword.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this, LostPasswordActivity.class);
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
    private void cekDataBranchIbankingAsyncTask() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=validasi_login_ibanking"+
                "&branch="+branch+
                "&norek="+akun+
                "&pin_ib="+password,
                req -> {
                    progressBar.setVisibility(View.GONE);
                    JSONObject response_json2 = null;
                    try {

                        response_json2 = new JSONObject(req);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        if(jsonObject2.getString("status_aktif_rek").equalsIgnoreCase("A")){
                            //dibuat jadi error saja nanti

                            //For apps targeting SDK higher than Build.VERSION_CODES.O_MR1 this field is set to UNKNOWN.
                            //final String security_phone = dataHelper.getIdUnique();
                            final String phone_model = Build.MODEL;
                            //String serial = Build.SERIAL;
                            //String android_id = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);

                            //String myKey = serial +android_id;

                            String gpsCoords = dataHelper.getGPSCoordinates(0, false, "");

                            String nama = jsonObject2.getString("nama");



                            //if(jsonObject2.getString("security_phone").equalsIgnoreCase(myKey)){
                            postStatusLoginAsyncTask(nama,gpsCoords,phone_model);
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
                    }
                }, error -> {
             progressBar.setVisibility(View.GONE);
            Log.w(DataHelper.getTAG(), "JSONException @ cekDataBranchIbankingAsyncTask()\n" + error.getMessage());
        }
        );


        serverRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(serverRequest);
    }

    int ex = 1;

    /**
     * UPDATE STATUS LOGIN
     */
    @SuppressLint("NewApi")
    private void postStatusLoginAsyncTask(String nama,String lokasi,String merek_hp) {
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET, link_api +"banking.php?"+
                "ib=update_status_login"+
                "&branch="+branch+
                "&norek="+akun,

                response -> {
                    progressBar.setVisibility(View.GONE);

                    try {
                        JSONObject response_json2 = new JSONObject(response);
                        JSONObject jsonObject2 = response_json2.getJSONObject("status");


                        if(jsonObject2.getString("pesan").equalsIgnoreCase("SUKSES")){

                            if(ex == 1){

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("link_api",link_api);
                                editor.putString("link_logo_bank",link_api_logo);
                                editor.putString("branch",branch);
                                editor.putString("branch_nama",branch_nama);
                                editor.putString("nama",nama);
                                editor.putString("norek",akun);
                                editor.putString("pinib",password);
                                editor.apply();

                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                                finish();

                                Toast.makeText(context,"Berhasil masuk",Toast.LENGTH_SHORT).show();

                            }

                            ex++;

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
                    progressBar.setVisibility(View.GONE);
                    Log.w(DataHelper.getTAG(), "JSONException @ postStatusLoginAsyncTask()\n" + error.getMessage());
                }
        );


        StringRequest serverRequest2 = new StringRequest(Request.Method.GET, link_api +"banking.php?"+
                "ib=insert_history_login_banking"+
                "&norek="+akun+
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
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        CacheRequest serverRequest = new CacheRequest(Request.Method.GET,DataHelper.getAlamatServer() + "/informasi_bank",
                req -> {
                    progressBar.setVisibility(View.GONE);
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
                    progressBar.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, OptionalActivity.class);
        startActivity(intent);
        finish();
    }
}