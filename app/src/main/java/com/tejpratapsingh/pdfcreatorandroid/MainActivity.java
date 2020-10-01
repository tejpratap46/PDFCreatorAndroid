package com.tejpratapsingh.pdfcreatorandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.print.PDFPrint;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.PDFTableView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFImageView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;

import java.io.File;
import java.net.URLConnection;
import java.util.Locale;

public class MainActivity extends PDFCreatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        createPDF("test", new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                Toast.makeText(MainActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(MainActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected PDFHeaderView getHeaderView(int pageIndex) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());

        PDFTextView pdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1));
        pdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        pdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);

        headerView.addView(pdfTextViewPage);

        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.HEADER);
        SpannableString word = new SpannableString("INVOICE");
        word.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdfTextView.setText(word);
        pdfTextView.setLayout(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        pdfTextView.getView().setGravity(Gravity.CENTER_VERTICAL);
        pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);

        horizontalView.addView(pdfTextView);

        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(
                60,
                60, 0);
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.mipmap.ic_launcher);
        imageLayoutParam.setMargins(0, 0, 10, 0);
        imageView.setLayout(imageLayoutParam);

        horizontalView.addView(imageView);

        headerView.addView(horizontalView);

        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        headerView.addView(lineSeparatorView1);

        return headerView;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        PDFTextView pdfCompanyNameView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfCompanyNameView.setText("Company Name");
        pdfBody.addView(pdfCompanyNameView);
        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView1);
        PDFTextView pdfAddressView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfAddressView.setText("Address Line 1\nCity, State - 123456");
        pdfBody.addView(pdfAddressView);

        String[] textInTable = {"1", "2", "3", "4"};

        PDFLineSeparatorView lineSeparatorView2 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView2);
        PDFTextView pdfTableTitleView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTableTitleView.setText("Table Example");
        pdfBody.addView(pdfTableTitleView);

        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText("Header Title: " + s);
            tableHeader.addToRow(pdfTextView);
        }
        PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
        for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText("Row 1 : " + s);
            tableRowView1.addToRow(pdfTextView);
        }

        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);
        for (int i = 0; i < 40; i++) {
            // Create 10 rows
            PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
            for (String s : textInTable) {
                PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextView.setText("Row " + (i + 2) + ": " + s);
                tableRowView.addToRow(pdfTextView);
            }
            tableView.addRow(tableRowView);
        }
        pdfBody.addView(tableView);

        PDFLineSeparatorView lineSeparatorView3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        pdfBody.addView(lineSeparatorView3);

        PDFTextView pdfIconLicenseView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        Spanned icon8Link = Html.fromHtml("Icon from <a href='https://icons8.com'>https://icons8.com</a>");
        pdfIconLicenseView.getView().setText(icon8Link);
        pdfBody.addView(pdfIconLicenseView);

        return pdfBody;
    }

    @Override
    protected void onNextClicked(final File savedPDFFile) {

        new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog)
                .setTitle("Choose One")
                .setSingleChoiceItems(new String[]{"Open Pdf Viewer", "Share Pdf"/*, "html Pdf"*/}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        dialogInterface.dismiss();

                        switch (position) {
                            case 0: {
                                Uri pdfUri = Uri.fromFile(savedPDFFile);

                                Intent intentPdfViewer = new Intent(MainActivity.this, PdfViewerActivity.class);
                                intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

                                startActivity(intentPdfViewer);
                                break;
                            }
                            case 1: {
                                Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                                Uri apkURI = FileProvider.getUriForFile(
                                        getApplicationContext(),
                                        getApplicationContext()
                                                .getPackageName() + ".provider", savedPDFFile);
                                intentShareFile.setDataAndType(apkURI, URLConnection.guessContentTypeFromName(savedPDFFile.getName()));
                                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                intentShareFile.putExtra(Intent.EXTRA_STREAM,
                                        Uri.parse("file://" + savedPDFFile.getAbsolutePath()));

                                startActivity(Intent.createChooser(intentShareFile, "Share File"));
                                break;
                            }
                            case 2: {
                                File savedPDFFile = FileManager.getInstance().createTempFile(getApplicationContext(), "pdf", false);
                                PDFUtil.generatePDFFromHTML(getApplicationContext(), savedPDFFile, "<h1>Test Data</h1>", new PDFPrint.OnPDFPrintListener() {
                                    @Override
                                    public void onSuccess(File file) {
                                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                                        Uri apkURI = FileProvider.getUriForFile(
                                                getApplicationContext(),
                                                getApplicationContext()
                                                        .getPackageName() + ".provider", file);
                                        intentShareFile.setDataAndType(apkURI, URLConnection.guessContentTypeFromName(file.getName()));
                                        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                                                Uri.parse("file://" + file.getAbsolutePath()));

                                        startActivity(Intent.createChooser(intentShareFile, "Share File"));
                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        exception.printStackTrace();
                                    }
                                });
                                break;
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.text_cancel, null)
                .create()
                .show();
    }
}
