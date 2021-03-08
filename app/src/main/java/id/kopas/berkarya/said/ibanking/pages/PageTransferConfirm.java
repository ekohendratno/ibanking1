package id.kopas.berkarya.said.ibanking.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.kopas.berkarya.said.ibanking.LoginActivity;
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


    String link_api, branch, norek, nama, pinib;
    String link_api_tujuan, branch_tujuan, norek_tujuan, nama_tujuan, nominal_transfer, transfer_keterangan;

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
        nama = sharedpreferences.getString("nama", "");
        norek = sharedpreferences.getString("norek", "");
        pinib = sharedpreferences.getString("pinib", "");


        progressBar = (RelativeLayout) findViewById(R.id.progressBar);

        Intent intentData = getIntent();
        link_api_tujuan = intentData.getStringExtra("link_api_tujuan");
        branch_tujuan = intentData.getStringExtra("branch_tujuan");
        norek_tujuan = intentData.getStringExtra("norek_tujuan");
        nama_tujuan = intentData.getStringExtra("nama_tujuan");
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
        actLanjutkan.setOnClickListener(v -> {
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
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=validasi_login_ibanking"+
                "&branch="+branch+
                "&norek="+norek+
                "&pin_ib="+pinib,
                req -> {
                    progressBar.setVisibility(View.GONE);
                    JSONObject response_json2 = null;
                    try {

                        response_json2 = new JSONObject(req);
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
                            int nominal = Integer.parseInt(nominal_transfer);
                            int hitung = saldo - nominal;
                            if( hitung < minimal_saldo ){
                                progressBar.setVisibility(View.GONE);
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

                                postSaldoPenerimasyncTask(jsonObject2.getInt("saldo"));
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
     * SALDO PENERIMA
     */
    @SuppressLint("NewApi")
    private void postSaldoPenerimasyncTask(int saldo_pengirim) {
        Log.w(DataHelper.getTAG(), "@postSaldoPenerimasyncTask()");
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api_tujuan +"banking.php?"+
                "ib=cek_saldo_nasabah"+
                "&branch="+branch_tujuan+
                "&norek="+norek_tujuan,
                response -> {
                    try {
                        JSONObject response_json2 = new JSONObject(response);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        if(jsonObject2.getInt("saldo") > 0){
                            int ss = jsonObject2.getInt("saldo");

                            //confirm pin
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            LayoutInflater inflater = this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.alert_pin, null);
                            builder.setView(dialogView);

                            EditText editText = (EditText) dialogView.findViewById(R.id.editPassword);

                            builder.setCancelable(false);
                            builder.setTitle("Konfirmasi Pin");
                            builder.setPositiveButton("OK", (dialog, which) -> {
                                String pina = editText.getText().toString();
                                if(!pina.equals(pinib) || TextUtils.isEmpty(pina)){
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context,"Maaf pin salah!",Toast.LENGTH_SHORT).show();
                                }else{

                                    postTransferAsyncTask(saldo_pengirim, ss);

                                }
                            });
                            builder.setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.cancel();

                                Intent intent = new Intent(context, PageTransfer.class);
                                startActivity(intent);
                                finish();
                            });

                            builder.show();


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
     * GET ID TRANSFER
     */

    @SuppressLint("NewApi")
    private void getIdTransferAsyncTask() {
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String tgl = sdf.format(new Date());


        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=get_id_transaksi_bs"+
                "&nourut="+tgl,
                response -> {
                    try {
                        JSONObject response_json2 = new JSONObject(response);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        //Log.e("response", jsonObject2.getString("id"));
                        long ix = 0;

                        if(jsonObject2.getString("id").equals("KOSONG")){

                            ix = (long) 0;
                        }else{
                            ix = jsonObject2.getLong("id");
                        }

                        ix = ix+1;

                        String nn = String.format("%07d", ix);
                        Log.e("id", tgl +""+ nn);


                        tv0.setText( tgl +""+ nn );

                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }


                }, error -> {
                    Log.w(DataHelper.getTAG(), "JSONException @ getIdTransferAsyncTask()\n" + error.getMessage());
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
    private void postTransferAsyncTask(int saldo_pengirim, int saldo_penerima) {
        Log.w(DataHelper.getTAG(), "@postTransferAsyncTask()");

        String __id = tv0.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        int nominal = Integer.parseInt(nominal_transfer);

        int biaya = 0;

        int nominal_total = nominal+biaya;


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String tgl_kirim = sdf.format(new Date());


        String link_pengirim = link_api +"banking.php?"+
                "ib=insert_trx_transfer_pengirim"+
                "&id="+__id+
                "&branch="+branch+
                "&norek="+norek+
                "&debet="+nominal+
                "&saldo="+(saldo_pengirim-nominal)+
                "&ket_transaksi=Transfer IB ke Rek. "+norek_tujuan;

        String link_penerima = link_api_tujuan +"banking.php?"+
                "ib=insert_trx_transfer_penerima"+
                "&id="+__id+
                "&branch="+branch_tujuan+
                "&norek="+norek_tujuan+
                "&kredit="+nominal+
                "&saldo="+(saldo_penerima+nominal)+
                "&ket_transaksi=Penerimaan Transfer IB dari "+norek;

        Log.e("pengirim",link_pengirim);
        Log.e("penerima",link_penerima);


        //post pengirim
        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_pengirim,
                response -> {

                    //post penerima / tujuan
                    StringRequest serverRequest2 = new StringRequest(Request.Method.GET, link_penerima,
                            response2 -> {
                                progressBar.setVisibility(View.GONE);

                                new AlertDialog.Builder(context)
                                        .setTitle("Perhatian")
                                        .setMessage("Transaksi Anda telah diproses!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation

                                                dialog.dismiss();

                                                Intent intentData = new Intent(context, PageTransferReport.class);
                                                //intent.putExtra("link_api_tujuan",);
                                                intentData.putExtra("report_tanggal",tgl_kirim);
                                                intentData.putExtra("report_norefrensi",__id);
                                                intentData.putExtra("report_sumberdana_nama",nama);
                                                intentData.putExtra("report_sumberdana_norek",norek);
                                                intentData.putExtra("report_jenistransaksi","");
                                                intentData.putExtra("report_rektujuan_nama",nama_tujuan);
                                                intentData.putExtra("report_rektujuan_norek",norek_tujuan);
                                                intentData.putExtra("report_nominal",String.valueOf(nominal));
                                                intentData.putExtra("report_biayaadmin",String.valueOf(biaya));
                                                intentData.putExtra("report_total",String.valueOf(nominal_total));
                                                startActivity(intentData);
                                                finish();

                                            }
                                        })
                                        .show();

                            }, error2 -> {
                                progressBar.setVisibility(View.GONE);
                                Log.w(DataHelper.getTAG(), "JSONException @ initBranchAsyncTask()\n" + error2.getMessage());
                            }
                    );
                    serverRequest2.setRetryPolicy(
                            new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                    );
                    requestQueue.add(serverRequest2);





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

        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api_tujuan +"banking.php?"+
                "ib=informasi_rek_tujuan"+
                "&branch="+branch_tujuan+
                "&norek="+norek_tujuan,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try{

                        JSONObject response_json = new JSONObject(response);
                        JSONObject jsonObject2 = response_json.getJSONObject("data");

                        if(!TextUtils.isEmpty(jsonObject2.getString("norekening"))){

                            /**
                             * TAMPIL KE LAYOUT
                             */
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String tgl_transaksi = sdf.format(new Date());

                            tv1.setText(  jsonObject2.getString("norekening") );
                            tv2.setText(  jsonObject2.getString("nama") );
                            tv3.setText(  jsonObject2.getString("alamat") );
                            tv4.setText(  tgl_transaksi );
                            tv5.setText(  transfer_keterangan );
                            tv6.setText(  dataHelper.formatRupiah(nominal_transfer) );

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
