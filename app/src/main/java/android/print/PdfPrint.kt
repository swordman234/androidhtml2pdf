package android.print

import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File

class PdfPrint(private val printAttributes: PrintAttributes) {

    fun print(printAdapter: PrintDocumentAdapter, path: File, fileName: String) {
        printAdapter.onLayout(null, printAttributes, null, object : PrintDocumentAdapter.LayoutResultCallback() {
            override fun onLayoutFinished(info: PrintDocumentInfo?, changed: Boolean) {
                printAdapter.onWrite(
                    arrayOf(PageRange.ALL_PAGES),
                    getOutputFile(path, fileName),
                    CancellationSignal(),
                    object : PrintDocumentAdapter.WriteResultCallback() {
                        override fun onWriteFinished(pages: Array<out PageRange>?) {
                            super.onWriteFinished(pages)
                        }
                    }
                )
            }
        }, null)
    }

    private fun getOutputFile(path: File, fileName: String): ParcelFileDescriptor? {
        if (!path.exists()) {
            path.mkdirs()
        }
        val file = File(path, fileName)
        return try {
            file.createNewFile()
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e)
            null
        }
    }

    companion object {
        private val TAG = PdfPrint::class.java.simpleName
    }
}
