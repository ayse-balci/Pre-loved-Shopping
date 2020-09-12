package com.example.pre_lovedshopping.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private ListView listView; // Listview
    private boolean success = false; // boolean
    private ConnectionClass connectionClass; //Connection Class Variable
    private Connection con;

    public DashboardFragment() {

    }



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

        listView = (ListView) getView().findViewById(R.id.listView); //Listview Declaration
        connectionClass = new ConnectionClass(); // Connection Class Initialization
        itemArrayList = new ArrayList<ClassListItems>(); // Arraylist Initialization

        // Calling Async Task
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

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try
            {
                con = connectionClass(ConnectionClass.database.toString(), ConnectionClass.port.toString(),ConnectionClass.ip.toString(), ConnectionClass.un.toString(), ConnectionClass.pass.toString());
                if (con == null) {
                    success = false;
                }
                else {
                    // Change below query according to your own database.
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
                    myAppAdapter = new MyAppAdapter(itemArrayList, getContext());
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listView.setAdapter(myAppAdapter);
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

    public class MyAppAdapter extends BaseAdapter         //has a class viewholder which holds
    {
        public class ViewHolder
        {
            TextView textName;
            ImageView imageView;
            TextView textPrice;
        }

        public List<ClassListItems> parkingList;

        public Context context;
        ArrayList<ClassListItems> arraylist;

        private MyAppAdapter(List<ClassListItems> apps, Context context)
        {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<ClassListItems>();
            arraylist.addAll(parkingList);
        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) // inflating the layout and initializing widgets
        {

            View rowView = convertView;
            ViewHolder viewHolder= null;
            if (rowView == null)
            {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_content, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textName = (TextView) rowView.findViewById(R.id.cont_title_in_list);
                viewHolder.textPrice = (TextView) rowView.findViewById(R.id.cont_price_in_list);
                viewHolder.imageView = (ImageView) rowView.findViewById(R.id.imageView_cont);
                rowView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // here setting up names and images
            viewHolder.textName.setText(parkingList.get(position).getName());
            viewHolder.textPrice.setText(parkingList.get(position).getPrice());
           // Picasso.get().load("http://"+parkingList.get(position).getImg()).into(viewHolder.imageView);
           // Picasso.get().load("data:image/jpeg;base64,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").into(viewHolder.imageView);

            Picasso.get().load("https://img2.exportersindia.com/product_images/bc-full/2020/1/6885975/fresh-apple-1579841793-5267599.jpeg").into(viewHolder.imageView);


            return rowView;
        }
    }


}