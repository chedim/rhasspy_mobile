package org.rhasspy.mobile.logic.nativeutils

actual class FileWriterText actual constructor(filename: String, maxFileSize: Long?) : FileWriter(filename) {

    actual val maxFileSize: Long?
        get() = null //TODO("Not yet implemented")

    /**
     * create file and return if it was successfully
     */
    actual fun createFile(): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    /**
     * read all file contents
     */
    actual inline fun <reified T> decodeFromFile(): T {
        TODO("Not yet implemented")
    }

    /**
     * append text to the file if not bigger than max file size
     * if max file size is reached this file will be copied to filename_old
     * and a new file is created
     */
    actual fun appendText(element: String) {
        //TODO("Not yet implemented")
    }

    /**
     * open share file system dialog
     */
    actual fun shareFile() {
        //TODO("Not yet implemented")
    }

    /**
     * copy file to specific new file
     */
    actual fun copyFile(fileName: String, fileType: String) {
        //TODO("Not yet implemented")
    }

}