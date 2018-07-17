package in.rgpvnotes.alert.myresource.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import in.rgpvnotes.alert.myresource.Constants;
import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.adapter.SubjectArrayAdapter;
import in.rgpvnotes.alert.myresource.models.SubjectModel;

/**
 * Created by Anoop on 26-03-2018.
 */

public class FilterNotesDialog extends DialogFragment {

    private static final String TAG = "FilterNotesDialog";

    private SelectListener mListener;

    private Spinner semSpinner;
    private Spinner subSpinner;
    public Button okButton;

    private FirebaseFirestore mDatabase;

    private ArrayList<SubjectModel> subList = new ArrayList<>();
    private SubjectArrayAdapter adapter;

    private String sem;

    private String subjectCode;

    private String branch;

    private boolean subSelected = false;

    private SubjectModel other = new SubjectModel();


    public FilterNotesDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterNotesDialog newInstance(String branch) {
        FilterNotesDialog frag = new FilterNotesDialog();
        Bundle args = new Bundle();
        args.putString("branch", branch);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.filter_notes_dialog, container);

        mDatabase = FirebaseFirestore.getInstance();

        branch = getArguments().getString("branch");

        semSpinner = v.findViewById(R.id.filter_notes_sem);
        subSpinner = v.findViewById(R.id.filter_notes_sub);
        okButton = v.findViewById(R.id.filter_notes_button);

        adapter = new SubjectArrayAdapter(getContext(),R.layout.subject_spinner, subList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        subSpinner.setAdapter(adapter);

        other.setSubjectName("Other");
        other.setSubjectCode("Common");
        subList.add(other);

        semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                subList.clear();
                subList.add(other);
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
                    default: sem = "non";subSpinner.setVisibility(View.GONE);break;
                }

                if (!sem.equals("non")) {
                    if(sem.equals("Common")){
                        subSpinner.setVisibility(View.GONE);
                        subSelected = false;
                    }else {
                        subSpinner.setVisibility(View.VISIBLE);
                        subSelected = true;
                        getSubList();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInputs()){

                    if(subSelected){


                        int a = subSpinner.getSelectedItemPosition();
                        subjectCode = subList.get(a).getSubjectCode();
                        onButtonPressed(branch,sem,subjectCode);
                        getDialog().dismiss();

                    }else {
                        onButtonPressed(branch,sem,"Common");
                        getDialog().dismiss();
                    }
                }

            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String branch,String sem,String subCode) {
        if (mListener != null) {
            mListener.onSelect(branch,sem,subCode);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FilterNotesDialog.SelectListener) {
            mListener = (SelectListener) context;
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

    private boolean checkInputs(){

        boolean sem = true;

        int semIndex = semSpinner.getSelectedItemPosition();

        if(semIndex == 0){
            Toast.makeText(getActivity(), "Select Semester First", Toast.LENGTH_SHORT).show();
            sem = false;
        }


        return sem;


    }

    private void getSubList() {


        Query query = mDatabase.collection(Constants.SUBJECT_COLLECTION_+branch).whereEqualTo("semester",sem).whereEqualTo("type","main");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                for (DocumentSnapshot dc : documentSnapshots) {
                    String a  = dc.getId();
                    Log.d(TAG,a);
                    subList.add(dc.toObject(SubjectModel.class));
                    adapter.notifyDataSetChanged();
                }

            }
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        getDialog().setTitle("Select Semester");

    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    public interface SelectListener {
        // TODO: Update argument type and name
        void onSelect(String branch, String sem, String subCode);
    }


}