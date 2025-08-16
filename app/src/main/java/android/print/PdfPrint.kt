package android.print

import android.os.ParcelFileDescriptor

class PdfPrint(private val printAttributes: PrintAttributes) {

    fun printToSaf(adapter: PrintDocumentAdapter, fileDescriptor: ParcelFileDescriptor, onFinish: () -> Unit) {
        adapter.onLayout(null, printAttributes, null, object : PrintDocumentAdapter.LayoutResultCallback() {
            override fun onLayoutFinished(info: PrintDocumentInfo?, changed: Boolean) {
                adapter.onWrite(
                    arrayOf(PageRange.ALL_PAGES),
                    fileDescriptor,
                    null,
                    object : PrintDocumentAdapter.WriteResultCallback() {
                        override fun onWriteFinished(pages: Array<PageRange>) {
                            onFinish()
                        }
                    }
                )
            }
        }, null)
    }
}
