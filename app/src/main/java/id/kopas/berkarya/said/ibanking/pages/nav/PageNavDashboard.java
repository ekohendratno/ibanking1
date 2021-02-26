package id.kopas.berkarya.said.ibanking.pages.nav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.pages.PageTransaksi;

public class PageNavDashboard extends Fragment {
    public static PageNavDashboard newInstance() {

        return new PageNavDashboard();
    }

    static SharedPreferences sharedpreferences;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_dashboard, container, false);

        sharedpreferences = getActivity().getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        rootView.findViewById(R.id.pageTransfer).setOnClickListener(v->{
            Intent intent = new Intent(getContext(), PageTransaksi.class);
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




        return rootView;
    }

}
