package uoft.assignment3;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;

import android.widget.Toast;



@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    private ShakeListener mShaker;
    public double longitude;
    public double latitude;
    Preview preview;
    Camera camera;
    Activity act;
    Context ctx;
    Button buttonClick;
    LocationManager mlocManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        act = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // preview
        preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);
        preview.setKeepScreenOn(true);
        Toast.makeText(ctx, getString(R.string.take_photo_help),Toast.LENGTH_SHORT).show();

        // gps
        Location location = getLastKnownLocation();
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.i("asdf",location.toString());
            }else{
                Log.i("location", "nothing");
            }

        //shake it off
        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(
                new ShakeListener.OnShakeListener() {
                    public void onShake() {
                        Toast.makeText(MainActivity.this, "Taking picture in 1 second", Toast.LENGTH_SHORT).show();
                        new CountDownTimer(1000, 1000) {
                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                Toast.makeText(MainActivity.this, "Picture Took ", Toast.LENGTH_SHORT).show();
                                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                            }
                        }.start();
                    }
                });

        // button click to view pictures;
        buttonClick = (Button) findViewById(R.id.display);
        buttonClick.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(ctx, "buttonbutton", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ctx, ImageGallery.class);
                //String message = "/Assignment3";
                //intent.putExtra("FILELOCATION", message);
                startActivity(intent);
            }
        });
    }
    private Location getLastKnownLocation() {
        mlocManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mlocManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            try{
                Location l = mlocManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }catch (SecurityException e){
                Log.i("exception",provider);
            }
        }
        return bestLocation;
    }
    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open(0);
                camera.startPreview();
                preview.setCamera(camera);
            } catch (RuntimeException ex){
                Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
        mShaker.resume();
    }

    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        mShaker.pause();

        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;
            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/Assignment3");
                dir.mkdirs();
                Time now = new Time();
                now.setToNow();
                String time = now.format2445();
                String fileName = String.format("Lat%.2f,Lon%.2f,%s.jpg", latitude,longitude,time);
                Log.i("filename",fileName);
                File outFile = new File(dir, fileName);
                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();
                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
