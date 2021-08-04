package com.rasel.demoapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import com.rasel.demoapplication.BuildConfig
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*

class SupportedDeviceChecker {

    companion object {
        const val TAG: String = "SupportedDeviceChecker"
    }

    fun verify(context: Context?): Boolean {
        if (!BuildConfig.DEBUG) {
            if (RootCheckUtil.isDeviceRooted(context)) {
                return false
            }
            if (!checkAppSignature(context)) {
                return false
            }
        }
        return true
    }

    private fun checkAppSignature(context: Context?): Boolean {
        try {
            val fingerprint = getCertificateSHA1Fingerprint(context)
            if (TextUtils.isEmpty(fingerprint)) return false
            if (fingerprint == BuildConfig.SIGNATURE) {
                return true
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    @SuppressLint("PackageManagerGetSignatures")
    private fun getCertificateSHA1Fingerprint(context: Context?): String? {
        if (context == null) return null
        val pm = context.packageManager
        val packageName = context.packageName
        val flags = PackageManager.GET_SIGNATURES
        val packageInfo: PackageInfo? = try {
            pm.getPackageInfo(packageName, flags)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, e)
            return null
        }
        val signatures = packageInfo?.signatures
        val cert = signatures?.get(0)?.toByteArray()
        val input: InputStream = ByteArrayInputStream(cert)
        val cf: CertificateFactory? = try {
            CertificateFactory.getInstance("X509")
        } catch (e: CertificateException) {
            Log.w(TAG, e)
            return null
        }
        val c: X509Certificate? = try {
            cf?.generateCertificate(input) as X509Certificate
        } catch (e: CertificateException) {
            Log.w(TAG, e)
            return null
        }
        return try {
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = md.digest(c!!.encoded)
            byte2HexFormatted(publicKey)
        } catch (e: NoSuchAlgorithmException) {
            Log.w(TAG, e)
            return null
        } catch (e: CertificateEncodingException) {
            Log.w(TAG, e)
            return null
        }
    }

    private fun byte2HexFormatted(arr: ByteArray): String {
        val str = StringBuilder(arr.size * 2)
        for (i in arr.indices) {
            var h = Integer.toHexString(arr[i].toInt())
            val l = h.length
            if (l == 1) h = "0$h"
            if (l > 2) h = h.substring(l - 2, l)
            str.append(h.uppercase(Locale.getDefault()))
            if (i < arr.size - 1) str.append(':')
        }
        return str.toString()
    }
}