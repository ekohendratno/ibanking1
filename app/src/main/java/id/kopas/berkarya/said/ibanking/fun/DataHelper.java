package id.kopas.berkarya.said.ibanking.fun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataHelper  implements LocationListener {

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

    public static String getIdUnique() {

        String uniqueID = null;
        final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(MyBackupAgent.PREFS, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();

                //backup the changes
                BackupManager mBackupManager = new BackupManager(context);
                mBackupManager.dataChanged();
            }
        }

        return uniqueID;

    }

    public String formatRupiah(String number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(Double.parseDouble(number));
    }

    @SuppressLint("MissingPermission")
    public String getGPSCoordinates(int promptUserLocationOn, final Boolean showPromptToast, String sendSMSToNumber) {
        //promptUserLocationOn=Number of times to forcibly take user to Location settings
        //showPromptToast=Whether or not to show a toast message to user prompting to enable Location Service
        //sendSMSToNumber=Number to send latlong SMS to.. Can be "" in which case SMS will not be sent
        String latLong = "";
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            while (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) && promptUserLocationOn > 0) {
                //if neither GPS nor NETWORK location provider is enabled,
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (showPromptToast)
                            Toast.makeText(context.getApplicationContext(), "Google Play Services requires Location Services to be enabled.", Toast.LENGTH_LONG).show();
                    }
                });
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException iex) {
                }
                promptUserLocationOn--;
            }
            Location location = null;
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //use gps
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 0, this, Looper.getMainLooper());
                try {
                    DataHelper.waitWithTimeoutN(new Callable() {
                        @Override
                        public Object call() throws Exception {
                            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }, null, 3 * 60 * 1000);//wait for GPS based location. this takes a couple minutes
                } catch (Exception ex) {
                    Log.w(DataHelper.getTAG(), ex.getMessage());
                }
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (location == null) {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    //use network
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50, 0, this, Looper.getMainLooper());
                    try {
                        DataHelper.waitWithTimeoutN(new Callable() {
                            @Override
                            public Object call() throws Exception {
                                return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }
                        }, null, 5000);//network based location is quick. only wait 5s
                    } catch (Exception ex) {
                        Log.w(DataHelper.getTAG(), ex.getMessage());
                    }
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if (location != null) {
                Log.w(DataHelper.getTAG(), "Lat : " + Double.toString(location.getLatitude()) + "\n" + "Long : " + Double.toString(location.getLongitude()));
                latLong = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
            } else {
                Log.w(DataHelper.getTAG(), "No location found");
            }
        }
        return latLong;
    }








    static void waitWithTimeoutN(Callable testCallable, Object continueValue, long timeoutmillisecond) throws Exception {
        long initmillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - initmillis < timeoutmillisecond) {
            if (testCallable.call() != continueValue) break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void openFile(Context c, File filepath){
        //Log.e("a",filepath);

        String str = "com.microsoft.office.excel";
        //Uri pathe = Uri.fromFile(new File(filepath+".xls"));


        //Uri pathe = FileProvider.getUriForFile(c, c.getApplicationContext().getPackageName() + ".provider", new File(filepath));
        Uri pathe = Uri.fromFile(filepath);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(pathe, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PackageManager packageManager = c.getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            c.startActivity(intent.createChooser(intent, "Choose app to open document"));
        }
        else
        {
            Toast.makeText(c, "Aplikasi Excel belum tersedia harap download dahulu dari Play Store", Toast.LENGTH_SHORT).show();
            //Launch PlayStore
            try {
                c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+str)));

            } catch (android.content.ActivityNotFoundException n) {
                c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+str)));
            }
        }

    }
}
