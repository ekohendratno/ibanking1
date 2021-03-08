package id.kopas.berkarya.said.ibanking.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.print.PrintHelper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.kopas.berkarya.said.ibanking.MainActivity;
import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.adapters.AdapterBranch;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.CacheRequest;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.fun.TextViewCustom;
import id.kopas.berkarya.said.ibanking.models.Branch;

public class PageTransferReport extends AppCompatActivity {
    static RelativeLayout progressBar;


    static SharedPreferences sharedpreferences;

    DataHelper dataHelper;
    static Context context;

    String report_tanggal, report_norefrensi, report_sumberdana_nama, report_sumberdana_norek;
    String report_jenistransaksi, report_rektujuan_nama, report_rektujuan_norek, report_nominal;
    String report_biayaadmin, report_total;
    MaterialButton actLanjutkan;
    CoordinatorLayout coordinatorLayout;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_down);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        context = PageTransferReport.this;

        dataHelper = new DataHelper(getApplicationContext());


        Intent intentData = getIntent();
        report_tanggal = intentData.getStringExtra("report_tanggal");
        report_norefrensi = intentData.getStringExtra("report_norefrensi");
        report_sumberdana_nama = intentData.getStringExtra("report_sumberdana_nama");
        report_sumberdana_norek = intentData.getStringExtra("report_sumberdana_norek");
        report_jenistransaksi = intentData.getStringExtra("report_jenistransaksi");
        report_rektujuan_nama = intentData.getStringExtra("report_rektujuan_nama");
        report_rektujuan_norek = intentData.getStringExtra("report_rektujuan_norek");
        report_nominal = intentData.getStringExtra("report_nominal");
        report_biayaadmin = intentData.getStringExtra("report_biayaadmin");
        report_total = intentData.getStringExtra("report_total");


        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        TextViewCustom tv0 = findViewById(R.id.report_tanggal);
        TextViewCustom tv1 = findViewById(R.id.report_norefrensi);
        TextViewCustom tv2 = findViewById(R.id.report_sumberdana_nama);
        TextViewCustom tv3 = findViewById(R.id.report_sumberdana_norek);
        TextViewCustom tv4 = findViewById(R.id.report_jenistransaksi);
        TextViewCustom tv5 = findViewById(R.id.report_rektujuan_nama);
        TextViewCustom tv6 = findViewById(R.id.report_rektujuan_norek);
        TextViewCustom tv7 = findViewById(R.id.report_nominal);
        TextViewCustom tv8 = findViewById(R.id.report_biayaadmin);
        TextViewCustom tv9 = findViewById(R.id.report_total);

        tv0.setText(report_tanggal);
        tv1.setText(report_norefrensi);
        tv2.setText(report_sumberdana_nama);
        tv3.setText(report_sumberdana_norek);
        tv4.setText(report_jenistransaksi);
        tv5.setText(report_rektujuan_nama);
        tv6.setText(report_rektujuan_norek);
        tv7.setText(dataHelper.formatRupiah(report_nominal));
        tv8.setText(dataHelper.formatRupiah(report_biayaadmin));
        tv9.setText(dataHelper.formatRupiah(report_total));

        actLanjutkan = findViewById(R.id.actLanjutkan);
        actLanjutkan.setEnabled(true);
        actLanjutkan.setBackgroundTintList(getResources().getColorStateList(R.color.colorBiru));

        actLanjutkan.setOnClickListener(v -> {
            backToMain();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        actLanjutkan.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        backToMain();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else if(item.getItemId() == R.id.action_send){
            actLanjutkan.setVisibility(View.INVISIBLE);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            View u = findViewById(R.id.scroll1);
            NestedScrollView z = (NestedScrollView) findViewById(R.id.scroll1);

            Bitmap bitmap = getTakeScreenShot(u, z);

            /**PrintHelper printHelper = new PrintHelper(PageTransferReport.this);
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap("Print Bitmap", bitmap);*/

            simpanBitmap(bitmap);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void backToMain(){

        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public Bitmap getTakeScreenShot(View u, NestedScrollView z) {
        int totalHeight = z.getChildAt(0).getHeight();
        int totalWidth = z.getChildAt(0).getWidth();

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = u.getBackground();
        if (bgDrawable != null) bgDrawable.draw(canvas);
        else canvas.drawColor(Color.WHITE);
        u.draw(canvas);
        return returnedBitmap;
    }

    public void simpanBitmap(Bitmap b){
        final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + getString(R.string.app_name);

        File folder = new File(path);
        if(!folder.exists()) folder.mkdir();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault());
        String tanggal_dibuat = sdf.format(new Date());
        // Create a path where we will place our List of objects on external storage
        File file = new File(path, tanggal_dibuat+".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Log.w("FileUtils", "Writing file" + file);


            Snackbar.make(coordinatorLayout, "Export Data " + file.toString() + " success!", Snackbar.LENGTH_LONG)
                    .setAction("Buka File", v -> dataHelper.openFile(context, file))
                    .show();

            actLanjutkan.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != fos)
                    fos.close();
            } catch (Exception ex) {
            }
        }


    }




}
