package com.rasel.demoapplication.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.*
import java.util.*

class RootCheckUtil {

    companion object {
        private val knownRootAppsPackages = arrayOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine",
            "eu.chainfire.supersu"
        )

        private val knownDangerousAppsPackages = arrayOf(
            "com.koushikdutta.rommanager",
            "com.koushikdutta.rommanager.license",
            "com.dimonvideo.luckypatcher",
            "com.chelpus.lackypatch",
            "com.ramdroid.appquarantine",
            "com.ramdroid.appquarantinepro"
        )

        private val suPaths = arrayOf(
            "/data/local/",
            "/data/local/bin/",
            "/data/local/xbin/",
            "/sbin/",
            "/su/bin/",
            "/system/bin/",
            "/system/bin/.ext/",
            "/system/bin/failsafe/",
            "/system/sd/xbin/",
            "/system/usr/we-need-root/",
            "/system/xbin/"
        )

        private val pathsThatShouldNotBeWritable = arrayOf(
            "/system",
            "/system/bin",
            "/system/sbin",
            "/system/xbin",
            "/vendor/bin",  //"/sys",
            "/sbin",
            "/etc"
        )

        fun isDeviceRooted(context: Context?): Boolean {
            return (detectRootManagementApps(context)
                    || detectPotentiallyDangerousApps(context)
                    || checkForBinary("su")
                    || checkForBinary("busybox")
                    || checkForBinary("SuperUser.apk", arrayOf("/system/app/"))
                    || checkForDangerousProps()
                    || checkForRWPaths()
                    || detectTestKeys()
                    || checkSuExists())
        }

        private fun detectTestKeys(): Boolean {
            val buildTags = Build.TAGS
            val buildFinger = Build.FINGERPRINT
            val product = Build.PRODUCT
            val hardware = Build.HARDWARE
            val display = Build.DISPLAY
            return buildTags != null && (buildTags.contains("test-keys")
                    || buildFinger.contains("genric.*test-keys")
                    || product.contains("generic")
                    || product.contains("sdk")
                    || hardware.contains("goldfish")
                    || display.contains(".*test-keys"))
        }

        private fun detectRootManagementApps(context: Context?): Boolean {
            return isAnyPackageFromListInstalled(context, listOf(*knownRootAppsPackages))
        }


        private fun detectPotentiallyDangerousApps(context: Context?): Boolean {
            return isAnyPackageFromListInstalled(context, listOf(*knownDangerousAppsPackages))
        }

        private fun checkForBinary(filename: String?): Boolean {
            return checkForBinary(filename, suPaths)
        }

        private fun checkForBinary(filename: String?, paths: Array<String>?): Boolean {
            if (filename == null || paths == null) return false
            var result = false
            for (path in paths) {
                val completePath = path + filename
                val f = File(completePath)
                val fileExists = f.exists()
                if (fileExists) {
                    result = true
                }
            }
            return result
        }

        private fun propsReader(): Array<String> {
            var inputstream: InputStream? = null
            try {
                inputstream = Runtime.getRuntime().exec("getprop").inputStream
            } catch (ignored: IOException) {
            }
            var propval = ""
            try {
                propval = Scanner(inputstream).useDelimiter("\\A").next()
            } catch (ignored: NoSuchElementException) {
            }
            return propval.split("\n".toRegex()).toTypedArray()
        }

        private fun mountReader(): Array<String>? {
            var inputstream: InputStream? = null
            try {
                inputstream = Runtime.getRuntime().exec("mount").inputStream
            } catch (ignored: IOException) {
            }
            if (inputstream == null) return null
            var propval = ""
            try {
                propval = Scanner(inputstream).useDelimiter("\\A").next()
            } catch (ignored: NoSuchElementException) {
            }
            return propval.split("\n".toRegex()).toTypedArray()
        }

        private fun isAnyPackageFromListInstalled(context: Context?, packages: List<String>): Boolean {
            requireNotNull(context) { "Context is null in route check processing." }
            var result = false
            val pm = context.packageManager
            for (packageName in packages) {
                try {
                    pm.getPackageInfo(packageName, 0)
                    result = true
                } catch (ignored: PackageManager.NameNotFoundException) {
                }
            }
            return result
        }

        private fun checkForDangerousProps(): Boolean {
            val dangerousProps: MutableMap<String, String> = HashMap()
            dangerousProps["ro.debuggable"] = "1"
            dangerousProps["ro.secure"] = "0"
            var result = false
            val lines = propsReader()
            for (line in lines) {
                for (key in dangerousProps.keys) {
                    if (line.contains(key)) {
                        var badValue = dangerousProps[key]
                        badValue = "[$badValue]"
                        if (line.contains(badValue)) {
                            result = true
                        }
                    }
                }
            }
            return result
        }

        private fun checkForRWPaths(): Boolean {
            var result = false
            val lines = mountReader()
            for (line in lines!!) {
                val args = line.split(" ".toRegex()).toTypedArray()
                if (args.size < 4) {
                    continue
                }
                val mountPoint = args[1]
                val mountOptions = args[3]
                for (pathToCheck in pathsThatShouldNotBeWritable) {
                    if (mountPoint.equals(pathToCheck, ignoreCase = true)) {
                        for (option in mountOptions.split(",".toRegex()).toTypedArray()) {
                            if (option.equals("rw", ignoreCase = true)) {
                                result = true
                                break
                            }
                        }
                    }
                }
            }
            return result
        }

        private fun checkSuExists(): Boolean {
            var process: Process? = null
            return try {
                process = Runtime.getRuntime().exec(arrayOf("which", "su"))
                val `in` = BufferedReader(InputStreamReader(process.inputStream))
                `in`.readLine() != null
            } catch (t: Throwable) {
                false
            } finally {
                process?.destroy()
            }
        }
    }
}