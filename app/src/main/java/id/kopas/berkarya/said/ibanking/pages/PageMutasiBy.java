package id.kopas.berkarya.said.ibanking.pages;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.RegisterActivity;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.models.HistoryTransaksi;
import info.androidhive.fontawesome.FontTextView;

public class PageMutasiBy extends AppCompatActivity {
    static SharedPreferences sharedpreferences;
    DataHelper dataHelper;
    Context context;
    String link_api, branch, norek, pinib;

    TextInputEditText editNoRekening;
    AutoCompleteTextView editTanggal, editTanggalEnd;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

    private RelativeLayout progressBar;

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @SuppressLint("NewApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_mutasiby);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_down);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        progressBar = (RelativeLayout) findViewById(R.id.progressBar);

        link_api = sharedpreferences.getString("link_api", "");
        branch = sharedpreferences.getString("branch", "");
        norek = sharedpreferences.getString("norek", "");
        pinib = sharedpreferences.getString("pinib", "");

        context = PageMutasiBy.this;
        dataHelper = new DataHelper(context);



        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        editNoRekening = findViewById(R.id.editNoRekening);
        editNoRekening.setText(norek);
        editNoRekening.setEnabled(false);

        editTanggal = findViewById(R.id.editTanggal);
        editTanggalEnd = findViewById(R.id.editTanggalEnd);

        editTanggal.setText(sdf.format(new Date()));
        editTanggal.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editTanggal.setText(sdf.format(cal.getTime()));
            };

            new DatePickerDialog(
                    PageMutasiBy.this,
                    date,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();

        });

        editTanggalEnd.setText(sdf.format(new Date()));
        editTanggalEnd.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editTanggalEnd.setText(sdf.format(cal.getTime()));
            };

            new DatePickerDialog(
                    PageMutasiBy.this,
                    date,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();

        });

        findViewById(R.id.actLanjutkan).setOnClickListener(v -> {

            String tgl1 = editTanggal.getText().toString();
            String tgl2 = editTanggalEnd.getText().toString();

            postDatasyncTask(tgl1,tgl2);

        });
    }





    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemPelaporanViewHolder> {

        Context context;
        List<HistoryTransaksi> historyTransaksiList;

        public RecyclerViewAdapter(Context context, List<HistoryTransaksi> historyTransaksiList) {
            this.context = context;
            this.historyTransaksiList = historyTransaksiList;
        }


        @Override
        public ItemPelaporanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history_transaksi, parent, false);

            return new ItemPelaporanViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemPelaporanViewHolder holder, int position) {
            HistoryTransaksi item = historyTransaksiList.get(position);

            holder.tv1.setText(item.ket_transaksi);
            holder.tv2.setText(item.tanggal);
            holder.tv3.setText("Saldo " + dataHelper.formatRupiah(item.saldo));

            if(!item.kredit.equalsIgnoreCase("0")){
                holder.tv4.setText(dataHelper.formatRupiah(item.kredit));

            }else{
                holder.tv4.setText(dataHelper.formatRupiah(item.debet));

            }

            holder.tv5.setText(item.id_user);
        }


        @Override
        public int getItemCount() {
            return historyTransaksiList.size();
        }

        public class ItemPelaporanViewHolder extends RecyclerView.ViewHolder {
            public TextView tv1, tv2,tv3,tv4,tv5;
            public FontTextView imageView1;

            public ItemPelaporanViewHolder(View itemView) {
                super(itemView);
                imageView1 = itemView.findViewById(R.id.imageView1);
                tv1 = itemView.findViewById(R.id.tv1);
                tv2 = itemView.findViewById(R.id.tv2);
                tv3 = itemView.findViewById(R.id.tv3);
                tv4 = itemView.findViewById(R.id.tv4);
                tv5 = itemView.findViewById(R.id.tv5);
            }
        }
    }



    /**
     * UPDATE SALDO
     */
    @SuppressLint("NewApi")
    private void postDatasyncTask(String tgl1, String tgl2) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=get_history_transaksi_by_tanggal"+
                "&branch="+branch+
                "&norek="+norek+
                "&tgl1="+tgl1+
                "&tgl2="+tgl2,
                req -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = new JSONArray(req);

                        List<HistoryTransaksi> historyTransaksiArrayList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject command = jsonArray.getJSONObject(i);

                            historyTransaksiArrayList.add(new HistoryTransaksi(
                                    command.getString("tanggal"),
                                    command.getString("ket_transaksi"),
                                    command.getString("debet"),
                                    command.getString("kredit"),
                                    command.getString("saldo"),
                                    command.getString("id_user")
                            ));
                        }




                        adapter = new RecyclerViewAdapter(context, historyTransaksiArrayList);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);


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
