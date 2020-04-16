package com.seachange.healthandsafty.fragment


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.viewmodel.ManageUsersViewModel
import kotlinx.android.synthetic.main.fragment_pass_code.*

class PassCodeFragment : Fragment() {

    private val manageUsersViewModel by activityViewModels<ManageUsersViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pass_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pin_view_pass_code.setAnimationEnable(true)
        pin_view_pass_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.toString()?.count() == pin_view_pass_code.itemCount) {

                }
            }

        })

        pin_view_pass_code.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            return@OnEditorActionListener if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                true

            } else false
        })

    }
}
