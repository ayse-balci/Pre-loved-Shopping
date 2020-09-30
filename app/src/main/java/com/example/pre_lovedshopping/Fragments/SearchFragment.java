package com.example.pre_lovedshopping.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pre_lovedshopping.Classes.SearchItems;
import com.example.pre_lovedshopping.Connection.ConnectionClass;
import com.example.pre_lovedshopping.R;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchView mySearchView;
    private ListView myListView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private boolean success = false; // boolean
    private ConnectionClass connectionClass;

    List<SearchItems> itemsModelList = new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mySearchView = (SearchView) getView().findViewById(R.id.search_view);
        myListView = (ListView) getView().findViewById(R.id.list_search);

        connectionClass = new ConnectionClass(); // Connection Class Initialization

        list = new ArrayList<String>(); // Arraylist Initialization

        // Calling Async Task
        SyncData orderData = new SyncData();
        orderData.execute("");

      adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
      myListView.setAdapter(adapter);

      mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String s) {
              return false;
          }

          @Override
          public boolean onQueryTextChange(String s) {
              adapter.getFilter().filter(s);
              return false;
          }
      });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //getMenuInflater().inflate(R.menu.menu_search, menu);
    }

    // Async Task has three overrided methods,
    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
        //ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try {
                Connection con = connectionClass(ConnectionClass.database.toString(), ConnectionClass.port.toString(), ConnectionClass.ip.toString(), ConnectionClass.un.toString(), ConnectionClass.pass.toString());
                if (con == null) {
                    success = false;
                } else {
                    //String query = "SELECT contribution_title,contribution_image,contribution_price FROM order_table";
                    String query = "SELECT cont_title FROM contribution_table";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                SearchItems s = new SearchItems(rs.getString("cont_title"));
                                list.add(s.name);
                                //list.add(rs.getString("name"));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        msg = "Found";
                        success = true;
                    } else {
                        msg = "No Data found!";
                        success = false;
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

    public Connection connectionClass(String database, String port, String ip, String un, String pass) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + database + ";user=" + un + ";password=" + pass + ";";

            connection = DriverManager.getConnection(connectionURL);
        } catch (Exception e) {
            Log.e("SQL Connection Error : ", e.getMessage());
        }

        return connection;
    }

}
