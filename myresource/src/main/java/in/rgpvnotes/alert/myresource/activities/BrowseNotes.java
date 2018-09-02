package in.rgpvnotes.alert.myresource.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.dialog.FilterNotesDialog;
import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.fragments.AllBranchFragment;
import in.rgpvnotes.alert.myresource.fragments.BrowseNotesFragment;
import in.rgpvnotes.alert.myresource.model.NotesModel;

public class BrowseNotes extends AppCompatActivity implements AllBranchFragment.BranchSelectListener,FilterNotesDialog.SelectListener,BrowseNotesFragment.OnButtonClickListener {

    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Browse Notes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = getIntent().getStringExtra("user_type");

        AllBranchFragment first = AllBranchFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame,first, "first")
                // Add this transaction to the back stack
                .commit();


    }

    @Override
    public void onBranchSelect(String branch) {

        FragmentManager fm = getSupportFragmentManager();
        FilterNotesDialog dialog = FilterNotesDialog.newInstance(branch);

        dialog.show(fm, "fragment_select_semester");

    }


    @Override
    public void onSelect(String branch, String sem, String subCode) {

        BrowseNotesFragment second = BrowseNotesFragment.newInstance(branch,sem,subCode);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame,second, "second")
                // Add this transaction to the back stack
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void OnButtonClick(NotesModel model) {

        final AlertDialog dialog = new MyProgressDialog(this);
        dialog.show();

        dialog.setMessage("Please wait...");
        dialog.setTitle("Loading...");

        dialog.setCancelable(false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference ref = database.collection(user).document(auth.getCurrentUser().getUid()).collection("user_notes").document(model.getNoteId());

        ref.set(model).addOnSuccessListener(BrowseNotes.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(BrowseNotes.this,"Added",Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        }).addOnFailureListener(BrowseNotes.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(BrowseNotes.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
