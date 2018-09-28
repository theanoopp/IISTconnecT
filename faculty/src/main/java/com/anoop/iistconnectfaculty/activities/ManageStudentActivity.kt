package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.model.StudentCoursesMap
import `in`.rgpvnotes.alert.myresource.model.StudentModel
import `in`.rgpvnotes.alert.myresource.utils.Constants
import `in`.rgpvnotes.alert.myresource.utils.GlideApp
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.anoop.iistconnectfaculty.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_manage_student.*
import android.support.v7.app.AlertDialog
import android.widget.Toast
import de.hdodenhof.circleimageview.CircleImageView



class ManageStudentActivity : AppCompatActivity() {

    private lateinit  var adapter: FirestoreRecyclerAdapter<StudentCoursesMap, StudentViewHolder>

    private lateinit var courseId : String


    private val mDatabase = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_student)

        courseId = intent.getStringExtra("courseId")

        studentList.layoutManager = LinearLayoutManager(this)

        studentList.itemAnimator = DefaultItemAnimator()

        setupRecyclerView()

    }

    private fun setupRecyclerView() {

        val query = mDatabase.collection(Constants.studentCoursesMap).whereEqualTo("courseId",courseId)

        val response = FirestoreRecyclerOptions.Builder<StudentCoursesMap>()
                .setQuery(query, StudentCoursesMap::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<StudentCoursesMap, StudentViewHolder>(response) {

            override fun onBindViewHolder(holder: StudentViewHolder, position: Int, model: StudentCoursesMap) {

                val docId = snapshots.getSnapshot(position).id

                holder.bind(model,applicationContext)

                holder.itemView.setOnClickListener {

                    val options = arrayOf<CharSequence>("View Student", "Remove Student")
                    val builder = AlertDialog.Builder(this@ManageStudentActivity)

                    builder.setItems(options) { _, i ->
                        when (i) {

                            0 -> {

                                val intent = Intent(this@ManageStudentActivity, ViewStudentActivity::class.java)
                                intent.putExtra("studentId", model.studentId)
                                startActivity(intent)

                            }

                            1 -> {

                                val conformDeleteDialog = AlertDialog.Builder(this@ManageStudentActivity)
                                conformDeleteDialog.setCancelable(false)
                                conformDeleteDialog.setTitle("Delete Student ?")
                                conformDeleteDialog.setMessage("Are you sure you want to remove this student from class ...")

                                conformDeleteDialog.setPositiveButton("No") { _, _ ->


                                }

                                conformDeleteDialog.setNegativeButton("Yes Remove") { _, _ ->

                                    removeStudent(docId)

                                }
                                conformDeleteDialog.show()

                            }
                        }
                    }

                    builder.show()

                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.student_row, parent, false)
                return StudentViewHolder(view)
            }
        }

        adapter.notifyDataSetChanged()
        studentList.adapter = adapter


    }

    private fun removeStudent( docId : String) {


        val docPath = mDatabase.collection(Constants.studentCoursesMap).document(docId)

        docPath.delete().addOnSuccessListener {

            Toast.makeText(applicationContext,"Student Removed",Toast.LENGTH_SHORT).show()


        }.addOnFailureListener {

            Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()

        }



    }

    public override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    public override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }



    class StudentViewHolder (val view: View) : RecyclerView.ViewHolder(view) {

        val studentName: TextView = view.findViewById(R.id.student_row_name)
        val studentEnroll: TextView = view.findViewById(R.id.student_row_enroll)
        private val studentPhoto : CircleImageView = view.findViewById(R.id.student_row_photo)

        fun bind(map: StudentCoursesMap,context: Context) {

            // TODO : it can be realtime :P
            val database = FirebaseFirestore.getInstance()

            database.collection(Constants.STUDENTS_COLLECTION).document(map.studentId!!).get().addOnSuccessListener {

                val student : StudentModel = it.toObject(StudentModel::class.java)!!
                studentName.text = student.studentName
                studentEnroll.text = student.enrollmentNumber

                GlideApp.with(view)
                        .load(student.profileImage)
                        .into(studentPhoto)


            }.addOnFailureListener {

                studentName.text = "Unable to get Student info.."
                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }



        }

    }

}
