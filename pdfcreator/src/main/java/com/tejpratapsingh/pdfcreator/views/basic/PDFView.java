package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;

public class PDFView implements Serializable {
    private static final String TAG = "PDFView";

    public int paddingTop = 0, paddingRight = 0, paddingBottom = 0, paddingLeft = 0;

    private final ArrayList<PDFView> childViewList = new ArrayList<>();

    private boolean hasParent = false;

    private View currentView = null;

    private final Context context;

    protected PDFView(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public PDFView setView(View view) {
        this.currentView = view;
        return this;
    }

    public PDFView setPadding(int left, int top, int right, int bottom) {
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        this.paddingLeft = left;

        this.currentView.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);

        return this;
    }

    public int getPaddingBottom() {
        return this.currentView.getPaddingBottom();
    }

    public int getPaddingLeft() {
        return this.currentView.getPaddingLeft();
    }

    public int getPaddingRight() {
        return this.currentView.getPaddingRight();
    }

    public int getPaddingTop() {
        return this.currentView.getPaddingTop();
    }

    protected PDFView addView(PDFView viewToAdd) throws IllegalStateException {
        if (viewToAdd.hasParent) {
            throw new IllegalStateException("View already has parent");
        }
        viewToAdd.hasParent = true;
        childViewList.add(viewToAdd);
        return this;
    }

    public ArrayList<PDFView> getChildViewList() {
        return childViewList;
    }

    protected PDFView setLayout(ViewGroup.LayoutParams layoutParams) {
        this.currentView.setLayoutParams(layoutParams);
        return this;
    }

    public View getView() {
        return this.currentView;
    }

    /**
     * Set background color
     *
     * @param color color to set
     * @return PdfView instance
     */
    public PDFView setBackgroundColor(int color) {
        this.currentView.setBackgroundColor(color);
        return this;
    }
}
