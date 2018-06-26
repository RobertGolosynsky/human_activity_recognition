package org.cra.contextrecognition.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.cra.contextrecognition.R;
import org.cra.contextrecognition.network.domain.CRAAuthRequest;
import org.cra.contextrecognition.network.domain.CRAAuthResponse;
import org.cra.contextrecognition.network.service.CRACallback;
import org.cra.contextrecognition.services.UserService;
import org.cra.contextrecognition.network.domain.CRAErrorResponse;
import org.cra.contextrecognition.network.service.CRAWebApi;
import org.cra.contextrecognition.network.service.RetrofitService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    private Button logInButton;
    final Retrofit retrofit = RetrofitService.createRetrofit(null);
    final UserService userService = new UserService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logInButton = findViewById(R.id.login_button);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String login = ((TextView)findViewById(R.id.login)).getText().toString();
                String password = ((TextView)findViewById(R.id.password)).getText().toString();

                if (login.length()==0 || password.length()==0){
                    Toast.makeText(LoginActivity.this,"Credentials required", Toast.LENGTH_SHORT).show();
                    return;
                }

                logInButton.setEnabled(false);

                Call<CRAAuthResponse> call = retrofit
                        .create(CRAWebApi.class)
                        .auth(
                                new CRAAuthRequest(
                                        login,
                                        password
                                )
                        );

                call.enqueue(new CRACallback<CRAAuthResponse>(LoginActivity.this) {

                    @Override
                    public void onSuccess(Call<CRAAuthResponse> call, Response<CRAAuthResponse> response) {
                        onApiTokenReceived(response.body().getToken());
                    }

                    @Override
                    public void finaly() {
                        logInButton.setEnabled(true);
                    }
                });
            }
        });
    }

    private void onApiTokenReceived(String token) {
        userService.saveApiToken(LoginActivity.this, token);
        RetrofitService.createRetrofit(token);
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }
}
