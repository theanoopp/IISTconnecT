package com.anoop.iistconnectfaculty.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.anoop.iistconnectfaculty.R;
import com.anoop.iistconnectfaculty.adapter.AddAssignmentAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.model.AssignmentFile;
import in.rgpvnotes.alert.myresource.model.MyClassModel;

public class AddAssignment extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private static final int GALLERY_REQUEST = 1;
    private static final int PDF_REQUEST = 2;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;

    private SimpleDateFormat simpleDateFormat;

    private EditText titleET;
    private EditText descriptionET;
    private AutoCompleteTextView questionsInput;

    private TextView submitDateText;

    private Spinner workType;

    private Date date;


    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    private DocumentReference documentReference;
    private String doc_id;

    private ArrayList<String> fileURLList = new ArrayList<>();


    private ArrayList<AssignmentFile> fileList = new ArrayList<>();

    private RecyclerView recyclerView;


    private AddAssignmentAdapter addAssignmentAdapter;


    //
    String fileName="";

    private MyProgressDialog dialog ;

    private MyClassModel model;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = (MyClassModel) getIntent().getSerializableExtra("class_model");

        titleET = findViewById(R.id.note_title_input);
        descriptionET = findViewById(R.id.gwork_description);

        submitDateText = findViewById(R.id.gwork_submitDate);

        workType = findViewById(R.id.gwork_type);

        questionsInput = findViewById(R.id.gwork_input);

        recyclerView = findViewById(R.id.gwork_recyclerView);

        dialog = new MyProgressDialog(AddAssignment.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        addAssignmentAdapter = new AddAssignmentAdapter(fileList);

        recyclerView.setAdapter(addAssignmentAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance();

        date = calendar.getTime();

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        submitDateText.setText(simpleDateFormat.format(date));

        datePickerDialog = new DatePickerDialog(this, AddAssignment.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        submitDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.show();

            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitWork();

            }
        });


    }

    private void submitWork() {


        if(checkInputs()){

            documentReference = mDatabase.collection("assignments").document();
            doc_id = documentReference.getId();

            dialog.setTitle("Please wait...");
            dialog.setMessage("Uploading...");
            dialog.setCancelable(false);

            if(TextUtils.isEmpty(questionsInput.getText())){

                AlertDialog.Builder builder = new AlertDialog.Builder(AddAssignment.this);
                builder.setTitle("Submit without any questions...?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialog.show();
                        uploadFiles();

                    }
                });

                builder.setNegativeButton("Cancel",null);

                builder.show();

            }else {

                dialog.show();
                uploadFiles();

            }

        }



    }

    private void uploadFiles() {

        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("assignment_files").child(doc_id);

        if(fileList.size()>0){

            for (AssignmentFile a : fileList) {

                final String fileName = a.getName();

                Log.d(a.getName(),a.getUri().toString());
                mStorageRef.child(a.getName()).putFile(a.getUri()).addOnSuccessListener(this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String downUrl = mStorageRef.getDownloadUrl().toString();
                        fileURLList.add(downUrl);
                        if(fileList.size() == fileURLList.size()){

                            uploadWork();

                        }


                    }
                }).addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        dialog.setMessage("Uploading file : "+fileName);

                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("assignment_files").child(doc_id);

                        mStorageRef.delete();

                        dialog.dismiss();

                    }
                });

            }

        }else {

            uploadWork();

        }




    }

    private void uploadWork() {


        Map<String,Object> workMap = new HashMap<>();

        String titleText = titleET.getText().toString();
        String descriptionText = descriptionET.getText().toString();
        String workTypeText = workType.getSelectedItem().toString();
        String questionsText = questionsInput.getText().toString();


        //assignment
        workMap.put("title", titleText);
        workMap.put("description", descriptionText);
        workMap.put("type", workTypeText);
        workMap.put("questions", questionsText);
        workMap.put("submitDate",date);
        workMap.put("subjectCode",model.getSubjectCode());
        workMap.put("subjectName",model.getSubjectName());

        //fac
        workMap.put("byName",model.getFacultyName());
        workMap.put("byId",mAuth.getCurrentUser().getUid());
        workMap.put("classId",model.getClassId());

        //images
        workMap.put("files",fileURLList);

        documentReference.set(workMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_SHORT).show();
                titleET.setText("");
                descriptionET.setText("");
                workType.setSelection(0);
                questionsInput.setText("");

                fileURLList.clear();
                fileList.clear();
                addAssignmentAdapter.notifyDataSetChanged();

                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("assignment_files").child(doc_id);

                mStorageRef.delete();

                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

    }

    private boolean checkInputs() {

        titleET.setError(null);
        boolean title = true;
        boolean desc = true;
        boolean type = true;

        TextView typeErrorText = (TextView)workType.getSelectedView();

        if(TextUtils.isEmpty(titleET.getText())){

            titleET.setError("Enter title first");
            title = false;

        }
        if(TextUtils.isEmpty(descriptionET.getText())){

            descriptionET.setError("Enter description !");
            desc = false;

        }
        if(workType.getSelectedItem().toString().equals("Select Type")){

            typeErrorText.setError("");
            typeErrorText.setTextColor(Color.RED);//just to highlight that this is an error
            typeErrorText.setText("None selected");//changes the selected item text to this
            type = false;

        }

        return title && desc && type;

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date = calendar.getTime();

        String time = simpleDateFormat.format(date);
        submitDateText.setText(time);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_assignment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.add_image){

            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_PICK);
            galleryIntent.setType("image/jpeg");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"),GALLERY_REQUEST);

        }else  if (id == android.R.id.home) {
            super.onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){

            Uri imageUri = data.getData();

            fileName = getFileName(imageUri);

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                AssignmentFile assignmentFile = new AssignmentFile(result.getUri(),fileName);

                fileList.add(assignmentFile);
                addAssignmentAdapter.notifyDataSetChanged();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(AddAssignment.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode==PDF_REQUEST && resultCode==RESULT_OK){

            Uri pdfUri = data.getData();

            fileName = getFileName(pdfUri);

            AssignmentFile assignmentFile = new AssignmentFile(pdfUri,fileName);

            fileList.add(assignmentFile);
            addAssignmentAdapter.notifyDataSetChanged();


        }



    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
