package com.tejpratapsingh.pdfcreator.views;

import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.Serializable;
import java.util.ArrayList;

public class PDFBody implements Serializable {

    private final ArrayList<PDFView> childViewList = new ArrayList<>();

    public PDFBody() {
    }

    public PDFBody addView(PDFView pdfViewToAdd) {
        if (pdfViewToAdd instanceof PDFTableView) {
            childViewList.addAll(pdfViewToAdd.getChildViewList());
        } else {
            childViewList.add(pdfViewToAdd);
        }
        return this;
    }

    public ArrayList<PDFView> getChildViewList() {
        return childViewList;
    }
}
