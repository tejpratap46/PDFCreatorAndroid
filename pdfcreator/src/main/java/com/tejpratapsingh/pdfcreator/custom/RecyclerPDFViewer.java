package com.tejpratapsingh.pdfcreator.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.tejpratapsingh.pdfcreator.adapter.RecyclerPdfViewerAdapter;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.util.LinkedList;

public class RecyclerPDFViewer extends RecyclerView {
    public RecyclerPDFViewer(@NonNull Context context) {
        super(context);
    }

    public RecyclerPDFViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerPDFViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void loadPdf(File pdfFile) {
        try {
            LinkedList<Bitmap> pdfBitmapList = PDFUtil.pdfToBitmap(pdfFile);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            this.setLayoutManager(layoutManager);
            this.setAdapter(new RecyclerPdfViewerAdapter(pdfBitmapList));

            this.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
