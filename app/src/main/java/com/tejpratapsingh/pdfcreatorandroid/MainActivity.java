package com.tejpratapsingh.pdfcreatorandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PDFPrint;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        LinearLayout layoutPdfCreator = (LinearLayout) findViewById(R.id.layoutGeneratePdf);
        LinearLayout layoutHtmlPdfCreator = (LinearLayout) findViewById(R.id.layoutGenerateHtmlPdf);
        LinearLayout layoutEditHtmlPdf = (LinearLayout) findViewById(R.id.layoutEditHtmlPdf);

        layoutPdfCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PdfCreatorActivity.class));
            }
        });

        layoutHtmlPdfCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileManager.getInstance().cleanTempFolder(getApplicationContext());
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
            }
        });

        layoutEditHtmlPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PdfEditorActivity.class));
            }
        });
    }
}
