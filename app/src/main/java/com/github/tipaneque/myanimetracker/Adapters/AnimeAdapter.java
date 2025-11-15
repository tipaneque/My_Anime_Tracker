package com.github.tipaneque.myanimetracker.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.tipaneque.myanimetracker.R;
import com.github.tipaneque.myanimetracker.models.Anime;
import com.github.tipaneque.myanimetracker.models.AnimeNode;
import com.github.tipaneque.myanimetracker.models.ListStatus;
import com.github.tipaneque.myanimetracker.models.UserAnimeListItem;

import java.util.ArrayList;
import java.util.List;


public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {

    private List<Object> animeList; // ✅ MUDAR para List<Object> para suportar addData
    private OnItemClickListener listener;
    private boolean showStatusBadge = false;

    public interface OnItemClickListener {
        void onItemClick(Anime anime);
    }

    public AnimeAdapter(List<?> animeList) {
        // ✅ Converter para List<Object> mantendo os dados
        this.animeList = animeList != null ? new ArrayList<Object>(animeList) : new ArrayList<Object>();
    }

    public AnimeAdapter(List<?> animeList, boolean showStatusBadge) {
        this.animeList = animeList != null ? new ArrayList<Object>(animeList) : new ArrayList<Object>();
        this.showStatusBadge = showStatusBadge;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // ✅ MÉTODO ORIGINAL - Substitui toda a lista
    public void updateData(List<?> newAnimeList) {
        this.animeList.clear();
        if (newAnimeList != null) {
            this.animeList.addAll(newAnimeList);
        }
        notifyDataSetChanged();
        Log.d("AnimeAdapter", "Data updated with " + (newAnimeList != null ? newAnimeList.size() : 0) + " items. Total: " + animeList.size());
    }

    // ✅ NOVO MÉTODO - Adiciona dados à lista existente (PAGINAÇÃO)
    public void addData(List<?> newAnimeList) {
        if (newAnimeList != null && !newAnimeList.isEmpty()) {
            int startPosition = animeList.size();
            this.animeList.addAll(newAnimeList);
            notifyItemRangeInserted(startPosition, newAnimeList.size());
            Log.d("AnimeAdapter", "Added " + newAnimeList.size() + " items. Total: " + animeList.size());
        } else {
            Log.d("AnimeAdapter", "No new items to add");
        }
    }

    // ✅ MÉTODO para obter a quantidade total de itens
    public int getTotalItemCount() {
        return animeList != null ? animeList.size() : 0;
    }

    // ✅ MÉTODO para limpar a lista
    public void clearData() {
        int oldSize = animeList.size();
        animeList.clear();
        notifyItemRangeRemoved(0, oldSize);
        Log.d("AnimeAdapter", "Cleared all data. Previous size: " + oldSize);
    }

    public void setShowStatusBadge(boolean showStatusBadge) {
        this.showStatusBadge = showStatusBadge;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_anime, parent, false);
        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        Anime anime = getAnimeFromPosition(position);
        if (anime != null) {
            holder.bind(anime, listener, showStatusBadge);

            // Se for UserAnimeListItem, obter o status da lista
            if (showStatusBadge && animeList.get(position) instanceof UserAnimeListItem) {
                UserAnimeListItem userItem = (UserAnimeListItem) animeList.get(position);
                ListStatus listStatus = userItem.getListStatus();
                if (listStatus != null) {
                    holder.setStatusBadge(listStatus.getStatus());
                }
            }
        } else {
            Log.e("AnimeAdapter", "Anime is null at position: " + position);
        }
    }

    private Anime getAnimeFromPosition(int position) {
        if (animeList == null || animeList.isEmpty() || position >= animeList.size()) {
            return null;
        }

        Object item = animeList.get(position);
        if (item instanceof AnimeNode) {
            return ((AnimeNode) item).getNode();
        } else if (item instanceof UserAnimeListItem) {
            return ((UserAnimeListItem) item).getNode();
        } else if (item instanceof Anime) {
            return (Anime) item;
        }

        Log.w("AnimeAdapter", "Unknown item type: " + (item != null ? item.getClass().getSimpleName() : "null"));
        return null;
    }

    @Override
    public int getItemCount() {
        return animeList != null ? animeList.size() : 0;
    }

    // ✅ ViewHolder (mantém igual - já está ótimo!)
    static class AnimeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAnime, ivFavorite;
        private TextView tvTitle, tvSynopsis, tvScore, tvType, tvEpisodes, tvStatusBadge;

        public AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAnime = itemView.findViewById(R.id.ivAnime);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSynopsis = itemView.findViewById(R.id.tvSynopsis);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvType = itemView.findViewById(R.id.tvType);
            tvEpisodes = itemView.findViewById(R.id.tvEpisodes);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }

        public void bind(Anime anime, OnItemClickListener listener, boolean showStatusBadge) {
            tvTitle.setText(anime.getTitle());

            if (anime.getSynopsis() != null && !anime.getSynopsis().isEmpty()) {
                String synopsis = anime.getSynopsis();
                if (synopsis.length() > 100) {
                    synopsis = synopsis.substring(0, 100) + "...";
                }
                tvSynopsis.setText(synopsis);
            } else {
                tvSynopsis.setText("No synopsis available");
            }

            if (anime.getMean() != null) {
                tvScore.setText(String.format("%.2f", anime.getMean()));
            } else {
                tvScore.setText("N/A");
            }

            if (anime.getMedia_type() != null) {
                tvType.setText(anime.getMedia_type());
            } else {
                tvType.setText("Unknown");
            }

            if (anime.getEpisodes() != null) {
                tvEpisodes.setText(anime.getEpisodes() + " eps");
            } else {
                tvEpisodes.setText("? eps");
            }

            if (anime.getMain_picture() != null && anime.getMain_picture().getMedium() != null) {
                Glide.with(itemView.getContext())
                        .load(anime.getMain_picture().getMedium())
                        .placeholder(R.drawable.ic_anime_placeholder)
                        .error(R.drawable.ic_anime_placeholder)
                        .into(ivAnime);
            } else {
                ivAnime.setImageResource(R.drawable.ic_anime_placeholder);
            }

            if (showStatusBadge) {
                tvStatusBadge.setVisibility(View.VISIBLE);
            } else {
                tvStatusBadge.setVisibility(View.GONE);
            }

            ivFavorite.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                Log.d("AnimeAdapter", "Item clicked: " + anime.getTitle());
                if (listener != null) {
                    listener.onItemClick(anime);
                }
            });

            itemView.setOnLongClickListener(v -> {
                Log.d("AnimeAdapter", "Item long clicked: " + anime.getTitle());
                return true;
            });
        }

        public void setStatusBadge(String status) {
            if (status == null || tvStatusBadge == null) {
                return;
            }

            tvStatusBadge.setVisibility(View.VISIBLE);

            switch (status.toLowerCase()) {
                case "watching":
                    tvStatusBadge.setText("Watching");
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_status_watching);
                    break;
                case "completed":
                    tvStatusBadge.setText("Completed");
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_status_completed);
                    break;
                case "on_hold":
                    tvStatusBadge.setText("On Hold");
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_status_on_hold);
                    break;
                case "dropped":
                    tvStatusBadge.setText("Dropped");
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_status_dropped);
                    break;
                case "plan_to_watch":
                    tvStatusBadge.setText("Plan to Watch");
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_status_plan_to_watch);
                    break;
                default:
                    tvStatusBadge.setVisibility(View.GONE);
            }
        }
    }
}