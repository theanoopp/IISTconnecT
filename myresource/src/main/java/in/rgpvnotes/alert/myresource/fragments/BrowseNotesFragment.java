package in.rgpvnotes.alert.myresource.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.ViewHolder.NotesViewHolder;
import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.model.NotesModel;


public class BrowseNotesFragment extends Fragment {

    private String branch;
    private String sem;
    private String subCode;

    private FirebaseFirestore mDatabase;

    private RecyclerView recyclerView;

    private FirestoreRecyclerAdapter mAdapter;

    private OnButtonClickListener mListener;


    public BrowseNotesFragment() {
        // Required empty public constructor
    }

    public static BrowseNotesFragment newInstance(String branch, String sem,String subCode) {
        BrowseNotesFragment fragment = new BrowseNotesFragment();
        Bundle args = new Bundle();
        args.putString("branch", branch);
        args.putString("sem", sem);
        args.putString("subCode", subCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            branch = getArguments().getString("branch");
            sem = getArguments().getString("sem");
            subCode = getArguments().getString("subCode");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.recycler_view_layout, container, false);

        mDatabase = FirebaseFirestore.getInstance();

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        setupRecyclerView();


        return v;
    }

    private void setupRecyclerView() {

        Query query;

        if(sem.equals("Common")){
            query = mDatabase.collection("notes").whereEqualTo("branch",branch).whereEqualTo("semester",sem);
        }else {
            query = mDatabase.collection("notes").whereEqualTo("branch",branch).whereEqualTo("semester",sem).whereEqualTo("subjectCode",subCode);
        }

        FirestoreRecyclerOptions<NotesModel> options = new FirestoreRecyclerOptions.Builder<NotesModel>()
                .setLifecycleOwner(this)
                .setQuery(query, NotesModel.class)
                .build();

        mAdapter = new FirestoreRecyclerAdapter<NotesModel, NotesViewHolder>(options) {
            @Override
            public void onBindViewHolder(NotesViewHolder holder, int position, final NotesModel model) {

                holder.bind(model);
                holder.setIconView(1);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Add to your notes ?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                onButtonPressed(model);

                            }
                        });
                        builder.setNegativeButton("No",null);

                        builder.show();

                    }
                });
            }

            @Override
            public NotesViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.notes_row, group, false);
                return new NotesViewHolder(view);
            }
        };

        recyclerView.setAdapter(mAdapter);


    }

    public void onButtonPressed(NotesModel model) {
        if (mListener != null) {
            mListener.OnButtonClick(model);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnButtonClickListener) {
            mListener = (OnButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnButtonClickListener {
        // TODO: Update argument type and name
        void OnButtonClick(NotesModel model);
    }



}
