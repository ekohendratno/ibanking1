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

public class PageNavHistori extends Fragment {
    public static PageNavHistori newInstance() {

        return new PageNavHistori();
    }

    static SharedPreferences sharedpreferences;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_historitransaksi, container, false);

        sharedpreferences = getActivity().getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);




        return rootView;
    }


}
