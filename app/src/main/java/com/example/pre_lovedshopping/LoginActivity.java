package com.example.pre_lovedshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pre_lovedshopping.Connection.ConnectionClass;
import com.example.pre_lovedshopping.Session.SessionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {

    EditText emaillogin, passwordlogin;
    Button btn_login, btn_registerfromlogin;

    Connection con;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());

        emaillogin = (EditText) findViewById(R.id.emaillogin);
        passwordlogin = (EditText) findViewById(R.id.passwordlogin);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_registerfromlogin = (Button) findViewById(R.id.btn_registerfromlogin);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginActivity.checkLogin().execute("");
            }
        });

        btn_registerfromlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    public class checkLogin extends AsyncTask <String, String, String> {

        String message = null;
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected String doInBackground(String... strings) {
            String email = emaillogin.getText().toString();
            String password = passwordlogin.getText().toString();

            con = connectionClass(ConnectionClass.database.toString(), ConnectionClass.port.toString(),ConnectionClass.ip.toString(), ConnectionClass.un.toString(), ConnectionClass.pass.toString());
            if (con == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Check Internet Connection", Toast.LENGTH_LONG).show();
                    }
                });
                message = "On Internet Connection";
            }else {
                try {
                    String sql = "SELECT * FROM register WHERE email = '" + emaillogin.getText().toString() + "' AND password = '" + passwordlogin.getText().toString() +"' ";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    if (rs.next()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                            }
                        });
                        message = "Success";

                        sessionManager.createLoginSession(email, password);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Check email or password", Toast.LENGTH_LONG).show();
                            }
                        });
                        emaillogin.setText("");
                        passwordlogin.setText("");
                    }

                } catch (Exception e) {
                    isSuccess = false;
                    Log.e("SQL Error: " , e.getMessage());
                }
            }
            return message;
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionClass( String database, String port , String ip, String un, String pass){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + ip +":" + port + "/" + database + ";user=" + un + ";password=" + pass + ";";

            connection = DriverManager.getConnection(connectionURL);
        }catch (Exception e){
            Log.e("SQL Connection Error : ", e.getMessage());
        }

        return connection;
    }

}