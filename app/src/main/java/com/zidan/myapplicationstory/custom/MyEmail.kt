package com.zidan.myapplicationstory.custom

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.zidan.myapplicationstory.R


class MyEmail : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s!!).matches()) {
                    error = context.getString(R.string.email_invalid)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // do nothing
            }
        })
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        hint = "Email"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START

    }
}