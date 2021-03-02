package id.kopas.berkarya.said.ibanking;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.kopas.berkarya.said.ibanking.adapters.AdapterBranch;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.models.Branch;

public class RegisterActivity extends AppCompatActivity {
    String TAG = "RegisterActivity";

    String akun, password;

    static RelativeLayout progressBar;

    static SharedPreferences sharedpreferences;

    static AppCompatEditText editKeyBranch;
    static AppCompatEditText editKeyBranchLinkApi;
    static AutoCompleteTextView editBranch;
    static TextInputEditText editNoRekening;
    static TextInputEditText editTanggalLahir;
    static TextInputEditText editNamaIbuKandung;
    static TextInputEditText editNoHandphone;

    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String dateToday, dateTime;

    DataHelper dataHelper;
    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_down);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        context = RegisterActivity.this;

        dataHelper = new DataHelper(getApplicationContext());

        /**
         * Loading
         */






        /**
         * Find Id
         */

        progressBar = (RelativeLayout) findViewById(R.id.progressBar);

        editKeyBranch = findViewById(R.id.editKeyBranch);
        editKeyBranchLinkApi = findViewById(R.id.editKeyBranchLinkApi);
        editBranch = findViewById(R.id.editBranch);

        editNoRekening = findViewById(R.id.editNoRekening);
        editTanggalLahir = findViewById(R.id.editTanggalLahir);
        editNamaIbuKandung = findViewById(R.id.editNamaIbuKandung);
        editNoHandphone = findViewById(R.id.editNoHandphone);

        final MaterialButton actLanjutkan = findViewById(R.id.actLanjutkan);
        final MaterialCheckBox editAgree = findViewById(R.id.editAgree);

        editNoRekening.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        editTanggalLahir.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        editNamaIbuKandung.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        editNoHandphone.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        editAgree.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_regular));
        actLanjutkan.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.overpass_bold));

        editTanggalLahir.setText(sdf.format(new Date()));
        editTanggalLahir.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editTanggalLahir.setText(sdf.format(myCalendar.getTime()));
            };

            new DatePickerDialog(
                    RegisterActivity.this,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });


        actLanjutkan.setEnabled(false);

        editAgree.setOnClickListener(view -> {
            if (((MaterialCheckBox) view).isChecked()) {
                actLanjutkan.setEnabled(true);
                actLanjutkan.setBackgroundTintList(getResources().getColorStateList(R.color.colorBiru));

            }
        });

        actLanjutkan.setOnClickListener(v -> {

            String branch = editKeyBranch.getText().toString();
            if(!TextUtils.isEmpty(branch)){

                //cekDataBranch();

                cekDataBranchAsyncTask();

            }else{
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Perhatian")
                        .setMessage("Silahkan pilih Branch dahulu!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                dialog.dismiss();
                            }
                        })
                        .show();

                //Toast.makeText(v.getContext(),"Silahkan pilih Branch",Toast.LENGTH_SHORT).show();

            }

        });


        /**
         * INIT
         */
        initBranchAsyncTask();
        //initBranch();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onStart() {
        super.onStart();
    }

    public void onBackPressed() {
        super.onBackPressed();
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


    /**
     * INIT BRANCH
     */

    @SuppressLint("NewApi")
    private static void initBranchAsyncTask() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,DataHelper.getAlamatServer() + "/informasi_bank",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (!response.equals("")) {
                            JSONArray json = new JSONArray(response);

                            ArrayList<Branch> branchArrayList = new ArrayList<>();

                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                if(jsonObject.getString("status_view").equalsIgnoreCase("I")){
                                    //items.add( jsonObject.getString("nama") );
                                    branchArrayList.add(new Branch(
                                            jsonObject.getString("branch"),
                                            jsonObject.getString("nama"),
                                            jsonObject.getString("link_logo_bank"),
                                            jsonObject.getString("status_view"),
                                            jsonObject.getString("status_izin"),
                                            jsonObject.getString("link_api")
                                    ));
                                }
                            }

                            if(branchArrayList.size() > 0){

                                ArrayAdapter arrayAdapter = new AdapterBranch(context, R.layout.item_branch, branchArrayList);
                                editBranch.setAdapter(arrayAdapter);
                                editBranch.setOnItemClickListener((parent, view, position, id) -> {
                                    String selected = branchArrayList.get(position).nama;
                                    editBranch.setText(selected,false);
                                    editKeyBranch.setText(branchArrayList.get(position).branch);
                                    editKeyBranchLinkApi.setText(branchArrayList.get(position).link_api);

                                });

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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
     * CEK DATA BRANCH
     */
    @SuppressLint("NewApi")
    private void cekDataBranchAsyncTask() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String branchLink = editKeyBranchLinkApi.getText().toString();
        String branch = editKeyBranch.getText().toString();
        String norek = editNoRekening.getText().toString();
        String tgl_lahir = editTanggalLahir.getText().toString();
        String nama_ibu = editNamaIbuKandung.getText().toString();
        String no_hp = editNoHandphone.getText().toString();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,branchLink +"banking.php?"+
                "ib=informasi_nasabah"+
                "&branch="+branch+
                "&norek="+norek+
                "&tgl_lahir="+tgl_lahir+
                "&nama_ibu="+nama_ibu+
                "&no_hp="+no_hp,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try{

                        JSONObject response_json = new JSONObject(response);
                        JSONObject jsonObject = response_json.getJSONObject("data");

                        if(!jsonObject.getString("norekening").isEmpty()){

                            /**
                             * CEK DATA IBANK
                             */
                            cekDataBranchIbankingAsyncTask(branchLink,branch,norek);


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



    @SuppressLint("NewApi")
    private void cekDataBranchIbankingAsyncTask(String branchLink, String branch, String norek) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        StringRequest serverRequest = new StringRequest(Request.Method.GET,branchLink +"banking.php?"+
                "ib=cek_user_ibanking"+
                "&branch="+branch+
                "&norek="+norek,
                response -> {
                    progressBar.setVisibility(View.GONE);

                    JSONObject response_json2 = null;
                    try {
                        response_json2 = new JSONObject(response);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        if(jsonObject2.getInt("sukses") > 0){
                            //dibuat jadi error saja nanti

                            new AlertDialog.Builder(context)
                                    .setTitle("Perhatian")
                                    .setMessage("Anda sudah pernah membuat akun ibanking!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                            //Toast.makeText(getApplicationContext(),"Anda sudah pernah membuat akun ibanking",Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent(context, RegisterPinActivity.class);
                            intent.putExtra("link_api",branchLink);
                            intent.putExtra("branch",branch);
                            intent.putExtra("norek",norek);
                            startActivity(intent);
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






}