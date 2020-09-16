package com.example.pre_lovedshopping.Fragments;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pre_lovedshopping.Connection.ConnectionClass;
import com.example.pre_lovedshopping.R;
import com.squareup.picasso.Picasso;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {

    private ArrayList<ClassListItems> itemArrayList;  //List items Array
    private MyAppAdapter myAppAdapter; //Array Adapter
    private RecyclerView recyclerView; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean success = false; // boolean
    private ConnectionClass connectionClass; //Connection Class Variable


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView); //Listview Declaration
        recyclerView.setHasFixedSize(true);
       // recyclerView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager( mLayoutManager); //getActivity() ???

        connectionClass = new ConnectionClass(); // Connection Class Initialization
        itemArrayList = new ArrayList<ClassListItems>(); // Arraylist Initialization

        // Calling Async Task
        SyncData orderData = new SyncData();
        orderData.execute("");

    }


    // Async Task has three overrided methods,
    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
        //ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
           // progress = ProgressDialog.show(DashboardFragment.this, "Synchronising", "Listview Loading! Please Wait...", true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try
            {
                Connection con = connectionClass(ConnectionClass.database.toString(), ConnectionClass.port.toString(),ConnectionClass.ip.toString(), ConnectionClass.un.toString(), ConnectionClass.pass.toString());
                if (con == null) {
                    success = false;
                }
                else {
                    String query = "SELECT contribution_title,contribution_image,contribution_price FROM cars_table";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {
                                itemArrayList.add(new ClassListItems(rs.getString("contribution_title"), rs.getString("contribution_image"), rs.getString("contribution_price")));
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
            } catch (Exception e)
            {
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
//            progress.dismiss();
            //Toast.makeText(MainActivity.this, msg + "", Toast.LENGTH_LONG).show();
            if (success == false)
            {
            }
            else {
                try {
                    myAppAdapter = new MyAppAdapter(itemArrayList , getContext());
                    recyclerView.setAdapter(myAppAdapter);

                    //recyclerView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                } catch (Exception ex)
                {

                }

            }
        }
    }

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

    public class MyAppAdapter extends RecyclerView.Adapter<MyAppAdapter.ViewHolder>  //has a class viewholder which holds
    {
        private List<ClassListItems> itemList;
        public Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textName;
            public TextView textPrice;
            public ImageView imageView;
            public View layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                layout = itemView;
                textName = (TextView) itemView.findViewById(R.id.cont_title_in_list);
                imageView = (ImageView) itemView.findViewById(R.id.imageView_cont);
                textPrice = (TextView) itemView.findViewById(R.id.cont_price_in_list);
            }
        }

        public MyAppAdapter(List<ClassListItems> itemsArrayList, Context context) {
            itemList = itemsArrayList;
            this.context = context;
        }

       // @NonNull
        @Override
        public MyAppAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_content, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder( ViewHolder holder, final int position) {
            final ClassListItems classListItems = itemList.get(position);
            holder.textName.setText(classListItems.getName());
            holder.textPrice.setText(classListItems.getPrice());

            Picasso.get().load("https://img2.exportersindia.com/product_images/bc-full/2020/1/6885975/fresh-apple-1579841793-5267599.jpeg").into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

    }
}