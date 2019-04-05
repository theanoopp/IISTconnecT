package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.activities.BrowseSyllabusActivity
import `in`.rgpvnotes.alert.myresource.activities.GalleryActivity
import `in`.rgpvnotes.alert.myresource.utils.Constants
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.anoop.iistconnectfaculty.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.home_board.*


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener  {


    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this@HomeActivity)  }

    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private val mCurrentUser by lazy { mAuth.currentUser }
    private val mDatabase by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowCustomEnabled(true)

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val actionBarView = inflater.inflate(R.layout.home_custom_bar, null)

        supportActionBar!!.customView = actionBarView


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        b_my_classes.setOnClickListener(this)
        b_timetable.setOnClickListener(this)
        b_notes.setOnClickListener(this)
        b_syllabus.setOnClickListener(this)

        initUser()


    }

    private fun initUser() {

        if (mCurrentUser == null) {

            startActivity(Intent(this@HomeActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()

        } else {

            val studentsReference = mDatabase.collection(Constants.FACULTY_COLLECTION).document(mCurrentUser!!.uid)

            studentsReference.addSnapshotListener(this) { documentSnapshot, e ->

                if (documentSnapshot!!.exists()) {

                    val name = documentSnapshot.getString("name")
                    val email = documentSnapshot.getString("email")

                    val userSessionEdit = sharedPreferences.edit()
                    userSessionEdit.putString("username", name)
                    userSessionEdit.putString("useremail", email)
                    userSessionEdit.apply()

                    show_name.text = "Hello $name"
                    show_enrollment.text = email


                } else {

                    Toast.makeText(applicationContext, "Please contact admin..", Toast.LENGTH_LONG).show()
                    mAuth.signOut()
                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()

                }
            }

        }

    }

    override fun onClick(view: View) {

        when {
            view.id == R.id.b_my_classes -> startActivity(Intent(this@HomeActivity, ClassHomeActivity::class.java))
            view.id == R.id.b_timetable -> Toast.makeText(applicationContext, "t", Toast.LENGTH_SHORT).show()
            view.id == R.id.b_notes -> startActivity(Intent(this@HomeActivity, NotesActivity::class.java))
            view.id == R.id.b_syllabus -> startActivity(Intent(this@HomeActivity, BrowseSyllabusActivity::class.java))
        }


    }

    override fun onBackPressed() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_gallery -> startActivity(Intent(this@HomeActivity, GalleryActivity::class.java))
            R.id.nav_share -> {

                // TODO: 31-03-2018 share this app

            }
            R.id.nav_feedback -> Toast.makeText(applicationContext, "f", Toast.LENGTH_SHORT).show()
            // TODO: 31-03-2018 feedback
            R.id.nav_logout -> {

                mAuth.signOut()
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                finish()

            }
            R.id.nav_my_class -> startActivity(Intent(this@HomeActivity, ClassHomeActivity::class.java))
        }


        drawer_layout.closeDrawer(GravityCompat.START)
        return false

    }


}
