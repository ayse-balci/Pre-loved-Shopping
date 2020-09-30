package com.example.pre_lovedshopping.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pre_lovedshopping.Connection.ConnectionClass;
import com.example.pre_lovedshopping.R;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ContributionDetailsFragment extends Fragment {

    private ImageView imageView;
    TextView cont_title,cont_description, cont_price, cont_location;;
    boolean success;
    private int current_id;
    private MyProfileFragment.MyAppAdapter myAppAdapter; //Array Adapter
    Connection con;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contribution_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = (ImageView) getView().findViewById(R.id.detail_img);
        cont_title = (TextView) getView().findViewById(R.id.detail_title);
        cont_description = (TextView) getView().findViewById(R.id.detail_description);
        cont_price = (TextView) getView().findViewById(R.id.detail_price);
        cont_location = (TextView) getView().findViewById(R.id.detail_location);

        if (getArguments() != null) {
            ContributionDetailsFragmentArgs args = ContributionDetailsFragmentArgs.fromBundle(getArguments());
            current_id = args.getMessage();
            Log.d("message " , String.valueOf(current_id));
        }


        SyncData orderData = new SyncData();
        orderData.execute("");

    }


    // Async Task has three overrided methods,
    private class SyncData extends AsyncTask<String, String, String>
    {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
        //ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            // progress = ProgressDialog.show(DashboardFragment.this, "Synchronising", "Listview Loading! Please Wait...", true);
        }

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list


        {

            try {
                con = connectionClass(ConnectionClass.database.toString(), ConnectionClass.port.toString(),ConnectionClass.ip.toString(), ConnectionClass.un.toString(), ConnectionClass.pass.toString());
                if (con == null) {
                    success = false;
                }
                else {

                    String query = "SELECT * FROM contribution_table where cont_id = '" + current_id + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    Log.d("ContrubitionDetailsFragment", String.valueOf(current_id));
                   if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {

                                /*String image = rs.getString("cont_image");

                                byte[] bytes = Base64.decode(image,Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                imageView.setImageBitmap(bitmap);*/

                                cont_title.setText(rs.getString("cont_title"));
                                cont_description.setText(rs.getString("cont_description"));
                                cont_price.setText(rs.getString("cont_price"));
                                cont_location.setText(rs.getString("cont_location"));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        success = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = writer.toString();
                success = false;
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my listview
        {

        }
    }




    public Connection connectionClass(String database, String port , String ip, String un, String pass){
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