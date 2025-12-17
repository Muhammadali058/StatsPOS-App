package com.example.statspos.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, msg, length).show()

fun Context.showToast1(msg: String, length: Int = Toast.LENGTH_SHORT) {
    val toast = Toast.makeText(this, msg, length)
    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
    toast.show()
}

object HP {

}