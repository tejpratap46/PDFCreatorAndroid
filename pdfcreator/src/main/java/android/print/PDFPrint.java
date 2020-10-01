package android.print;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

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
        mWebView.loadData(htmlString, "text/HTML", "UTF-8");
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
}
