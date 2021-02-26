package id.kopas.berkarya.said.ibanking.pages.nav;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.Splash;

public class PageNavAkun extends Fragment {
    public static PageNavAkun newInstance() {

        return new PageNavAkun();
    }

    static SharedPreferences sharedpreferences;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_akun, container, false);

        sharedpreferences = getActivity().getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);




        return rootView;
    }

}
