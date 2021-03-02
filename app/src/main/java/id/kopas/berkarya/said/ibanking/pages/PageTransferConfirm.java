package id.kopas.berkarya.said.ibanking.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.kopas.berkarya.said.ibanking.MainActivity;
import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.adapters.AdapterBranch;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.CacheRequest;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.models.Branch;

public class PageTransferConfirm extends AppCompatActivity {
    static RelativeLayout progressBar;


    static SharedPreferences sharedpreferences;

    DataHelper dataHelper;
    static Context context;


    String link_api, branch, norek, pinib;
    String link_api_tujuan, branch_tujuan, norek_tujuan, nominal_transfer, transfer_keterangan;

    TextView tv0, tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_down);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        context = PageTransferConfirm.this;

        dataHelper = new DataHelper(getApplicationContext());


        link_api = sharedpreferences.getString("link_api", "");
        branch = sharedpreferences.getString("branch", "");
        norek = sharedpreferences.getString("norek", "");
        pinib = sharedpreferences.getString("pinib", "");


        progressBar = (RelativeLayout) findViewById(R.id.progressBar);

        Intent intentData = getIntent();
        link_api_tujuan = intentData.getStringExtra("link_api_tujuan");
        branch_tujuan = intentData.getStringExtra("branch_tujuan");
        norek_tujuan = intentData.getStringExtra("norek_tujuan");
        nominal_transfer = intentData.getStringExtra("nominal_transfer");
        transfer_keterangan = intentData.getStringExtra("transfer_keterangan");



        tv0 = findViewById(R.id.tv0);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);
        tv7 = findViewById(R.id.tv7);
        tv8 = findViewById(R.id.tv8);


        final MaterialButton actLanjutkan = findViewById(R.id.actLanjutkan);
        actLanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!branch.equalsIgnoreCase(branch_tujuan)){
                    new AlertDialog.Builder(context)
                            .setTitle("Perhatian")
                            .setMessage("Untuk saat ini pengiriman antar pesantren masih belum dibuka!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    cekDataBranchIbankingAsyncTask();
                }
            }
        });

        getIdTransferAsyncTask();
        cekDataBranchAsyncTask();

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
        if(!progressBar.isShown()) progressBar.setVisibility(View.VISIBLE);
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
                            int nominal = Integer.parseInt(nominal_transfer);
                            int hitung = saldo - nominal;
                            if( hitung < minimal_saldo ){
                                if(!progressBar.isShown()) progressBar.setVisibility(View.GONE);
                                new AlertDialog.Builder(context)
                                        .setTitle("Perhatian")
                                        .setMessage("Maaf saldo tidak cukup!")
                                        .setPositiveButton("Batalkan transaksi", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                                dialog.dismiss();

                                                Intent intent = new Intent(context, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .show();
                            }else{

                                postTransferAsyncTask();
                            }

                        }

                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }, error -> {
            if(!progressBar.isShown()) progressBar.setVisibility(View.GONE);
            Log.w(DataHelper.getTAG(), "JSONException @ initBranchAsyncTask()\n" + error.getMessage());
        }
        );


        serverRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(serverRequest);
    }

    /**
     * GET ID TRANSFER
     */

    @SuppressLint("NewApi")
    private void getIdTransferAsyncTask() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String nourut = sdf.format(new Date());


        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=get_id_transaksi_bs"+
                "&nourut="+nourut,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject response_json2 = new JSONObject(response);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        int _id = 0;
                        if(!jsonObject2.getString("id").equalsIgnoreCase("KOSONG")){
                            _id = jsonObject2.getInt("id");
                        }

                        _id = _id+1;

                        String nn = String.format("%07d", _id);

                        tv0.setText(nourut + nn);


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
     * POS TRANSFER
     */

    @SuppressLint("NewApi")
    private void postTransferAsyncTask() {
        String no = tv0.getText().toString();
        //if(!progressBar.isShown()) progressBar.setVisibility(View.VISIBLE);
        //RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        Log.e("a",link_api +"banking.php?"+
                "ib=insert_trx_transfer_pengirim"+
                "&id="+no+
                "&branch="+branch+
                "&norek="+norek+
                "&id_transaksi=13"+
                "&ket_transaksi=Transfer IB ke Rek. Nomor Rekening Tujuan"+
                "&type_transaksi=DEBET"+
                "&debet="+nominal_transfer+
                "&kredit=0"+
                "&saldo="+nominal_transfer+
                "&userid=IBANKING"+
                "&status_print=P");

        Log.e("a",link_api_tujuan +"banking.php?"+
                "ib=insert_trx_transfer_pengirim"+
                "&id="+no+
                "&branch="+branch_tujuan+
                "&norek="+norek_tujuan+
                "&id_transaksi=03"+
                "&ket_transaksi=Penerimaan Transfer IB Dari Nomor Rekening Pengirim"+
                "&type_transaksi=KREDIT"+
                "&debet="+nominal_transfer+
                "&kredit=0"+
                "&saldo="+nominal_transfer+
                "&userid=IBANKING"+
                "&status_print=P");
        /**
        //post pengirim
        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=insert_trx_transfer_pengirim"+
                "&id="+_id+
                "&branch="+branch+
                "&norek="+norek+
                "&id_transaksi=13"+
                "&ket_transaksi=Transfer IB ke Rek. Nomor Rekening Tujuan"+
                "&type_transaksi=DEBET"+
                "&debet="+nominal_transfer+
                "&kredit=0"+
                "&saldo="+nominal_transfer+
                "&userid=IBANKING"+
                "&status_print=P",
                response -> {
                    if(!progressBar.isShown()) progressBar.setVisibility(View.GONE);

                }, error -> {
            if(!progressBar.isShown()) progressBar.setVisibility(View.GONE);
            Log.w(DataHelper.getTAG(), "JSONException @ initBranchAsyncTask()\n" + error.getMessage());
        }
        );


        //post penerima / tujuan
        StringRequest serverRequest2 = new StringRequest(Request.Method.GET,link_api_tujuan +"banking.php?"+
                "ib=insert_trx_transfer_pengirim"+
                "&id="+_id+
                "&branch="+branch_tujuan+
                "&norek="+norek_tujuan+
                "&id_transaksi=03"+
                "&ket_transaksi=Penerimaan Transfer IB Dari Nomor Rekening Pengirim"+
                "&type_transaksi=KREDIT"+
                "&debet="+nominal_transfer+
                "&kredit=0"+
                "&saldo="+nominal_transfer+
                "&userid=IBANKING"+
                "&status_print=P",
                response -> {
                    if(!progressBar.isShown()) progressBar.setVisibility(View.GONE);

                }, error -> {
            if(!progressBar.isShown()) progressBar.setVisibility(View.GONE);
            Log.w(DataHelper.getTAG(), "JSONException @ initBranchAsyncTask()\n" + error.getMessage());
        }
        );


        serverRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        serverRequest2.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(serverRequest);
        requestQueue.add(serverRequest2);*/
    }





    /**
     * CEK DATA BRANCH
     */
    @SuppressLint("NewApi")
    private void cekDataBranchAsyncTask() {
        if(!progressBar.isShown()) progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api_tujuan +"banking.php?"+
                "ib=informasi_rek_tujuan"+
                "&branch="+branch_tujuan+
                "&norek="+norek_tujuan,
                response -> {
                    if(!progressBar.isShown()) progressBar.setVisibility(View.GONE);
                    try{

                        JSONObject response_json = new JSONObject(response);
                        JSONObject jsonObject2 = response_json.getJSONObject("data");

                        if(!TextUtils.isEmpty(jsonObject2.getString("norekening"))){

                            /**
                             * TAMPIL KE LAYOUT
                             */

                            tv1.setText(  jsonObject2.getString("norekening") );
                            tv2.setText(  jsonObject2.getString("nama") );
                            tv3.setText(  jsonObject2.getString("alamat") );
                            tv4.setText(  "" );
                            tv5.setText(  transfer_keterangan );
                            tv6.setText(  dataHelper.formatRupiah(nominal_transfer) );
                            tv7.setText(  dataHelper.formatRupiah(String.valueOf(2500)) );
                            tv8.setText(  dataHelper.formatRupiah(String.valueOf(Integer.valueOf(nominal_transfer) + 2500)) );

                            //Intent intent = new Intent(context, PageTransferConfirm.class);
                            //startActivity(intent);

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
            if(!progressBar.isShown()) progressBar.setVisibility(View.GONE);
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
