package com.example.hans.agrigo.MenuLogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hans.agrigo.Menu.MenuUtama;
import com.example.hans.agrigo.MenuLogin.NewSupport.SharedPrefManager;
import com.example.hans.agrigo.MenuRegister.Registerr;
import com.example.hans.agrigo.MenuScanBarcode.AddDevice;
import com.example.hans.agrigo.MenuScanBarcode.Support.ServerDevice;
import com.example.hans.agrigo.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity{
    SharedPrefManager sharedPrefManager;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.btnSignin)
    Button btnLogin;
    @BindView(R.id.relativ)
    RelativeLayout coordinatorLayout;
    ProgressDialog loading;

    String name, email, name2, macSensor;
    Boolean session = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sharedPrefManager = new SharedPrefManager(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check_Inputan();
            }
        });
        if (sharedPrefManager.getSudahLogin()){
            startActivity(new Intent(Login.this, MenuUtama.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }

    }

    private void Check_Inputan() {
        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
            username.setFocusable(false);
            password.setFocusable(false);
            showSnackbar();
        } else {
            loading = ProgressDialog.show(Login.this,"Loading.....",null,true,true);
            RequestLogin();
        }

    }

    private void RequestLogin() {
        String user = String.valueOf(username.getText());
        String pas = String.valueOf(password.getText());
        if (user.equals("")) {
            showSnackbar();
        } else if (pas.equals("")) {
            showSnackbar();
        } else {
            retrofit2.Call<ResponseBody> call = ServerDevice.getInstance().getApi().userLogin(user,pas);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        loading.dismiss();
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("status").equals("true")){
                                Log.d("response api", jsonRESULTS.toString());
                                String json=jsonRESULTS.getString("app");
                                Toast.makeText(Login.this, "BERHASIL LOGIN", Toast.LENGTH_SHORT).show();

                                sharedPrefManager.saveSPBoolean( SharedPrefManager.SP_SUDAH_LOGIN, true);
                                startActivity(new Intent(Login.this, MenuUtama.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            } else {
                                String error_message = jsonRESULTS.getString("error_msg");
                                Toast.makeText(Login.this, error_message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        loading.dismiss();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("debug", "onFailure: ERROR > " + t.toString());
                    loading.dismiss();

                }
            });
        }
    }


    @OnClick(R.id.btnRegis)
    void btnRegis(){
        Intent b = new Intent(Login.this, Registerr.class);
        startActivity(b);
        finish();
    }

    public void showSnackbar() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Harus Di isi !!!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ulangi", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Silahkan ulangi", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                        username.setFocusableInTouchMode(true);
                        password.setFocusableInTouchMode(true);
                    }
                });
        snackbar.show();
    }
}
