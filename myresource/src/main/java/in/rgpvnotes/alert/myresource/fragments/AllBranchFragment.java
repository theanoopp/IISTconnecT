package in.rgpvnotes.alert.myresource.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.rgpvnotes.alert.myresource.R;


public class AllBranchFragment extends Fragment implements View.OnClickListener{

    private BranchSelectListener mListener;

    public AllBranchFragment() {
        // Required empty public constructor
    }


    public static AllBranchFragment newInstance() {
        return new AllBranchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_branch, container, false);

        CardView cseCard = v.findViewById(R.id.b_cse);
        CardView meCard = v.findViewById(R.id.b_me);
        CardView civilCard = v.findViewById(R.id.b_civil);
        CardView itCard = v.findViewById(R.id.b_it);
        CardView chCard = v.findViewById(R.id.b_cm);
        CardView ecCard = v.findViewById(R.id.b_ec);
        CardView otherCard = v.findViewById(R.id.b_others);

        cseCard.setOnClickListener(this);
        meCard.setOnClickListener(this);
        civilCard.setOnClickListener(this);
        itCard.setOnClickListener(this);
        chCard.setOnClickListener(this);
        ecCard.setOnClickListener(this);
        otherCard.setOnClickListener(this);

        return v;
    }

    public void onButtonPressed(String branch) {
        if (mListener != null) {
            mListener.onBranchSelect(branch);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BranchSelectListener) {
            mListener = (BranchSelectListener) context;
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

        if(view.getId()==R.id.b_cse){

            onButtonPressed("CS");

        }else if(view.getId()==R.id.b_me){

            onButtonPressed("ME");

        }else if(view.getId()==R.id.b_civil){

            onButtonPressed("CE");

        }else if(view.getId()==R.id.b_it){

            onButtonPressed("IT");

        }else if(view.getId()==R.id.b_cm){

            onButtonPressed("CM");

        }else if(view.getId()==R.id.b_ec){

            onButtonPressed("EC");

        }else if(view.getId()==R.id.b_others){

            onButtonPressed("Common");

        }

    }


    public interface BranchSelectListener {
        // TODO: Update argument type and name
        void onBranchSelect(String branch);
    }
}
