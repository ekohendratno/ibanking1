package id.kopas.berkarya.said.ibanking.pages;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.Splash;
import id.kopas.berkarya.said.ibanking.fun.DataHelper;

public class PageEtc extends AppCompatActivity {
    static SharedPreferences sharedpreferences;
    DataHelper dataHelper;
    Context context;
    String link_api, branch, norek, pinib;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_pembayaran);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_down);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        link_api = sharedpreferences.getString("link_api", "");
        branch = sharedpreferences.getString("branch", "");
        norek = sharedpreferences.getString("norek", "");
        pinib = sharedpreferences.getString("pinib", "");

        context = PageEtc.this;
        dataHelper = new DataHelper(context);



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
