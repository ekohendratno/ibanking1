package id.kopas.berkarya.said.ibanking.navigasi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.CacheRequest;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.models.HistoryTransaksi;
import info.androidhive.fontawesome.FontTextView;

public class PageHistori extends Fragment {
    public static PageHistori newInstance() {

        return new PageHistori();
    }
    String link_api, branch, norek, pinib;
    static SharedPreferences sharedpreferences;
    DataHelper dataHelper;
    Context context;
    private RelativeLayout progressBar;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_historitransaksi, container, false);

        sharedpreferences = getActivity().getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        progressBar = (RelativeLayout) rootView.findViewById(R.id.progressBar);

        link_api = sharedpreferences.getString("link_api", "");
        branch = sharedpreferences.getString("branch", "");
        norek = sharedpreferences.getString("norek", "");
        pinib = sharedpreferences.getString("pinib", "");

        context = getActivity();
        dataHelper = new DataHelper(context);


        recyclerView = rootView.findViewById(R.id.recycle_view);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        postDatasyncTask();

        return rootView;
    }




    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemPelaporanViewHolder> {

        Context context;
        List<HistoryTransaksi> historyTransaksiList;

        public RecyclerViewAdapter(Context context, List<HistoryTransaksi> historyTransaksiList) {
            this.context = context;
            this.historyTransaksiList = historyTransaksiList;
        }


        @Override
        public RecyclerViewAdapter.ItemPelaporanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history_transaksi, parent, false);

            return new RecyclerViewAdapter.ItemPelaporanViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ItemPelaporanViewHolder holder, int position) {
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
    private void postDatasyncTask() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=get_history_transaksi"+
                "&branch="+branch+
                "&norek="+norek,
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


}
