package com.arya.mypdfmaker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PdfConverter
import android.print.PdfPrint
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.provider.Settings
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.arya.mypdfmaker.databinding.ActivityMainBinding
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var webView : WebView
    private lateinit var binding: ActivityMainBinding

    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    fun exportAsPdf(context: Context) {
        val innerWebView = WebView(context)
        innerWebView.webViewClient = WebViewClient()
//        innerWebView.settings.javaScriptEnabled = true
        innerWebView.loadDataWithBaseURL(null, getHtmlFromFile(), "text/html", "UTF-8", null)

//        innerWebView.loadUrl("https://ocean.hellobill.co.id/webview-v2/report/microbiz-daily/673ff65e2c3b2")

        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = innerWebView.createPrintDocumentAdapter("sample from html.pdf")
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf",600,600))
            .setMinMargins(PrintAttributes.Margins(16,16,16,16))
            .build()
        printManager.print(
            "Test PDF",
            printAdapter,
            printAttributes)

//        try {
//            val jobName = "PDF Maker Document"
//            val attributes = PrintAttributes.Builder()
//                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
//                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()
//            val path =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//
//            val pdfPrint = PdfPrint(attributes)
//            pdfPrint.print(
//                printAdapter,
//                path,
//                "PDF Maker Document.pdf"
//            )
//            Log.i("pdf", "pdf created")
//        } catch (e: Exception) {
//            Log.e("pdf", " pdf failed ${e.localizedMessage}")
//        }
    }

    private fun setupPermissions() {
        if (!checkPermission(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 10)
        }
    }
    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (ActivityCompat.checkSelfPermission(this, permissionArray[i]) != PackageManager.PERMISSION_GRANTED){
                allSuccess = false
            }
        }
        return allSuccess
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.main)

        setupPermissions()

        webView = binding.webview

        binding.btnCreatePdf.setOnClickListener {
            exportAsPdf(this)
        }



//        PDFBoxResourceLoader.init(applicationContext)

//        binding.btnCreatePdf.setOnClickListener {
//            val invoiceGenerator = InvoiceGenerator(this)
//            invoiceGenerator.checkPermissionAndGenerate()
//        }

//        binding.btnCreatePdf.setOnClickListener {
//            val converter = PdfConverter.getInstance()
//            val htmlContent = getHTMLInvoice()
//            val filePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/sample from html.pdf"
//
//            val printAttributes = PrintAttributes.Builder()
//            printAttributes.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//            printAttributes.setResolution(PrintAttributes.Resolution("pdf", "PDF", 300, 300))
//            printAttributes.setMinMargins(PrintAttributes.Margins.NO_MARGINS)
//
//            converter.pdfPrintAttrs = printAttributes.build()
//            converter.convert(this, htmlContent, File(filePath))
//
//            val webView = WebView(this)
//            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
//
//            webView.post {
//                val printManager = this.getSystemService(Context.PRINT_SERVICE) as PrintManager
//                val printAdapter = webView.createPrintDocumentAdapter(filePath)
//
//                val printJob = printManager.print(filePath, printAdapter, PrintAttributes.Builder().build())
//
//                if (printJob.isCompleted) {
//                    Toast.makeText(this, "PDF saved successfully!", Toast.LENGTH_SHORT).show()
//                } else if (printJob.isFailed) {
//                    Toast.makeText(this, "Failed to save PDF.", Toast.LENGTH_SHORT).show()
//                }
//            }

//        }

    }

    private fun getHtmlFromFile(): String {
       return """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Laporan Harian</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            padding: 20px;
            border: 1px solid #ddd;
        }
        .header {
            text-align: center;
            margin-bottom: 20px;
        }
        .header h1 {
            margin: 0;
        }
        .summary, .profit {
            display: flex;
            justify-content: space-around;
            margin-bottom: 20px;
        }
        .summary div, .profit div {
            border: 1px solid #ddd;
            padding: 15px;
            text-align: center;
            width: 30%;
            background-color: #f9f9f9;
        }
        .table-container {
            width: 100%;
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #f4f4f4;
        }
        .chart-container {
            width: 100%;
            text-align: center;
            margin-bottom: 20px;
        }

        .profit-container {
            border: 1px solid #ddd;
            padding: 20px;
            border-radius: 10px;
            display: flex;
            flex-direction: column;
            max-width: 100%;
            background-color: #fff;
        }

        .profit-summary {
            font-weight: bold;
            font-size: 18px;
        }

        .profit-summary h2 {
            margin: 5px 0;
            font-size: 28px;
            font-weight: bold;
            color: #333;
        }

        .profit-details {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 10px;
            margin-top: 15px;
        }

        .profit-details div {
            background: #f8f8f8;
            padding: 10px;
            border-radius: 5px;
        }

        .profit-details p {
            margin: 0;
            font-size: 14px;
            color: #555;
        }

        .profit-details h3 {
            margin: 5px 0;
            font-size: 16px;
            font-weight: bold;
        }

        .sales-container {
            border: 1px solid #ddd;
            padding: 20px;
            border-radius: 10px;
            max-width: 600px;
            background-color: #fff;
            margin-top: 20px;
        }
    </style>

</head>
<body>

<div class="header">
    <h1>LAPORAN HARIAN</h1>
    <p><strong>Branch ID:</strong> 0123456789</p>
    <p><strong>Branch Name:</strong> Toko Bunga Matahari</p>
    <p><strong>Date:</strong> Thursday, 16 January 2025</p>
</div>

<div class="summary">
    <div>
        <p><strong>Penjualan Kotor</strong></p>
        <h2>Rp35.000.000</h2>
        <p>+10% dari hari sebelumnya</p>
    </div>
    <div>
        <p><strong>Penjualan Bersih</strong></p>
        <h2>Rp30.000.000</h2>
        <p>+10% dari hari sebelumnya</p>
    </div>
    <div>
        <p><strong>Total Pesanan</strong></p>
        <h2>5.500</h2>
        <p>+10% dari hari sebelumnya</p>
    </div>
</div>

<h2>10 Produk Terbaik</h2>
<table>
    <tr>
        <th>Produk</th>
        <th>Jumlah Pesanan</th>
        <th>Penjualan Kotor</th>
    </tr>
    <tr><td>Baju Merah</td><td>42</td><td>Rp4.247.602</td></tr>
    <tr><td>Baju Putih</td><td>30</td><td>Rp3.047.602</td></tr>
    <tr><td>Baju Hitam</td><td>29</td><td>Rp2.947.602</td></tr>
    <tr><td>Baju Kuning</td><td>28</td><td>Rp2.847.602</td></tr>
    <tr><td>Baju Hijau</td><td>27</td><td>Rp2.747.602</td></tr>
    <tr><td>Baju Ungu</td><td>26</td><td>Rp2.647.602</td></tr>
    <tr><td>Baju Coklat</td><td>25</td><td>Rp2.547.602</td></tr>
    <tr><td>Baju Oranye</td><td>24</td><td>Rp2.447.602</td></tr>
    <tr><td>Baju Biru</td><td>23</td><td>Rp2.347.602</td></tr>
    <tr><td>Baju Jingga</td><td>22</td><td>Rp2.247.602</td></tr>
</table>

<h2>Tipe Pembayaran</h2>
<table>
    <tr>
        <th>Tipe Pembayaran</th>
        <th>Jumlah Transaksi</th>
        <th>Total Nilai</th>
    </tr>
    <tr><td>Cash</td><td>150</td><td>Rp2.200.000</td></tr>
    <tr><td>Credit Card</td><td>67</td><td>Rp2.200.000</td></tr>
    <tr><td>OVO</td><td>56</td><td>Rp2.200.000</td></tr>
    <tr><td>Mimin</td><td>17</td><td>Rp2.200.000</td></tr>
    <tr><td>Voucher Bali Tourism</td><td>9</td><td>Rp2.200.000</td></tr>
    <tr><td>DSP Debit</td><td>90</td><td>Rp2.200.000</td></tr>
</table>

<h2>Kinerja Staff</h2>
<table>
    <tr>
        <th>Nama</th>
        <th>Jumlah Transaksi</th>
        <th>Total Nilai</th>
    </tr>
    <tr><td>Budi</td><td>150</td><td>Rp2.247.602</td></tr>
    <tr><td>Herman</td><td>67</td><td>Rp2.347.602</td></tr>
    <tr><td>Lala</td><td>17</td><td>Rp2.447.602</td></tr>
    <tr><td>Joko</td><td>96</td><td>Rp2.547.602</td></tr>
</table>

<h2>Laba Rugi</h2>
<div class="profit-container">
    <div class="profit-summary">
        <p>Profit</p>
        <h2>Rp4.500.000</h2>
    </div>
    <div class="profit-details">
        <div><p>Total Barang Terjual</p><h3>Rp5.000.000</h3></div>
        <div><p>Subtotal</p><h3>Rp5.000.000</h3></div>
        <div><p>Total Diskon Bill</p><h3>Rp5.000</h3></div>
        <div><p>Biaya Layanan</p><h3>Rp5.000</h3></div>
        <div><p>Pajak</p><h3>Rp5.000</h3></div>
        <div><p>Penjualan Kotor</p><h3>Rp5.005.000</h3></div>
        <div><p>Penjualan Bersih</p><h3>Rp5.000.000</h3></div>
        <div><p>Total COGS</p><h3>Rp5.000</h3></div>
    </div>
</div>

<h2>Gross and Net Sales</h2>
<div class="sales-container">
    <canvas id="weeklySalesChart"></canvas>
    <canvas id="monthlySalesChart"></canvas>
</div>

<!-- Include Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
    // Data for Weekly Sales Chart
    const weeklySalesData = {
        labels: ["08 Dec - 14 Dec", "15 Dec - 21 Dec", "22 Dec - 28 Dec", "29 Dec - 04 Jan"],
        datasets: [
            {
                label: "Gross Sales",
                data: [28700000, 0, 0, 0],
                backgroundColor: "rgba(72, 180, 127, 0.6)"
            },
            {
                label: "Net Sales",
                data: [26100000, 0, 0, 0],
                backgroundColor: "rgba(144, 238, 144, 0.6)"
            }
        ]
    };

    // Data for Monthly Sales Chart
    const monthlySalesData = {
        labels: ["October", "November", "December", "January"],
        datasets: [
            {
                label: "Gross Sales",
                data: [0, 41600000, 48500000, 0],
                backgroundColor: "rgba(72, 180, 127, 0.6)"
            },
            {
                label: "Net Sales",
                data: [0, 38100000, 44000000, 0],
                backgroundColor: "rgba(144, 238, 144, 0.6)"
            }
        ]
    };

    // Render Weekly Sales Chart
    new Chart(document.getElementById("weeklySalesChart"), {
        type: "bar",
        data: weeklySalesData,
        options: {
            responsive: true,
            plugins: {
                legend: { position: "top" }
            }
        }
    });

    // Render Monthly Sales Chart
    new Chart(document.getElementById("monthlySalesChart"), {
        type: "bar",
        data: monthlySalesData,
        options: {
            responsive: true,
            plugins: {
                legend: { position: "top" }
            }
        }
    });
</script>

<h2>Total Order</h2>
<div class="sales-container">
    <canvas id="monthlyOrderChart"></canvas>
    <canvas id="weeklyOrderChart"></canvas>
</div>

<script>
    // Data for Monthly Order Chart
    const monthlyOrderData = {
        labels: ["October", "November", "December", "January"],
        datasets: [
            {
                label: "Total Order",
                data: [0, 115, 184, 0],
                backgroundColor: "rgba(144, 238, 144, 0.6)"
            }
        ]
    };

    // Data for Weekly Order Chart
    const weeklyOrderData = {
        labels: ["08 Dec - 14 Dec", "15 Dec - 21 Dec", "22 Dec - 28 Dec", "29 Dec - 04 Jan"],
        datasets: [
            {
                label: "Total Order",
                data: [81, 0, 0, 0],
                backgroundColor: "rgba(144, 238, 144, 0.6)"
            }
        ]
    };

    // Render Monthly Order Chart
    new Chart(document.getElementById("monthlyOrderChart"), {
        type: "bar",
        data: monthlyOrderData,
        options: {
            responsive: true,
            plugins: {
                legend: { position: "top" }
            }
        }
    });

    // Render Weekly Order Chart
    new Chart(document.getElementById("weeklyOrderChart"), {
        type: "bar",
        data: weeklyOrderData,
        options: {
            responsive: true,
            plugins: {
                legend: { position: "top" }
            }
        }
    });
</script>

</body>
</html>
""".trimIndent()
    }

    private fun getHTMLInvoice(): String {
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


    private fun createHTML(context: Context) {
        val htmlContent = "<html><body><h1>Hello, PDF!</h1></body></html>"
        val filePath =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/sample from html.pdf"

        generatePdfFromHtml(context, htmlContent, filePath)
    }

    private fun generatePdfFromHtml(context: Context, htmlContent: String, filePath: String) {
        val webView = WebView(context)
        webView.webViewClient = WebViewClient()
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        webView.post {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter = webView.createPrintDocumentAdapter(filePath)

            val printJob =
                printManager.print(filePath, printAdapter, PrintAttributes.Builder().build())

            if (printJob.isCompleted) {
                Toast.makeText(context, "PDF saved successfully!", Toast.LENGTH_SHORT).show()
            } else if (printJob.isFailed) {
                Toast.makeText(context, "Failed to save PDF.", Toast.LENGTH_SHORT).show()
            }
        }

    }

//    private fun createPdfWithPdfBox(){
//        val document = PDDocument()
//        val page = PDPage(PDRectangle.A4)
//        document.addPage(page)
//
//        val font = PDType1Font.HELVETICA
//
//        lateinit var contentStream: PDPageContentStream
//
//        try {
//            // Define a content stream for adding to the PDF
//            contentStream = PDPageContentStream(document, page)
//
//            // Write Hello World in blue text
//            contentStream.beginText()
//            contentStream.setNonStrokingColor(15, 38, 192)
//            contentStream.setFont(font, 12f)
//            contentStream.newLineAtOffset(100f, 700f)
//            contentStream.showText("Hello World")
//            contentStream.endText()
//
//            // Load in the images
//            val `in`: InputStream = assets.open("falcon.jpg")
//            val alpha: InputStream = assets.open("trans.png")
//
//            // Draw a green rectangle
//            contentStream.addRect(5f, 500f, 100f, 100f)
//            contentStream.setNonStrokingColor(0, 255, 125)
//            contentStream.fill()
//
//            // Draw the falcon base image
//            val ximage = JPEGFactory.createFromStream(document, `in`)
//            contentStream.drawImage(ximage, 20f, 20f)
//
//            // Draw the red overlay image
//            val alphaImage = BitmapFactory.decodeStream(alpha)
//            val alphaXimage = LosslessFactory.createFromImage(document, alphaImage)
//            contentStream.drawImage(alphaXimage, 20f, 20f)
//
//            // Make sure that the content stream is closed:
//            contentStream.close()
//
//            // Save the final pdf document to a file
////            val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/sample.pdf"
////            document.save(path)
////            document.close()
////            Toast.makeText(this, "Successfully wrote PDF to $path", Toast.LENGTH_SHORT).show()
//        } catch (e: IOException) {
//            Log.e("PdfBox-Android-Sample", "Exception thrown while creating PDF", e)
//        }
//
//        val page2 = PDPage(PDRectangle.A4)
//        document.addPage(page2)
//
//        try {
//            // Define a content stream for adding to the PDF
//            contentStream = PDPageContentStream(document, page2)
//
//            // Write Hello World in blue text
//            contentStream.beginText()
//            contentStream.setNonStrokingColor(15, 38, 192)
//            contentStream.setFont(font, 12f)
//            contentStream.newLineAtOffset(100f, 700f)
//            contentStream.showText("Hello World")
//            contentStream.endText()
//
//            // Load in the images
//            val `in`: InputStream = assets.open("falcon.jpg")
//            val alpha: InputStream = assets.open("trans.png")
//
//            // Draw a green rectangle
//            contentStream.addRect(5f, 500f, 100f, 100f)
//            contentStream.setNonStrokingColor(0, 255, 125)
//            contentStream.fill()
//
//            // Draw the falcon base image
//            val ximage = JPEGFactory.createFromStream(document, `in`)
//            contentStream.drawImage(ximage, 20f, 20f)
//
//            // Draw the red overlay image
//            val alphaImage = BitmapFactory.decodeStream(alpha)
//            val alphaXimage = LosslessFactory.createFromImage(document, alphaImage)
//            contentStream.drawImage(alphaXimage, 20f, 20f)
//
//            // Make sure that the content stream is closed:
//            contentStream.close()
//
//            // Save the final pdf document to a file
//            val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/sample.pdf"
//            document.save(path)
//            document.close()
//            Toast.makeText(this, "Successfully wrote PDF to $path", Toast.LENGTH_SHORT).show()
//
//        }catch (e: IOException) {
//            Log.e("PdfBox-Android-Sample", "Exception thrown while creating PDF", e)
//        }
//    }

//    private fun createPdf() {
//        val pdfDocument = PdfDocument()
//        val paint = Paint()
//
//        // Create an A4 size page (595x842 px)
//        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
//        val page = pdfDocument.startPage(pageInfo)
//        val canvas = page.canvas
//
//        // Draw text on the PDF
//        paint.textSize = 16f
//        canvas.drawText("Hello, this is a sample PDF!", 50f, 50f, paint)
//        canvas.drawText("Hello, this is a sample PDF!", 50f, 800f, paint)
//
//        // Finish the page
//        pdfDocument.finishPage(page)
//
//        // Create an A4 size page (595x842 px)
//        val pageInfo2 = PdfDocument.PageInfo.Builder(595, 842, 2).create()
//        val page2 = pdfDocument.startPage(pageInfo2)
//        val canvas2 = page2.canvas
//
//        // Draw text on the PDF
//        paint.textSize = 16f
//        canvas2.drawText("Hello, this is a sample PDF!", 50f, 50f, paint)
//        canvas2.drawText("Hello, this is a sample PDF!", 50f, 800f, paint)
//
//        // Finish the page
//        pdfDocument.finishPage(page2)
//
//        // Save PDF to app's private storage
//        val filePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/sample.pdf"
//        val file = File(filePath)
//
//        try {
//            val fos = FileOutputStream(file)
//            pdfDocument.writeTo(fos)
//            pdfDocument.close()
//            fos.close()
//            println("PDF saved to: ${file.absolutePath}")
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }

//    private fun makePDFDailyReport(){
//        val filePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/sample_itext.pdf"
//        val file = File(filePath)
//        val writer = PdfWriter(file)
//        val pdf = PdfDocument(writer)
//        val document = Document(pdf)
//
//// Create a PdfFont
//        val font: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
//
//// Add a Paragraph
//        document.add(Paragraph("iText is:").setFont(font))
//
//// Create a List
//        val list = com.itextpdf.layout.element.List()
//            .setSymbolIndent(12f)
//            .setListSymbol("\u2022") // Bullet point
//            .setFont(font) // Set font
//
//// Add ListItem objects
//        list.add(ListItem("Never gonna give you up"))
//            .add(ListItem("Never gonna let you down"))
//            .add(ListItem("Never gonna run around and desert you"))
//            .add(ListItem("Never gonna make you cry"))
//            .add(ListItem("Never gonna say goodbye"))
//            .add(ListItem("Never gonna tell a lie and hurt you"))
//            .add(ListItem("Never gonna give you up"))
//            .add(ListItem("Never gonna let you down"))
//            .add(ListItem("Never gonna run around and desert you"))
//            .add(ListItem("Never gonna make you cry"))
//            .add(ListItem("Never gonna say goodbye"))
//            .add(ListItem("Never gonna tell a lie and hurt you"))
//            .add(ListItem("Never gonna give you up"))
//            .add(ListItem("Never gonna let you down"))
//            .add(ListItem("Never gonna run around and desert you"))
//            .add(ListItem("Never gonna make you cry"))
//            .add(ListItem("Never gonna say goodbye"))
//            .add(ListItem("Never gonna tell a lie and hurt you"))
//            .add(ListItem("Never gonna give you up"))
//            .add(ListItem("Never gonna let you down"))
//            .add(ListItem("Never gonna run around and desert you"))
//            .add(ListItem("Never gonna make you cry"))
//            .add(ListItem("Never gonna say goodbye"))
//            .add(ListItem("Never gonna tell a lie and hurt you"))
//            .add(ListItem("Never gonna give you up"))
//            .add(ListItem("Never gonna let you down"))
//            .add(ListItem("Never gonna run around and desert you"))
//            .add(ListItem("Never gonna make you cry"))
//            .add(ListItem("Never gonna say goodbye"))
//            .add(ListItem("Never gonna tell a lie and hurt you"))
//            .add(ListItem("Never gonna give you up"))
//            .add(ListItem("Never gonna let you down"))
//            .add(ListItem("Never gonna run around and desert you"))
//            .add(ListItem("Never gonna make you cry"))
//            .add(ListItem("Never gonna say goodbye"))
//            .add(ListItem("Never gonna tell a lie and hurt you"))
//            .add(ListItem("Never gonna give you up"))
//            .add(ListItem("Never gonna let you down"))
//            .add(ListItem("Never gonna run around and desert you"))
//            .add(ListItem("Never gonna make you cry"))
//            .add(ListItem("Never gonna say goodbye"))
//            .add(ListItem("Never gonna tell a lie and hurt you"))
//
//// Add the list
//        document.add(list)
//        document.close()
//    }
}
