package com.tejpratapsingh.pdfcreator.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.print.PDFPrint;
import android.print.PrintAttributes;
import android.print.PrintJob;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * A Class used to generate PDF for the given Views.
 */

public class PDFUtil {


    /**
     * TAG.
     */
    private static final String TAG = PDFUtil.class.getName();
    /**
     * Page width for our PDF.
     */
    private static final double PDF_PAGE_WIDTH = 8.3 * 72;
    /**
     * Page height for our PDF.
     */
    private static final double PDF_PAGE_HEIGHT = 11.7 * 72;
    /**
     * Singleton instance for PDFUtil.
     */
    private static PDFUtil sInstance;

    /**
     * Constructor.
     */
    private PDFUtil() {

    }

    /**
     * Return singleton instance of PDFUtil.
     *
     * @return singleton instance of PDFUtil.
     */
    public static PDFUtil getInstance() {
        if (sInstance == null) {
            sInstance = new PDFUtil();
        }
        return sInstance;
    }

    /**
     * Generates PDF for the given content views to the file path specified.
     * <p/>
     * Method gets List of views as the input and each view will be written to the single page in
     * the PDF.
     * <p/>
     * If API is not support then PDFUtilListener's  pdfGenerationFailure method will be called with
     * APINotSupportedException.
     *
     * @param contentViews List of Content Views to be converted as PDF.
     * @param filePath     FilePath where the PDF has to be stored.
     * @param listener     PDFUtilListener to send callback for PDF generation.
     */
    public final void generatePDF(final List<View> contentViews, final String filePath,
                                  final PDFUtilListener listener) {
        // Check Api Version.
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            // Kitkat
            new GeneratePDFAsync(contentViews, filePath, listener).execute();
        } else {
            // Before Kitkat
            Log.e(TAG, "Generate PDF is not available for your android version.");
            listener.pdfGenerationFailure(
                    new APINotSupportedException("Generate PDF is not available for your android version."));
        }
    }

    public static void generatePDFFromHTML(final Context context, final File file, final String htmlString, PDFPrint.OnPDFPrintListener onPDFPrintListener) {
        PDFPrint.generatePDFFromHTML(context, file, htmlString, onPDFPrintListener);
    }

    public static void generatePDFFromWebView(final File file, final WebView webView, PDFPrint.OnPDFPrintListener onPDFPrintListener) {
        PDFPrint.generatePDFFromWebView(file, webView, onPDFPrintListener);
    }

    public static PrintJob printPdf(final Activity activity, File pdfFileToPrint, PrintAttributes printAttributes) {
        return PDFPrint.printPDF(activity, pdfFileToPrint, printAttributes);
    }

    /**
     * Listener used to send PDF Generation callback.
     */
    public interface PDFUtilListener {
        /**
         * Called on the success of PDF Generation.
         */
        void pdfGenerationSuccess(File savedPDFFile);

        /**
         * Called when PDF Generation failed.
         *
         * @param exception Exception occurred during PDFGeneration.
         */
        void pdfGenerationFailure(final Exception exception);
    }

    /**
     * Async task class used to generate PDF in separate thread.
     */
    private static class GeneratePDFAsync extends AsyncTask<Void, Void, File> {

        // mContentViews.
        private List<View> mContentViews;

        // mFilePath.
        private String mFilePath;

        // mListener.
        private PDFUtilListener mListener = null;

        // mException.
        private Exception mException;

        /**
         * Constructor.
         *
         * @param contentViews List of Content Views to be converted as PDF.
         * @param filePath     FilePath where the PDF has to be stored.
         * @param listener     PDFUtilListener to send callback for PDF generation.
         */
        GeneratePDFAsync(final List<View> contentViews, final String filePath, final PDFUtilListener listener) {
            this.mContentViews = contentViews;
            this.mFilePath = filePath;
            this.mListener = listener;
        }

        /**
         * Do In Background.
         *
         * @param params Params
         * @return TRUE if PDF successfully generated else FALSE.
         */
        @Override
        protected File doInBackground(Void... params) {
            try {
                // Create PDF Document.
                PdfDocument pdfDocument = new PdfDocument();

                // Write content to PDFDocument.
                writePDFDocument(pdfDocument);

                // Save document to file.
                return savePDFDocumentToStorage(pdfDocument);
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        /**
         * On Post Execute.
         *
         * @param savedPDFFile Saved pdf file, null if not generated successfully
         */
        @Override
        protected void onPostExecute(File savedPDFFile) {
            super.onPostExecute(savedPDFFile);
            if (savedPDFFile != null) {
                //Send Success callback.
                mListener.pdfGenerationSuccess(savedPDFFile);
            } else {
                //Send Error callback.
                mListener.pdfGenerationFailure(mException);
            }
        }

        /**
         * Writes given PDFDocument using content views.
         *
         * @param pdfDocument PDFDocument to be written.
         */
        private void writePDFDocument(final PdfDocument pdfDocument) {

            for (int i = 0; i < mContentViews.size(); i++) {

                //Get Content View.
                View contentView = mContentViews.get(i);

                // crate a page description
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.
                        Builder((int) PDF_PAGE_WIDTH, (int) PDF_PAGE_HEIGHT, i + 1).create();

                // start a page
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                // draw view on the page
                Canvas pageCanvas = page.getCanvas();
                pageCanvas.scale(1f, 1f);
                int pageWidth = pageCanvas.getWidth();
                int pageHeight = pageCanvas.getHeight();
                int measureWidth = View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY);
                int measuredHeight = View.MeasureSpec.makeMeasureSpec(pageHeight, View.MeasureSpec.EXACTLY);
                contentView.measure(measureWidth, measuredHeight);
                contentView.layout(0, 0, pageWidth, pageHeight);
                contentView.draw(pageCanvas);

                // finish the page
                pdfDocument.finishPage(page);

            }
        }

        /**
         * Save PDFDocument to the File in the storage.
         *
         * @param pdfDocument Document to be written to the Storage.
         * @throws java.io.IOException
         */
        private File savePDFDocumentToStorage(final PdfDocument pdfDocument) throws IOException {
            FileOutputStream fos = null;
            // Create file.
            File pdfFile = null;
            if (mFilePath == null || mFilePath.isEmpty()) {
                pdfFile = File.createTempFile(Long.toString(new Date().getTime()), "pdf");
            } else {
                pdfFile = new File(mFilePath);
            }

            //Create parent directories
            File parentFile = pdfFile.getParentFile();
            if (parentFile == null) {
                return null;
            }
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                throw new IllegalStateException("Couldn't create directory: " + parentFile);
            }
            boolean fileExists = pdfFile.exists();
            // If File already Exists. delete it.
            if (fileExists) {
                fileExists = !pdfFile.delete();
            }
            try {
                if (!fileExists) {
                    // Create New File.
                    fileExists = pdfFile.createNewFile();
                }

                if (fileExists) {
                    // Write PDFDocument to the file.
                    fos = new FileOutputStream(pdfFile);
                    pdfDocument.writeTo(fos);

                    //Close output stream
                    fos.close();

                    // close the document
                    pdfDocument.close();
                }
                return pdfFile;
            } catch (IOException exception) {
                exception.printStackTrace();
                if (fos != null) {
                    fos.close();
                }
                throw exception;
            }
        }
    }

    /**
     * APINotSupportedException will be thrown If the device doesn't support PDF methods.
     */
    private static class APINotSupportedException extends Exception {
        // mErrorMessage.
        private String mErrorMessage;

        /**
         * Constructor.
         *
         * @param errorMessage Error Message.
         */
        APINotSupportedException(final String errorMessage) {
            this.mErrorMessage = errorMessage;
        }

        /**
         * To String.
         *
         * @return error message as a string.
         */
        @Override
        public String toString() {
            return "APINotSupportedException{" +
                    "mErrorMessage='" + mErrorMessage + '\'' +
                    '}';
        }
    }

    /**
     * Convert PDF to bitmap, only works on devices above LOLLIPOP
     *
     * @param pdfFile pdf file
     * @return list of bitmap of every page
     * @throws Exception
     */
    public static LinkedList<Bitmap> pdfToBitmap(File pdfFile) throws Exception, IllegalStateException {
        if (pdfFile == null || pdfFile.exists() == false) {
            throw new IllegalStateException("PDF File Does Not Exist");
        }

        LinkedList<Bitmap> bitmaps = new LinkedList<>();

        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));

            Bitmap bitmap;
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                int width = page.getWidth();
                int height = page.getHeight();

                /* FOR HIGHER QUALITY IMAGES, USE:
                int width = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                */

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                bitmaps.add(bitmap);

                // close the page
                page.close();
            }
            // close the renderer
            renderer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmaps;
    }

    /**
     * Get total number of pages
     *
     * @param pdfFile pdf file to get page count of
     * @return pdf page count
     * @throws IOException
     * @throws Exception
     */
    private int getTotalPages(File pdfFile) throws IOException, Exception {
        ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            return pdfRenderer.getPageCount();
        } else {
            throw new Exception("PDF cannot be processed in this device");
        }
    }
}