package com.seachange.healthandsafty.fragment


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.model.UserData
import com.seachange.healthandsafty.utills.hideSoftInput
import com.seachange.healthandsafty.viewmodel.ManageUsersViewModel
import kotlinx.android.synthetic.main.fragment_add_edit_user.*

class AddEditUserFragment : Fragment() {

    private val manageUsersViewModel by activityViewModels<ManageUsersViewModel>()

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            //no-op
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //no-op
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setSubmitButtonEnabledOrDisabled()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_edit_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbar_add_edit_user)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setTitle(
                    if (manageUsersViewModel.selectedUser == null) {
                        btn_add_update_user.setText(R.string.add_user)
                        R.string.add_user
                    } else {
                        btn_add_update_user.setText(R.string.update_user)
                        R.string.edit_user
                    }
            )
        }

        manageUsersViewModel.addEditSuccessLiveData.observe(this, Observer {
            requireActivity().onBackPressed()
        })
        manageUsersViewModel.progressAddUpdateUserLiveData.observe(this, Observer(this::showAddUpdateProgress))

        edt_first_name.addTextChangedListener(textWatcher)
        edt_last_name.addTextChangedListener(textWatcher)
        edt_email.addTextChangedListener(textWatcher)

        if (edt_first_name.text.isNullOrEmpty()) {
            edt_first_name.setText(manageUsersViewModel.selectedUser?.firstName)
        }
        if (edt_last_name.text.isNullOrEmpty()) {
            edt_last_name.setText(manageUsersViewModel.selectedUser?.lastName)
        }
        if (edt_email.text.isNullOrEmpty()) {
            edt_email.setText(manageUsersViewModel.selectedUser?.email)
        }

        if (toggle_group_user_role.checkedButtonId == View.NO_ID) {
            toggle_group_user_role.check(
                    if (manageUsersViewModel.selectedUser?.userRole == UserData.USER_ROLE_MANAGER) {
                        R.id.btn_caygo_manager
                    } else {
                        R.id.btn_caygo_champion
                    }
            )
        }
        if (toggle_group_user_type.checkedButtonId == View.NO_ID) {
            toggle_group_user_type.check(
                    if (manageUsersViewModel.selectedUser?.isPasscodeOnly != false) {
                        R.id.btn_mobile_only
                    } else {
                        R.id.btn_portal_user
                    }
            )
        }

        toggle_group_user_type.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked && group.checkedButtonId == View.NO_ID) {
                group.check(checkedId)
            }
            if (isChecked) {
                setSubmitButtonEnabledOrDisabled()
                til_email.visibility = if (checkedId == R.id.btn_mobile_only) View.GONE else View.VISIBLE
            }
        }
        toggle_group_user_role.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked && group.checkedButtonId == View.NO_ID) {
                group.check(checkedId)
            }
            if (isChecked) setSubmitButtonEnabledOrDisabled()
        }

        btn_add_update_user.setOnClickListener {
            it.hideSoftInput()
            val selectedUser = manageUsersViewModel.selectedUser
            val isPasscodeOnly = toggle_group_user_type.checkedButtonId == R.id.btn_mobile_only
            val email = if (isPasscodeOnly) {
                null
            } else {
                edt_email.text?.toString().orEmpty()

            }
            if (selectedUser == null) {
                manageUsersViewModel.addUser(
                        edt_first_name.text?.toString().orEmpty(),
                        edt_last_name.text?.toString().orEmpty(),
                        email,
                        if (toggle_group_user_role.checkedButtonId == R.id.btn_caygo_manager) {
                            UserData.USER_ROLE_MANAGER
                        } else {
                            UserData.USER_ROLE_CHAMPION
                        },
                        isPasscodeOnly,
                        if (toggle_group_user_role.checkedButtonId == R.id.btn_caygo_manager) {
                            6
                        }else {
                            7
                        }
                )
            } else {
                manageUsersViewModel.editUser(
                        selectedUser.id,
                        selectedUser.tenantId,
                        edt_first_name.text?.toString(),
                        edt_last_name.text?.toString(),
                        email,
                        if (toggle_group_user_role.checkedButtonId == R.id.btn_caygo_manager) {
                            UserData.USER_ROLE_MANAGER
                        } else {
                            UserData.USER_ROLE_CHAMPION
                        },
                        isPasscodeOnly,
                        if (toggle_group_user_role.checkedButtonId == R.id.btn_caygo_manager) {
                            6
                        } else {
                            7
                        }
                )
            }
        }

        showAddUpdateProgress(manageUsersViewModel.progressAddUpdateUserLiveData.value)
        setSubmitButtonEnabledOrDisabled()
        til_email.visibility = if (toggle_group_user_type.checkedButtonId == R.id.btn_mobile_only) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        edt_first_name.removeTextChangedListener(textWatcher)
        edt_last_name.removeTextChangedListener(textWatcher)
        edt_email.removeTextChangedListener(textWatcher)

        toggle_group_user_role.clearOnButtonCheckedListeners()
        toggle_group_user_type.clearOnButtonCheckedListeners()
    }

    private fun setSubmitButtonEnabledOrDisabled() {
        btn_add_update_user.isEnabled = manageUsersViewModel.areInputsValidAndUpdated(
                edt_first_name.text?.toString(),
                edt_last_name.text?.toString(),
                edt_email.text?.toString(),
                if (toggle_group_user_role.checkedButtonId == R.id.btn_caygo_manager) {
                    UserData.USER_ROLE_MANAGER
                } else {
                    UserData.USER_ROLE_CHAMPION
                },
                toggle_group_user_type.checkedButtonId == R.id.btn_mobile_only
        )
    }

    private fun showAddUpdateProgress(showProgress: Boolean?) {
        progress_add_update_user.visibility = if (showProgress == true) {
            btn_add_update_user.isEnabled = false
            btn_add_update_user.text = null
            View.VISIBLE
        } else {
            setSubmitButtonEnabledOrDisabled()
            btn_add_update_user.setText(
                    if (manageUsersViewModel.selectedUser == null) {
                        R.string.add_user
                    } else {
                        R.string.update_user
                    }
            )
            View.GONE
        }
    }

}
