package com.gitlab.bluestring.myanimetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.gitlab.bluestring.myanimetracker.R;
import com.gitlab.bluestring.myanimetracker.model.Post;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts = new ArrayList<>();
    private OnItemClickListener listener;

    public void setPosts(List<Post> posts) {
        this.posts = posts != null ? posts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView postImageView;
        private TextView scoreTextView;
        private TextView tagsTextView;
        private ImageView favoriteIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postImageView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            tagsTextView = itemView.findViewById(R.id.tagsTextView);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(posts.get(position));
                    }
                }
            });

            favoriteIcon.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onFavoriteClick(posts.get(position));
                    }
                }
            });
        }

        public void bind(Post post) {
            // Carregar imagem com Glide
            if (post.getFileUrl() != null && !post.getFileUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(post.getFileUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(postImageView);
            } else {
                postImageView.setImageResource(R.drawable.placeholder);
            }

            scoreTextView.setText("Score: " + post.getScore());

            // Limitar tamanho das tags
            String tags = post.getTagString();
            if (tags != null && tags.length() > 50) {
                tags = tags.substring(0, 50) + "...";
            }
            tagsTextView.setText(tags != null ? tags : "No tags");

            // Atualizar ícone de favorito
            int favoriteRes = (post.getIsFavorited() != null && post.getIsFavorited()) ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline;
            favoriteIcon.setImageResource(favoriteRes);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Post post);
        void onFavoriteClick(Post post);
    }
}