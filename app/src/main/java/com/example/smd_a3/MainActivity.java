package com.example.smd_a3;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private final String TAG = MainActivity.this.getClass().getSimpleName();
    private static ListView contact_listview;
    private static ArrayList<Contact_Model> arrayList;
    private static Contact_Adapter adapter;
    String img_path=""+R.mipmap.prof_round;

    private static ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contact_listview = (ListView) findViewById(R.id.list);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        FloatingActionButton btnSubmit = (FloatingActionButton) findViewById(R.id.fab);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,add_contact.class);
                startActivity(intent);
                addnew();
            }
        });
        new LoadContacts().execute();// Execute the async task

    }
    private void addnew(){
        Button btn=findViewById(R.id.btn_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
        Button addImg= findViewById(R.id.add_prof);
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 10:
                if(requestCode==RESULT_OK){
                    img_path= data.getData().getPath();

                }
        }
    }

    private void addUser(){
        List<ContactDB> userItemDBList = new ArrayList<>();
        EditText enterName = findViewById(R.id.edit_name);
        EditText enterContact = findViewById(R.id.edit_contact);
        EditText enterEmail= findViewById(R.id.edit_email);
        String name = enterName.getText().toString().trim();
        String contact = enterContact.getText().toString().trim();
        String email = enterEmail.getText().toString().trim();
        if((name != null && !name.isEmpty())&& (contact != null && !contact.isEmpty())
                && (email != null && !email.isEmpty())){
            ContactDB userItemDB = new ContactDB();
            userItemDB.setEmail(email);
            userItemDB.setName(name);
            userItemDB.setContact(contact);
            userItemDB.setImage(img_path);
            addUserToDB(userItemDB);
            userItemDBList = getAllUsers();
            for(ContactDB itemDB: userItemDBList){
                Log.i(TAG,"name: "+itemDB.getName());
            }
        }
    }
    public int addUserToDB(ContactDB userItemDB){
        int isSuccess;
        isSuccess = DBmanager.getInstance(getApplicationContext()).insertUserItem(userItemDB,false);
        if(isSuccess == 0){
            Toast.makeText(getApplicationContext(),"Save User",Toast.LENGTH_SHORT).show();
        }else if(isSuccess == 1){
            Toast.makeText(getApplicationContext(),"User with this id exist",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"User adding failed",Toast.LENGTH_SHORT).show();
        }
        return isSuccess;
    }

    public void presentToast(){
        Toast.makeText(getApplicationContext(),"Save User",Toast.LENGTH_SHORT).show();
    }

    public List<ContactDB> getAllUsers(){
        return  DBmanager.getInstance(getApplicationContext()).getAllUsers();
    }

    // Async task to load contacts
    private class LoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            arrayList = readContacts();// Get contacts array list from this
            // method
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

            // If array list is not null and is contains value
            if (arrayList != null && arrayList.size() > 0) {

                // then set total contacts to subtitle
                getSupportActionBar().setSubtitle(
                        arrayList.size() + " Contacts");
                adapter = null;
                if (adapter == null) {
                    adapter = new Contact_Adapter(getApplicationContext(), arrayList);
                    contact_listview.setAdapter(adapter);// set adapter
                }
                adapter.notifyDataSetChanged();
            } else {

                // If adapter is null then show toast
                Toast.makeText(MainActivity.this, "There are no contacts.",
                        Toast.LENGTH_LONG).show();
            }

            // Hide dialog if showing
            if (pd.isShowing())
                pd.dismiss();

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Show Dialog
            pd = ProgressDialog.show(MainActivity.this, "Loading Contacts",
                    "Please Wait...");
        }

    }

    // Method that return all contact details in array format
    private ArrayList<Contact_Model> readContacts() {
        ArrayList<Contact_Model> contactList = new ArrayList<Contact_Model>();

        Uri uri = ContactsContract.Contacts.CONTENT_URI; // Contact URI
        Cursor contactsCursor = getContentResolver().query(uri, null, null,
                null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
        // Return all contacts name containing in URI in ascending order Move cursor at starting
        if (contactsCursor.moveToFirst()) {
            do {
                long contctId = contactsCursor.getLong(contactsCursor
                        .getColumnIndex("_ID")); // Get contact ID
                Uri dataUri = ContactsContract.Data.CONTENT_URI; // URI to get
                // data of
                // contacts
                Cursor dataCursor = getContentResolver().query(dataUri, null,
                        ContactsContract.Data.CONTACT_ID + " = " + contctId,
                        null, null);// Retrun data cusror represntative to
                // contact ID

                // Strings to get all details
                String displayName;
                String nickName ;
                String homePhone;
                String mobilePhone;
                String workPhone ;
                String photoPath = ""+R.drawable.prof; // Photo path
                byte[] photoByte=null;// Byte to get photo since it will come
                // in BLOB
                String homeEmail ;
                String workEmail ;
                String companyName;
                String title;

                // This strings stores all contact numbers, email and other
                // details like nick name, company etc.
                String contactNumbers = "";
                String contactEmailAddresses = "";
                String contactOtherDetails = "";

                // Now start the cusrsor
                if (dataCursor.moveToFirst()) {
                    displayName = dataCursor
                            .getString(dataCursor
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));// get
                    // the
                    // contact
                    // name
                    do {
                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)) {
                            nickName = dataCursor.getString(dataCursor
                                    .getColumnIndex("data1")); // Get Nick Name
                            contactOtherDetails += "NickName : " + nickName
                                    + "\n";// Add the nick name to string

                        }

                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {

                            // In this get All contact numbers like home,
                            // mobile, work, etc and add them to numbers string
                            switch (dataCursor.getInt(dataCursor
                                    .getColumnIndex("data2"))) {
                                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                    homePhone = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactNumbers += "Home Phone : " + homePhone
                                            + "\n";
                                    break;

                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                    workPhone = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactNumbers += "Work Phone : " + workPhone
                                            + "\n";
                                    break;

                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                    mobilePhone = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactNumbers += "Mobile Phone : "
                                            + mobilePhone + "\n";
                                    break;

                            }
                        }
                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {

                            // In this get all Emails like home, work etc and
                            // add them to email string
                            switch (dataCursor.getInt(dataCursor
                                    .getColumnIndex("data2"))) {
                                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                                    homeEmail = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactEmailAddresses += "Home Email : "
                                            + homeEmail + "\n";
                                    break;
                                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                                    workEmail = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactEmailAddresses += "Work Email : "
                                            + workEmail + "\n";
                                    break;

                            }
                        }

                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
                            companyName = dataCursor.getString(dataCursor
                                    .getColumnIndex("data1"));// get company
                            // name
                            contactOtherDetails += "Company Name : "
                                    + companyName + "\n";
                            title = dataCursor.getString(dataCursor
                                    .getColumnIndex("data4"));// get Company
                            // title
                            contactOtherDetails += "Title : " + title + "\n";

                        }

                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                            photoByte = dataCursor.getBlob(dataCursor
                                    .getColumnIndex("data15")); // get photo in
                            // byte

                            if (photoByte != null) {

                                // Now make a cache folder in file manager to
                                // make cache of contacts images and save them
                                // in .png
                                Bitmap bitmap = BitmapFactory.decodeByteArray(
                                        photoByte, 0, photoByte.length);
                                File cacheDirectory = getBaseContext()
                                        .getCacheDir();
                                File tmp = new File(cacheDirectory.getPath()
                                        + "/_androhub" + contctId + ".png");
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(
                                            tmp);
                                    bitmap.compress(Bitmap.CompressFormat.PNG,
                                            100, fileOutputStream);
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                }
                                photoPath = tmp.getPath();// finally get the
                                // saved path of
                                // image

                            }

                        }

                    } while (dataCursor.moveToNext()); // Now move to next
                    // cursor

                    contactList.add(new Contact_Model(Long.toString(contctId),
                            displayName, contactNumbers, contactEmailAddresses,
                            photoPath, contactOtherDetails));// Finally add
                    // items to
                    // array list
                }

            } while (contactsCursor.moveToNext());
        }
        return contactList;
    }

}