package com.tejpratapsingh.pdfcreator.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.tejpratapsingh.pdfcreator.R;
import com.tejpratapsingh.pdfcreator.custom.TouchImageView;
import com.tejpratapsingh.pdfcreator.custom.TouchImageViewFling;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFFooterView;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFImageView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFVerticalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class PDFCreatorActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PDFCreatorActivity";

    private int heightRequiredByHeader = 0;
    private int heightRequiredByFooter = 0;
    private int selectedPreviewPage = 0;

    LinearLayout layoutPageParent, layoutPrintPreview;
    TextView textViewGeneratingPDFHolder, textViewPageNumber, textViewPreviewNotAvailable;
    AppCompatImageView imageViewPDFPreview;
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
        imageViewPDFPreview = layoutPrintPreview.findViewById(R.id.imagePreviewPdf);
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

        View footer = null;
        PDFFooterView pdfFooterView = getFooterView(0);
        if (pdfFooterView != null && pdfFooterView.getView().getChildCount() > 1) {
            // pdfFooterView.getView().getChildCount() > 1, because first view is ALWAYS empty space filler.
            footer = pdfFooterView.getView();
            footer.setTag(PDFFooterView.class.getSimpleName());
            addViewToTempLayout(layoutPageParent, footer);
        }

        createPDFFromViewList(header, footer, bodyViewList, fileName, new PDFUtil.PDFUtilListener() {
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
    private void createPDFFromViewList(final View headerView, final View footerView, @NonNull final ArrayList<View> tempViewList, @NonNull final String filename, final PDFUtil.PDFUtilListener pdfUtilListener) {
        tempViewList.get(tempViewList.size() - 1).post(new Runnable() {
            @Override
            public void run() {

                // Clean temp folder
                final FileManager fileManager = FileManager.getInstance();
                fileManager.cleanTempFolder(getApplicationContext());

                // get height per page
                final int HEIGHT_ALLOTTED_PER_PAGE = (getResources().getDimensionPixelSize(R.dimen.pdf_height) - (getResources().getDimensionPixelSize(R.dimen.pdf_margin_vertical) * 2));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final List<View> pdfPageViewList = new ArrayList<>();
                        FrameLayout currentPDFLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.item_pdf_page, layoutPageParent, false);
                        currentPDFLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                        pdfPageViewList.add(currentPDFLayout);

                        // Add watermark layout
                        PDFView watermarkPDFView = getWatermarkView(0);
                        if (watermarkPDFView != null && watermarkPDFView.getView() != null) {
                            currentPDFLayout.addView(watermarkPDFView.getView());
                        }

                        LinearLayout currentPDFView = new PDFVerticalView(getApplicationContext()).getView();
                        final LinearLayout.LayoutParams verticalPageLayoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        currentPDFView.setLayoutParams(verticalPageLayoutParams);
                        currentPDFLayout.addView(currentPDFView);

                        int currentPageHeight = 0;

                        if (headerView != null) {
                            // If item is a page header, store its height so we can add it to all pages without waiting to render it every time
                            heightRequiredByHeader = headerView.getHeight();
                        }

                        if (footerView != null) {
                            // If item is a page header, store its height so we can add it to all pages without waiting to render it every time
                            heightRequiredByFooter = footerView.getHeight();
                        }

                        int pageIndex = 1;
                        for (int i = 0; i < tempViewList.size(); i++) {
                            View viewItem = tempViewList.get(i);

                            if (currentPageHeight + viewItem.getHeight() > HEIGHT_ALLOTTED_PER_PAGE) {
                                currentPDFLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.item_pdf_page, layoutPageParent, false);
                                currentPDFLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                                pdfPageViewList.add(currentPDFLayout);
                                currentPageHeight = 0;

                                // Add watermark layout
                                watermarkPDFView = getWatermarkView(pageIndex);
                                if (watermarkPDFView != null && watermarkPDFView.getView() != null) {
                                    currentPDFLayout.addView(watermarkPDFView.getView());
                                }

                                currentPDFView = new PDFVerticalView(getApplicationContext()).getView();
                                currentPDFView.setLayoutParams(verticalPageLayoutParams);
                                currentPDFLayout.addView(currentPDFView);

                                // Add page header again
                                if (heightRequiredByHeader > 0) {
                                    // If height is available, only then add header
                                    LinearLayout layoutHeader = getHeaderView(pageIndex).getView();
                                    addViewToTempLayout(layoutPageParent, layoutHeader);
                                    currentPageHeight += heightRequiredByHeader;
                                    layoutPageParent.removeView(layoutHeader);
                                    currentPDFView.addView(layoutHeader);

                                    pageIndex = pageIndex + 1;
                                }
                            }

                            currentPageHeight += viewItem.getHeight();

                            layoutPageParent.removeView(viewItem);
                            currentPDFView.addView(viewItem);

                            // See if we have enough space to add Next View with Footer
                            // We we don't, add Footer View to current page
                            // Height required to add this view in current page
                            int heightRequiredToAddNextView = 0;
                            boolean shouldAddFooterNow = false;

                            if (tempViewList.size() > i + 1) {
                                // Check if we can add CURRENT_VIEW + NEXT_VIEW + FOOTER in current page
                                View nextViewItem = tempViewList.get(i + 1);
                                heightRequiredToAddNextView = nextViewItem.getHeight();

                                if (currentPageHeight + heightRequiredToAddNextView + heightRequiredByFooter > HEIGHT_ALLOTTED_PER_PAGE) {
                                    shouldAddFooterNow = true;
                                }

                            } else {
                                // Add Views are already added, we should add footer next
                                shouldAddFooterNow = true;
                            }

                            if (shouldAddFooterNow) {
                                // Cannot Add Next View with Footer in current Page
                                // Add Footer View to Current Page

                                if (heightRequiredByFooter > 0) {
                                    // Footer is NOT prematurely added, so we need to subtract 1 from pageIndex
                                    LinearLayout layoutFooter = getFooterView(pageIndex - 1).getView();
                                    addViewToTempLayout(layoutPageParent, layoutFooter);
                                    currentPageHeight += heightRequiredByFooter;
                                    layoutPageParent.removeView(layoutFooter);
                                    currentPDFView.addView(layoutFooter);
                                }
                            }
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

    /**
     * Get header per page, starts with page: 0
     * MAKE SURE HEIGHT OF EVERY HEADER IS SAME FOR EVERY PAGE
     *
     * @param forPage page number
     * @return View for header
     */
    protected abstract PDFHeaderView getHeaderView(int forPage);

    /**
     * Content that has to be paginated
     *
     * @return PDFBody, which is a List of Views
     */
    protected abstract PDFBody getBodyViews();

    /**
     * Get header per page, starts with page: 0
     * MAKE SURE HEIGHT OF EVERY FOOTER IS SAME FOR EVERY PAGE
     *
     * @param forPage page number
     * @return View for header
     */
    protected abstract PDFFooterView getFooterView(int forPage);

    /**
     * Can add watermark images to per page, starts with page: 0
     *
     * @param forPage page number
     * @return PDFImageView or null
     */
    @Nullable
    protected PDFImageView getWatermarkView(int forPage) {
        return null;
    }

    protected abstract void onNextClicked(File savedPDFFile);
}
