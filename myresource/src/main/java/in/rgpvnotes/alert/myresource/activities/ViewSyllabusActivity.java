package in.rgpvnotes.alert.myresource.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.models.subject.SingleSubject;
import in.rgpvnotes.alert.myresource.models.subject.SingleUnit;

public class ViewSyllabusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String branch = getIntent().getStringExtra("branch");
        String sem = getIntent().getStringExtra("sem");
        String code = getIntent().getStringExtra("subjectCode");
        String name = getIntent().getStringExtra("name");
        String program = getIntent().getStringExtra("program");

        getSupportActionBar().setTitle(name);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("syllabus")
                .child(program)
                .child(branch)
                .child(sem)
                .child(code)
                .child("syllabus");

        query.keepSynced(true);

        FirebaseRecyclerOptions<SingleUnit> options =
                new FirebaseRecyclerOptions.Builder<SingleUnit>()
                        .setLifecycleOwner(this)
                        .setQuery(query, SingleUnit.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<SingleUnit, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_unit_row, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, int position, @NonNull SingleUnit model) {

                holder.bind(model);

            }
        };

        recyclerView.setAdapter(adapter);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView content;


        private ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.unit_row_title);
            content = itemView.findViewById(R.id.unit_row_content);

        }

        private void bind(SingleUnit model) {

            title.setText(model.getTitle());
            content.setText(model.getContent());

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
