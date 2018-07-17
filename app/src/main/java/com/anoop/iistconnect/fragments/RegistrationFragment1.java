package com.anoop.iistconnect.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anoop.iistconnect.R;


public class RegistrationFragment1 extends Fragment {

    private static final int GALLERY_REQUEST = 1;


    private RegistrationFragment1Listener mListener;

    private EditText nameET;
    private EditText emailET;
    private EditText passET;
    private EditText cpassET;
    private EditText enrollmentET;
    private RadioButton BE;
    private RadioButton BTech;
    private RadioGroup radioGroup;


    private String name;
    private String email;
    private String pass;
    private String enroll;
    private String program;
    private String branch;


    private boolean enrollCheck = true;

    public RegistrationFragment1() {
        // Required empty public constructor
    }


    public static RegistrationFragment1 newInstance() {
        return new RegistrationFragment1();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_registration_fragment1, container, false);

        //editTexts
        nameET = rootView.findViewById(R.id.nameET);
        emailET = rootView.findViewById(R.id.emailET);
        passET = rootView.findViewById(R.id.passET);
        cpassET = rootView.findViewById(R.id.cpassET);
        enrollmentET = rootView.findViewById(R.id.enrollment_no);

        BE = rootView.findViewById(R.id.radioBE);
        BTech = rootView.findViewById(R.id.radioBTech);
        radioGroup = rootView.findViewById(R.id.radioGroup);

        enrollmentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if(charSequence.length()==12){

                    enroll = enrollmentET.getText().toString();
                    branch = enroll.substring(4,6);
                    branch = branch.toUpperCase();

                    switch (branch){
                        case "CS":enrollCheck=true;break;
                        case "ME":enrollCheck=true;break;
                        case "CE":enrollCheck=true;break;
                        case "IT":enrollCheck=true;break;
                        case "CM":enrollCheck=true;break;
                        case "EC":enrollCheck=true;break;
                        default: enrollCheck=false;enrollmentET.setError("Invalid enrollment");break;
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button nextButton = rootView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInputs()){

                    int id = radioGroup.getCheckedRadioButtonId();
                    if(id == R.id.radioBE ){
                        program = "BE";
                    }else {
                        program = "B.Tech";
                    }

                    onButtonPressed(name,email,pass,enroll,program,branch);
                }

            }
        });

        return rootView;
    }

    private boolean checkInputs() {

        name = nameET.getText().toString();
        email = emailET.getText().toString();
        enroll = enrollmentET.getText().toString();
        pass = passET.getText().toString();
        String cpass = cpassET.getText().toString();

        enroll = enroll.toUpperCase();

        boolean n = true, e = true ,p=true,c=true,radio=true;

        nameET.setError(null);
        emailET.setError(null);
        enrollmentET.setError(null);
        passET.setError(null);
        cpassET.setError(null);


        if(name.length()<3 ){
            nameET.setError("Enter valid name");
            n = false;
        }
        if(email.length()<3){
            emailET.setError("Enter valid Email");
            e = false;
        }
        if(enroll.length()!=12){
            enrollmentET.setError("Enter valid enrollment");
            enrollCheck=false;
        }

        if (pass.length() < 6) {
            passET.setError("password must be of minimum 6 character");
            p = false;
        }

        if (!pass.equals(cpass)) {
            cpassET.setError("password must be same");
            c = false;
        }

        if(radioGroup.getCheckedRadioButtonId() == -1){
            radio = false;
            Toast.makeText(getActivity(), "Please select your program", Toast.LENGTH_SHORT).show();//Set error to last Radio button
        }


        return p && c && n && e && enrollCheck && radio;

    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String name,String email,String pass,String enroll,String program,String branch) {
        if (mListener != null) {
            mListener.onSubmit(name,email,pass,enroll,program,branch);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegistrationFragment1Listener) {
            mListener = (RegistrationFragment1Listener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface RegistrationFragment1Listener {
        void onSubmit(String name,String email,String pass,String enroll,String program,String branch);
    }
}
