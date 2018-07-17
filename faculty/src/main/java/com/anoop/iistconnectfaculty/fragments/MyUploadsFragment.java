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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anoop.iistconnectfaculty.R;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import in.rgpvnotes.alert.myresource.Constants;
import in.rgpvnotes.alert.myresource.ViewHolder.NotesViewHolder;
import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.models.NotesModel;

public class MyUploadsFragment extends Fragment {

    private FirebaseFirestore mDatabase;

    private TextView infoText;


    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;

    private FirestoreRecyclerAdapter mAdapter;


    private MyUploadsListener mListener;

    private SharedPreferences sharedPref ;

    private SharedPreferences.Editor editor;

    public MyUploadsFragment() {
        // Required empty public constructor
    }


    public static MyUploadsFragment newInstance() {
        return new MyUploadsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_uploads, container, false);

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.my_upload_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        sharedPref = this.getActivity().getSharedPreferences("notes_download_status", Context.MODE_PRIVATE);

        editor = sharedPref.edit();

        infoText = view.findViewById(R.id.myupload_inf_text);

        view.findViewById(R.id.fab_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadNote();

            }
        });

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        Query query = mDatabase.collection(Constants.NOTES_COLLECTION).whereEqualTo("byId",user.getEmail());

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

                        CharSequence options[] = new CharSequence[]{"Delete from server"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                switch (i) {

                                    case 0:

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setMessage("Delete note ?");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                deleteNote(model.getNoteId());

                                            }
                                        });
                                        builder.setNegativeButton("No",null);

                                        builder.show();
                                        break;


                                }

                            }
                        });

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

    private void deleteNote(final String note_id) {

        final AlertDialog dialog = new MyProgressDialog(getActivity());
        dialog.show();

        dialog.setMessage("Please wait...");
        dialog.setTitle("Loading...");

        dialog.setCancelable(false);

        File storagePath = new File(Environment.getExternalStorageDirectory(), "Notes");

        if(!storagePath.exists()) {
            storagePath.mkdirs();
        }

        File myFile = new File(storagePath,note_id+".pdf");

        myFile.delete();

        final StorageReference storage = FirebaseStorage.getInstance().getReference("notes/"+note_id+".pdf");

        storage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference ref = database.collection("notes").document(note_id);

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
        }).addOnFailureListener(new OnFailureListener() {
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
            Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider",myFile);
            pdfOpenintent.setDataAndType(photoURI, "application/pdf");
            pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        try {
            getActivity().startActivity(pdfOpenintent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "There is no app to handle pdf files...", Toast.LENGTH_SHORT).show();
        }

    }


    public void uploadNote() {
        if (mListener != null) {
            mListener.myUploadInteraction();
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyUploadsListener) {
            mListener = (MyUploadsListener) context;
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


    public interface MyUploadsListener {
        // TODO: Update argument type and name
        void myUploadInteraction();
    }
}
