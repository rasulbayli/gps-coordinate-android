package ca.rasuldadashbayli;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;

import java.util.Locale;

public class MainActivity extends Activity {
    private static final int LOCATION_PERMISSION = 41;
    private LocationManager locationManager;
    private TextView latitudeValue, longitudeValue, accuracyValue, statusValue, updatedValue;
    private Button locateButton;
    private LocationListener listener;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(244,248,246));
        getWindow().setNavigationBarColor(Color.rgb(244,248,246));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        buildScreen();
        refreshPermissionState();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);
        page.setPadding(dp(24), dp(24), dp(24), dp(24));
        page.setBackgroundColor(Color.WHITE);
        scroll.addView(page);

        TextView title = text("My Location", 28, 0xFF16352F, true);
        title.setGravity(Gravity.CENTER);
        page.addView(title, params(-1,-2));

        TextView subtitle = text("Current GPS coordinates", 16, 0xFF687A75, false);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = params(-1,-2); lp.topMargin = dp(8); lp.bottomMargin = dp(30);
        page.addView(subtitle, lp);

        latitudeValue = text("--.------°", 24, 0xFF16352F, true);
        latitudeValue.setGravity(Gravity.CENTER);
        page.addView(latitudeValue, params(-1,-2));
        TextView latLabel = text("Latitude", 14, 0xFF687A75, false);
        latLabel.setGravity(Gravity.CENTER);
        lp = params(-1,-2); lp.topMargin=dp(4); lp.bottomMargin=dp(20); page.addView(latLabel,lp);

        longitudeValue = text("--.------°", 24, 0xFF16352F, true);
        longitudeValue.setGravity(Gravity.CENTER);
        page.addView(longitudeValue, params(-1,-2));
        TextView lonLabel = text("Longitude", 14, 0xFF687A75, false);
        lonLabel.setGravity(Gravity.CENTER);
        lp = params(-1,-2); lp.topMargin=dp(4); lp.bottomMargin=dp(24); page.addView(lonLabel,lp);

        accuracyValue = text("Accuracy: waiting for GPS", 14, 0xFF687A75, false);
        accuracyValue.setGravity(Gravity.CENTER);
        page.addView(accuracyValue, params(-1,-2));
        statusValue = text("Tap below to get your location.", 14, 0xFF687A75, false);
        statusValue.setGravity(Gravity.CENTER);
        lp=params(-1,-2);lp.topMargin=dp(8);page.addView(statusValue,lp);

        locateButton = new Button(this);
        locateButton.setText("Get my location");
        locateButton.setTextColor(Color.WHITE);
        locateButton.setAllCaps(false);
        locateButton.setTextSize(16);
        locateButton.setBackground(round(0xFF137C69,12));
        lp=params(-1,dp(52));lp.topMargin=dp(20);page.addView(locateButton,lp);
        locateButton.setOnClickListener(v -> beginLocation());
        setContentView(scroll);
    }
    private void refreshPermissionState() {
        if (hasLocationPermission()) { statusValue.setText("Ready to find your GPS position."); locateButton.setText("Get my location"); }
        else { statusValue.setText("Location permission is needed to show your position."); locateButton.setText("Allow location access"); }
    }

    private void beginLocation() {
        if (!hasLocationPermission()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            return;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            statusValue.setText("Turn on Location in Android settings, then try again."); return;
        }
        statusValue.setText("Searching for GPS satellites…");
        accuracyValue.setText("Acquiring GPS fix…");
        locateButton.setText("Finding your location…");
        listener = new LocationListener() {
            @Override public void onLocationChanged(Location location) { showLocation(location); }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { statusValue.setText("GPS is turned off."); }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, listener, Looper.getMainLooper());
            Location cached=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(cached!=null) showLocation(cached);
        } catch (SecurityException ignored) { refreshPermissionState(); }
    }

    private void showLocation(Location l) {
        latitudeValue.setText(String.format(Locale.US,"%.6f°",l.getLatitude()));
        longitudeValue.setText(String.format(Locale.US,"%.6f°",l.getLongitude()));
        accuracyValue.setText(l.hasAccuracy()?String.format(Locale.US,"Accuracy: ± %.0f m",l.getAccuracy()):"GPS fix received");
        statusValue.setText("GPS fix received · updates automatically");
        locateButton.setText("Refresh GPS location");
    }

    private boolean hasLocationPermission() { return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED; }
    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] results) {
        super.onRequestPermissionsResult(requestCode,permissions,results);
        if(requestCode==LOCATION_PERMISSION && results.length>0 && results[0]==PackageManager.PERMISSION_GRANTED) beginLocation();
        else { statusValue.setText("Permission denied. Allow location access to continue."); locateButton.setText("Try permission again"); }
    }
    @Override protected void onPause() { super.onPause(); if(listener!=null){ locationManager.removeUpdates(listener); listener=null; } }

    private TextView text(String s,int size,int color,boolean bold) { TextView t=new TextView(this);t.setText(s);t.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);return t; }
    private GradientDrawable round(int color,int radius) { GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d; }
    private LinearLayout.LayoutParams params(int w,int h) { return new LinearLayout.LayoutParams(w,h); }
    private int dp(int n) { return (int)(n*getResources().getDisplayMetrics().density+0.5f); }
}




