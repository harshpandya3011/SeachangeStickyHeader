package com.seachange.healthandsafty.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.ManageUsersFragment
import com.seachange.healthandsafty.viewmodel.ManageUsersViewModel
import java.net.HttpURLConnection

class ManageUsersActivity : AppCompatActivity() {

    private val manageUsersViewModel by viewModels<ManageUsersViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame_layout)

        manageUsersViewModel.fetchUsers()

        manageUsersViewModel.errorLiveData.observe(this, Observer {
            if (it == null) return@Observer

            when (it) {
                is NoConnectionError -> {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.no_connection_error_title)
                            .setMessage(R.string.no_connection_error)
                            .setPositiveButton(R.string.close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_cloud_off_black_24dp)
                            .show()
                }
                is ServerError -> {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.server_error_title)
                            .setMessage(R.string.server_error)
                            .setPositiveButton(R.string.close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_mood_bad_black_24dp)
                            .show()
                }
                is TimeoutError -> {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.timeout_error_title)
                            .setMessage(R.string.timeout_error)
                            .setPositiveButton(R.string.close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_access_time_black_24dp)
                            .show()
                }
                is AuthFailureError -> {
                    if (it.networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
                        AlertDialog.Builder(this)
                                .setTitle(R.string.permission_denied_error_title)
                                .setMessage(R.string.permission_denied_error)
                                .setPositiveButton(R.string.close) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .setIcon(R.drawable.ic_block_black_24dp)
                                .show()
                    }
                }
            }

        })

        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.commit {
                replace(R.id.fl_container_manage_user, ManageUsersFragment())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        manageUsersViewModel.cancelUserRequest()
    }
}
