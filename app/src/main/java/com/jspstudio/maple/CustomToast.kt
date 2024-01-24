package com.jspstudio.maple

import android.content.Context
import android.widget.Toast

class CustomToast(context: Context?, msg: String?) : Toast(context) {
    init {
        if (toast == null) toast = makeText(context, msg, LENGTH_SHORT)
        else toast!!.setText(msg)
        toast!!.show()
    }
    companion object { private var toast: Toast? = null }
}