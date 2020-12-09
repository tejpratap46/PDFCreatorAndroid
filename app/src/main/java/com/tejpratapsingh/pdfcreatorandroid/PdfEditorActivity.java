package com.tejpratapsingh.pdfcreatorandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.print.PDFPrint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PdfEditorActivity extends AppCompatActivity {
    private static final String TAG = "PdfEditorActivity";

    private WebView webView;

    public static class MyWebViewClient extends WebViewClient {

        public interface OnSourceReceived {
            void success(String html);
        }

        private final OnSourceReceived onSourceReceived;

        public MyWebViewClient(OnSourceReceived onSourceReceived) {
            this.onSourceReceived = onSourceReceived;
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("source://")) {
                try {
                    String html = URLDecoder.decode(url, "UTF-8").substring(9);
                    onSourceReceived.success(html);
                } catch (UnsupportedEncodingException e) {
                    Log.e("example", "failed to decode source", e);
                }
                return true;
            }
            // For all other links, let the WebView do it's normal thing
            return false;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_editor);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pdf Editor");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.colorTransparentBlack)));
        }

        webView = (WebView) findViewById(R.id.webViewEditor);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new MyWebViewClient(new MyWebViewClient.OnSourceReceived() {
            @Override
            public void success(String html) {
                Log.d(TAG, "success: html: " + html);
                FileManager.getInstance().cleanTempFolder(getApplicationContext());
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
            }
        }));

//        webView.loadData("<!DOCTYPE html>\n" +
//                "<html>\n" +
//                "<body>\n" +
//                "\n" +
//                "<p contenteditable=\"true\">This is a paragraph. It is editable. Try to change this text.</p>\n" +
//                "\n" +
//                "</body>\n" +
//                "</html>\n", "text/HTML", "UTF-8");

        webView.loadUrl("file:///android_asset/invoice.html");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pdf_editor, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menuPrintPdf) {
            webView.loadUrl("javascript:this.document.location.href = 'source://' + encodeURI(document.documentElement.outerHTML);");
        }
        return super.onOptionsItemSelected(item);
    }
}