package com.arya.mypdfmaker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.print.PdfPrint
import android.print.PrintAttributes
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.arya.mypdfmaker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var webView : WebView
    private lateinit var binding: ActivityMainBinding

    private var folderPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val folderUri = result.data?.data ?: return@registerForActivityResult

            // Persist permission
            contentResolver.takePersistableUriPermission(
                folderUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            // Call your PDF export
            exportPdf(this, folderUri)
        }
    }

    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    fun openFolderPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        }
        folderPickerLauncher.launch(intent)
    }

    fun exportPdf(context: Context, folderUri: Uri) {
        val innerWebView = WebView(context)
        innerWebView.settings.javaScriptEnabled = true

        innerWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                try {
                    val attributes = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build()

                    val pdfPrint = PdfPrint(attributes)

                    // Create the file in the selected folder
                    val folder = DocumentFile.fromTreeUri(context, folderUri)
                    val pdfFile = folder?.createFile("application/pdf", "BAJIGUR.pdf")

                    if (pdfFile != null) {
                        val pfd = context.contentResolver.openFileDescriptor(pdfFile.uri, "w")
                        pdfPrint.printToSaf(innerWebView.createPrintDocumentAdapter("auto-making-pdf"), pfd!!) {
                            Toast.makeText(context, "PDF saved successfully", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Log.e("PDF", "Error: ${e.message}", e)
                    Toast.makeText(context, "PDF failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }

                innerWebView.settings.javaScriptEnabled = false
            }
        }

        val headerHTML = headerHTML("123", "Arya Suka", "12 April 2020")
        val summaryHTML = summaryHTML("12.000", "12", "11.000", "50", "1", "100")
        val top10productList = ArrayList<Product>()
        for (i in 1..10){
            top10productList.add(Product("Barang $i", "${i+i}", "10.100.100"))
        }
        val top10product = top10ProductHTML(top10productList)
        val paymentTypeList = ArrayList<PaymentType>()
        for (i in 1..5){
            paymentTypeList.add(PaymentType("EDC $i", "${i+5}", "12.000.000"))
        }
        val paymentType = paymentTypeHTML(paymentTypeList)
        val staffList = ArrayList<Staff>()
        for (i in 1..3){
            staffList.add(Staff("Orang Ke-$i", "${i+3}", "1.000"))
        }
        val staff = staffPerformanceHTML(staffList)
        val profitLossHTML = profitLossHTML("12.000", "30", "1.000", "50.000.000", "10.000.000", "1.000", "1.001", "1", "1000")

        val dataHTML = settingHTML() + headerHTML + summaryHTML + top10product + paymentType + staff + profitLossHTML + chartHTML()
        innerWebView.loadDataWithBaseURL(null, dataHTML, "text/html", "UTF-8", null)
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

//        webView = binding.webview
//        webView.webViewClient = WebViewClient()
//        webView.settings.javaScriptEnabled = true
//        webView.loadDataWithBaseURL(null, getHtmlFromFile(), "text/html", "UTF-8", null)

        binding.btnCreatePdf.setOnClickListener {
            openFolderPicker()
        }

    }

    //style setting, script for chart, and first body in here
    private fun settingHTML():String{
        return """
            <!DOCTYPE html>
            <html lang="en">
            
            <!-- Include Chart.js -->
            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            
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
                        width: 600px;
                        background-color: #fff;
                        margin-top: 20px;
                    }
                </style>
            
            </head>
            <body>
        """.trimIndent()
    }

    private fun headerHTML(branchID : String, branchName : String, date : String):String{
        return """
            <div class="header">
                <h1>LAPORAN HARIAN</h1>
                <p><strong>Branch ID:</strong> $branchID</p>
                <p><strong>Branch Name:</strong> $branchName</p>
                <p><strong>Date:</strong> $date</p>
            </div>
        """.trimIndent()
    }

    private fun summaryHTML(gross : String, grossPercent : String, net : String, netPercent : String, sumOrder : String, sumOrderPercent : String):String{
        return """
            <div class="summary">
                <div>
                    <p><strong>Penjualan Kotor</strong></p>
                    <h2>Rp$gross</h2>
                    <p>$grossPercent% dari hari sebelumnya</p>
                </div>
                <div>
                    <p><strong>Penjualan Bersih</strong></p>
                    <h2>Rp$net</h2>
                    <p>$netPercent% dari hari sebelumnya</p>
                </div>
                <div>
                    <p><strong>Total Pesanan</strong></p>
                    <h2>$sumOrder</h2>
                    <p>$sumOrderPercent% dari hari sebelumnya</p>
                </div>
            </div>
        """.trimIndent()
    }

    private fun top10ProductHTML(top10product : ArrayList<Product>):String{
        val top10productHTML = StringBuilder()
        top10product.forEach{
            top10productHTML.append("<tr><td>${it.name}</td><td>${it.totalOrder}</td><td>Rp${it.gross}</td></tr>")
        }
        return """
            <h2>10 Produk Terbaik</h2>
            <table>
                <tr>
                    <th>Produk</th>
                    <th>Jumlah Pesanan</th>
                    <th>Penjualan Kotor</th>
                </tr>
                $top10productHTML
            </table>
        """.trimIndent()
    }

    private fun paymentTypeHTML(paymentTypeList : ArrayList<PaymentType>):String{
        val paymentTypeHTML = StringBuilder()
        paymentTypeList.forEach{
            paymentTypeHTML.append("<tr><td>${it.name}</td><td>${it.totalTransaction}</td><td>Rp${it.summary}</td></tr>")
        }
        return """
            <h2>Tipe Pembayaran</h2>
            <table>
                <tr>
                    <th>Tipe Pembayaran</th>
                    <th>Jumlah Transaksi</th>
                    <th>Total Nilai</th>
                </tr>
                $paymentTypeHTML
            </table>
        """.trimIndent()
    }

    private fun staffPerformanceHTML(staff : ArrayList<Staff>):String{
        val staffPerformanceHTML = StringBuilder()
        staff.forEach{
            staffPerformanceHTML.append("<tr><td>${it.name}</td><td>${it.totalTransaction}</td><td>Rp${it.summary}</td></tr>")
        }
        return """
            <h2>Kinerja Staff</h2>
            <table>
                <tr>
                    <th>Nama</th>
                    <th>Jumlah Transaksi</th>
                    <th>Total Nilai</th>
                </tr>
                $staffPerformanceHTML
            </table>
        """.trimIndent()
    }

    private fun profitLossHTML(profit : String, salesTotal : String, subtotal : String, discountTotal : String, serviceCharge : String, tax : String, gross: String, net: String, cogs : String):String{
        return """
            <h2>Laba Rugi</h2>
            <div class="profit-container">
                <div class="profit-summary">
                    <p>Profit</p>
                    <h2>Rp$profit</h2>
                </div>
                <div class="profit-details">
                    <div><p>Total Barang Terjual</p><h3>Rp$salesTotal</h3></div>
                    <div><p>Subtotal</p><h3>Rp$subtotal</h3></div>
                    <div><p>Total Diskon Bill</p><h3>Rp$discountTotal</h3></div>
                    <div><p>Biaya Layanan</p><h3>Rp$serviceCharge</h3></div>
                    <div><p>Pajak</p><h3>Rp$tax</h3></div>
                    <div><p>Penjualan Kotor</p><h3>Rp$gross</h3></div>
                    <div><p>Penjualan Bersih</p><h3>Rp$net</h3></div>
                    <div><p>Total COGS</p><h3>Rp$cogs</h3></div>
                </div>
            </div>
        """.trimIndent()
    }

    private fun chartHTML():String{
        return """
            <h2>Gross and Net Sales</h2>
            <div class="sales-container">
                <canvas id="weeklySalesChart"></canvas>
                <canvas id="monthlySalesChart"></canvas>
            </div>

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

}

data class Product(
    val name : String,
    val totalOrder : String,
    val gross : String
)

data class PaymentType(
    val name: String,
    val totalTransaction : String,
    val summary : String
)

data class Staff(
    val name: String,
    val totalTransaction: String,
    val summary: String
)
