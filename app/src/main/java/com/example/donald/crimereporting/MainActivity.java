package com.example.donald.crimereporting;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText name;
    EditText phoneNo;
    Spinner crimeType;
    Spinner loc;
    EditText desc;
    EditText crimePlace;
    EditText userEmail;
    Button btnDate;
    Button btnTime;
    Button camera;
    Button video;
    Button audio;
    Button submit;
    Button reset;
    VideoView vid;

    private Firebase mRootRef;
    private ProgressBar progressBar;

    private int mYear, mMonth, mDay, mHour, mMinute;
    ImageView image;
    private StorageReference mStorage;
    private StorageReference storageRef;
    private StorageReference vstorageRef;
    String id;

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;
    private Uri videoUri;
    private DatabaseReference usersDatabase;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int VIDEO_REQUEST_CODE = 101;



    private MediaRecorder mRecorder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootRef = new Firebase("https://crimesreporting.firebaseio.com/Crime Reported");
        mStorage = FirebaseStorage.getInstance().getReference();
        storageRef =FirebaseStorage.getInstance().getReference();
        vstorageRef =FirebaseStorage.getInstance().getReference();


        name=(EditText) findViewById(R.id.username);
        phoneNo =(EditText) findViewById(R.id.phone);
        crimeType =(Spinner) findViewById(R.id.crimeType);
        loc =(Spinner) findViewById(R.id.states);
        desc = (EditText)findViewById(R.id.crimeDesc);
        userEmail = (EditText) findViewById(R.id.email);
        btnDate = (Button) findViewById(R.id.btn_date);
        btnTime = (Button) findViewById(R.id.btn_time);
        submit = (Button) findViewById(R.id.report);
        reset = (Button) findViewById(R.id.reset);
        camera = (Button) findViewById(R.id.camera);
        video = (Button) findViewById(R.id.video);
        audio = (Button) findViewById(R.id.audio);
        image = (ImageView) findViewById(R.id.camImage);
        vid = (VideoView)findViewById(R.id.video2);
        crimePlace = (EditText)findViewById(R.id.crimePlace);

        progressBar =(ProgressBar) findViewById(R.id.pbar);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/report.3gp";

        usersDatabase= FirebaseDatabase.getInstance().getReference("Reporters");

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent,VIDEO_REQUEST_CODE);

            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        btnDate.setText(dayOfMonth+ "-"+ (monthOfYear + 1)+ "-"+ year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        btnTime.setText(hourOfDay + ":"+ minute);
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction()==motionEvent.ACTION_DOWN){
                    startRecording();
                    Toast.makeText(getApplication(),"Audio Recording Started......", Toast.LENGTH_LONG).show();

                } else if (motionEvent.getAction()==motionEvent.ACTION_UP){
                    stopRecording();
                    Toast.makeText(getApplication(),"Audio Recording Stopped......", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String username = name.getText().toString();
                final String phone = phoneNo.getText().toString().trim();
                final String state = loc.getItemAtPosition(loc.getSelectedItemPosition()).toString();
                final String crimeTypes = crimeType.getItemAtPosition(crimeType.getSelectedItemPosition()).toString();
                final String crimeDesc = desc.getText().toString().trim();
                final String email = userEmail.getText().toString().trim();
                final String crimeDate = btnDate.getText().toString().trim();
                final String crimeTime = btnTime.getText().toString().trim();
                final String crimePlaces = crimePlace.getText().toString().trim();

                if (state.equalsIgnoreCase("Select State")){
                    Toast.makeText(getApplicationContext(), " Please Select a Valid State!", Toast.LENGTH_LONG).show();
                } else if (crimeTypes.equalsIgnoreCase("Select crime type")){
                    Toast.makeText(getApplicationContext(), " Please Select a Valid Crime type!", Toast.LENGTH_LONG).show();

                } else if (crimeDate.isEmpty()){
                    Toast.makeText(getApplicationContext(), " Crime date cannot be empty!", Toast.LENGTH_LONG).show();

                } else if (crimeTime.isEmpty()){
                    Toast.makeText(getApplicationContext(), " Crime Time cannot be empty!", Toast.LENGTH_LONG).show();

                }else if (crimePlaces.isEmpty()){
                    Toast.makeText(getApplicationContext(), " Place of Crime cannot be empty!", Toast.LENGTH_LONG).show();

                } else{
                    addUserData();
                    uploadAudio();
                    uploadImage();
                    uploadVideo();


                }

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setText("");
                phoneNo.setText("");
                userEmail.setText("");
                crimePlace.setText("");
                crimeType.setSelection(0);
                btnDate.setText("Date");
                btnTime.setText("Time");
                desc.setText("");
                loc.setSelection(0);
            }
        });

    }
    public void addUserData(){
        final String username = name.getText().toString();
        final String phone = phoneNo.getText().toString().trim();
        final String state = loc.getItemAtPosition(loc.getSelectedItemPosition()).toString();
        final String crimeTypes = crimeType.getItemAtPosition(crimeType.getSelectedItemPosition()).toString();
        final String crimeDesc = desc.getText().toString().trim();
        final String email = userEmail.getText().toString().trim();
        final String crimeDate = btnDate.getText().toString().trim();
        final String crimeTime = btnTime.getText().toString().trim();
        final String crimePlaces = crimePlace.getText().toString().trim();


        id = usersDatabase.push().getKey();
        Users users = new Users(username, phone, state, crimeTypes,
                crimeDesc, crimePlaces, crimeDate, crimeTime, email);
        usersDatabase.child(id).setValue(users);

        Toast.makeText(getApplicationContext(), "Report Added", Toast.LENGTH_LONG).show();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data );
        if(requestCode==CAMERA_REQUEST_CODE && resultCode==RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(bitmap);

        }

        if (requestCode == VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
        videoUri = data.getData();
        }

    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void uploadImage(){

        storageRef= FirebaseStorage.getInstance().getReference();

        StorageReference mountainsRef = storageRef.child("images").child(id).child("image.jpg");
        if (image!=null) {

            image.setDrawingCacheEnabled(true);
            image.buildDrawingCache();
            Bitmap bitmap = image.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Image Successfully Uploaded", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void uploadAudio(){
        StorageReference filepath = mStorage.child("Audio").child(id).child("audio.3gp");
        if(mFileName!=null) {
            Uri uri = Uri.fromFile(new File(mFileName));
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Audio Successfully Uploaded", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void uploadVideo(){
        StorageReference vidPath = vstorageRef.child("Videos").child(id).child("Video.3gp");
        if (videoUri!=null){
            UploadTask uploadTask =vidPath.putFile(videoUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"Video Successfully Uploaded",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Video Failed to Upload",Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public void onSuccess(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Thank You")
                .setMessage("Report Successfully Submitted!")
                .setNeutralButton("Close", null).show();

    }

}

