package com.seachange.healthandsafty.model

/**
 * Created by kevinsong on 13/12/2017.
 */

class JSAAnswer {

    var answer: String? = null
    var isChecked: Boolean = false

    constructor() {

    }

    constructor(content: String, checked: Boolean) {
        this.answer = content
        this.isChecked = checked
    }
}
