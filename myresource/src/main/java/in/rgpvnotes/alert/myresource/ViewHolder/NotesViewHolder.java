package in.rgpvnotes.alert.myresource.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.model.NotesModel;

/**
 * Created by Anoop on 11-03-2018.
 */

public class NotesViewHolder extends RecyclerView.ViewHolder {


    private TextView size;
    private TextView title;
    private TextView subject;
    private TextView sem;
    private TextView date;
    private TextView byName;

    private ImageView iconView;

    public View mView;

    private ProgressBar progressBar;


    public NotesViewHolder(View itemView) {
        super(itemView);
        mView = itemView;


        size = itemView.findViewById(R.id.notes_row_size);
        title = itemView.findViewById(R.id.notes_row_title);
        subject = itemView.findViewById(R.id.notes_row_sub);
        sem = itemView.findViewById(R.id.notes_row_sem);
        date = itemView.findViewById(R.id.notes_row_date);
        byName = itemView.findViewById(R.id.notes_row_name);

        iconView = itemView.findViewById(R.id.notes_row_icon);

        progressBar = itemView.findViewById(R.id.notes_row_download_bar);

    }


    public void setCardClickable(boolean clickable){

        mView.setClickable(clickable);

    }


    public void setIconView(int status){

        /*

        1 = file exists
        2 = file does not exists
        3 = downloading file

         */

        if(status == 1){

            progressBar.setVisibility(View.GONE);
            iconView.setVisibility(View.VISIBLE);
            iconView.setImageResource(R.drawable.ic_pdf_logo);

        }else if(status == 2) {

            progressBar.setVisibility(View.GONE);
            iconView.setVisibility(View.VISIBLE);
            iconView.setImageResource(R.drawable.ic_file_download);

        }else if(status == 3){

            progressBar.setVisibility(View.VISIBLE);
            iconView.setVisibility(View.GONE);

        }

    }


    public void bind(NotesModel model) {

        String a = getStringSizeLengthFile(model.getFileSize());

        size.setText(a);
        title.setText(model.getTitle());
        byName.setText("By : " + model.getByName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

        String time = simpleDateFormat.format(model.getUploadDate());
        date.setText(time);

        if (!model.getSemester().equals("Common")) {

            sem.setText(model.getSemester() + " Semester");
            subject.setText(model.getSubjectName());
            sem.setVisibility(View.VISIBLE);
            subject.setVisibility(View.VISIBLE);

        } else {

            sem.setVisibility(View.GONE);
            subject.setVisibility(View.GONE);

        }

        if(model.getSubjectName().equals("default")){

            subject.setVisibility(View.GONE);
        }


    }

    private String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.0");

        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;


        if (size < sizeMo)
            return df.format(size / sizeKb) + " Kb";
        else if (size < sizeGo)
            return df.format(size / sizeMo) + " MB";
        else if (size < sizeTerra)
            return df.format(size / sizeGo) + " GB";

        return "";
    }

}
