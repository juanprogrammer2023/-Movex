package com.example.myapplicationhector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentsList;

    public CommentsAdapter(Context context, List<Comment> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentsList.get(position);

        // Asignar nombre del usuario
        holder.commentUserName.setText(comment.getUserName());

        // Asignar texto del comentario
        holder.commentText.setText(comment.getText());

        // Asignar fecha del comentario
        holder.commentDate.setText(formatDate(comment.getCreatedAt()));

        // Cargar imagen de perfil con Glide
        Glide.with(context)
                .load(comment.getUserImageUrl())
                .error(R.drawable.iconamoon__profile_circle_fill__1_)
                .circleCrop()
                .into(holder.commentUserImage);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    // Formato de fecha
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return targetFormat.format(originalFormat.parse(dateString));
        } catch (Exception e) {
            e.printStackTrace();
            return dateString;
        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView commentUserImage;
        TextView commentUserName;
        TextView commentDate;
        TextView commentText;
        ImageButton btnDeleteComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUserImage = itemView.findViewById(R.id.commentProfileImageView);
            commentUserName = itemView.findViewById(R.id.commentUserName);
            commentDate = itemView.findViewById(R.id.commentDate);
            commentText = itemView.findViewById(R.id.commentText);
            btnDeleteComment = itemView.findViewById(R.id.btnDeleteComment);
        }
    }
}
