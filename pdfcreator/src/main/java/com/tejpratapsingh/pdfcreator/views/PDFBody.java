package com.tejpratapsingh.pdfcreator.views;

import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.Serializable;
import java.util.ArrayList;

public class PDFBody implements Serializable {

    private ArrayList<PDFView> childViewList = new ArrayList<>();

    public PDFBody() {
    }

    public PDFBody addView(PDFView pdfViewToAdd) {
        childViewList.add(pdfViewToAdd);
        return this;
    }

    public ArrayList<PDFView> getChildViewList() {
        return childViewList;
    }
}
