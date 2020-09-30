package com.example.pre_lovedshopping.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pre_lovedshopping.Connection.ConnectionClass;
import com.example.pre_lovedshopping.R;
import com.example.pre_lovedshopping.Session.SessionManager;
import com.example.pre_lovedshopping.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password;
    Button btn_register;
    TextView status;
    ProgressBar progressBar;
    ArrayList<String> checkList;

    Connection con;
    Statement stmt;

    SessionManager sessionManager;
    String _name;
    Integer uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        btn_register = (Button)findViewById(R.id.btn_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        checkList = new ArrayList<>();

        status = (TextView)findViewById(R.id.status);

        sessionManager = new SessionManager(this);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RegisterActivity.registeruser().execute("");
            }
        });
    }

    public class registeruser extends AsyncTask<String, String , String>{

        String _message = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            //status.setText("Sending Data to Database");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
           //status.setText("Registration Successful");
            name.setText("");
            email.setText("");
            password.setText("");
        }

        @Override
        protected String doInBackground(String... strings) {
            String _name = name.getText().toString();
            String _email = email.getText().toString();
            String _password = password.getText().toString();
            try{
                con = connectionClass(ConnectionClass.database.toString(), ConnectionClass.port.toString(),ConnectionClass.ip.toString(), ConnectionClass.un.toString(), ConnectionClass.pass.toString());
                if(con == null){
                    _message = "Check Your Internet Connection";
                }
                else{

                    String query = "SELECT email FROM register_table";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {
                                checkList.add(rs.getString("email"));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    Log.d("RegisterActivity", String.valueOf(checkList.size()));
                    for (int i= 0; i < checkList.size(); i++ ) {
                        Log.d("RegisterActivity", checkList.get(i));
                    }

                    if (!checkList.contains(_email)) {

                        Log.d("RegisterActivity", "icermiyor");

                        String sql = "INSERT INTO register_table (name,email,password) VALUES ('" + _name + "','" + _email + "','"+ _password +"')";
                        stmt = con.createStatement();
                        stmt.executeUpdate(sql);

                       /* Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();*/

                         sql = "SELECT * FROM register_table WHERE email = '" + email.getText().toString() + "' AND password = '" + password.getText().toString() +"' ";
                         stmt = con.createStatement();
                         ResultSet rs0 = stmt.executeQuery(sql);

                        if (rs0.next()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //_name = rs.getString("name");
                                        uId = rs0.getInt("user_id");

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            User user = new User();
                            user.name = name.getText().toString();
                            user.email = email.getText().toString();
                            user.id = uId;

                            //User user = new User();
                            // user.email = emaillogin.getText().toString();

                            sessionManager.createSession(email.getText().toString(), name.getText().toString(), password.getText().toString());

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }
                    else {
                        status.setText("That user already exist. Use another email or back to login screen");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                        builder.setTitle("Logout");
                        builder.setMessage("Are you sure to Log out?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // Intent intent = new Intent(getContext(), MainActivity.class);
                                // startActivity(intent);
                                //finish();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }

            }catch (Exception e){
                isSuccess = false;
                _message = e.getMessage();
            }
            return _message;
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