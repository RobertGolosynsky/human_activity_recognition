package org.cra.contextrecognition.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xw.repo.BubbleSeekBar;

import org.cra.contextrecognition.R;
import org.cra.contextrecognition.model.State;
import org.cra.contextrecognition.network.service.RetrofitService;
import org.cra.contextrecognition.sensors.BackgroundAccelerometerService;
import org.cra.contextrecognition.services.UserService;

import retrofit2.Retrofit;

public class FragmentRecord extends SightFragment implements BubbleSeekBar.OnProgressChangedListener, View.OnClickListener {

    private BubbleSeekBar bubbleSeekBar;
    private Button startStopButton;
    private State currentState = State.values()[0];
    private boolean isRecording = false;

    private UserService userService = new UserService();
    private String apiToken;
    private Retrofit retrofit ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiToken = userService.getApiToken(getContext());
        retrofit = RetrofitService.getInstance();

        bubbleSeekBar = view.findViewById(R.id.seek_bar);
        bubbleSeekBar.getConfigBuilder()
                .min(0)
                .max(2)
                .progress(0)
                .sectionCount(2)
                .trackColor(ContextCompat.getColor(getActivity(), R.color.color_gray))
                .secondTrackColor(ContextCompat.getColor(getActivity(), R.color.color_blue))
                .thumbColor(ContextCompat.getColor(getActivity(), R.color.color_blue))
                .showSectionText()
                .sectionTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .sectionTextSize(18)
                .showThumbText()
                .thumbTextColor(ContextCompat.getColor(getActivity(), R.color.color_red))
                .thumbTextSize(18)
                .bubbleColor(ContextCompat.getColor(getActivity(), R.color.color_red))
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

        startStopButton = view.findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(this);
        startStopButton.setText(getString(R.string.start)+" "+stateStringFor(currentState));
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
        Intent intent = new Intent(getActivity(), BackgroundAccelerometerService.class);
        intent.putExtra("state", currentState.getCode());

//        Log.e(LOG_TAG, "in onPressStartService, userFilePath is "+userFilepath);
        getActivity().startService(intent);
    }

    private void stopRecording(){
        getActivity().stopService(new Intent(getActivity().getApplicationContext(), BackgroundAccelerometerService.class));
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
        currentState = State.fromCode(progress);
        startStopButton.setText(getString(R.string.start)+" "+stateStringFor(currentState));
    }

    @Override
    public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
    }
}
