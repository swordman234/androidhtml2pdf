package com.arya.mypdfmaker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class InvoiceGenerator(private val context: Activity) {

    private lateinit var webView: WebView
    private val fileName = "Invoice_${System.currentTimeMillis()}.pdf"

    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 1001
    }

    fun checkPermissionAndGenerate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { // Android 10 and below need permission
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )
                return
            }
        }
        generateInvoice()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun generateInvoice() {
        webView = WebView(context)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                savePdf()
            }
        }

        val htmlContent = getInvoiceHtml()
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    private fun getInvoiceHtml(): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Invoice</title>
                <style>
                    body { font-family: Arial, sans-serif; padding: 20px; }
                    .invoice-header { text-align: center; margin-bottom: 20px; }
                    .invoice-header img { width: 150px; margin-bottom: 10px; }
                    .invoice-details, .items { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                    .items th, .items td { border: 1px solid #ddd; padding: 10px; text-align: left; }
                    .items th { background-color: #f4f4f4; }
                    .total { text-align: right; font-size: 18px; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="invoice-header">
                    <img src="https://yourcompany.com/logo.png" alt="Company Logo"/>
                    <h1>Invoice</h1>
                    <p><strong>Date:</strong> 2025-02-19</p>
                </div>
                <table class="invoice-details">
                    <tr>
                        <td><strong>Bill To:</strong></td>
                        <td><strong>Invoice #:</strong> 123456</td>
                    </tr>
                    <tr>
                        <td>John Doe</td>
                        <td><strong>Due Date:</strong> 2025-02-26</td>
                    </tr>
                </table>
                <table class="items">
                    <tr>
                        <th>Item</th>
                        <th>Quantity</th>
                        <th>Unit Price</th>
                        <th>Total</th>
                    </tr>
                    <tr>
                        <td>Product 1</td>
                        <td>2</td>
                        <td>$50</td>
                        <td>$100</td>
                    </tr>
                    <tr>
                        <td>Product 2</td>
                        <td>1</td>
                        <td>$30</td>
                        <td>$30</td>
                    </tr>
                    <tr>
                        <td colspan="3" class="total">Subtotal:</td>
                        <td>$130</td>
                    </tr>
                    <tr>
                        <td colspan="3" class="total">Tax (10%):</td>
                        <td>$13</td>
                    </tr>
                    <tr>
                        <td colspan="3" class="total">Grand Total:</td>
                        <td>$143</td>
                    </tr>
                </table>
                <p><strong>Payment Instructions:</strong> Please send payment to XYZ Bank, Account #123456789</p>
            </body>
            </html>
        """.trimIndent()
    }


    private fun savePdf() {
        webView.postDelayed({
            webView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            webView.layout(0, 0, webView.measuredWidth, webView.measuredHeight)

            val pageWidth = if (webView.width > 0) webView.width else 595
            val pageHeight = if (webView.height > 0) webView.height else 842

            val pdfDocument = PrintedPdfDocument(
                context, PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("pdf", "PDF", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
            )

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            webView.draw(page.canvas)
            pdfDocument.finishPage(page)

            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            try {
                FileOutputStream(file).use { pdfDocument.writeTo(it) }
                showToast("PDF Saved: ${file.absolutePath}")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to save PDF")
            } finally {
                pdfDocument.close()
            }
        }, 500) // Delay to allow rendering
    }


    private fun sharePdf(file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Invoice PDF"))
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
    }
}
