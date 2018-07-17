package com.anoop.iistconnect.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.anoop.iistconnect.R;


public class RegistrationFragment2 extends Fragment {

    private Spinner branchSP;
    private Spinner sectionSP;
    private Spinner semesterSP;

    private String branch;
    private String section;
    private String semester;

    private ArrayAdapter<CharSequence> sectionAdapter ;


    private OnSubmitListener mListener;

    public RegistrationFragment2() {
        // Required empty public constructor
    }


    public static RegistrationFragment2 newInstance(String branch) {
        RegistrationFragment2 fragment = new RegistrationFragment2();
        Bundle args = new Bundle();
        args.putString("branch", branch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            branch = getArguments().getString("branch");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_registration_fragment2, container, false);

        branchSP = rootView.findViewById(R.id.spinnerBranch);
        sectionSP = rootView.findViewById(R.id.spinnerSection);
        semesterSP = rootView.findViewById(R.id.spinnerSemester);

        int m = R.array.select_class;
        switch (branch){
            case "CS": branchSP.setSelection(0);m = R.array.cs_class;break;
            case "ME": branchSP.setSelection(1);m = R.array.me_class;break;
            case "CE": branchSP.setSelection(2);m = R.array.ce_class;break;
            case "IT": branchSP.setSelection(3);m = R.array.it_class;break;
            case "CM": branchSP.setSelection(4);m = R.array.cm_class;break;
            case "EC": branchSP.setSelection(5);m = R.array.ec_class;break;
        }

        sectionAdapter = ArrayAdapter.createFromResource(getContext(), m, android.R.layout.simple_spinner_item);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSP.setAdapter(sectionAdapter);
        sectionAdapter.notifyDataSetChanged();

        branchSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int m = R.array.select_class;
                switch (i){
                    case 0 : m = R.array.cs_class;branch="CS";break;
                    case 1 : m = R.array.me_class;branch="ME";break;
                    case 2 : m = R.array.ce_class;branch="CE";break;
                    case 3 : m = R.array.it_class;branch="IT";break;
                    case 4 : m = R.array.cm_class;branch="CM";break;
                    case 5 : m = R.array.ec_class;branch="EC";break;
                }

                sectionAdapter = ArrayAdapter.createFromResource(getContext(), m, android.R.layout.simple_spinner_item);
                sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sectionSP.setAdapter(sectionAdapter);
                sectionAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        rootView.findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInputs()){
                    onButtonPressed(branch,section,semester);
                    getActivity().onBackPressed();
                }

            }
        });

        return rootView;
    }

    private boolean checkInputs() {

        section = sectionSP.getSelectedItem().toString();
        TextView sectionErrorText = (TextView)sectionSP.getSelectedView();

        semester = semesterSP.getSelectedItem().toString();
        TextView semErrorText = (TextView)semesterSP.getSelectedView();

        boolean sec=true,sem=true;

        if(section.equals("Select Section")){

            sectionErrorText.setError("");
            sectionErrorText.setTextColor(Color.RED);//just to highlight that this is an error
            sectionErrorText.setText("None selected");//changes the selected item text to this
            sec=false;

        }

        if(semester.equals("Select Semester")){

            semErrorText.setError("");
            semErrorText.setTextColor(Color.RED);//just to highlight that this is an error
            semErrorText.setText("None selected");//changes the selected item text to this
            sem=false;

        }

        return sem && sec;

    }


    public void onButtonPressed(String branch,String section,String semester) {
        if (mListener != null) {
            mListener.OnSubmit(branch,section,semester);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitListener) {
            mListener = (OnSubmitListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnSubmitListener {
        void OnSubmit(String branch,String section,String semester);
    }
}
