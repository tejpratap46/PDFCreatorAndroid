package com.tejpratapsingh.pdfcreator.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.tejpratapsingh.pdfcreator.R;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFFooterView;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.util.ArrayList;
import java.util.List;

public abstract class PDFCreatorActivity extends AppCompatActivity {
    private static final String TAG = "PDFCreatorActivity";

    public static final String EXTRA_HEADER_VIEW = "EXTRA_HEADER_VIEW";

    private int headerLayoutHeight = 0;
    private int selectedPreviewPage = 0;

    LinearLayout layoutPageParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcreator);

        layoutPageParent = (LinearLayout) findViewById(R.id.layoutPdfPreview);
    }

    public void createPDF(String fileName, PDFHeaderView headerView, PDFBody bodyView, PDFUtil.PDFUtilListener pdfUtilListener) {
        View header = headerView.getView();
        addViewToTempLayout(layoutPageParent, header);
//        View footer = footerView.getView();
//        addViewToTempLayout(layoutPageParent, footer);

        ArrayList<View> bodyViewList = new ArrayList<>();
        for (PDFView pdfView : bodyView.getChildViewList()) {
            bodyViewList.add(pdfView.getView());
            addViewToTempLayout(layoutPageParent, pdfView.getView());
        }

        createPDFFromViewList(header, bodyViewList, fileName, pdfUtilListener);
    }

    /**
     * Creates a paginated PDF page views from list of views those are already rendered on screen
     * (Only rendered views can give height)
     *
     * @param tempViewList list of views to create pdf views from, view should be already rendered to screen
     */
    private void createPDFFromViewList(final View headerView, @NonNull final ArrayList<View> tempViewList, @NonNull final String filename, final PDFUtil.PDFUtilListener pdfUtilListener) {
        tempViewList.get(tempViewList.size() - 1).post(new Runnable() {
            @Override
            public void run() {

                // Clean temp folder
                final FileManager myOPDFileManager = FileManager.getInstance();
                myOPDFileManager.cleanTempFolder(getApplicationContext());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final List<View> pdfPageViewList = new ArrayList<>();
                        LinearLayout currentPDFLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_pdf_page, null);
                        currentPDFLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                        pdfPageViewList.add(currentPDFLayout);
                        int currentPageHeight = 0;

                        View header = headerView;

                        if (header != null) {
                            // If item is a page header, store its height so we can add it to all pages without waiting to render it every time
                            headerLayoutHeight = header.getHeight();
                        }

                        for (View viewItem : tempViewList) {
                            if (currentPageHeight + viewItem.getHeight() > (getResources().getDimensionPixelSize(R.dimen.pdf_height)
                                    - (getResources().getDimensionPixelSize(R.dimen.pdf_margin_vertical) * 2))) {
                                currentPDFLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_pdf_page, null);
                                currentPDFLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                                pdfPageViewList.add(currentPDFLayout);
                                currentPageHeight = 0;

                                // Add page header again
                                if (headerLayoutHeight > 0) {
                                    // If height is available, only then add header
//                                    LinearLayout layoutHeader = getHeaderLayout();
//                                    addViewToTempLayout(layoutPageParent, layoutHeader);
//                                    currentPageHeight += headerLayoutHeight;
//                                    layoutPageParent.removeView(layoutHeader);
//                                    header = header.
//                                    currentPDFLayout.addView(layoutHeader);
                                }
                            }

                            currentPageHeight += viewItem.getHeight();

                            layoutPageParent.removeView(viewItem);
                            currentPDFLayout.addView(viewItem);
                        }

                        PDFUtil.getInstance().generatePDF(pdfPageViewList, myOPDFileManager.createTempFileWithName(getApplicationContext(), filename + ".pdf", false).getAbsolutePath(), pdfUtilListener);
                    }
                });
            }
        });
    }

    private void addViewToTempLayout(LinearLayout tempPageLayout, View viewToAdd) {
        tempPageLayout.addView(viewToAdd);
    }
}
