package id.kopas.berkarya.said.ibanking.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.RegisterActivity;
import id.kopas.berkarya.said.ibanking.RegisterPinActivity;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.adapters.AdapterBranch;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.CacheRequest;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.models.Branch;

public class PageTransfer extends AppCompatActivity {
    static RelativeLayout progressBar;


    static SharedPreferences sharedpreferences;

    DataHelper dataHelper;
    static Context context;

    static AppCompatEditText editKeyBranch;
    static AppCompatEditText editKeyBranchLinkApi;
    static AutoCompleteTextView editBranch;
    static TextInputEditText editNoRekening;
    static TextInputEditText editNominalTransfer;
    static TextInputEditText editKeteranganTransfer;


    String link_api, branch, norek, pinib;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_down);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        context = PageTransfer.this;

        dataHelper = new DataHelper(getApplicationContext());


        link_api = sharedpreferences.getString("link_api", "");
        branch = sharedpreferences.getString("branch", "");
        norek = sharedpreferences.getString("norek", "");
        pinib = sharedpreferences.getString("pinib", "");


        progressBar = (RelativeLayout) findViewById(R.id.progressBar);

        editKeyBranch = findViewById(R.id.editKeyBranch);
        editKeyBranchLinkApi = findViewById(R.id.editKeyBranchLinkApi);
        editBranch = findViewById(R.id.editBranch);

        editNoRekening = findViewById(R.id.editNoRekening);
        editNominalTransfer = findViewById(R.id.editNominalTransfer);
        editKeteranganTransfer = findViewById(R.id.editKeteranganTransfer);

        final MaterialButton actLanjutkan = findViewById(R.id.actLanjutkan);
        actLanjutkan.setEnabled(false);

        editNominalTransfer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                actLanjutkan.setEnabled(true);
                actLanjutkan.setBackgroundTintList(getResources().getColorStateList(R.color.colorBiru));
            }
        });


        actLanjutkan.setOnClickListener(v -> {
            editNominalTransfer.getBackground().clearColorFilter();
            String branch = editKeyBranch.getText().toString();
            if(!TextUtils.isEmpty(branch)){
                String norek_tujuan = editNoRekening.getText().toString();
                if(TextUtils.isEmpty(norek_tujuan)){

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Perhatian")
                            .setMessage("Nomor rekening tujuan belum diisi!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }else{

                    //cekDataBranch();
                    if(norek_tujuan.equalsIgnoreCase(norek)){
                        new AlertDialog.Builder(context)
                                .setTitle("Perhatian")
                                .setMessage("Nomor rekening tujuan, adalah nomer rekening anda sendiri !!!")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{
                        int nominal = Integer.valueOf(editNominalTransfer.getText().toString());
                        if(nominal < 10000 ){
                            editNominalTransfer.setError("Minimal Transfer Rp10.000");
                            editNominalTransfer.getBackground().setColorFilter(getResources().getColor(R.color.colorOrange), PorterDuff.Mode.SRC_ATOP);
                        }else{
                            cekDataBranchIbankingAsyncTask();
                            //cekDataBranchAsyncTask();
                        }
                    }
                }

            }else{
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Perhatian")
                        .setMessage("Silahkan pilih Branch dahulu!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                dialog.dismiss();
                            }
                        })
                        .show();

                //Toast.makeText(v.getContext(),"Silahkan pilih Branch",Toast.LENGTH_SHORT).show();

            }
        });


        /**
         * INIT
         */

        initBranchAsyncTask();
    }


    /**
     * INIT BRANCH
     */

    @SuppressLint("NewApi")
    private static void initBranchAsyncTask() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,DataHelper.getAlamatServer() + "/informasi_bank",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (!response.equals("")) {
                            JSONArray json = new JSONArray(response);

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



    /**
     * CEK DATA IBANK
     *
     */
    @SuppressLint("NewApi")
    private void cekDataBranchIbankingAsyncTask() {
        if(!progressBar.isShown()) progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        CacheRequest serverRequest = new CacheRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=validasi_login_ibanking"+
                "&branch="+branch+
                "&norek="+norek+
                "&pin_ib="+pinib,
                req -> {
                    if(progressBar.isShown()) progressBar.setVisibility(View.GONE);
                    JSONObject response_json2 = null;
                    try {
                        final String jsonString = new String(req.data, HttpHeaderParser.parseCharset(req.headers));

                        response_json2 = new JSONObject(jsonString);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        if(jsonObject2.getString("status_transfer").equalsIgnoreCase("A")){
                            //dibuat jadi error saja nanti

                            postSaldosyncTask(jsonObject2.getInt("min_saldo"));

                        }else{
                            new AlertDialog.Builder(context)
                                    .setTitle("Info")
                                    .setMessage("Maaf untuk rekening ini status transfer tidak dapat dilakukkan!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            dialog.dismiss();

                                        }
                                    })
                                    .show();
                        }
                    } catch (JSONException e) {

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
     * UPDATE SALDO
     */
    @SuppressLint("NewApi")
    private void postSaldosyncTask(int minimal_saldo) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=cek_saldo_nasabah"+
                "&branch="+branch+
                "&norek="+norek,
                response -> {
                    try {
                        JSONObject response_json2 = new JSONObject(response);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        if(!jsonObject2.getString("saldo").isEmpty()){

                            int saldo = jsonObject2.getInt("saldo");
                            int nominal = Integer.valueOf(editNominalTransfer.getText().toString());
                            int hitung = saldo - nominal;
                            if( hitung < minimal_saldo ){
                                progressBar.setVisibility(View.GONE);
                                new AlertDialog.Builder(context)
                                        .setTitle("Perhatian")
                                        .setMessage("Maaf saldo tidak cukup!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }else{
                                cekDataBranchAsyncTask();
                            }

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


    /**
     * CEK DATA BRANCH
     */
    @SuppressLint("NewApi")
    private void cekDataBranchAsyncTask() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String branchLink_tujuan = editKeyBranchLinkApi.getText().toString();
        String branch_tujuan = editKeyBranch.getText().toString();
        String norek_tujuan = editNoRekening.getText().toString();
        String nominal_transfer = editNominalTransfer.getText().toString();
        String transfer_keterangan = editKeteranganTransfer.getText().toString();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,branchLink_tujuan +"banking.php?"+
                "ib=informasi_rek_tujuan"+
                "&branch="+branch_tujuan+
                "&norek="+norek_tujuan,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try{

                        JSONObject response_json = new JSONObject(response);
                        JSONObject jsonObject = response_json.getJSONObject("data");

                        if(!jsonObject.getString("norekening").isEmpty()){

                            /**
                             * CEK DATA IBANK
                             */


                            Intent intent = new Intent(context, PageTransferConfirm.class);
                            intent.putExtra("link_api_tujuan",branchLink_tujuan);
                            intent.putExtra("branch_tujuan",branch_tujuan);
                            intent.putExtra("norek_tujuan",norek_tujuan);
                            intent.putExtra("nominal_transfer",nominal_transfer);
                            intent.putExtra("transfer_keterangan",transfer_keterangan);
                            startActivity(intent);

                            Log.w(DataHelper.getTAG(), "OK\n");
                        }


                    }catch (JSONException e1){
                        new AlertDialog.Builder(context)
                                .setTitle("Perhatian")
                                .setMessage("Data yang anda masukkan tidak tepat!")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        //Toast.makeText(getApplicationContext(),"Data yang anda masukkan tidak tepat",Toast.LENGTH_SHORT).show();

                        e1.printStackTrace();
                    }

                }, error -> {
            progressBar.setVisibility(View.GONE);
            Log.w(DataHelper.getTAG(), "JSONException @ cekDataBranchAsyncTask()\n" + error.getMessage());
        }
        );


        serverRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(serverRequest);
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

    public void onBackPressed() {
        super.onBackPressed();
    }

}
