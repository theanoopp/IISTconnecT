package in.rgpvnotes.alert.myresource.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import in.rgpvnotes.alert.myresource.R;


public class SemesterFragment extends Fragment implements View.OnClickListener {


    private OnSemesterSelectedListener mListener;

    public SemesterFragment() {
        // Required empty public constructor
    }


    public static SemesterFragment newInstance() {
        return new SemesterFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_semester, container, false);

        CardView sem1 = v.findViewById(R.id.I_sem);
        CardView sem2 = v.findViewById(R.id.II_sem);
        CardView sem3 = v.findViewById(R.id.III_sem);
        CardView sem4 = v.findViewById(R.id.IV_sem);
        CardView sem5 = v.findViewById(R.id.V_sem);
        CardView sem6 = v.findViewById(R.id.VI_sem);
        CardView sem7 = v.findViewById(R.id.VII_sem);
        CardView sem8 = v.findViewById(R.id.VIII_sem);

        sem1.setOnClickListener(this);
        sem2.setOnClickListener(this);
        sem3.setOnClickListener(this);
        sem4.setOnClickListener(this);
        sem5.setOnClickListener(this);
        sem6.setOnClickListener(this);
        sem7.setOnClickListener(this);
        sem8.setOnClickListener(this);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String semester) {
        if (mListener != null) {
            mListener.OnSemesterSelected(semester);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSemesterSelectedListener) {
            mListener = (OnSemesterSelectedListener) context;
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

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.I_sem){
            onButtonPressed("I");
        }else if(id == R.id.II_sem){
            onButtonPressed("II");
        }else if(id == R.id.III_sem){
            onButtonPressed("III");
        }else if(id == R.id.IV_sem){
            onButtonPressed("IV");
        }else if(id == R.id.V_sem){
            onButtonPressed("V");
        }else if(id == R.id.VI_sem){
            onButtonPressed("VI");
        }else if(id == R.id.VII_sem){
            onButtonPressed("VII");
        }else if(id == R.id.VIII_sem){
            onButtonPressed("VIII");
        }

    }

    public interface OnSemesterSelectedListener {
        // TODO: Update argument type and name
        void OnSemesterSelected(String semester);
    }
}
