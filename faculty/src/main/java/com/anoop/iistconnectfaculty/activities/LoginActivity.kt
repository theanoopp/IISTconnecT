package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.dialog.MyProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.anoop.iistconnectfaculty.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    private val mAuth by lazy { FirebaseAuth.getInstance() }

    private val dialog by lazy { MyProgressDialog(this@LoginActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(toolbar)

        signBT.setOnClickListener {
            if (checkInput()) {

                dialog.setTitle("Loading...")
                dialog.setMessage("Please wait..")

                dialog.show()

                val email = emailEditText.editText!!.text.toString()
                val pass = passEditText.editText!!.text.toString()

                mAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener {
                    val mDatabase = FirebaseFirestore.getInstance()
                    val currentUser = mAuth.currentUser

                    mDatabase.collection("Faculty").document(currentUser!!.uid).get().addOnSuccessListener(this@LoginActivity) { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            dialog.dismiss()
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                            finish()

                        } else {

                            dialog.dismiss()
                            Toast.makeText(applicationContext, "No record found", Toast.LENGTH_SHORT).show()
                            mAuth.signOut()

                        }
                    }.addOnFailureListener(this@LoginActivity) { e ->
                        dialog.dismiss()
                        Log.d(TAG, e.message)
                        Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    dialog.dismiss()
                    Log.d(TAG, e.message)
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                }


            }
        }


    }

    private fun checkInput(): Boolean {

        var e = true
        var p = true

        emailEditText.error = null
        passEditText.error = null


        if (emailEditText.editText!!.text.toString().isEmpty()) {
            emailEditText.error = "Email is required!"
            e = false

        }
        if (passEditText.editText!!.text.toString().length < 6) {
            passEditText.error = "Password must be of minimum 6 character"
            p = false
        }

        return e && p

    }
}