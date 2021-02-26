package id.kopas.berkarya.said.ibanking.fun;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataHelper {

    private static final String restapi_url = "https://bithousetech.com/restapi-ib/api";
    public static final String TAG = "ibanking";

    public static String getTAG() {
        return TAG;
    }

    static Context context;

    public DataHelper(Context context) {
        this.context = context;
    }

    public static String getAlamatServer() {
        return restapi_url;
    }


    //panggil saat sync data
    public static String getServer(String requestPath) throws IOException {
        Log.e("getDariServer", requestPath);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(requestPath)
                .build();
        Response response = client.newCall(request).execute();
        String response_body = response.body().string();

        return response_body;

    }

    //panggil saat sync data
    public static String getPhoneNumber() {
        String mPhoneNumber = "";
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumber = tMgr.getLine1Number();
        }
        return mPhoneNumber;

    }
}
