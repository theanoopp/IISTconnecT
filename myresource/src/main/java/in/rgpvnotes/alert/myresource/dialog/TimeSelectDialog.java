package in.rgpvnotes.alert.myresource.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import in.rgpvnotes.alert.myresource.R;

public class TimeSelectDialog extends DialogFragment {

    private SelectListener mListener;

    private Spinner semSpinner;
    private Spinner timeSpinner;
    public Button okButton;

    private String semester;
    private String timeSlot;

    public TimeSelectDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static TimeSelectDialog newInstance() {
        return new TimeSelectDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.time_select_dialog, container);

        semSpinner = v.findViewById(R.id.select_time_sem);
        timeSpinner = v.findViewById(R.id.select_time_time);
        Button okButton = v.findViewById(R.id.select_time_button);
        Button cancelButton = v.findViewById(R.id.select_time_cancel_button);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInputs()){
                    onButtonPressed(semester,timeSlot);
                }



            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().finish();

            }
        });



        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String semester,String timeSlot) {
        if (mListener != null) {
            mListener.onSelect(semester,timeSlot);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectListener) {
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

        semester = semSpinner.getSelectedItem().toString();
        timeSlot = timeSpinner.getSelectedItem().toString();

        if(semester.equals("Select Semester")){
            Toast.makeText(getActivity(), "Select semester", Toast.LENGTH_SHORT).show();
            return false;
        }else if(timeSlot.equals("Select Time slot")){
            Toast.makeText(getActivity(), "Select time slot", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;


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
        void onSelect(String semester, String timeSlot);

    }



}
