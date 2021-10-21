package id.kopas.berkarya.said.ibanking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.navigasi.PageAkun;
import id.kopas.berkarya.said.ibanking.navigasi.PageDashboard;
import id.kopas.berkarya.said.ibanking.navigasi.PageHistori;

public class MainActivity extends AppCompatActivity {
    String link_api, link_api_logo, branch, norek;
    static SharedPreferences sharedpreferences;
    DataHelper dataHelper;
    private static BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation1:

                        getFragment(PageDashboard.newInstance());

                        return true;
                    case R.id.navigation2:

                        getFragment(PageHistori.newInstance());

                        return true;
                    case R.id.navigation3:

                        getFragment(PageAkun.newInstance());

                        return true;
                }

                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        link_api = sharedpreferences.getString("link_api", "");
        branch = sharedpreferences.getString("branch", "");
        norek = sharedpreferences.getString("norek", "");

        dataHelper = new DataHelper(getApplicationContext());


        if( TextUtils.isEmpty(link_api) ){
            //Intent intent = new Intent(MainActivity.this, OptionalActivity.class);
            //startActivity(intent);
            //finish();
        }


        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {

            getFragment( PageDashboard.newInstance() );

        }

    }

    public void getFragment(Fragment fr){
        FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
        ft4.replace(R.id.frame_container, fr);
        ft4.commit();
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Konfirmasi")
                .setMessage("Yakin mau keluar dari aplikasi!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.dismiss();

                        new postStatusLogoutsyncTask(MainActivity.this).execute();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .show();


    }




    /**
     * UPDATE STATUS LOGOUT
     */


    private class postStatusLogoutsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog;
        Context context;

        public postStatusLogoutsyncTask(Context activity) {
            this.context = activity;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Doing something, please wait.");
            dialog.show();
        }
        @Override
        protected String doInBackground(Void... args) {
            // do background work here

            String response_body2 = null;
            try{

                response_body2 = dataHelper.getServer(link_api +"banking.php?"+
                        "ib=update_status_logout"+
                        "&branch="+branch+
                        "&norek="+norek
                );



            }catch (IOException e1){

                e1.printStackTrace();

            }


            return response_body2;
        }
        @Override
        protected void onPostExecute(String result) {
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            try {
                JSONObject response_json2 = new JSONObject(result);
                JSONObject jsonObject2 = response_json2.getJSONObject("status");


                if(jsonObject2.getString("pesan").equalsIgnoreCase("SUKSES")){

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.remove("link_api");
                    editor.remove("branch");
                    editor.remove("norek");
                    editor.remove("tgl_lahir");
                    editor.remove("nama_ibu");
                    editor.remove("no_hp");
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, OptionalActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(context,"Berhasil keluar",Toast.LENGTH_SHORT).show();

                }else{
                    new AlertDialog.Builder(context)
                            .setTitle("Perhatian")
                            .setMessage("Gagal keluar, coba kembali!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }

            } catch (JSONException e2) {
                e2.printStackTrace();
            }

        }
    }
}