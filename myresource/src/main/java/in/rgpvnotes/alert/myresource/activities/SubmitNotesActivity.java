package in.rgpvnotes.alert.myresource.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.rgpvnotes.alert.myresource.Constants;
import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.adapter.SubjectArrayAdapter;
import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.models.SubjectModel;

public class SubmitNotesActivity extends AppCompatActivity {

    private static final String TAG = "AddNotes";

    private static final int REQUEST_CODE_DOC = 1;

    private EditText inputTitle;
    private Spinner branchSpinner;
    private Spinner semSpinner;
    private Spinner subSpinner;

    private Button selectFile;

    private String fileName;

    private String branch;
    private String sem;

    private FirebaseFirestore mDatabase;

    private ArrayList<SubjectModel> subList = new ArrayList<>();


    private SubjectArrayAdapter adapter;

    private Uri fileUri;
    private long fileSize;

    private SubjectModel subjectModel = new SubjectModel();

    private boolean uploadSubject = false;

    private View lineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        inputTitle = findViewById(R.id.note_title_input);
        branchSpinner = findViewById(R.id.note_input_branch);
        semSpinner = findViewById(R.id.notes_input_sem);
        subSpinner = findViewById(R.id.notes_input_subject);

        lineView = findViewById(R.id.line4);

        mDatabase = FirebaseFirestore.getInstance();

        adapter = new SubjectArrayAdapter(this,R.layout.subject_spinner, subList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        subSpinner.setAdapter(adapter);

        Button button = findViewById(R.id.add_notes_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upload();

            }
        });

        selectFile = findViewById(R.id.select_file_button);
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent,"ChooseFile"), REQUEST_CODE_DOC);

            }
        });

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                subList.clear();
                adapter.notifyDataSetChanged();

                switch (i){
                    case 1 : branch = "Common";break;
                    case 2 : branch = "CS";break;
                    case 3 : branch = "ME";break;
                    case 4 : branch = "CE";break;
                    case 5 : branch = "IT";break;
                    case 6 : branch = "CM";break;
                    case 7 : branch = "EC";break;
                    default:branch="non";break;
                }

                semSpinner.setSelection(0);

                if(branch.equals("Common")){
                    lineView.setVisibility(View.GONE);
                    subSpinner.setVisibility(View.GONE);
                    uploadSubject = false;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                subList.clear();
                adapter.notifyDataSetChanged();

                switch (i){
                    case 1 : sem = "Common";break;
                    case 2 : sem = "I";break;
                    case 3 : sem = "II";break;
                    case 4 : sem = "III";break;
                    case 5 : sem = "IV";break;
                    case 6 : sem = "V";break;
                    case 7 : sem = "VI";break;
                    case 8 : sem = "VII";break;
                    case 9 : sem = "VIII";break;
                    default:sem="non";break;
                }

                if(branch.equals("Common") || sem.equals("Common")){
                    lineView.setVisibility(View.GONE);
                    subSpinner.setVisibility(View.GONE);
                    uploadSubject = false;
                }else {

                    if (!sem.equals("non")) {

                        lineView.setVisibility(View.VISIBLE);
                        subSpinner.setVisibility(View.VISIBLE);
                        uploadSubject = true;
                        getSubList();


                    }

                }




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void upload() {

        if(checkInput()){

            final AlertDialog dialog = new MyProgressDialog(this);
            dialog.show();

            dialog.setTitle("Please wait...");
            dialog.setMessage("Uploading...");

            dialog.setCancelable(false);

            String title = inputTitle.getText().toString();
            String branch = branchSpinner.getSelectedItem().toString();
            String semester = semSpinner.getSelectedItem().toString();

            String branchLow;
            switch (branch){

                case "Common":branchLow = "Common";break;
                case "Computer Science":branchLow = "CS";break;
                case "Mechanical":branchLow = "ME";break;
                case "Civil":branchLow = "CE";break;
                case "Information Technology":branchLow = "IT";break;
                case "Chemical":branchLow = "CM";break;
                case "Electronic and Communication":branchLow = "EC";break;
                default:branchLow = "Common";break;
            }


            String year;
            switch (semester){

                case "I":year = "1st Year";break;
                case "II":year = "1st Year";break;
                case "III":year = "2nd Year";break;
                case "IV":year = "2nd Year";break;
                case "V":year = "3rd Year";break;
                case "VI":year = "3rd Year";break;
                case "VII":year = "4th Year";break;
                case "VIII":year = "4th Year";break;
                default:year = "Common";break;

            }

            if(uploadSubject){
                subjectModel = subList.get(subSpinner.getSelectedItemPosition());
            }else {
                subjectModel.setSubjectName("Common");
                subjectModel.setSubjectCode("Common");
            }

            String doc_id;
            final DocumentReference documentReference = mDatabase.collection("notes").document();
            doc_id = documentReference.getId();

            FirebaseAuth auth = FirebaseAuth.getInstance();

            FirebaseUser user = auth.getCurrentUser();


            // TODO: 27-03-2018 make sure user has setup the username first
            String name = user.getDisplayName();
            String email = user.getEmail();

            final Map<String,Object> map = new HashMap<>();
            map.put("title",title);
            map.put("branch",branchLow);
            map.put("semester",semester);
            map.put("year",year);
            map.put("subjectCode",subjectModel.getSubjectCode());
            map.put("subjectName",subjectModel.getSubjectName());
            map.put("uploadDate", FieldValue.serverTimestamp());
            map.put("fileSize",fileSize);
            map.put("byName",name);
            map.put("byId",email);
            map.put("verifiedBy",email);
            map.put("verified",true);
            map.put("version",1);
            map.put("noteId",doc_id);

            final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(Constants.NOTES_COLLECTION).child(doc_id+".pdf");

            mStorageRef.putFile(fileUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String url = taskSnapshot.getDownloadUrl().toString();

                    map.put("fileUrl",url);
                    documentReference.set(map).addOnSuccessListener(SubmitNotesActivity.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SubmitNotesActivity.this,"Added",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }
                    }).addOnFailureListener(SubmitNotesActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SubmitNotesActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            mStorageRef.delete();
                            dialog.dismiss();
                        }
                    });

                }
            }).addOnFailureListener(SubmitNotesActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SubmitNotesActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            });




        }

    }

    private boolean checkInput() {


        inputTitle.setError(null);
        boolean title = true;
        boolean sem = true;
        boolean branch = true;
        boolean file = true;

        TextView branchError = (TextView)branchSpinner.getSelectedView();
        TextView semError = (TextView)semSpinner.getSelectedView();

        if(TextUtils.isEmpty(inputTitle.getText())){

            inputTitle.setError("Enter title first");
            title = false;

        }
        if(branchSpinner.getSelectedItem().toString().equals("Select Branch")){

            branchError.setError("");
            branchError.setTextColor(Color.RED);//just to highlight that this is an error
            branchError.setText("None selected");//changes the selected item text to this
            branch = false;

        }

        if(semSpinner.getSelectedItem().toString().equals("Select Semester")){

            semError.setError("");
            semError.setTextColor(Color.RED);//just to highlight that this is an error
            semError.setText("None selected");//changes the selected item text to this
            sem = false;

        }


        if(fileUri == null){

            Toast.makeText(SubmitNotesActivity.this,"Select file first",Toast.LENGTH_SHORT).show();
            file = false;

        }

        return title && sem && branch && file ;


    }

    private void getSubList() {

        Query query;

        if(!branch.equals("Common")){
            query = mDatabase.collection(Constants.SUBJECT_COLLECTION_+branch).whereEqualTo("semester",sem).whereEqualTo("type","main");

            query.get().addOnSuccessListener(SubmitNotesActivity.this, new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    for (DocumentSnapshot dc : documentSnapshots) {
                        subList.add(dc.toObject(SubjectModel.class));
                        adapter.notifyDataSetChanged();
                    }

                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_DOC && resultCode==RESULT_OK){

            fileUri = data.getData();

            fileName = getFileName(fileUri);
            fileSize = getFileSize(fileUri);
            selectFile.setText(fileName);

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

    public long getFileSize(Uri returnUri) {

        Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);

        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        return returnCursor.getLong(sizeIndex);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
