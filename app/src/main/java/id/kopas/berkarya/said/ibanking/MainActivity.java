package id.kopas.berkarya.said.ibanking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import id.kopas.berkarya.said.ibanking.pages.nav.PageNavAkun;
import id.kopas.berkarya.said.ibanking.pages.nav.PageNavDashboard;
import id.kopas.berkarya.said.ibanking.pages.nav.PageNavHistori;

public class MainActivity extends AppCompatActivity {

    private static BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation1:

                        getFragment(PageNavDashboard.newInstance());

                        return true;
                    case R.id.navigation2:

                        getFragment(PageNavHistori.newInstance());

                        return true;
                    case R.id.navigation3:

                        getFragment(PageNavAkun.newInstance());

                        return true;
                }

                return false;
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {

            getFragment( PageNavDashboard.newInstance() );

        }
    }

    public void getFragment(Fragment fr){
        FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
        ft4.replace(R.id.frame_container, fr);
        ft4.commit();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}