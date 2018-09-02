package com.anoop.iistconnectfaculty.activities;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.anoop.iistconnectfaculty.R;
import com.anoop.iistconnectfaculty.adapter.ViewAssignmentAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import in.rgpvnotes.alert.myresource.dialog.SlideshowDialogFragment;
import in.rgpvnotes.alert.myresource.model.AssignmentModel;
import in.rgpvnotes.alert.myresource.models.PhotosModel;

public class ViewAssignment extends AppCompatActivity implements ViewAssignmentAdapter.ItemClickListener  {

    private static final String TAG = "ViewAssignment";

    private ConstraintLayout questionLayout;
    private ConstraintLayout filesLayout;

    private FirebaseFirestore mDatabase;

    private TextView subNameText;
    private TextView descriptionText;
    private TextView typeText;
    private TextView sDateText;
    private TextView questionsText;

    private ArrayList<String> urlList = new ArrayList<>();

    private RecyclerView recyclerView;

    private ViewAssignmentAdapter viewAssignmentAdapter;

    private ArrayList<PhotosModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        subNameText = findViewById(R.id.subName);
        descriptionText = findViewById(R.id.description);
        typeText = findViewById(R.id.type);
        sDateText = findViewById(R.id.sdate);
        questionsText = findViewById(R.id.questions);

        questionLayout = findViewById(R.id.questions_layout);
        filesLayout = findViewById(R.id.files_layout);

        recyclerView = findViewById(R.id.view_recyclerView);

        mDatabase = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        viewAssignmentAdapter = new ViewAssignmentAdapter(urlList);
        viewAssignmentAdapter.setClickListener(this);

        recyclerView.setAdapter(viewAssignmentAdapter);

        AssignmentModel model = (AssignmentModel) getIntent().getSerializableExtra("model");

        urlList.addAll(model.getFiles());

        getSupportActionBar().setTitle(model.getTitle());

        subNameText.setText(model.getSubjectName());
        descriptionText.setText(model.getDescription());
        typeText.setText(model.getType());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(model.getSubmitDate());

        sDateText.setText(date);


        if(model.getQuestions().length()>0){
            questionsText.setText(model.getQuestions());
            questionLayout.setVisibility(View.VISIBLE);
        }

        if(urlList.size()>0){
            filesLayout.setVisibility(View.VISIBLE);
            viewAssignmentAdapter.notifyDataSetChanged();
        }





    }



    @Override
    public void onItemClick(View view, int position) {

        list.clear();

        for (String a :urlList) {

            PhotosModel photosModel = new PhotosModel();
            photosModel.setDownloadURL(a);
            list.add(photosModel);

        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("images", list);
        bundle.putInt("position", position);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
