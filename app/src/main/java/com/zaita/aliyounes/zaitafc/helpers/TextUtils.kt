package com.zaita.aliyounes.zaitafc.helpers

/*
    this is a utility class used for common text actions
 */
object TextUtils {
    // checks for null or empty string
    fun isEmpty(s: String?): Boolean {
        if (s != null) {
            if (!s.equals("", ignoreCase = true)) {
                return false
            }
        }
        return true
    }

    // Check if a given path is an image
    fun isImagePath(path: String): Boolean {
        val extension = path.substring(path.lastIndexOf('.'))
        return (extension.equals(".jpg", ignoreCase = true)
                || extension.equals(".jpeg", ignoreCase = true)
                || extension.equals(".png", ignoreCase = true)
                || extension.equals(".bmp", ignoreCase = true))
    }

    // Check if a given path is a video
    fun isVideoPath(path: String): Boolean {
        val extension = path.substring(path.lastIndexOf('.'))
        return (extension.equals(".avi", ignoreCase = true)
                || extension.equals(".mp4", ignoreCase = true)
                || extension.equals(".wmv", ignoreCase = true)
                || extension.equals(".flv", ignoreCase = true)
                || extension.equals(".3gp", ignoreCase = true)
                || extension.equals(".3gpp", ignoreCase = true))
    }

    // check if a string is an Integer string
    fun isInteger(text: String): Boolean {
        try {

            Integer.parseInt(text)
            return true
        } catch (ex: NumberFormatException) {
            return false
        }

    }

    // check if a string is a Double string
    fun isDouble(text: String): Boolean {
        try {

            java.lang.Double.parseDouble(text)
            return true
        } catch (ex: NumberFormatException) {
            return false
        }

    }
}
