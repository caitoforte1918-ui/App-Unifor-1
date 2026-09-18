package com.lithub.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lithub.app.R;
import com.lithub.app.dataclass.ImageBook;
import com.lithub.app.student.BookDetailsActivity;

import java.util.List;

public class MyBooksListAdapter extends RecyclerView.Adapter<MyBooksListAdapter.ViewHolder> {

    private List<ImageBook> imageBooks;
    private Context context;

    public MyBooksListAdapter(Context context, List<ImageBook> imageBooks) {
        this.context = context;
        this.imageBooks = imageBooks;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titulo;

        public ViewHolder(@NonNull View view) {
            super(view);

            imageView = view.findViewById(R.id.imageView);
            titulo = view.findViewById(R.id.textTituloLivro);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ImageBook images = imageBooks.get(position);

        holder.imageView.setImageResource(images.getImage());
        holder.titulo.setText(images.getTitle());

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, BookDetailsActivity.class);

            intent.putExtra("tituloLivro", images.getTitle());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageBooks.size();
    }
}