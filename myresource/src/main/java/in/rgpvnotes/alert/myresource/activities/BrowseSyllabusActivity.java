package in.rgpvnotes.alert.myresource.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.fragments.AllBranchFragment;
import in.rgpvnotes.alert.myresource.fragments.SemesterFragment;
import in.rgpvnotes.alert.myresource.fragments.ViewSyllabusFragment;

public class BrowseSyllabusActivity extends AppCompatActivity implements AllBranchFragment.BranchSelectListener,SemesterFragment.OnSemesterSelectedListener {

    private String branch;
    private String program;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_syllabus);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Syllabus");

        AllBranchFragment first = AllBranchFragment.newInstance();

        // TODO: 17-04-2018 remove other from from view

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame,first, "first")
                .commit();

    }

    @Override
    public void onBranchSelect(String branch) {

        this.branch = branch;

        CharSequence options[] = new CharSequence[]{"BE", "B.Tech"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {

                    case 0:

                        program = "BE";
                        break;

                    case 1:

                        program = "B.Tech";
                        break;


                }

                SemesterFragment second = SemesterFragment.newInstance();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame,second, "second")
                        // Add this transaction to the back stack
                        .addToBackStack(null)
                        .commit();



            }
        });

        builder.show();


    }

    @Override
    public void OnSemesterSelected(String semester) {

        ViewSyllabusFragment third = ViewSyllabusFragment.newInstance(branch,program,semester);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame,third, "third")
                // Add this transaction to the back stack
                .addToBackStack(null)
                .commit();

    }
}
