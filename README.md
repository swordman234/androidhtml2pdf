# Android HTML to PDF Maker

This repository contains an Android application that generates PDF documents from HTML content and
allows users to save them to a selected folder. It includes features to generate sales report PDFs
with charts using Chart.js directly in your Android app.

## Features

- Generate daily sales report with summary, top products, payment types, staff performance, and
  profit/loss breakdown
- Visualization of weekly and monthly sales data via embedded charts
- Save generated PDF to custom user-selected location (using Android Folder Picker)
- Video demonstration example
- PDF result example

## Demo

### PDF Example

[example file.pdf](https://github.com/user-attachments/files/21812329/example.file.pdf)

### Video Example

https://github.com/user-attachments/assets/365d54ec-5625-4f34-b2a9-b716925d16a7

## Getting Started


### Prerequisites

- Android Studio
- Android device or emulator (API 21+ recommended)
- Internet permission for loading Chart.js CDN (can be disabled for offline chart script)

### Building & Running

1. Clone this repository:
   ```sh
   git clone <https://github.com/swordman234/androidhtml2pdf.git>
   ```
2. Open with Android Studio
3. Build and run the project on your device or emulator

### Usage

1. Open the app
2. Tap the "Create PDF" button
3. Select your desired folder for PDF export
4. PDF will be generated based on dummy data and chart and saved in the selected location

## Folder Structure

```
app/
└── src/
    └── main/
        └── java/
            └──com/arya/mypdfmaker/
                └── MainActivity.kt
            └──android/print/
                └── PdfPrint.kt
```

## Main Components

- `MainActivity.kt`: Generates HTML, renders in WebView, and prints to PDF using system PDF print
- `PdfPrint.kt`: Utility for sending WebView content to PDF using Android Print framework
- Data classes: Product, PaymentType, Staff, SalesItem, SalesByWeek, SalesByMonth
- Chart rendering: [Chart.js](https://www.chartjs.org/)

## Permissions

App requests read/write permissions for saving PDFs to user-accessible folders.

## Customizing the Report

Modify the `gettingDataHtml()` method in `MainActivity.kt` to change data for PDF output.

## Contribution

Feel free to submit issues or fork this repo for your business reporting needs.

## License

_This project is provided for educational purposes. You may adapt for commercial or personal use._
