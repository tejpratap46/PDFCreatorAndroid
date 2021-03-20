package android.print;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PDFPrint {

    public interface OnPDFPrintListener {
        void onSuccess(File file);

        void onError(Exception exception);
    }

    public static void generatePDFFromHTML(final Context context, final File file, final String htmlString, final OnPDFPrintListener onPDFPrintListener) {
        final WebView mWebView = new WebView(context);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                PrintAttributes printAttributes = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(new PrintAttributes.Resolution("RESOLUTION_ID", "RESOLUTION_ID", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build();

                final PrintDocumentAdapter documentAdapter = mWebView.createPrintDocumentAdapter(file.getName());
                documentAdapter.onLayout(null, printAttributes, null, new PrintDocumentAdapter.LayoutResultCallback() {
                    @Override
                    public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                        documentAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, getOutputFile(file), null, new PrintDocumentAdapter.WriteResultCallback() {

                            @Override
                            public void onWriteCancelled() {
                                super.onWriteCancelled();
                                onPDFPrintListener.onError(new Exception("PDF Write cancelled."));
                            }

                            @Override
                            public void onWriteFailed(CharSequence error) {
                                super.onWriteFailed(error);
                                onPDFPrintListener.onError(new Exception(error.toString()));
                            }

                            @Override
                            public void onWriteFinished(PageRange[] pages) {
                                super.onWriteFinished(pages);
                                onPDFPrintListener.onSuccess(file);
                            }
                        });
                    }
                }, null);
            }
        });
        mWebView.loadData(htmlString.replaceAll("#", "%23"), "text/HTML", "UTF-8");
    }

    public static void generatePDFFromWebView(final File file, final WebView webView, final OnPDFPrintListener onPDFPrintListener) {
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("RESOLUTION_ID", "RESOLUTION_ID", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        final PrintDocumentAdapter documentAdapter = webView.createPrintDocumentAdapter(file.getName());
        documentAdapter.onLayout(null, printAttributes, null, new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                documentAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, getOutputFile(file), null, new PrintDocumentAdapter.WriteResultCallback() {

                    @Override
                    public void onWriteCancelled() {
                        super.onWriteCancelled();
                        onPDFPrintListener.onError(new Exception("PDF Write cancelled."));
                    }

                    @Override
                    public void onWriteFailed(CharSequence error) {
                        super.onWriteFailed(error);
                        onPDFPrintListener.onError(new Exception(error.toString()));
                    }

                    @Override
                    public void onWriteFinished(PageRange[] pages) {
                        super.onWriteFinished(pages);
                        onPDFPrintListener.onSuccess(file);
                    }
                });
            }
        }, null);
    }

    private static ParcelFileDescriptor getOutputFile(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrintJob printPDF(final Activity activity, final File pdfFileToPrint, final PrintAttributes printAttributes) {
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        String jobName = Long.valueOf(System.currentTimeMillis()).toString();
        return printManager.print(jobName, new PrintDocumentAdapter() {
            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                InputStream input = null;
                OutputStream output = null;

                try {

                    input = new FileInputStream(pdfFileToPrint);
                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        input.close();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(pdfFileToPrint.getName()).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                callback.onLayoutFinished(pdi, true);
            }
        }, printAttributes);
    }
}
