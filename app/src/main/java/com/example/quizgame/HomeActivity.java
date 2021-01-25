package com.example.quizgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference rootRef;
    private Button logoutButton;
    private Button startGameButton;
    private Button leaderboardButton;
    public TextView bestScoreNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://quizgame-fe94f-default-rtdb.europe-west1.firebasedatabase.app/");
        rootRef = database.getReference();
        logoutButton = findViewById(R.id.buttonLogout);
        startGameButton = findViewById(R.id.buttonStartGame);
        bestScoreNum = findViewById(R.id.textBestScoreNum);
        leaderboardButton = findViewById(R.id.buttonLeaderboard);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_PHONE_STATE}, 100);
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LeaderboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        String playerId = mAuth.getCurrentUser().getUid();
        String playerEmail = mAuth.getCurrentUser().getEmail();
        DatabaseReference playerRef = rootRef.child("players").child(playerId);

        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    bestScoreNum.setText(snapshot.child("bestScore").getValue(Long.class).toString());

                } else {
                    playerRef.child("bestScore").setValue(Integer.parseInt(bestScoreNum.getText().toString()));
                    playerRef.child("email").setValue(playerEmail);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("FirebaseMainActivity", "Failed to read player from database", error.toException());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Prava dana!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Prava nisu dana!", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

}