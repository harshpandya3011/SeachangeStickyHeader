package com.seachange.healthandsafty.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.adapter.ManageUsersRecyclerAdapter
import com.seachange.healthandsafty.viewmodel.ManageUsersViewModel
import kotlinx.android.synthetic.main.fragment_manage_users.*

class ManageUsersFragment : Fragment() {

    private val manageUsersViewModel by activityViewModels<ManageUsersViewModel>()

    private lateinit var manageUsersRecyclerAdapter: ManageUsersRecyclerAdapter

    private var removeDialog: AlertDialog? = null
    private var resetPasscodeDialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manage_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbar_manage_users)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        manageUsersViewModel.passCodeGeneratedLiveData.observe(this, Observer {
            resetPasscodeDialog?.findViewById<TextView>(R.id.tv_reset_pass_code_msg)?.text = it
        })
        manageUsersViewModel.errorGeneratePasscodeLiveData.observe(this, Observer {
            resetPasscodeDialog?.dismiss()
            resetPasscodeDialog = null
        })
        manageUsersViewModel.progressGeneratePasscodeLiveData.observe(this, Observer {
            resetPasscodeDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = it != true
            resetPasscodeDialog?.findViewById<View>(R.id.progress_reset_pass_code)?.visibility = if (it == true) View.VISIBLE else View.INVISIBLE
        })
        manageUsersViewModel.progressDeleteUserLiveData.observe(this, Observer {
            if (it == true) return@Observer

            removeDialog?.dismiss()
            removeDialog = null
        })
        manageUsersViewModel.snackBarMessageLiveData.observe(this, Observer {
            if (it == null) return@Observer

            Snackbar.make(coordinator_manage_users, it, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab_add_user)
                    .show()
        })

        manageUsersViewModel.selectedUser = null

        manageUsersViewModel.usersLiveData.observe(this, Observer {
            if (manageUsersRecyclerAdapter.itemCount < it?.count() ?: 0) {
                manageUsersRecyclerAdapter.submitList(it) {
                    rv_manage_users.smoothScrollToPosition(0)
                }
            } else {
                manageUsersRecyclerAdapter.submitList(it)
            }
        })
        manageUsersRecyclerAdapter = ManageUsersRecyclerAdapter { clickedView, item, adapterPosition ->
            (clickedView.parent.parent as EasySwipeMenuLayout).resetStatus()
            when (clickedView.id) {
                R.id.tv_remove_user -> {
                    removeDialog?.dismiss()
                    removeDialog = MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.remove_user)
                            .setMessage(getString(R.string.msg_remove_user, item.fullName.orEmpty()))
                            .setNegativeButton(R.string.cancel_button) { dialog, which ->
                                dialog.cancel()
                            }
                            .setPositiveButton(R.string.remove, null)
                            .setCancelable(false)
                            .show()

                    removeDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                        (it as TextView).text = null
                        it.setCompoundDrawables(
                                CircularProgressDrawable(requireContext()).apply {
                                    centerRadius = resources.getDimension(com.google.android.material.R.dimen.abc_dialog_padding_material) / 2
                                    setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                                    strokeWidth = resources.getDimension(com.google.android.material.R.dimen.abc_progress_bar_height_material)
                                    start()
                                    setBounds(0, 0, it.width, it.height)
                                },
                                null,
                                null,
                                null
                        )
                        it.isEnabled = false
                        removeDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.isEnabled = false
                        manageUsersViewModel.deleteUser(item.id)
                    }
                }
                R.id.tv_edit_user -> {
                    manageUsersViewModel.selectedUser = item
                    requireFragmentManager().commit {
                        replace(R.id.fl_container_manage_user, AddEditUserFragment())
                        addToBackStack(null)
                    }
                }
                R.id.tv_reset_passcode -> {
                    if (item.tenantId != null) {
                        resetPasscodeDialog?.dismiss()
                        resetPasscodeDialog = AlertDialog.Builder(requireContext())
                                .setTitle(R.string.temporary_passcode)
                                .setView(R.layout.dialog_reset_passcode)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    dialog.dismiss()

                                }
                                .setCancelable(false)
                                .show()

                        manageUsersViewModel.generatePasscode(item, item.tenantId)
                    }
                }
            }
        }
        rv_manage_users.adapter = manageUsersRecyclerAdapter
        manageUsersRecyclerAdapter.submitList(manageUsersViewModel.usersLiveData.value)

        fab_add_user.setOnClickListener {
            manageUsersViewModel.selectedUser = null
            requireFragmentManager().commit {
                replace(R.id.fl_container_manage_user, AddEditUserFragment())
                addToBackStack(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeDialog = null
        resetPasscodeDialog = null
    }

}
