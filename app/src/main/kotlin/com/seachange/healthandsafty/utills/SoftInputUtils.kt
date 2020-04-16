package com.seachange.healthandsafty.utills

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun View?.hideSoftInput() {
    (this?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
            ?.hideSoftInputFromWindow(this?.windowToken, 0)
}

fun View?.showSoftInput() {
    (this?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
            ?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText?.showSoftInput(setSelectionToLastChar: Boolean = false) {
    (this as View).showSoftInput()
    setSelection(length())
}