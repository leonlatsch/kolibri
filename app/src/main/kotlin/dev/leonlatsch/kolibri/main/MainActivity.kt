package dev.leonlatsch.kolibri.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

import com.google.android.material.navigation.NavigationView

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.broker.MessageConsumer
import dev.leonlatsch.kolibri.broker.queue.MessageQueue
import dev.leonlatsch.kolibri.database.EntityChangedListener
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.database.model.User
import dev.leonlatsch.kolibri.main.fragment.ChatFragment
import dev.leonlatsch.kolibri.main.fragment.ProfileFragment
import dev.leonlatsch.kolibri.main.fragment.SettingsFragment
import dev.leonlatsch.kolibri.main.login.LoginActivity
import dev.leonlatsch.kolibri.util.ImageUtil

/**
 * The main activity which holds the main menu and the main fragments
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EntityChangedListener<User> {

    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    var progressOverlay: View? = null
        private set
    private var titleTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        UserInterface.addEntityChangedListener(this)

        MessageConsumer.start(this)
        MessageQueue.start()

        navigationView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        progressOverlay = findViewById(R.id.progressOverlay)
        titleTextView = findViewById(R.id.main_toolbar_title)

        navigationView!!.setNavigationItemSelectedListener(this)

        val logout = navigationView!!.findViewById<TextView>(R.id.nav_logout)
        logout.setOnClickListener { logout() }

        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close)
        drawerLayout!!.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        mapUserToDrawer(UserInterface.user)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    ChatFragment()).commit()
            navigationView!!.setCheckedItem(R.id.nav_chat)
        }
    }

    override fun onNavigationItemSelected(@NonNull menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {

            R.id.nav_chat -> displayFragment(ChatFragment(), getString(R.string.app_name))

            R.id.nav_profile -> displayFragment(ProfileFragment(), getString(R.string.profile))

            R.id.nav_settings -> displayFragment(SettingsFragment(), getString(R.string.settings))

            R.id.nav_info -> startActivity(Intent(applicationContext, InfoActivity::class.java))
        }

        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.findFragmentById(R.id.fragment_container) is ChatFragment) {
                super.onBackPressed()
            } else {
                displayFragment(ChatFragment(), getString(R.string.app_name))
                navigationView!!.setCheckedItem(R.id.nav_chat)
            }
        }
    }

    private fun displayFragment(fragment: Fragment, title: String) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit()
        titleTextView!!.setText(title)
    }

    /**
     * Display a user in the drawer view
     *
     * @param user
     */
    private fun mapUserToDrawer(user: User?) {
        if (user == null) {
            return
        }
        val header = navigationView!!.getHeaderView(0)
        val profilePic = header.findViewById<CardView>(R.id.nav_profile_pic_card).findViewById<ImageView>(R.id.nav_profile_pic)
        val username = header.findViewById<TextView>(R.id.nav_username)
        val email = header.findViewById<TextView>(R.id.nav_email)

        if (user.profilePicTn != null) {
            profilePic.setImageBitmap(ImageUtil.createBitmap(user.profilePicTn))
        }
        username.text = user.username
        email.text = user.email
    }

    /**
     * Called when the logout button is pressed
     *
     *
     * Delete the logged in user, all chats and contacts
     */
    fun logout() {
        val user = UserInterface.user
        if (user != null) {
            UserInterface.delete(user)
        }
        ContactInterface.deleteAll()
        ChatInterface.deleteAll()
        MessageConsumer.stop()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    /**
     * Called when the logged in user has changed
     *
     * @param newEntity
     */
    override fun entityChanged(newEntity: User?) {
        if (newEntity != null) {
            mapUserToDrawer(newEntity)
        }
    }

    fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }
}
