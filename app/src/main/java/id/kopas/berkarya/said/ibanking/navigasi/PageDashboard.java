package id.kopas.berkarya.said.ibanking.navigasi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.fun.TextViewCustom;
import id.kopas.berkarya.said.ibanking.pages.PageTransaksi;
import id.kopas.berkarya.said.ibanking.pages.PageTransfer;

public class PageDashboard extends Fragment {
    public static PageDashboard newInstance() {

        return new PageDashboard();
    }

    String link_api, branch, norek, pinib;
    static SharedPreferences sharedpreferences;
    DataHelper dataHelper;
    Context context;

    TextViewCustom tvNamaPengguna, tvSaldoSekarang;

    private RelativeLayout progressBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_dashboard, container, false);

        sharedpreferences = getActivity().getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        progressBar = (RelativeLayout) rootView.findViewById(R.id.progressBar);

        link_api = sharedpreferences.getString("link_api", "");
        branch = sharedpreferences.getString("branch", "");
        norek = sharedpreferences.getString("norek", "");
        pinib = sharedpreferences.getString("pinib", "");

        context = getActivity();
        dataHelper = new DataHelper(context);


        tvNamaPengguna = rootView.findViewById(R.id.tvNamaPengguna);
        tvSaldoSekarang = rootView.findViewById(R.id.tvSaldoSekarang);
        tvSaldoSekarang.setText( dataHelper.formatRupiah("0"));


        rootView.findViewById(R.id.pageTransfer).setOnClickListener(v->{
            Intent intent = new Intent(getContext(), PageTransfer.class);
            startActivity(intent);
        });
        rootView.findViewById(R.id.pagePembayaran).setOnClickListener(v->{
            Intent intent = new Intent(getContext(), PageTransaksi.class);
            startActivity(intent);
        });
        rootView.findViewById(R.id.pageTopUp).setOnClickListener(v->{
            Intent intent = new Intent(getContext(), PageTransaksi.class);
            startActivity(intent);
        });
        rootView.findViewById(R.id.pageKirimUang).setOnClickListener(v->{
            Intent intent = new Intent(getContext(), PageTransaksi.class);
            startActivity(intent);
        });
        rootView.findViewById(R.id.pagePermintaanUang).setOnClickListener(v->{
            Intent intent = new Intent(getContext(), PageTransaksi.class);
            startActivity(intent);
        });
        rootView.findViewById(R.id.pagePembelian).setOnClickListener(v->{
            Intent intent = new Intent(getContext(), PageTransaksi.class);
            startActivity(intent);
        });



        postSaldosyncTask();

        return rootView;
    }




    /**
     * UPDATE SALDO
     */
    @SuppressLint("NewApi")
    private void postSaldosyncTask() {
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

                            String saldo = jsonObject2.getString("saldo");
                            tvSaldoSekarang.setText( dataHelper.formatRupiah(saldo) );


                        }

                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }, error -> {
            progressBar.setVisibility(View.GONE);
            Log.w(DataHelper.getTAG(), "JSONException @ initBranchAsyncTask()\n" + error.getMessage());
        }
        );

        StringRequest serverRequest2 = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=validasi_login_ibanking"+
                "&branch="+branch+
                "&norek="+norek+
                "&pin_ib="+pinib,
                response -> {
                    progressBar.setVisibility(View.GONE);

                    try {
                        JSONObject response_json2 = new JSONObject(response);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        if(!jsonObject2.getString("nama").isEmpty()){

                            String nama = jsonObject2.getString("nama");
                            tvNamaPengguna.setText(nama);


                        }

                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }


                }, error -> {Log.w(DataHelper.getTAG(), "JSONException @ postStatusLoginAsyncTask()\n" + error.getMessage());}
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

}
