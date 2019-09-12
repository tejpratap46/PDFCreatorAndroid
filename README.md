# PDFCreatorAndroid
Simple library to generate and view PDF in Android

![Cover](https://github.com/tejpratap46/PDFCreatorAndroid/raw/master/cover.png)

[![](https://jitpack.io/v/tejpratap46/PDFCreatorAndroid.svg)](https://jitpack.io/#tejpratap46/PDFCreatorAndroid)

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
	        implementation 'com.github.tejpratap46:PDFCreatorAndroid:1.0'
	}
```

### Concept
* Android has capability to print documents to PDF, this library simplifies those API's to generate PDF easily.
* At basic level, API renders Views to PDF. To create A PDF with pages we need to submit views exactly height of one page, any view larges then that that will be trimmed.
* This library creates pages by adding views to a parent view unitil the next view is about to exceed current page. If next view exceeds current page, that view will be added to new page.

### Implementation
* First thing first, Look at [MainActivity](https://github.com/tejpratap46/PDFCreatorAndroid/blob/master/app/src/main/java/com/tejpratapsingh/pdfcreatorandroid/MainActivity.java) of app.

1. PDF creater uses views which can be rendered, So we need to exted an activity in order to create activity.
2. Create a Empty `Activity` without any layout and extend it with `PDFCreatorActivity`. Do not set use `setContentView(int resourceId)` inside your created activity.
3. There are 3 abstract methods you have to override.
    1. `getHeaderView()`
        * This will be header for PDF and will be added to each page. (Use PDFHeaderView if )
    2. `getBodyViews()`
        * This will return a PDFBody which consist of list of views which can be broken between pages.
    3. `onNextClicked()`
        * This is a handler method to get callback when user taps on Next.
4. In `onCreate` of you activity, you have to call `createPDF(String fileName, PDFUtilListener listener)`. It will generate PDF and give you a PDF file in callback (if success). After receiving callback you can close activity and do whatever you need to do with PDF.
5. This library also provides `PDFUtil.pdfToBitmap(File pdfFile)` method to get image preview of all pages of sepcified PDF file.

#### Available Views

1. `TextView` -> `PDFTextView`
2. `VerticalView` -> `PDFVerticalView`
3. `HorizontalView` -> `PDFHorizontalView`
4. `ImageView` -> `PDFImageView`
5. `TableView` -> `PDFTableView`
6. `Saperator` -> `PDFLineSaperatorView`

#### Advanced
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

#### Advanced, Proceed with caution
This is a unfinished feature, Use only for basic cases [After using this feature you cannot add child view to your custom view].
Another Thing, if you want to add a custom view to PDF, you just can create your own like this:
```java
PDFVerticalView verticalView = new PDFVerticalView(context);
verticalView.setView(View view);
```
