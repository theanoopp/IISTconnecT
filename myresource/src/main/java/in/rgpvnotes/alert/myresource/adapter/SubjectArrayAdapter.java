package in.rgpvnotes.alert.myresource.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.models.SubjectModel;


/**
 * Created by Anoop on 09-03-2018.
 */

public class SubjectArrayAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<SubjectModel> items;
    private final int mResource;

    public SubjectArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);

        TextView subName = view.findViewById(R.id.spinner_sub_name);
        TextView subCode = view.findViewById(R.id.spinner_sub_code);

        SubjectModel model = items.get(position);

        if(model.getSubjectCode().equals("Common")){
            subCode.setVisibility(View.GONE);
        }

        subName.setText(model.getSubjectName());
        subCode.setText(model.getSubjectCode());

        return view;

    }

}
