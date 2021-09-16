package ug.app.ihrisbiometric.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import ug.app.ihrisbiometric.R;
import ug.app.ihrisbiometric.extra.APIService;
import ug.app.ihrisbiometric.extra.APIUtils;
import ug.app.ihrisbiometric.extra.AuthUser;
import ug.app.ihrisbiometric.extra.SessionHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText et_username;
    EditText et_password;
    FrameLayout fl_login;

    ProgressBar progressBar;

    APIService apiService;

    SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionHandler(getApplicationContext());

        Boolean isLoggedIn = session.isLoggedIn();
        if (isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        apiService = APIUtils.getAPIService();

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        fl_login = (FrameLayout) findViewById(R.id.fl_login);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        fl_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fl_login.setClickable(false);
                fl_login.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                String username = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
//                    Toast.makeText(LoginActivity.this, "Attempting to login", Toast.LENGTH_SHORT).show();
                    authenticate(username, password);
                } else {
                    fl_login.setClickable(true);
                    fl_login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "You must provide a username and password", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void authenticate(String username, String password) {
        apiService.authenticate(username, password).enqueue(new Callback<AuthUser>() {
            @Override
            public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                if(response.isSuccessful()) {

                    String status = response.body().getStatus();
                    if(status.equals("USER_FOUND")) {
                        session.createLoginSession(response.body().getUser().getFacilityId(), response.body().getUser().getFacility());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        fl_login.setClickable(true);
                        fl_login.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();

                    }
                } else {
                    fl_login.setClickable(true);
                    fl_login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(LoginActivity.this, "Unable to Login, Please try again later", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<AuthUser> call, Throwable t) {

                String message = t.getLocalizedMessage();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                fl_login.setClickable(true);
                fl_login.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
