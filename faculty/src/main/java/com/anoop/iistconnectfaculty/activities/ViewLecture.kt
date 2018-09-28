package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.dialog.QRDialog
import `in`.rgpvnotes.alert.myresource.model.Lecture
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.anoop.iistconnectfaculty.R
import kotlinx.android.synthetic.main.activity_view_lecture.*
import java.text.SimpleDateFormat

class ViewLecture : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_lecture)

        val lecture = intent.getSerializableExtra("lecture") as Lecture

        val dayFormat = SimpleDateFormat("EEEE : dd/MM/yyyy")
        val day = dayFormat.format(lecture.timestamp)
        lecuteDay.text = day


        if(lecture.topic != ""){

            lecuteTopic.visibility = View.VISIBLE
            lecuteTopic.text = lecture.topic

        }
        if(lecture.remark != ""){

            lecuteRemark.visibility = View.VISIBLE
            lecuteRemark.text = lecture.remark

        }

        QRbutton.setOnClickListener {
            val string = lecture.lectureId+ "@_@" +lecture.courseId
            val qrDialog = QRDialog(this@ViewLecture,string, "Scan this QR code to mark Attendance")
            qrDialog.show()
        }



    }
}
