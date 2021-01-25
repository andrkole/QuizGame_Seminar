package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameOverActivity extends AppCompatActivity {

    private TextView congratsText;
    private TextView scoreNumText;
    private Button redirectToHomeButton;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    LottieAnimationView starView;
    LottieAnimationView sadStarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        congratsText = findViewById(R.id.textCongrats);
        scoreNumText = findViewById(R.id.textEndScoreNum);
        redirectToHomeButton = findViewById(R.id.buttonEndToHome);
        database = FirebaseDatabase.getInstance("https://quizgame-fe94f-default-rtdb.europe-west1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        starView = findViewById(R.id.starAnimationView);
        sadStarView = findViewById(R.id.sadStarAnimationView);

        Intent intent = getIntent();
        int score = intent.getIntExtra("playerScore", 0);
        scoreNumText.setText(String.valueOf(score));

        saveUserScoreToDb(score);

        if (score > 0) {
            startAnimation();
            sadStarView.setVisibility(View.GONE);
            starView.playAnimation();
        } else {
            startAnimation();
            congratsText.setText("Desi se i najboljima :(");
            congratsText.setTextSize(18);
            starView.setVisibility(View.GONE);
            sadStarView.playAnimation();
        }

        redirectToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.congrats_anim);
        congratsText.startAnimation(animation);
    }

    private void saveUserScoreToDb(int score) {
        String playerId = mAuth.getCurrentUser().getUid();
        DatabaseReference playerScoreRef = database.getReference().child("players").child(playerId).child("bestScore");

        playerScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Long currentScore = snapshot.getValue(Long.class);
                if (currentScore < score) {
                    playerScoreRef.setValue(score);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("FirebaseMainActivity", "Failed to read from database", error.toException());
            }
        });
    }

}
