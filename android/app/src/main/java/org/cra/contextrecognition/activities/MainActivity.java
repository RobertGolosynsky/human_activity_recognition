package org.cra.contextrecognition.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.xw.repo.BubbleSeekBar;

import org.cra.contextrecognition.R;
import org.cra.contextrecognition.fragments.FragmentModels;
import org.cra.contextrecognition.fragments.FragmentRecordings;
import org.cra.contextrecognition.fragments.FragmentRecord;
import org.cra.contextrecognition.model.State;
import org.cra.contextrecognition.network.service.RetrofitService;
import org.cra.contextrecognition.services.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private BubbleSeekBar bubbleSeekBar;
    private Button startStopButton;
    private State currentState = State.values()[0];
    private boolean isRecording = false;
    private UserService userService = new UserService();
    private String apiToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiToken = userService.getApiToken(this);
        if (apiToken == null || apiToken.length()==0) {
            promptLogIn();
        }
        else {
            RetrofitService.createRetrofit(apiToken);
        }

        ViewPager viewPager = findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new FragmentRecord(), "Record");
        adapter.addFragment(new FragmentRecordings(), "Saved readings");
        adapter.addFragment(new FragmentModels(), "Models");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void promptLogIn() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
