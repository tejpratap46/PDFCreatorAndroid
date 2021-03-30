package com.tejpratapsingh.pdfcreator.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.tejpratapsingh.pdfcreator.R;
import com.tejpratapsingh.pdfcreator.custom.TouchImageView;
import com.tejpratapsingh.pdfcreator.custom.TouchImageViewFling;
import com.tejpratapsingh.pdfcreator.custom.ViewPagerForPhotoView;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.Locale;

public class PDFViewerActivity extends AppCompatActivity {
    private static final String TAG = "PDFViewerActivity";

    public static final String PDF_FILE_URI = "pdfFileUri";

    private File pdfFile = null;

    private static LinkedList<Bitmap> pdfBitmapList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        if (!getIntent().hasExtra(PDF_FILE_URI)) {
            new IllegalStateException("set PdfViewerActivity.PDF_FILE_URI before using PdfViewerActivity").printStackTrace();
            finish();
            return;
        }

        Uri pdfFileUri = getIntent().getParcelableExtra(PDF_FILE_URI);

        if (pdfFileUri == null || pdfFileUri.getPath() == null) {
            new IllegalStateException("pdf File Uri is null").printStackTrace();
            finish();
            return;
        }

        pdfFile = new File(pdfFileUri.getPath());

        if (!pdfFile.exists()) {
            new IllegalStateException("File Does Not Exist.").printStackTrace();
            finish();
            return;
        }
        try {
            pdfBitmapList = PDFUtil.pdfToBitmap(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewPagerForPhotoView viewPager = findViewById(R.id.viewPagerPdfViewer);
        viewPager.setAdapter(new PDFViewerPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    public File getPdfFile() {
        return this.pdfFile;
    }

    private static class PDFViewerPagerAdapter extends FragmentStatePagerAdapter {

        public PDFViewerPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new PdfPageFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(PdfPageFragment.ARG_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return pdfBitmapList.size();
        }
    }

    public static class PdfPageFragment extends Fragment {
        public static final String ARG_POSITION = "position";

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.item_pdf_viewer, container, false);


            Bundle args = getArguments();
            int position = args != null ? args.getInt(ARG_POSITION, 0) : 0;

            Bitmap currentImage = pdfBitmapList.get(position);

            ((TouchImageViewFling) rootView.findViewById(R.id.imageViewItemPdfViewer)).setImageBitmap(currentImage);
            ((AppCompatTextView) rootView.findViewById(R.id.textViewPdfViewerPageNumber)).setText(String.format(Locale.getDefault(), "%d OF %d", position + 1, pdfBitmapList.size()));

            return rootView;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        }
    }

    private static class ZoomOutPageTransformer implements ViewPager.PageTransformer {

        public void transformPage(View view, float position) {

            View viewToAnimateUTB = view.findViewById(R.id.textViewPdfViewerPageNumber);

            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                viewToAnimateUTB.setTranslationY(0);
            } else if (position <= 1) { // [-1,1]
                viewToAnimateUTB.setTranslationY(Math.abs(pageHeight * -position));
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                viewToAnimateUTB.setTranslationY(0);
            }
        }
    }
}