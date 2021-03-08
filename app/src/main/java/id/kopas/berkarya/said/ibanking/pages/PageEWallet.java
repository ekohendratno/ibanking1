package id.kopas.berkarya.said.ibanking.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.fun.AppController;
import id.kopas.berkarya.said.ibanking.fun.CacheRequest;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;
import id.kopas.berkarya.said.ibanking.fun.TextViewCustom;

public class PageEWallet extends AppCompatActivity {
    static SharedPreferences sharedpreferences;
    DataHelper dataHelper;
    Context context;
    String link_api, branch, branch_nama, nama, norek, pinib;
    static RelativeLayout progressBar;
    ImageView imageView;
    TextViewCustom tv1, tv2, tv3;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_ewallet);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_down);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        link_api = sharedpreferences.getString("link_api", "");
        branch = sharedpreferences.getString("branch", "");
        branch_nama = sharedpreferences.getString("branch_nama", "");
        nama = sharedpreferences.getString("nama", "");
        norek = sharedpreferences.getString("norek", "");
        pinib = sharedpreferences.getString("pinib", "");

        context = PageEWallet.this;
        dataHelper = new DataHelper(context);

        progressBar = (RelativeLayout) findViewById(R.id.progressBar);
        imageView = findViewById(R.id.qrcode);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);

        tv1.setText(nama);
        tv2.setText(branch_nama);
        tv3.setText(norek);

        /**
         * INIT CEK
         */

        cekDataBranchIbankingAsyncTask();
    }


    /**
     * CEK DATA IBANK
     *
     */
    @SuppressLint("NewApi")
    private void cekDataBranchIbankingAsyncTask() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        CacheRequest serverRequest = new CacheRequest(Request.Method.GET,link_api +"banking.php?"+
                "ib=validasi_login_ibanking"+
                "&branch="+branch+
                "&norek="+norek+
                "&pin_ib="+pinib,
                req -> {
                    progressBar.setVisibility(View.GONE);
                    JSONObject response_json2 = null;
                    try {
                        final String jsonString = new String(req.data, HttpHeaderParser.parseCharset(req.headers));

                        response_json2 = new JSONObject(jsonString);
                        JSONObject jsonObject2 = response_json2.getJSONObject("data");


                        if(jsonObject2.getString("status_transfer").equalsIgnoreCase("A")){
                            //dibuat jadi error saja nanti


                            //barcode

                            String text =""; // Whatever you need to encode in the QR code
                            if(!jsonObject2.getString("barcode").isEmpty()){
                                text = jsonObject2.getString("barcode");
                            }

                            Bitmap bitmap = textToImageEncode(text);
                            imageView.setImageBitmap(bitmap);
                            imageView.invalidate();


                        }else{
                            new AlertDialog.Builder(context)
                                    .setTitle("Info")
                                    .setMessage("Maaf untuk rekening ini status transfer tidak dapat dilakukkan!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            dialog.dismiss();

                                        }
                                    })
                                    .show();
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
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

    private Bitmap textToImageEncode(String text) {
        BitMatrix bitMatrix = null;
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);

        }catch (WriterException e) {
            e.printStackTrace();
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        for (int x = 0; x < bitMatrixHeight; x++){
            for (int y = 0; y < bitMatrixWidth; y++){
                bitmap.setPixel(x, y, bitMatrix.get(x,y) ? Color.parseColor("#000000") : Color.parseColor("#ffffff"));
            }
        }
        
        
        return bitmap;
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
