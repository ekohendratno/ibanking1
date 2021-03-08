package id.kopas.berkarya.said.ibanking.navigasi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.CacheRequest;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;

public class PageAkun extends Fragment {
    public static PageAkun newInstance() {

        return new PageAkun();
    }

    String link_api, branch, norek, pinib;
    static SharedPreferences sharedpreferences;
    DataHelper dataHelper;
    Context context;

    TextView tv1, tv2, tv3, tv4, tv5, tv6;

    private RelativeLayout progressBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_akun, container, false);


        sharedpreferences = getActivity().getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        progressBar = (RelativeLayout) rootView.findViewById(R.id.progressBar);

        link_api = sharedpreferences.getString("link_api", "");
        branch = sharedpreferences.getString("branch", "");
        norek = sharedpreferences.getString("norek", "");
        pinib = sharedpreferences.getString("pinib", "");

        context = getActivity();
        dataHelper = new DataHelper(context);


        tv1 = rootView.findViewById(R.id.tv1);
        tv2 = rootView.findViewById(R.id.tv2);
        tv3 = rootView.findViewById(R.id.tv3);
        tv4 = rootView.findViewById(R.id.tv4);
        tv5 = rootView.findViewById(R.id.tv5);
        tv6 = rootView.findViewById(R.id.tv6);

        rootView.findViewById(R.id.card_share).setOnClickListener(v -> {

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            share.putExtra(Intent.EXTRA_TEXT, getString(R.string.url_apps));

            startActivity(Intent.createChooser(share, "Share link!"));
        });
        rootView.findViewById(R.id.btn_send_email).setOnClickListener(v -> sendFeedback(getActivity()));
        rootView.findViewById(R.id.btn_faq).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.url_apps_web_term)))));
        rootView.findViewById(R.id.btn_kebijakan).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.url_terms)))));


        postSaldosyncTask();

        return rootView;
    }

    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, R.string.email_developer);
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

    /**
     * UPDATE SALDO
     */
    @SuppressLint("NewApi")
    private void postSaldosyncTask() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        CacheRequest serverRequest2 = new CacheRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=validasi_login_ibanking"+
                "&branch="+branch+
                "&norek="+norek+
                "&pin_ib="+pinib,
                req -> {
                    progressBar.setVisibility(View.GONE);

                    try {
                        final String jsonString = new String(req.data, HttpHeaderParser.parseCharset(req.headers));
                        JSONObject response_json2 = new JSONObject(jsonString);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        tv1.setText(  jsonObject2.getString("norekening") );
                        tv2.setText(  jsonObject2.getString("jenis_rek") );
                        tv3.setText(  jsonObject2.getString("nama") );
                        tv4.setText(  jsonObject2.getString("tgllahir") );
                        tv5.setText(  jsonObject2.getString("tmptlahir") );
                        tv6.setText(  jsonObject2.getString("alamat") );

                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }, error -> {Log.w(DataHelper.getTAG(), "JSONException @ postStatusLoginAsyncTask()\n" + error.getMessage());}
        );

        serverRequest2.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(serverRequest2);
    }
}
