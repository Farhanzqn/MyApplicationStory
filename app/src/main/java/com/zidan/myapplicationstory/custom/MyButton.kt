package com.zidan.myapplicationstory.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.zidan.myapplicationstory.R

class MyButton : AppCompatButton {

  private lateinit var enabledBackground: Drawable
  private lateinit var disabledBackground: Drawable
  private var txtColor: Int = 1

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  ) {
    init()
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    background = if(isEnabled) enabledBackground else disabledBackground

    setTextColor(ContextCompat.getColor(context, R.color.white))
    textSize = 15f
    gravity = Gravity.CENTER
    text = if(isEnabled) context.getString(R.string.login) else context.getString(R.string.login)
  }

  private fun init(){
    txtColor = ContextCompat.getColor(context, R.color.white)
    enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_enabled) as Drawable
    disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
  }
}