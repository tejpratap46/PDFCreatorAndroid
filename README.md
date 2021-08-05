# PDFCreatorAndroid
Simple library to generate and view PDF in Android

***
[![](https://jitpack.io/v/tejpratap46/PDFCreatorAndroid.svg)](https://jitpack.io/#tejpratap46/PDFCreatorAndroid) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-PDFCreatorAndroid-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/8182)
***

![Cover](https://github.com/tejpratap46/PDFCreatorAndroid/raw/master/cover1.png)

A simple library to create and view PDF with zero dependency Or native code.

Add it in your root build.gradle at the end of repositories:
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency

```gradle
	dependencies {
	        implementation 'com.github.tejpratap46:PDFCreatorAndroid:3.0.2'
	}
```

## Try It
[![Download From Play Store](https://play.google.com/intl/en_us/badges/images/badge_new.png)](https://play.google.com/store/apps/details?id=com.tejpratapsingh.pdfcreatorandroid)

## Concept
* Android has capability to print documents to PDF, this library simplifies those API's to generate PDF easily.
* At basic level, API renders Views to PDF. To create A PDF with pages we need to submit views exactly height of one page, any view larges then that that will be trimmed.
* This library creates pages by adding views to a parent view unitil the next view is about to exceed current page. If next view exceeds current page, that view will be added to new page.

## Implementation
* First thing first, Look at [PdfCreatorExampleActivity](https://github.com/tejpratap46/PDFCreatorAndroid/blob/master/app/src/main/java/com/tejpratapsingh/pdfcreatorandroid/PdfCreatorExampleActivity.java) of app.

1. PDF creater uses views which can be rendered, So we need to exted an activity in order to create activity.
2. Create a Empty `Activity` without any layout and extend it with `PDFCreatorActivity`. Do not set use `setContentView(int resourceId)` inside your created activity.
3. There are 3 abstract methods you have to override.
    1. `getHeaderView()`
        * This will be header for PDF and will be added to each page. (Accepts PDFHeaderView)
    2. `getBodyViews()`
        * This will return a PDFBody which consist of list of views which can be broken between pages.
    3. `getFooterView()`
        * This will be footer for PDF and will be added to each page. (Accepts PDFFooterView)
    3. `getWatermarkView()`
        * [OPTIONAL] This add a watermark image to each page. (Accepts PDFImageView), see [issue #14](https://github.com/tejpratap46/PDFCreatorAndroid/issues/14)
    4. `onNextClicked()`
        * This is a handler method to get callback when user taps on Next.
4. In `onCreate` of you activity, you have to call `createPDF(String fileName, PDFUtilListener listener)`. It will generate PDF and give you a PDF file in callback (if success). After receiving callback you can close activity and do whatever you need to do with PDF.
5. This library also provides `PDFUtil.pdfToBitmap(File pdfFile)` method to get image preview of all pages of sepcified PDF file.

### Available Views

1. `TextView` -> `PDFTextView`
2. `VerticalView` -> `PDFVerticalView`
3. `HorizontalView` -> `PDFHorizontalView`
4. `ImageView` -> `PDFImageView`
5. `TableView` -> `PDFTableView`
6. `Saperator` -> `PDFLineSaperatorView`

### Advanced
If you cannot find some methods of a `View` Class inside `PDFView` class you can get view by calling `pdfView.getView()` on any available PDFView class and then update view properties.

For example Android `TextView` support setting html to TextView which is not available in `PDFTextView`, to do that see example below:
```java
PDFTextView pdfIconLicenseView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
Spanned icon8Link = Html.fromHtml("Icon from <a href='https://icons8.com'>https://icons8.com</a>");
pdfIconLicenseView.getView().setText(icon8Link);
```
Another example, Set gravity to View
```java
pdfIconLicenseView.getView().setGravity(Gravity.CENTER_VERTICAL);
```

### Advanced, Proceed with caution :warning:
This is a unfinished feature, Use only for basic cases [After using this feature you cannot add child view to your custom view].
If you want to add a custom view to PDF (such as chart or icon), you just can create your own like this:
```java
PDFVerticalView verticalView = new PDFVerticalView(context);
verticalView.setView(View view);
```

## Example:
An example is created, Look at [PdfCreatorExampleActivity](https://github.com/tejpratap46/PDFCreatorAndroid/blob/master/app/src/main/java/com/tejpratapsingh/pdfcreatorandroid/PdfCreatorExampleActivity.java) of app.

### VIEWS
* PDFVerticalView
```java
PDFVerticalView verticalView = new PDFVerticalView(getApplicationContext());
PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P)
				.setText("TextView1");
verticalView.addView(pdfTextView1)
PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P)
				.setText("TextView2");
verticalView.addView(pdfTextView2)
// Get View
LinearLayout layout = verticalView.getView();
```
* PDFHorizontalView
```java
PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());
PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P)
				.setText("TextView1");
horizontalView.addView(pdfTextView1)
PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P)
				.setText("TextView2");
horizontalView.addView(pdfTextView2)
// Get View
LinearLayout layout = horizontalView.getView();
```
* PDFTextView
```java
PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P)
				.setText("TextView1");
PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P)
				.setText(new SpanningString("TextView2"));
// Get View
TextView textView = pdfTextView2.getView();
```
* PDFImageView
```java
PDFImageView pdfImageView = new PDFImageView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P)
				.setImageResource(R.drawable.ic_example);
// Get View
ImageView imageView = pdfImageView.getView();
```
* PDFTableView
```java
String[] textInTable = {"1", "2", "3", "4"};

// Create table column headers
PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
for (String s : textInTable) {
    PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
    pdfTextView.setText("Header Title: " + s);
    tableHeader.addToRow(pdfTextView);
}
// Create first row
PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
for (String s : textInTable) {
    PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
    pdfTextView.setText("Row 1 : " + s);
    tableRowView1.addToRow(pdfTextView);
}

// PDFTableView takes table header and first row at once because if page ends after adding header then first row will be on next page. To avoid confusion to user, table header and first row is printed together.
PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);
for (int i = 0; i < 10; i++) {
    // Create 10 rows and add to table.
    PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
    for (String s : textInTable) {
	PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
	pdfTextView.setText("Row " + (i + 1) + ": " + s);
	tableRowView.addToRow(pdfTextView);
    }
    tableView.addRow(tableRowView);
}
```
* PDFLineSeparatorView
```java
PDFLineSeparatorView lineSeparatorWhite = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
PDFLineSeparatorView lineSeparatorBlack = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
// Get View
View separatorView = lineSeparatorWhite.getView();
```



**********
### Pdf Viewer
This library now has a built-in Pdf Reader which uses `PDFUtil.pdfToBitmap(savedPDFFile)` internally to show preview of Pdf as Images inside a View Pager, Pdf viewer also has a RecyclerView version as well, but you need to add RecyclerView as Your Dependency.
To Use ViewPager Based Pdf Viewer, you just have to create a Activity and extend it with `PDFViewerActivity` and call it using an Intent.

```java
Uri pdfUri = Uri.fromFile(savedPDFFile);

Intent intentPdfViewer = new Intent(MainActivity.this, PdfViewerActivity.class);
intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

startActivity(intentPdfViewer);
```
You can see Example Code At: [PdfViewerExampleActivity](https://github.com/tejpratap46/PDFCreatorAndroid/blob/master/app/src/main/java/com/tejpratapsingh/pdfcreatorandroid/PdfViewerExampleActivity.java) of app.

### Html To Pdf
You can create a Pdf from Html using Utility function `PDFUtil.generatePDFFromHTML(getApplicationContext(), pdfFileToSave, "<html string />", callback);`
```java
// Create Temp File to save Pdf To
final File savedPDFFile = FileManager.getInstance().createTempFile(getApplicationContext(), "pdf", false);
// Generate Pdf From Html
PDFUtil.generatePDFFromHTML(getApplicationContext(), savedPDFFile, " <!DOCTYPE html>\n" +
    "<html>\n" +
    "<body>\n" +
    "\n" +
    "<h1>My First Heading</h1>\n" +
    "<p>My first paragraph.</p>\n" +
    " <a href='https://www.example.com'>This is a link</a>" +
    "\n" +
    "</body>\n" +
    "</html> ", new PDFPrint.OnPDFPrintListener() {
        @Override
        public void onSuccess(File file) {
            // Open Pdf Viewer
            Uri pdfUri = Uri.fromFile(savedPDFFile);

            Intent intentPdfViewer = new Intent(MainActivity.this, PdfViewerActivity.class);
            intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

            startActivity(intentPdfViewer);
        }

        @Override
        public void onError(Exception exception) {
            exception.printStackTrace();
        }
});
```

### WebView To Pdf
With this feature, you can directly create Pdf from whatever your WebView is showing. You can add `contenteditable="true"` and have user edit data and create pdf from edited Data.
You can just call Utility function: `PDFUtil.generatePDFFromWebView(savedPDFFile, webView, callback)`

You can see Example Code At: [PdfEditorExampleActivity](https://github.com/tejpratap46/PDFCreatorAndroid/blob/master/app/src/main/java/com/tejpratapsingh/pdfcreatorandroid/PdfEditorExampleActivity.java) of app.

```java
// Create Temp File to save Pdf To
final File savedPDFFile = FileManager.getInstance().createTempFile(getApplicationContext(), "pdf", false);
// Generate Pdf From Html
PDFUtil.generatePDFFromWebView(savedPDFFile, webView, new PDFPrint.OnPDFPrintListener() {
    @Override
    public void onSuccess(File file) {
        // Open Pdf Viewer
        Uri pdfUri = Uri.fromFile(savedPDFFile);

        Intent intentPdfViewer = new Intent(PdfEditorActivity.this, PdfViewerActivity.class);
        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

        startActivity(intentPdfViewer);
    }

    @Override
    public void onError(Exception exception) {
        exception.printStackTrace();
    }
});
```

## Example PDF
* [Link to Output PDF](https://github.com/tejpratap46/PDFCreatorAndroid/raw/master/test.pdf)

## Donate
[![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/M4M413CJC)
