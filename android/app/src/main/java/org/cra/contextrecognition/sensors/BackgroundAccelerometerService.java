package org.cra.contextrecognition.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.cra.contextrecognition.network.domain.CRABasicResponse;
import org.cra.contextrecognition.network.domain.CRAErrorResponse;
import org.cra.contextrecognition.network.domain.GyroRecord;
import org.cra.contextrecognition.network.service.CRACallback;
import org.cra.contextrecognition.network.service.CRAWebApi;
import org.cra.contextrecognition.network.service.RetrofitService;
import org.cra.contextrecognition.services.ReadingsSaverService;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class BackgroundAccelerometerService extends Service implements SensorEventListener{

    static final String LOG_TAG = BackgroundAccelerometerService.class.getSimpleName();
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long lastTime = 0;
    private int period = 10; //ms
    private List<GyroRecord> recordList = new ArrayList<>();
    private ReadingsSaverService readingsSaverService = new ReadingsSaverService();


    public BackgroundAccelerometerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service Started","Service Started");


        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSensorManager.unregisterListener(this);
        readingsSaverService.saveReadings(getApplicationContext(),recordList);
        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) mInitialized = true;
        long tsLong = System.currentTimeMillis();
        if (tsLong > lastTime+period) {
            lastTime = tsLong;
            recordAccelData(x, y, z, tsLong);
        }
    }

    public void recordAccelData(float x, float y, float z, Long tsLong){
        recordList.add(new GyroRecord(x,y,z));
    }
}
