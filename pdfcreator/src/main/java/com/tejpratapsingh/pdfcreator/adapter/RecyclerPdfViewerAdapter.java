package com.tejpratapsingh.pdfcreator.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tejpratapsingh.pdfcreator.R;
import com.tejpratapsingh.pdfcreator.custom.TouchImageView;

import java.util.LinkedList;
import java.util.Locale;

public class RecyclerPdfViewerAdapter extends RecyclerView.Adapter<RecyclerPdfViewerAdapter.ViewHolderPdfPage> {
    private static final String TAG = "RecyclerPdfViewerAdapte";

    private final LinkedList<Bitmap> pdfPagesImage = new LinkedList<>();

    public RecyclerPdfViewerAdapter(@NonNull LinkedList<Bitmap> pdfPagesImage) {
        this.pdfPagesImage.addAll(pdfPagesImage);
    }

    @NonNull
    @Override
    public RecyclerPdfViewerAdapter.ViewHolderPdfPage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pdf_viewer, parent, false);
        return new ViewHolderPdfPage(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerPdfViewerAdapter.ViewHolderPdfPage holder, int position) {
        Log.d(TAG, "onBindViewHolder: rendering: " + position);
        Bitmap currentImage = pdfPagesImage.get(position);

        holder.imageView.setImageBitmap(currentImage);
        holder.textViewPageNumber.setText(String.format(Locale.getDefault(), "%d OF %d", position + 1, this.pdfPagesImage.size()));
    }

    @Override
    public int getItemCount() {
        return this.pdfPagesImage.size();
    }

    static class ViewHolderPdfPage extends RecyclerView.ViewHolder {

        TouchImageView imageView;
        TextView textViewPageNumber;

        ViewHolderPdfPage(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewItemPdfViewer);
            textViewPageNumber = itemView.findViewById(R.id.textViewPdfViewerPageNumber);
        }
    }
}
