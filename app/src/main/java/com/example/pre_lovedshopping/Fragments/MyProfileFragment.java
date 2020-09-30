package com.example.pre_lovedshopping.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pre_lovedshopping.Connection.ConnectionClass;
import com.example.pre_lovedshopping.R;
import com.example.pre_lovedshopping.Session.SessionManager;
import com.example.pre_lovedshopping.model.ClassListItems;
import com.example.pre_lovedshopping.model.User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyProfileFragment extends Fragment {

    TextView name;
    boolean success;
    String n;

    SessionManager sessionManager;

    Integer user_id;
    String uid;
    private ArrayList<ClassListItems> itemArrayList;  //List items Array
    private MyAppAdapter myAppAdapter; //Array Adapter
    private RecyclerView recyclerView; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    Connection con;

    ImageView cont_delete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_id = getArguments().getInt("user_id");
        }
    }

    public void removeContribution(int position) {
        itemArrayList.remove(position);
        myAppAdapter.notifyItemRemoved(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_myprofile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // uid = getArguments().getString("user_id");
        //user_id = Integer.valueOf(uid);
        Log.d("aaaaa", String.valueOf(user_id));
        sessionManager = new SessionManager(getContext());
        sessionManager.checkLogin();
        cont_delete = getView().findViewById(R.id.contribution_delete);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String uName = user.get(sessionManager.NAME);
        String uemail = user.get(sessionManager.EMAIL);

        name = (TextView) getView().findViewById(R.id.profile_name_text);
        name.setText(uName);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView); //Listview Declaration
        recyclerView.setHasFixedSize(true);
        // recyclerView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager( mLayoutManager); //getActivity() ???

        itemArrayList = new ArrayList<ClassListItems>(); // Arraylist Initialization

        SyncData orderData = new SyncData();
        orderData.execute("");

        getView().findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(view);
            }
        });
    }

    private void logout(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure to Log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sessionManager.logout();
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
            try {
                con = connectionClass(ConnectionClass.database.toString(), ConnectionClass.port.toString(),ConnectionClass.ip.toString(), ConnectionClass.un.toString(), ConnectionClass.pass.toString());
                if (con == null) {
                    success = false;
                }
                else {
                    User user = new User();

                    String query = "SELECT * FROM contribution_table where user_id = '" + user_id + "'";
                    Log.d("bu", String.valueOf(user_id));
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {
                                itemArrayList.add(new ClassListItems(rs.getInt("cont_id"),rs.getString("cont_title"), rs.getString("cont_image"), rs.getString(("cont_price"))));
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
            //Toast.makeText(MainActivity.this, msg + "", Toast.LENGTH_LONG).show();
            if (success == false)
            {
            }
            else {
                try {
                    myAppAdapter = new MyAppAdapter(itemArrayList , getContext());
                    recyclerView.setAdapter(myAppAdapter);
                    myAppAdapter.setOnItemClickListener(new MyAppAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            //changeItem(position, "Clicked");
                            removeContribution(position);


                        }
                    });

                } catch (Exception ex)
                {

                }

            }
        }
    }

    @SuppressLint("NewApi")
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

    public static class MyAppAdapter extends RecyclerView.Adapter<MyAppAdapter.ViewHolder>  //has a class viewholder which holds
    {
        private List<ClassListItems> itemList;
        public Context context;

        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textName;
            public TextView textPrice;
            public ImageView imageView;
            public View layout;

            public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
                super(itemView);
                layout = itemView;
                textName = (TextView) itemView.findViewById(R.id.cont_title_in_list);
                imageView = (ImageView) itemView.findViewById(R.id.imageView_cont);
                textPrice = (TextView) itemView.findViewById(R.id.cont_price_in_list);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                            }
                        }
                    }
                });
            }
        }

        public MyAppAdapter(List<ClassListItems> itemsArrayList, Context context) {
            itemList = itemsArrayList;
            this.context = context;
        }

        // @NonNull
        @Override
        public MyAppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_content_profile, parent, false);
            ViewHolder vh = new ViewHolder(v, mListener);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final ClassListItems classListItems = itemList.get(getItemCount() - position - 1);
            holder.textName.setText(classListItems.getName());
            holder.textPrice.setText(classListItems.getPrice());

            byte[] bytes = Base64.decode(classListItems.getImg(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.imageView.setImageBitmap(bitmap);
        }



        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }
}