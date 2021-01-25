package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    FirebaseDatabase database;
    private RecyclerView leaderboardRecycler;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_recycler_activity);

        database = FirebaseDatabase.getInstance("https://quizgame-fe94f-default-rtdb.europe-west1.firebasedatabase.app/");
        closeButton = findViewById(R.id.buttonCloseLeaderboard);
        leaderboardRecycler = findViewById(R.id.recyclerViewLeaderboard);
        LeaderboardAdapter leaderboardAdapter = new LeaderboardAdapter(this);
        leaderboardRecycler.setAdapter(leaderboardAdapter);
        leaderboardRecycler.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference playerRef = database.getReference().child("players");
        ArrayList<Player> players = new ArrayList<>();

        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Player player = dataSnapshot.getValue(Player.class);
                    players.add(player);
                }

                Collections.sort(players, Collections.reverseOrder());

                if (players.size() > 10) {
                    players.subList(9, players.size()).clear();
                }

                leaderboardAdapter.setPlayers(players);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FirebaseMainActivity", "Failed to read from database", error.toException());
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaderboardActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
