package com.anoop.iistconnectfaculty.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anoop.iistconnectfaculty.R;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;

import in.rgpvnotes.alert.myresource.ViewHolder.NotesViewHolder;
import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.model.NotesModel;
import in.rgpvnotes.alert.myresource.utils.Constants;

public class MyNotesFragment extends Fragment {

    private static final String TAG = "MyNotesFragment";

    private FirebaseFirestore mDatabase;

    private TextView infoText;


    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;

    private FirestoreRecyclerAdapter mAdapter;

    private MyNotesListener mListener;

    private SharedPreferences sharedPref ;

    private SharedPreferences.Editor editor;


    public MyNotesFragment() {
        // Required empty public constructor
    }


    public static MyNotesFragment newInstance() {
        return new MyNotesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_notes, container, false);

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = v.findViewById(R.id.my_notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        sharedPref = this.getActivity().getSharedPreferences("notes_download_status", Context.MODE_PRIVATE);

        editor = sharedPref.edit();

        setupRecyclerView();

        infoText = v.findViewById(R.id.notes_inf_text);

        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                browseNote();

            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyNotesListener) {
            mListener = (MyNotesListener) context;
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

    public void browseNote() {
        if (mListener != null) {
            mListener.myNotesInteraction();
        }
    }


    private void setupRecyclerView() {

        Query query = mDatabase.collection(Constants.FACULTY_COLLECTION).document(mAuth.getCurrentUser().getUid()).collection("user_notes");

        FirestoreRecyclerOptions<NotesModel> options = new FirestoreRecyclerOptions.Builder<NotesModel>()
                .setLifecycleOwner(this)
                .setQuery(query, NotesModel.class)
                .build();

        mAdapter = new FirestoreRecyclerAdapter<NotesModel, NotesViewHolder>(options) {
            @Override
            public void onBindViewHolder(final NotesViewHolder holder, int position, final NotesModel model) {

                infoText.setVisibility(View.GONE);
                holder.bind(model);

                File storagePath = new File(Environment.getExternalStorageDirectory(), "Notes");
                final String a = storagePath.getPath();
                if(!storagePath.exists()) {
                    storagePath.mkdirs();
                }

                final File myFile = new File(storagePath,model.getNoteId()+".pdf");

                int id = sharedPref.getInt(model.getNoteId(),-1);

                if(myFile.exists()){

                    holder.setIconView(1);

                }else {

                    if(Status.RUNNING == PRDownloader.getStatus(id)){
                        holder.setIconView(3);
                        holder.mView.setClickable(false);
                    }else {
                        holder.setIconView(2);
                    }

                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(myFile.exists()){

                            //openFile
                            openPdf(myFile);
                        }else {
                            //download file
                            holder.setIconView(3);
                            int downloadId = PRDownloader.download(model.getFileUrl(), a, model.getNoteId() + ".pdf")
                                    .build()
                                    .start(new OnDownloadListener() {
                                        @Override
                                        public void onDownloadComplete() {

                                            holder.setIconView(1);
                                            holder.setCardClickable(true);
                                            editor.remove(model.getNoteId());
                                            editor.commit();
                                            mAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onError(Error error) {

                                            holder.setIconView(2);
                                            holder.setCardClickable(true);
                                            editor.remove(model.getNoteId());
                                            editor.commit();
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });


                            editor.putInt(model.getNoteId(), downloadId);
                            editor.commit();

                        }
                    }
                });

                holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Remove from your notes ?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                removeNote(model.getNoteId());

                            }
                        });
                        builder.setNegativeButton("No",null);

                        builder.show();

                        return true;
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

    private void removeNote(String note_id) {

        final AlertDialog dialog = new MyProgressDialog(getActivity());
        dialog.show();

        dialog.setMessage("Please wait...");
        dialog.setTitle("Loading...");

        dialog.setCancelable(false);

        File storagePath = new File(Environment.getExternalStorageDirectory(), "Notes");
        final String a = storagePath.getPath();
        if(!storagePath.exists()) {
            storagePath.mkdirs();
        }

        final File myFile = new File(storagePath,note_id+".pdf");

        myFile.delete();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference ref = database.collection(Constants.FACULTY_COLLECTION).document(auth.getCurrentUser().getUid()).collection("user_notes").document(note_id);

        ref.delete().addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });


    }

    private void openPdf(File myFile) {

        Uri path = Uri.fromFile(myFile);
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            pdfOpenintent.setDataAndType(path, "application/pdf");
        }else {
            Uri photoURI = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".provider",myFile);
            pdfOpenintent.setDataAndType(photoURI, "application/pdf");
            pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        try {
            getActivity().startActivity(pdfOpenintent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "There is no app to handle pdf files...", Toast.LENGTH_SHORT).show();
        }

    }


    public interface MyNotesListener {
        // TODO: Update argument type and name
        void myNotesInteraction();
    }


}
