package id.kopas.berkarya.said.ibanking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.fun.TextViewCustom;
import id.kopas.berkarya.said.ibanking.models.Branch;

public class RegisterActivity extends AppCompatActivity {
    String TAG = "RegisterActivity";

    String akun, password;

    ProgressDialog progressDialog;

    static SharedPreferences sharedpreferences;

    AppCompatEditText editKeyBranch, editKeyBranchLinkApi;
    AutoCompleteTextView editBranch;
    TextInputEditText editNoRekening, editTanggalLahir, editNamaIbuKandung, editNoHandphone;


    DataHelper dataHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_down);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        dataHelper = new DataHelper(getApplicationContext());

        /**
         * Loading
         */






        /**
         * Find Id
         */

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

                cekDataBranch();

            }else{
                Toast.makeText(v.getContext(),"Silahkan pilih Branch",Toast.LENGTH_SHORT).show();

            }

        });


        /**
         * INIT
         */

        initBranch();

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
    private void initBranch(){

        try {

            //ArrayList<String> items = new ArrayList<>();
            ArrayList<Branch> branchArrayList = new ArrayList<>();
            String response_body = dataHelper.getServer(dataHelper.getAlamatServer() + "/informasi_bank" );
            JSONArray response_json = new JSONArray(response_body);

            for (int i = 0; i < response_json.length(); i++) {
                JSONObject jsonObject = response_json.getJSONObject(i);

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

            //ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            //editBranch.setAdapter(itemsAdapter);



            ArrayAdapter arrayAdapter = new AdapterBranch(RegisterActivity.this, R.layout.item_branch, branchArrayList);
            editBranch.setAdapter(arrayAdapter);
            editBranch.setOnItemClickListener((parent, view, position, id) -> {
                String selected = branchArrayList.get(position).nama;
                editBranch.setText(selected,false);
                editKeyBranch.setText(branchArrayList.get(position).branch);
                editKeyBranchLinkApi.setText(branchArrayList.get(position).link_api);
                /**
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("branch",branchArrayList.get(position).branch);
                editor.putString("branch_nama",branchArrayList.get(position).nama);
                editor.putString("branch_link_logo_bank",branchArrayList.get(position).link_logo_bank);
                editor.putString("branch_status_view",branchArrayList.get(position).status_view);
                editor.putString("branch_status_izin",branchArrayList.get(position).status_izin);
                editor.putString("branch_link_api",branchArrayList.get(position).link_api);
                editor.apply();*/


            });

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class AdapterBranch extends ArrayAdapter<Branch> {
        private final Context context;
        private final ArrayList<Branch> lists;

        public AdapterBranch(@NonNull Context context, int resource, @NonNull ArrayList<Branch> objects) {
            super(context, resource, objects);
            lists = objects;
            this.context = context;
        }

        @SuppressLint("ViewHolder")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.item_branch, parent, false);

            Branch item = lists.get(position);

            TextView tv1 = rowView.findViewById(R.id.tv1);
            tv1.setText(item.nama);

            return rowView;
        }
    }


    /**
     * CEK DATA BRANCH
     */


    @SuppressLint("NewApi")
    private void cekDataBranch(){

        String branchLink = editKeyBranchLinkApi.getText().toString();
        String branch = editKeyBranch.getText().toString();
        String norek = editNoRekening.getText().toString();
        String tgl_lahir = editTanggalLahir.getText().toString();
        String nama_ibu = editNamaIbuKandung.getText().toString();
        String no_hp = editNoHandphone.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("ib","informasi_nasabah");
        params.put("branch",editKeyBranch.getText().toString());
        params.put("norek",editNoRekening.getText().toString());
        params.put("tgl_lahir",editTanggalLahir.getText().toString());
        params.put("nama_ibu",editNamaIbuKandung.getText().toString());
        params.put("no_hp",editNoHandphone.getText().toString());

        String response_body = null;
        try {
            response_body = dataHelper.getServer(branchLink +"banking.php?"+
                    "ib=informasi_nasabah"+
                    "&branch="+branch+
                    "&norek="+norek+
                    "&tgl_lahir="+tgl_lahir+
                    "&nama_ibu="+nama_ibu+
                    "&no_hp="+no_hp
            );

            try{

                /**
                 * CEK DATA BANK
                 */
                JSONObject response_json = new JSONObject(response_body);
                JSONObject jsonObject = response_json.getJSONObject("data");

                if(!jsonObject.getString("norekening").isEmpty()){

                    /**
                     * CEK DATA IBANK
                     */
                    JSONObject response_json2 = null;
                    try{

                        String response_body2 = dataHelper.getServer(branchLink +"banking.php?"+
                                "ib=cek_user_ibanking"+
                                "&branch="+branch+
                                "&norek="+norek
                        );

                        response_json2 = new JSONObject(response_body2);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        if(jsonObject2.getInt("sukses") > 0){
                            //dibuat jadi error saja nanti
                            Toast.makeText(getApplicationContext(),"Anda sudah pernah membuat akun ibanking",Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent(RegisterActivity.this, RegisterPinActivity.class);
                            intent.putExtra("link_api",branchLink);
                            intent.putExtra("branch",branch);
                            intent.putExtra("norek",norek);
                            startActivity(intent);
                        }

                    }catch (JSONException e2){

                        e2.printStackTrace();

                    }


                }


            }catch (JSONException e1){
                Toast.makeText(getApplicationContext(),"Data yang anda masukkan tidak tepat",Toast.LENGTH_SHORT).show();

                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }








}