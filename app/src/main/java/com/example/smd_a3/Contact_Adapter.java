package com.example.smd_a3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Contact_Adapter extends BaseAdapter {
    private Context context;
    private ArrayList<Contact_Model> arrayList;

    public Contact_Adapter(Context context, ArrayList<Contact_Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {

        return arrayList.size();
    }

    @Override
    public Contact_Model getItem(int position) {

        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Contact_Model model = arrayList.get(position);
        ViewHodler holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom, parent, false);
            holder = new ViewHodler();
            holder.contactImage = (ImageView) convertView
                    .findViewById(R.id.contactImage);
            holder.contactName = (TextView) convertView
                    .findViewById(R.id.contactName);
            holder.contactEmail = (TextView) convertView
                    .findViewById(R.id.contactEmail);
            holder.contactNumber = (TextView) convertView
                    .findViewById(R.id.contactNumber);
            holder.contactOtherDetails = (TextView) convertView
                    .findViewById(R.id.contactOtherDetails);

            convertView.setTag(holder);
        } else {
            holder = (ViewHodler) convertView.getTag();
        }

        // Set items to all view
        if (!model.getContactName().equals("")
                && model.getContactName() != null) {
            holder.contactName.setText(model.getContactName());
        } else {
            holder.contactName.setText("No Name");
        }
        if (!model.getContactEmail().equals("")
                && model.getContactEmail() != null) {
            holder.contactEmail.setText("EMAIL ID: "
                    + model.getContactEmail());
        } else {
            holder.contactEmail.setText("EMAIL ID: " + "No EmailId");
        }

        if (!model.getContactNumber().equals("")
                && model.getContactNumber() != null) {
            holder.contactNumber.setText(""
                    + model.getContactNumber());
        } else {
            holder.contactNumber.setText("CONTACT NUMBER: "
                    + "No Contact Number");
        }

        if (!model.getContactOtherDetails().equals("")
                && model.getContactOtherDetails() != null) {
            holder.contactOtherDetails.setText("OTHER DETAILS - "
                    + model.getContactOtherDetails());
        } else {
            holder.contactOtherDetails.setText("OTHER DETAILS: "
                    + "Other details are empty");
        }

        // Bitmap for imageview
        Bitmap image = null;
        if (!model.getContactPhoto().equals("")
                && model.getContactPhoto() != null) {
            image = BitmapFactory.decodeFile(model.getContactPhoto());// decode
            // the
            // image
            // into
            // bitmap
            if (image != null) {
                //new
                image = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.prof);
                holder.contactImage.setImageBitmap(image);// Set image if bitmap
                // is not null
            }
            else {
                image = BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.prof_foreground);// if bitmap is null then set
                // default bitmap image to
                // contact image
                holder.contactImage.setImageBitmap(image);
            }
        } else {
            image = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.prof_round);
            holder.contactImage.setImageBitmap(image);
        }
        return convertView;
    }

    // View holder to hold views
    private class ViewHodler {
        ImageView contactImage;
        TextView contactName, contactNumber, contactEmail, contactOtherDetails;
    }
}
