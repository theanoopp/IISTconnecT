package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.dialog.QRDialog
import `in`.rgpvnotes.alert.myresource.model.Lecture
import `in`.rgpvnotes.alert.myresource.model.Student
import `in`.rgpvnotes.alert.myresource.model.StudentCoursesMap
import `in`.rgpvnotes.alert.myresource.utils.Constants
import `in`.rgpvnotes.alert.myresource.utils.GlideApp
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.anoop.iistconnectfaculty.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_view_lecture.*
import java.text.SimpleDateFormat

class ViewLecture : AppCompatActivity() {

    private val mDatabase = FirebaseFirestore.getInstance()

    private val presentStudents = arrayListOf<StudentCoursesMap>()

    private lateinit var lecture : Lecture

    private lateinit var adapter: FirestoreRecyclerAdapter<StudentCoursesMap, ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_lecture)

        lecture = intent.getSerializableExtra("lecture") as Lecture

        if(lecture.active){
            activeSwitch.isChecked = true
        }

        activeSwitch.setOnClickListener{

            activeSwitch.isEnabled = false
            mDatabase.collection(Constants.lectureCollection).document(lecture.lectureId!!).update("active",activeSwitch.isChecked).addOnSuccessListener {

                if(activeSwitch.isChecked){
                    Toast.makeText(applicationContext,"Active",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"Closed",Toast.LENGTH_SHORT).show()
                }
                activeSwitch.isEnabled = true


            }.addOnFailureListener {
                activeSwitch.isEnabled = true
                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }

        }

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

        studentList.layoutManager = LinearLayoutManager(this)

        studentList.itemAnimator = DefaultItemAnimator()

        val allQuery = mDatabase.collection(Constants.attendanceCollection).whereEqualTo("lectureId", lecture.lectureId)

        allQuery.get().addOnSuccessListener {

            for (document in it.documents) {
                val map = document.toObject(StudentCoursesMap::class.java)
                if (map != null) {
                    presentStudents.add(map)
                }
                d("MYE",map.toString())
            }

            setupRecyclerView()

        }.addOnFailureListener {

            Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()

        }

    }

    private fun setupRecyclerView() {

        val query = mDatabase.collection(Constants.studentCoursesMap).whereEqualTo("courseId",lecture.courseId!!)

        val response = FirestoreRecyclerOptions.Builder<StudentCoursesMap>()
                .setQuery(query, StudentCoursesMap::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<StudentCoursesMap, ViewHolder>(response) {

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: StudentCoursesMap) {


                val boolean = presentStudents.contains(model)
                holder.bind(model,boolean,applicationContext)

                holder.itemView.setOnClickListener {

                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.student_row, parent, false)
                return ViewHolder(view)
            }
        }

        adapter.notifyDataSetChanged()
        studentList.adapter = adapter

        adapter.startListening()


    }

    public override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    class ViewHolder(val view : View) : RecyclerView.ViewHolder(view) {

        private val photo : CircleImageView = view.findViewById(R.id.student_row_photo)
        private val name : TextView = view.findViewById(R.id.student_row_name)
        private val enrollment : TextView = view.findViewById(R.id.student_row_enroll)

        fun bind( map : StudentCoursesMap, boolean: Boolean,context: Context) {

            // TODO : it can be realtime :P
            val database = FirebaseFirestore.getInstance()

            database.collection(Constants.STUDENTS_COLLECTION).document(map.studentId!!).get().addOnSuccessListener {

                val student : Student = it.toObject(Student::class.java)!!
                enrollment.text = student.enrollmentNumber

                GlideApp.with(view)
                        .load(student.profileImage)
                        .into(photo)

                if(boolean){
                    name.text = student.studentName + " was present"
                }else{
                    name.text = student.studentName
                }


            }.addOnFailureListener {

                name.text = "Unable to get Student info.."
                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }


        }
    }
}
