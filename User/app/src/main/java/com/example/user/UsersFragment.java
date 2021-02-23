package com.example.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import java.text.DateFormat;
import java.util.Calendar;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import static android.app.Activity.RESULT_OK;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UsersFragment extends Fragment implements View.OnClickListener{
    EditText etname,etlastname,etdob,etgender,etcountry,etstate,ethometown,etphonenumber,ettelephonenumbr;
    Button button;
    ImageView imageView;
    ProgressBar progressBar;
    Uri imageUri;
    String imageUrl;
    private static final int PICK_IMAGE =1;
    DatabaseReference reff;
    Model model;
    long Maxid = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        imageView = (ImageView) getActivity().findViewById(R.id.iv_cp);


        etname = (EditText) getActivity().findViewById(R.id.et_name_cp);
        etlastname = (EditText) getActivity().findViewById(R.id.lastname);
        etcountry = (EditText) getActivity().findViewById(R.id.country);
        etdob = (EditText) getActivity().findViewById(R.id.dob);
        etgender = (EditText) getActivity().findViewById(R.id.gender);
        ethometown = (EditText) getActivity().findViewById(R.id.hometown);
        etstate = (EditText) getActivity().findViewById(R.id.state);
        etphonenumber = (EditText) getActivity().findViewById(R.id.phonenumber);
        ettelephonenumbr = (EditText) getActivity().findViewById(R.id.telephonenumber);

        button = (Button) getActivity().findViewById(R.id.btn_cp);
        progressBar = getActivity().findViewById(R.id.progressbar_cp);
        Calendar calendar  = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        month = month+1;
        String date = day + "/" + month + "/"+ year;
        etdob.setText(date);
        model = new Model();
        reff= FirebaseDatabase.getInstance().getReference().child("Users");

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    Maxid=(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final int finalMonth = month;
        etdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear+1;
                        String date = dayOfMonth + "/" + monthOfYear + "/"+ year;
                        etdob.setText(date);
                    }
                },year, finalMonth,day);
                datePickerDialog.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String name = etname.getText().toString();

                String lastname = etlastname.getText().toString().trim();
                String dob = etdob.getText().toString().trim();
                String gender = etgender.getText().toString().trim();
                String country = etcountry.getText().toString().trim();
                String state = etstate.getText().toString().trim();
                String hometown = ethometown.getText().toString().trim();
                String phoneno = etphonenumber.getText().toString().trim();
                String telno = ettelephonenumbr.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    etname.setError("Please enter your First Name");
                    etname.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(lastname)) {
                    etlastname.setError("Please enter your Last Name");
                    etlastname.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(dob)) {
                    etdob.setError("Please enter your Date of Birth");
                    etdob.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(gender)) {
                    etgender.setError("Please enter your Gender");
                    etgender.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(country)) {
                    etcountry.setError("Please enter your Country");
                    etcountry.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(state)) {
                    etstate.setError("Please enter your State");
                    etstate.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(hometown)) {
                    ethometown.setError("Please enter your Hometown");
                    ethometown.requestFocus();
                    return;
                }
                String MobilePattern = "[0-9]{10}";
                if (phoneno.equals("") || phoneno.equals(null) || !phoneno.matches(MobilePattern)) {
                    etphonenumber.setError("Please enter right phonenumber");
                    etphonenumber.requestFocus();
                    return;

                }
                if (telno.equals("") || telno.equals(null)) {
                    ettelephonenumbr.setError("Please enter your Telephone Number");
                    ettelephonenumbr.requestFocus();
                    return;
                }
                uploadData();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });


    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    public boolean isValidPhone(CharSequence phoneno) {
        if (TextUtils.isEmpty(phoneno)) {
            etphonenumber.setError("Please enter your Phone Number");
            etphonenumber.requestFocus();
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phoneno).matches();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK ||
                data != null || data.getData() != null) {
            imageUri = data.getData();


            Picasso.get().load(imageUri).into(imageView);
        }
    }



    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();





    }
    private void uploadData() {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference().child("UsersImages").child(imageUri.getLastPathSegment());

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing....");
        progressDialog.show();

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                uploadRecipe();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    public void uploadRecipe(){

        String name = etname.getText().toString().trim();
        String image = imageUrl;
        String lastname = etlastname.getText().toString().trim();
        String dob = etdob.getText().toString().trim();
        String gender = etgender.getText().toString().trim();
        String country = etcountry.getText().toString().trim();
        String state = etstate.getText().toString().trim();
        String hometown = ethometown.getText().toString().trim();
        String phoneno = etphonenumber.getText().toString().trim();
        String telno = ettelephonenumbr.getText().toString().trim();


            reff.orderByChild("phonenumber").equalTo(phoneno).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Toast.makeText(getActivity(), "phonenumber already registered", Toast.LENGTH_SHORT).show();

                    } else {

                            model.setName(name);
                            model.setCountry(country);
                            model.setDob(dob);
                            model.setHometown(hometown);
                            model.setPhonenumber(phoneno);
                            model.setTelephonenumber(telno);
                            model.setState(state);
                            model.setGender(gender);
                            model.setLastname(lastname);
                            model.setImage(image);
                            reff.child(String.valueOf(Maxid + 1)).setValue(model);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "User added successfully", Toast.LENGTH_SHORT).show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    etname.setText("");
                                    etlastname.setText("");
                                    imageView.setImageResource(0);
                                    etcountry.setText("");
                                    etdob.setText("");
                                    etgender.setText("");
                                    etstate.setText("");
                                    ettelephonenumbr.setText("");
                                    etphonenumber.setText("");
                                    ethometown.setText("");

                                }
                            }, 2000);



                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



    }

}



