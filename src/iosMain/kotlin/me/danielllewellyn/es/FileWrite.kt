package me.danielllewellyn.es

import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.writeToFile

class FileWrite {

    fun writeFile(text : String, path : String) {
        (text as NSString).writeToFile(path, true, NSUTF8StringEncoding, null)
    }
}