package com.example.wastescanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.wastescanner.Fragments.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView
    private lateinit var googleSignInMenuItem: MenuItem
    private lateinit var headerView: View
    private lateinit var navigationMenu: Menu
    // Request code for google sign in
    private val TAG = "MainActivity"
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    // Firebase authentication
    private var fireAuth: FirebaseAuth? = null
    private var fireUser: FirebaseUser? = null
    private lateinit var fireUserName: TextView
    private lateinit var fireUserEmail: TextView
    private lateinit var fireUserPhoto: ImageView
    private lateinit var toolbar: MaterialToolbar
    //------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        googleSignInMenuItem = navigationView.menu.findItem(R.id.menu_google_sign_in)

        headerView = navigationView.getHeaderView(0)
        navigationMenu = navigationView.menu
        fireUserName = headerView.findViewById(R.id.fire_user_name)
        fireUserEmail = headerView.findViewById(R.id.fire_user_email)
        fireUserPhoto = headerView.findViewById(R.id.fire_user_icon)


        navigationView.setNavigationItemSelectedListener(this)


        toggleDrawer(toolbar)

        // On device rotation checks state if exists, else defaults at homepage
        checkSavedInstanceState(savedInstanceState, navigationView)

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                handleSignInResult(task)
            }
        }

        googleSignInMenuItem.setOnMenuItemClickListener {
            // Handle Google Sign-In button click here
            signInWithGoogle()
            true
        }

        fireAuth = FirebaseAuth.getInstance();

    }
    //------------------------------------------------------------------
    override fun onStart() {
        super.onStart()

        checkIfAlreadySignedIn()
        addAuthListener()
        //setScannerButtonListener();
    }
    //------------------------------------------------------------------
    private fun checkIfAlreadySignedIn(){
        fireUser = fireAuth?.getCurrentUser();
        if (fireUser != null) {
            headerView.visibility = View.VISIBLE;
            fireUserName.text = fireUser!!.displayName
            fireUserEmail.text = fireUser!!.email
            Glide.with(this)
                .load(fireUser!!.photoUrl)
                .into(fireUserPhoto)
            // Hides Sign In button
            navigationMenu.getItem(0).isVisible = false
        } else {
            headerView.visibility = View.GONE;
            navigationMenu.getItem(0).isVisible = true
        }
    }
    //------------------------------------------------------------------
    private fun addAuthListener(){
        fireAuth?.addAuthStateListener { firebaseAuth ->
            fireUser = firebaseAuth.currentUser
            if (fireUser != null) {
                headerView.visibility = View.VISIBLE;
                fireUserName.text = fireUser!!.displayName
                fireUserEmail.text = fireUser!!.email
                Glide.with(this)
                    .load(fireUser!!.photoUrl)
                    .into(fireUserPhoto)
                navigationMenu.getItem(0).isVisible = false
            } else {
                headerView.visibility = View.GONE;
                navigationMenu.getItem(0).isVisible = true
            }
        }
    }
    //------------------------------------------------------------------
    private fun signInWithGoogle() {
        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
    //------------------------------------------------------------------
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            val credential = GoogleAuthProvider
                .getCredential(account.idToken, null)
            FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // TODO: additional code if needed
                    } else {

                    }
                }
        } catch (e: ApiException) {
            Log.w(TAG, "google signIn error:" + e.statusCode)
        }
    }
    //------------------------------------------------------------------
    private fun turnOffToolbarName(toolbar: MaterialToolbar){
        // Fixes hamburger too
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    //------------------------------------------------------------------
    private fun toggleDrawer(toolbar: MaterialToolbar) {
        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
    //------------------------------------------------------------------
    private fun checkSavedInstanceState(savedInstanceState: Bundle?,
        navigationView: NavigationView) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                HomeFragment()
            ).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }
    //------------------------------------------------------------------
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    //------------------------------------------------------------------
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        var fragment: Fragment = HomeFragment()
        when(item.itemId){
            R.id.nav_home -> fragment = HomeFragment()
            R.id.nav_products -> fragment = ProductListFragment()
            R.id.nav_wasterecords -> fragment = WasteRecordListFragment()
            /*R.id.nav_settings -> fragment = SettingsFragment()*/
            R.id.nav_aboutus -> fragment = AboutFragment()

            R.id.nav_scanner -> {
                val intent = Intent(this, ScannerActivity::class.java)
                transaction.addToBackStack("HomeFragment")
                startActivity(intent)
            }

            else -> return false
        }
        /*transaction.addToBackStack("HomeFragment")*/
        transaction.replace(R.id.fragment_container, fragment).commit()
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    //------------------------------------------------------------------
}