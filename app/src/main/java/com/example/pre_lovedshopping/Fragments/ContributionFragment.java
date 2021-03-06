package com.example.pre_lovedshopping.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pre_lovedshopping.Connection.ConnectionClass;
import com.example.pre_lovedshopping.R;
import com.example.pre_lovedshopping.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static android.app.Activity.RESULT_OK;


public class ContributionFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    int RESULT_LOAD_IMAGE = 1;
    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private ImageView imagebox;
    private TextInputEditText cont_title, cont_description, cont_price, cont_location;
    private ProgressBar progressBar;
    private Spinner spinner;
    private String itemType;
    private Integer user_id;

    private byte[] byteArray;
    private String encodedImage;

    private ResultSet rs;
    private Connection con;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contribution, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagebox = (ImageView) getView().findViewById(R.id.img_added);
        cont_title = (TextInputEditText) getView().findViewById(R.id.title);
        cont_description = (TextInputEditText) getView().findViewById(R.id.description);
        cont_price = (TextInputEditText) getView().findViewById(R.id.price);
        cont_location = (TextInputEditText) getView().findViewById(R.id.location);
        spinner = (Spinner) getView().findViewById(R.id.spinner_contType);
        progressBar = (ProgressBar) getView().findViewById(R.id.cont_progress_bar);
        progressBar.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.typesOfContribution, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        getView().findViewById(R.id.btn_selectImg_cont).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Opening the Gallery and selecting media

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)&& !Environment.getExternalStorageState().equals(Environment.MEDIA_CHECKING)) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE );
                    // this will jump to onActivity Function after selecting image
                }
                // End Opening the Gallery and selecting media
            }
        });

        getView().findViewById(R.id.btn_save_cont).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newContribution n = new newContribution();
                n.execute("");
                Navigation.findNavController(view).navigate(R.id.action_navigation_contribution_to_navigation_dashboard);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        itemType = adapterView.getItemAtPosition(i).toString();
        Log.d("ContributionFragment", itemType);
        //Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void ChooseImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK  && null != data) {
            // getting the selected image, setting in imageview and converting it to byte and base 64
            progressBar.setVisibility(View.VISIBLE);
            Bitmap originBitmap = null;
            Uri selectedImage = data.getData();
            InputStream imageStream;
            try {
                imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                originBitmap = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage().toString());
            }
            if (originBitmap != null)
            {
                this.imagebox.setImageBitmap(originBitmap);
                Log.w("Image Setted in", "Done Loading Image");
                try
                {
                    Bitmap image = ((BitmapDrawable) imagebox.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    // Calling the background process so that application wont slow down
                    //UploadImage uploadImage = new UploadImage();
                    //uploadImage.execute("");
                    //End Calling the background process so that application wont slow down
                }
                catch (Exception e)
                {
                    Log.w("a","exception");
                }
            }
            // End getting the selected image, setting in imageview and converting it to byte and base 64
        }
        else
        {
            System.out.println("Error Occured");
        }
    }

    public class newContribution extends AsyncTask<String, String , String> {

        String _message = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            cont_title.setText("");
            cont_description.setText("");
        }

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(String... strings) {
            String title = cont_title.getText().toString();
            String description = cont_description.getText().toString();
            String price = cont_price.getText().toString();
            String location = cont_location.getText().toString();
            String s = "false";
            boolean success = false;

            try{
                con = connectionClass(ConnectionClass.database.toString(), ConnectionClass.port.toString(),ConnectionClass.ip.toString(), ConnectionClass.un.toString(), ConnectionClass.pass.toString());


                if(con == null){
                    _message = "Check Your Internet Connection";
                }
                else{
                    User user = new User();
                    Log.d("ContributionFragment", user.email);
                    /*String query = "SELECT * FROM register_table where email = '" + user.email + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs != null)
                    {
                        while (rs.next())
                        {
                            try {
                                user_id = rs.getInt("user_id");
                                Log.d("ContributionFragment", user_id.toString());

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        success = true;
                    }*/
                    // I want to insert the user_id automaticlly inside contributions_table for the one who press on share button
                    //The user have registered and logged in, its id should seems in contributions_table
                    //tring sql = "INSERT INTO contributions_table (userid,contribution_title,contribution_description,contribution_price,contribution_image,contribution_city) VALUES ('" + user_id + "','" + cont_title + "','" + cont_description + "','" + "50.000$" + "','" + "encodedImage" + "','" + "ankara/turkey" + "')";
                    //String sql = "INSERT INTO temp_table (user_id) VALUES ('" + user_id + "')";

                     String sql = "INSERT INTO contribution_table (user_id,cont_title,cont_description,cont_price,cont_location,cont_image,cont_type) VALUES ('" + user.id  + "','" + title  + "','" + description  + "','" + price  + "','" + location  + "','" + encodedImage +  "','" + itemType +  "')";
                    //String sql = "INSERT INTO order_table (user_id,title) VALUES ('" + user.id  + "','" + title   +  "')";
                    Log.d("ContributionFragment userid ", user.id.toString());
                    PreparedStatement stmt1 = con.prepareStatement(sql);
                    stmt1.executeUpdate();
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