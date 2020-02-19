package dev.leonlatsch.kolibri.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import java.util.ArrayList

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.constants.Values
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.main.adapter.UserAdapter
import dev.leonlatsch.kolibri.main.chat.ChatActivity
import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.dto.UserDTO
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.rest.service.UserService
import dev.leonlatsch.kolibri.util.AndroidUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity to search for new users
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class UserSearchActivity : AppCompatActivity() {

    private var searchBtn: ImageView? = null
    private var searchBar: EditText? = null
    private var listView: ListView? = null
    private var userAdapter: UserAdapter? = null
    private var searchHint: TextView? = null

    private var progressOverlay: View? = null

    private var userService: UserService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)

        val toolbar = findViewById<Toolbar>(R.id.user_search_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userService = RestServiceFactory.getUserService()

        searchBtn = findViewById(R.id.userSearchBtn)
        searchBar = findViewById(R.id.userSearchEditText)
        listView = findViewById(R.id.user_search_list_view)
        searchHint = findViewById(R.id.user_search_hint)
        progressOverlay = findViewById(R.id.progressOverlay)

        userAdapter = UserAdapter(this, ArrayList())

        searchBtn!!.setOnClickListener { search() }
        searchBar!!.setOnEditorActionListener { _, _, _ ->
            search()
            true
        }
        setUserListVisibility()

        listView!!.adapter = userAdapter
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val user = listView!!.getItemAtPosition(position)
            if (user is UserDTO) {
                proceedUser(user)
            }
        }

        searchBar!!.requestFocus()
    }

    private fun setUserListVisibility() {
        if (!userAdapter!!.isEmpty()) {
            listView!!.visibility = View.VISIBLE
            searchHint!!.visibility = View.GONE
        } else {
            listView!!.visibility = View.GONE
            searchHint!!.visibility = View.VISIBLE
        }
    }

    /**
     * Called when a user is selected.
     *
     * @param user
     */
    private fun proceedUser(user: UserDTO) {
        val call = userService!!.getPublicKey(UserInterface.accessToken!!, user.uid!!)
        call.enqueue(object : Callback<Container<String>> {
            override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                if (response.isSuccessful) {
                    val intent = Intent(applicationContext, ChatActivity::class.java)
                    intent.putExtra(Values.INTENT_KEY_CHAT_UID, user.uid)
                    intent.putExtra(Values.INTENT_KEY_CHAT_USERNAME, user.username)
                    intent.putExtra(Values.INTENT_KEY_CHAT_PROFILE_PIC, user.profilePicTn)
                    intent.putExtra(Values.INTENT_KEY_CHAT_PUBLIC_KEY, response.body().content)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<Container<String>>, t: Throwable) {
            }
        })
    }

    /**
     * Search for users with the value from the search EditText.
     */
    private fun search() {
        if (searchBar!!.getText().toString().length >= 2) { // Only search if at least 2 chars where entered
            isLoading(true)
            val call = userService!!.search(UserInterface.accessToken!!, searchBar!!.text.toString())
            call.enqueue(object : Callback<Container<List<UserDTO>>> {
                override fun onResponse(call: Call<Container<List<UserDTO>>>, response: Response<Container<List<UserDTO>>>) {
                    if (response.isSuccessful) {
                        val container = response.body()
                        userAdapter!!.clear()
                        if (container?.content != null && container?.content!!.isNotEmpty()) {
                            userAdapter!!.addAll(container!!.content)
                        }
                        setUserListVisibility()
                        searchHint!!.setText(R.string.user_search_no_results)
                    }
                    isLoading(false)
                }

                override fun onFailure(call: Call<Container<List<UserDTO>>>, t: Throwable) {
                    isLoading(false)
                }
            })
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            AndroidUtils.animateView(progressOverlay!!, View.VISIBLE, 0.4f)
        } else {
            AndroidUtils.animateView(progressOverlay!!, View.GONE, 0.4f)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
