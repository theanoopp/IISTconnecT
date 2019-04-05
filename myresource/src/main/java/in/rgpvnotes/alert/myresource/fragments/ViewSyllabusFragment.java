package in.rgpvnotes.alert.myresource.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.activities.ViewSyllabusActivity;
import in.rgpvnotes.alert.myresource.models.subject.SingleSubject;


public class ViewSyllabusFragment extends Fragment {

    private String branch;
    private String program;
    private String semester;

    private RecyclerView recyclerView;


    public ViewSyllabusFragment() {
        // Required empty public constructor
    }


    public static ViewSyllabusFragment newInstance(String branch, String program,String semester) {
        ViewSyllabusFragment fragment = new ViewSyllabusFragment();
        Bundle args = new Bundle();
        args.putString("branch", branch);
        args.putString("program", program);
        args.putString("semester", semester);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            branch = getArguments().getString("branch");
            program = getArguments().getString("program");
            semester = getArguments().getString("semester");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.recycler_view_layout, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        if(program.equals("B.Tech")){
            program = "B_Tech";
        }

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("syllabus")
                .child(program)
                .child(branch)
                .child(semester);

        query.keepSynced(true);

        FirebaseRecyclerOptions<SingleSubject> options =
                new FirebaseRecyclerOptions.Builder<SingleSubject>()
                        .setLifecycleOwner(this)
                        .setQuery(query, SingleSubject.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<SingleSubject, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_assignment_list, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, int position, SingleSubject model) {

                final String code = getRef(position).getKey();
                final String name = model.getName();

                holder.bind(model);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent = new Intent(getActivity(), ViewSyllabusActivity.class);

                        intent.putExtra("name",name);
                        intent.putExtra("branch",branch);
                        intent.putExtra("sem",semester);
                        intent.putExtra("program",program);
                        intent.putExtra("subjectCode",code);

                        startActivity(intent);


                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);

        return v;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView subName;
        private View mView;

        private ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            subName = itemView.findViewById(R.id.list_sub_name);

        }

        private void bind(SingleSubject model) {
            subName.setText(model.getName());
        }
    }

}
