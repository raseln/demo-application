package com.rasel.demoapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.rasel.demoapplication.utils.Toaster.Companion.toast

object Toaster {

    @SuppressLint("ShowToast")
    fun showToast(context: Context, message: String, length: Int = Toast.LENGTH_SHORT) {
        dismissExistingToast()
        toast = Toast.makeText(context, message, length)
        toast?.show()
    }

    private fun dismissExistingToast() {
        if (toast != null) {
            toast?.cancel()
            toast = null
        }
    }

    object Companion{
        var toast: Toast? = null
    }
}