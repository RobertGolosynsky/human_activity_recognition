package org.cra.contextrecognition.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

import com.xw.repo.BubbleSeekBar;

import org.cra.contextrecognition.R;
import org.cra.contextrecognition.model.State;
import org.cra.contextrecognition.sensors.BackgroundAccelerometerService;
import org.cra.contextrecognition.services.UserService;
import org.cra.contextrecognition.network.service.RetrofitService;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BubbleSeekBar.OnProgressChangedListener {
    private BubbleSeekBar bubbleSeekBar;
    private Button startStopButton;
    private State currentState = State.values()[0];
    private boolean isRecording = false;
    private UserService userService = new UserService();
    private String apiToken;
    private Retrofit retrofit ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiToken = userService.getApiToken(this);
        if (apiToken == null) {
            promptLogIn();
        }
        setContentView(R.layout.activity_main);

        retrofit = RetrofitService.getRetrofit(apiToken);

        bubbleSeekBar = findViewById(R.id.seek_bar);
        bubbleSeekBar.getConfigBuilder()
                .min(0)
                .max(2)
                .progress(0)
                .sectionCount(2)
                .trackColor(ContextCompat.getColor(this, R.color.color_gray))
                .secondTrackColor(ContextCompat.getColor(this, R.color.color_blue))
                .thumbColor(ContextCompat.getColor(this, R.color.color_blue))
                .showSectionText()
                .sectionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .sectionTextSize(18)
                .showThumbText()
                .thumbTextColor(ContextCompat.getColor(this, R.color.color_red))
                .thumbTextSize(18)
                .bubbleColor(ContextCompat.getColor(this, R.color.color_red))
                .bubbleTextSize(18)
                .showSectionMark()
                .seekStepSection()
                .hideBubble()
                .touchToSeek()
                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();

        bubbleSeekBar.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                State[] states = State.values();
                for (int i = 0; i < states.length; i++) {
                    array.append(i, stateStringFor(states[i]));
                }
                return array;
            }
        });
        bubbleSeekBar.setOnProgressChangedListener(this);

        startStopButton = findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(this);
        startStopButton.setText(getString(R.string.start)+" "+stateStringFor(currentState));
    }


    private void promptLogIn() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }


    @Override
    public void onClick(View v) {
        isRecording = !isRecording;
        if (isRecording){
            int c = ResourcesCompat.getColor(getResources(),R.color.color_gray,null);
            int buttonColor = ResourcesCompat.getColor(getResources(),R.color.color_red,null);
            startStopButton.setText(getString(R.string.stop)+" "+stateStringFor(currentState));
            startStopButton.setBackgroundColor(buttonColor);
            bubbleSeekBar.setSecondTrackColor(c);
            bubbleSeekBar.setThumbColor(c);
            bubbleSeekBar.setEnabled(false);
            startRecording();
        }
        else {
            int c = ResourcesCompat.getColor(getResources(),R.color.color_blue,null);
            int buttonColor = ResourcesCompat.getColor(getResources(),R.color.color_green,null);
            startStopButton.setText(getString(R.string.start)+" "+stateStringFor(currentState));
            startStopButton.setBackgroundColor(buttonColor);
            bubbleSeekBar.setSecondTrackColor(c);
            bubbleSeekBar.setThumbColor(c);
            bubbleSeekBar.setEnabled(true);
            stopRecording();
        }

    }

    private void startRecording(){
        Intent intent = new Intent(this, BackgroundAccelerometerService.class);
//        Log.e(LOG_TAG, "in onPressStartService, userFilePath is "+userFilepath);
        startService(intent);
    }

    private void stopRecording(){
        stopService(new Intent(getApplicationContext(), BackgroundAccelerometerService.class));
    }

    private String stateStringFor(State state) {
        switch (state) {
            case WALK:
                return getString(R.string.state_walk);
            case STAND:
                return getString(R.string.state_stand);
            case SIT:
                return getString(R.string.state_sit);
            default:
                return "";
        }

    }

    @Override
    public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
    }

    @Override
    public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
        currentState = State.fromLiteral(progress);
        startStopButton.setText(getString(R.string.start)+" "+stateStringFor(currentState));
    }

    @Override
    public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
    }
}

//        webApi.saveGyroData(recordList).enqueue(new CRACallback<CRABasicResponse>() {
//            @Override
//            public void onSuccess(Call<CRABasicResponse> call, Response<CRABasicResponse> response) {
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(),
//                                "Successfully sent the readings",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Call<CRABasicResponse> call, Response<CRABasicResponse> response, final CRAErrorResponse errorResponse) {
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(),
//                                "Readings were not sent: "+errorResponse.getText(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Call<CRABasicResponse> call, Throwable t) {
//                super.onFailure(call, t);
//
//            }
//        });
