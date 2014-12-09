package com.osmsample.gkn.osmdroidsample;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends Activity {

    int tileSize = 256;
    private MapView mapView;
    // area of offline tiles
    double north = 40.739063;
    double south = 40.708361;
    double west = -73.967171;
    double east = -73.936272;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// center of offline tiles
        double centerlat = (double) ((north + south) / 2);
        double centerlon = (double) ((west + east) / 2);
// copy tiles to sd location for offline map
        putMapOnSD();
// create mapView and show layout
        mapView = new MapView(this, tileSize);
        final LinearLayout layout = new LinearLayout(this);
        final LinearLayout.LayoutParams mapViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        layout.addView(mapView, mapViewLayoutParams);
        setContentView(layout);
// set map to use offline tiles and display
        String[] arr = new String[1];
        arr[0] = "http://127.0.0.1";
        mapView.setTileSource(new XYTileSource("tiles", ResourceProxy.string.offline_mode, 13, 17, tileSize, ".png", arr));
        mapView.setUseDataConnection(false);
        mapView.setClickable(false);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);
        mapView.getController().setZoom(15);
        mapView.getController().setCenter(new GeoPoint(centerlat, centerlon));
// show pathOverlay
        PathOverlay pathOverlay = new PathOverlay(Color.RED, this);
        pathOverlay.addPoint(new GeoPoint(centerlat, centerlon));
        centerlat += 0.005;
        pathOverlay.addPoint(new GeoPoint(centerlat, centerlon));
        centerlon += 0.005;
        pathOverlay.addPoint(new GeoPoint(centerlat, centerlon));
        pathOverlay.getPaint().setStrokeWidth(10.0f);
        mapView.getOverlays().add(pathOverlay);
//        AddPolygon();
// refresh map, is this needed?
        mapView.invalidate();
    }

    private void AddPolygon() {
        int diff=1000;

        GeoPoint pt1=new GeoPoint(13.002798, 77.580000);
        GeoPoint pt2= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6());
        GeoPoint pt3= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6()+diff);
        GeoPoint pt4= new GeoPoint(pt1.getLatitudeE6(), pt1.getLongitudeE6()+diff);
        GeoPoint pt5= new GeoPoint(pt1);


        PathOverlay myOverlay= new PathOverlay(Color.RED, this);

        myOverlay.getPaint().setStyle(Paint.Style.FILL);

        myOverlay.addPoint(pt1);
        myOverlay.addPoint(pt2);
        myOverlay.addPoint(pt3);
        myOverlay.addPoint(pt4);
        myOverlay.addPoint(pt5);

        mapView.getOverlays().add(myOverlay);
    }

    // this copies the offline tiles to the proper location for OSMDroid to use them offline
    private void putMapOnSD() {
        new File("/mnt/sdcard/osmdroid/").mkdir();
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("debug", e.getMessage());
        }
        File mapsdfile = new File("/mnt/sdcard/osmdroid/tiles.zip");
        if (!mapsdfile.exists()) {
            for (String filename : files) {
                if (filename.contains("tiles")) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = assetManager.open(filename);
                        out = new FileOutputStream("/sdcard/osmdroid/" + filename);
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                        Log.v("debug", "Map file added to " + mapsdfile);
                    } catch (Exception e) {
                        Log.e("debug", e.getMessage());
                    }
                }
            }
        }
    }



    /*private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mMapView = new MapView(this, 256, new DefaultResourceProxyImpl(this));
        setContentView(mMapView);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
