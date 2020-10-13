package com.tejpratapsingh.pdfcreator.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.chrisbanes.photoview.PhotoView;
import com.tejpratapsingh.pdfcreator.R;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class PDFCreatorActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PDFCreatorActivity";

    private int headerLayoutHeight = 0;
    private int selectedPreviewPage = 0;

    LinearLayout layoutPageParent, layoutPrintPreview;
    TextView textViewGeneratingPDFHolder, textViewPageNumber, textViewPreviewNotAvailable;
    PhotoView imageViewPDFPreview;
    Button buttonEmailVisit;
    ImageButton buttonNextPage, buttonPreviousPage;

    ArrayList<Bitmap> pagePreviewBitmapList = new ArrayList<>();

    File savedPDFFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcreator);

        layoutPageParent = findViewById(R.id.layoutPdfPreview);
        textViewGeneratingPDFHolder = findViewById(R.id.textViewPdfGeneratingHolder);
        layoutPrintPreview = findViewById(R.id.layoutPrintPreview);
        imageViewPDFPreview = layoutPrintPreview.findViewById(R.id.imagePreviewPdfPrescription);
        textViewPageNumber = layoutPrintPreview.findViewById(R.id.textViewPreviewPageNumber);
        textViewPreviewNotAvailable = layoutPrintPreview.findViewById(R.id.textViewPreviewPDFNotSupported);

        layoutPageParent.removeAllViews();

        buttonNextPage = layoutPrintPreview.findViewById(R.id.buttonNextPage);
        buttonNextPage.setOnClickListener(this);
        buttonPreviousPage = layoutPrintPreview.findViewById(R.id.buttonPreviousPage);
        buttonPreviousPage.setOnClickListener(this);
        buttonEmailVisit = layoutPrintPreview.findViewById(R.id.buttonSendEmail);
        buttonEmailVisit.setOnClickListener(this);
    }

    public void createPDF(String fileName, final PDFUtil.PDFUtilListener pdfUtilListener) {
        ArrayList<View> bodyViewList = new ArrayList<>();
        View header = null;
        if (getHeaderView(0) != null) {
            header = getHeaderView(0).getView();
            header.setTag(PDFHeaderView.class.getSimpleName());
            bodyViewList.add(header);
            addViewToTempLayout(layoutPageParent, header);
        }

        if (getBodyViews() != null) {
            for (PDFView pdfView : getBodyViews().getChildViewList()) {
                View bodyView = pdfView.getView();
                bodyView.setTag(PDFBody.class.getSimpleName());
                bodyViewList.add(bodyView);
                addViewToTempLayout(layoutPageParent, bodyView);
            }
        }

        createPDFFromViewList(header, bodyViewList, fileName, new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                try {
                    pagePreviewBitmapList.clear();
                    pagePreviewBitmapList.addAll(PDFUtil.pdfToBitmap(savedPDFFile));
                    textViewGeneratingPDFHolder.setVisibility(View.GONE);
                    layoutPrintPreview.setVisibility(View.VISIBLE);
                    selectedPreviewPage = 0;
                    imageViewPDFPreview.setImageBitmap(pagePreviewBitmapList.get(selectedPreviewPage));
                    textViewPageNumber.setText(String.format(Locale.getDefault(), "%d OF %d", selectedPreviewPage + 1, pagePreviewBitmapList.size()));
                } catch (Exception e) {
                    e.printStackTrace();
                    imageViewPDFPreview.setVisibility(View.GONE);
                    textViewPageNumber.setVisibility(View.GONE);
                    buttonNextPage.setVisibility(View.GONE);
                    buttonPreviousPage.setVisibility(View.GONE);
                    textViewPreviewNotAvailable.setVisibility(View.VISIBLE);
                }
                PDFCreatorActivity.this.savedPDFFile = savedPDFFile;
                pdfUtilListener.pdfGenerationSuccess(savedPDFFile);
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                pdfUtilListener.pdfGenerationFailure(exception);
            }
        });
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
                final FileManager fileManager = FileManager.getInstance();
                fileManager.cleanTempFolder(getApplicationContext());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final List<View> pdfPageViewList = new ArrayList<>();
                        LinearLayout currentPDFLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_pdf_page, null);
                        currentPDFLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                        pdfPageViewList.add(currentPDFLayout);
                        int currentPageHeight = 0;

                        if (headerView != null) {
                            // If item is a page header, store its height so we can add it to all pages without waiting to render it every time
                            headerLayoutHeight = headerView.getHeight();
                        }

                        int pageIndex = 1;
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
                                    LinearLayout layoutHeader = getHeaderView(pageIndex).getView();
                                    addViewToTempLayout(layoutPageParent, layoutHeader);
                                    currentPageHeight += headerLayoutHeight;
                                    layoutPageParent.removeView(layoutHeader);
                                    currentPDFLayout.addView(layoutHeader);

                                    pageIndex = pageIndex + 1;
                                }
                            }

                            currentPageHeight += viewItem.getHeight();

                            layoutPageParent.removeView(viewItem);
                            currentPDFLayout.addView(viewItem);
                        }

                        PDFUtil.getInstance().generatePDF(pdfPageViewList, fileManager.createTempFileWithName(getApplicationContext(), filename + ".pdf", false).getAbsolutePath(), pdfUtilListener);
                    }
                });
            }
        });
    }

    private void addViewToTempLayout(LinearLayout layoutPageParent, View viewToAdd) {
        layoutPageParent.addView(viewToAdd);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonNextPage) {
            if (selectedPreviewPage == pagePreviewBitmapList.size() - 1) {
                return;
            }
            selectedPreviewPage = selectedPreviewPage + 1;
            imageViewPDFPreview.setImageBitmap(pagePreviewBitmapList.get(selectedPreviewPage));
            textViewPageNumber.setText(String.format(Locale.getDefault(), "%d of %d", selectedPreviewPage + 1, pagePreviewBitmapList.size()));
        } else if (v == buttonPreviousPage) {
            if (selectedPreviewPage == 0) {
                return;
            }
            selectedPreviewPage = selectedPreviewPage - 1;
            imageViewPDFPreview.setImageBitmap(pagePreviewBitmapList.get(selectedPreviewPage));
            textViewPageNumber.setText(String.format(Locale.getDefault(), "%d of %d", selectedPreviewPage + 1, pagePreviewBitmapList.size()));
        } else if (v == buttonEmailVisit) {
            onNextClicked(savedPDFFile);
        }
    }

    protected abstract PDFHeaderView getHeaderView(int page);

    protected abstract PDFBody getBodyViews();

    protected abstract void onNextClicked(File savedPDFFile);
}
