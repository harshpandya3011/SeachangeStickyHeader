package com.seachange.healthandsafty.helper

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by kevinsong on 13/11/2017.
 */

class EmailValidator {

    private val pattern: Pattern
    private var matcher: Matcher? = null

    init {
        pattern = Pattern.compile(EMAIL_PATTERN)
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex
     * hex for validation
     * @return true valid hex, false invalid hex
     */
    fun validate(hex: String): Boolean {

        matcher = pattern.matcher(hex)
        return matcher!!.matches()

    }

    companion object {

        private val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    }
}
