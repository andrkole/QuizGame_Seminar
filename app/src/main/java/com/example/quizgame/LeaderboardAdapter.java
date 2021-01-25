package com.example.quizgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    Context context;
    private ArrayList<Player> players = new ArrayList<>();

    public LeaderboardAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.leaderboard_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.MyViewHolder holder, int position) {
        Player currentPlayer = players.get(position);
        holder.usernameText.setText(currentPlayer.getEmail());
        holder.scoreText.setText(String.valueOf(currentPlayer.getBestScore()));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameText;
        private TextView scoreText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.textUsername);
            scoreText = itemView.findViewById(R.id.textLeaderScore);
        }
    }
}
