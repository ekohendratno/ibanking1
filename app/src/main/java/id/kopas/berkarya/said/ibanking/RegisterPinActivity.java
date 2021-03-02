package id.kopas.berkarya.said.ibanking;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.fun.DeviceUuidFactory;

public class RegisterPinActivity extends AppCompatActivity {
    String TAG = "RegisterPinActivity";

    static SharedPreferences sharedpreferences;

    static RelativeLayout progressBar;
    static DataHelper dataHelper;
    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerpin);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_down);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        dataHelper = new DataHelper(getApplicationContext());

        context = RegisterPinActivity.this;

        Intent intentData = getIntent();

        /**
         * Loading
         */
        progressBar = (RelativeLayout) findViewById(R.id.progressBar);

        final TextInputEditText editPin1 = findViewById(R.id.editPin1);
        final TextInputEditText editPin2 = findViewById(R.id.editPin2);

        final MaterialButton actDaftar = findViewById(R.id.actDaftar);
        //editPin1.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        //editPin2.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        actDaftar.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_bold));

        editPin1.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (editPin1.getText().toString().trim().length() < 6)
                    editPin1.setError("Failed");
                else
                    editPin1.setError(null);
            }
        });
        editPin2.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (editPin2.getText().toString().trim().length() < 6)
                    editPin2.setError("Failed");
                else
                    editPin2.setError(null);
            }
        });

        actDaftar.setOnClickListener(v -> {

            String txtPin1 = editPin1.getText().toString();
            String txtPin2 = editPin2.getText().toString();

            if(TextUtils.isEmpty(txtPin1) || TextUtils.isEmpty(txtPin2)){
                Toast.makeText(v.getContext(),"Pin kosong!",Toast.LENGTH_SHORT).show();
            }else{
                if(!txtPin1.equals(txtPin2)){
                    Toast.makeText(v.getContext(),"Pin tidak sama!",Toast.LENGTH_SHORT).show();
                }else if(editPin1.getText().toString().trim().length() < 6 || editPin2.getText().toString().trim().length() < 6){
                    Toast.makeText(v.getContext(),"Pin kurang dari 6 karakter!",Toast.LENGTH_SHORT).show();
                }else{
                    /**
                     * SECURITY PHONE
                     */


                    final String security_phone = dataHelper.getIdUnique();
                    final String phone_model = Build.MODEL;
                    final String no_hp_detect = dataHelper.getPhoneNumber();

                    Log.e("security_phone",security_phone);
                    Log.e("phone_model",phone_model);
                    Log.e("no_hp_detect",no_hp_detect);

                    postDataPinIBankingAsyncTask(intentData, txtPin1);
                    /**
                    final String security_phone = Build.SERIAL;
                    final String phone_model = Build.MODEL;
                    final String no_hp_detect = dataHelper.getPhoneNumber();

                    JSONObject response_json2 = null;
                    try{

                        String response_body2 = dataHelper.getServer(link_api +"banking.php?"+
                                "ib=insert_new_user_banking"+
                                "&branch="+branch+
                                "&norek="+norek+
                                "&pin_ib="+txtPin1+
                                "&security_phone="+security_phone+
                                "&no_hp="+no_hp_detect+
                                "&merek_hp="+phone_model
                        );
                        Log.e("response_body2",response_body2);

                        try {
                            response_json2 = new JSONObject(response_body2);
                            JSONObject jsonObject2 = response_json2.getJSONObject("status");


                            if(jsonObject2.getString("pesan").equalsIgnoreCase("SUKSES")){
                                new AlertDialog.Builder(getApplicationContext())
                                        .setTitle("Perhatian")
                                        .setMessage("Pin berhasil disimpan!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                                //Toast.makeText(v.getContext(),"Pin berhasil disimpan!",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterPinActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                new AlertDialog.Builder(getApplicationContext())
                                        .setTitle("Perhatian")
                                        .setMessage("Pin gagal disimpan, coba kembali!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                                //Toast.makeText(v.getContext(),"Pin gagal disimpan, coba kembali!",Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }

                    }catch (IOException e1){

                        e1.printStackTrace();

                    }*/

                }
            }

            //Intent intent = new Intent(RegisterPinActivity.this, LoginActivity.class);
            //startActivity(intent);
            //finish();
        });


    }

    @SuppressLint("NewApi")
    private void postDataPinIBankingAsyncTask(Intent intentData, String pin) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String link_api = intentData.getStringExtra("link_api");
        String branch = intentData.getStringExtra("branch");
        String norek = intentData.getStringExtra("norek");

        final String security_phone = dataHelper.getIdUnique();
        final String phone_model = Build.MODEL;
        final String no_hp_detect = dataHelper.getPhoneNumber();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=insert_new_user_banking"+
                "&branch="+branch+
                "&norek="+norek+
                "&pin_ib="+pin+
                "&security_phone="+security_phone+
                "&no_hp="+no_hp_detect+
                "&merek_hp="+phone_model,
                response -> {
                    progressBar.setVisibility(View.GONE);

                    try {
                        JSONObject response_json2 = new JSONObject(response);
                        JSONObject jsonObject2 = response_json2.getJSONObject("status");


                        if(jsonObject2.getString("pesan").equalsIgnoreCase("SUKSES")){
                            new AlertDialog.Builder(context)
                                    .setTitle("Perhatian")
                                    .setMessage("Pin berhasil disimpan!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation

                                            dialog.dismiss();

                                            Intent intent = new Intent(context, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();

                            //Toast.makeText(context,"Pin berhasil disimpan!",Toast.LENGTH_SHORT).show();

                        }else{
                            new AlertDialog.Builder(context)
                                    .setTitle("Perhatian")
                                    .setMessage("Pin gagal disimpan, coba kembali!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                            //Toast.makeText(context,"Pin gagal disimpan, coba kembali!",Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e2) {
                        e2.printStackTrace();
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

    public void onStart() {
        super.onStart();
    }


    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}