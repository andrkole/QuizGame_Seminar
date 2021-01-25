package com.example.quizgame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference rootRef;
    private TextView question;
    private TextView questionCount;
    private TextView timerText;
    private Button buttonAns1;
    private Button buttonAns2;
    private Button buttonAns3;
    private PhoneStateListener phoneStateListener;

    private CountDownTimer timer;
    private List<Question> questionsAnswers;
    private String correctAnswer;
    private int correctAnswerCount = 0;
    private int questionCounter = 1;
    static final private int TOTAL_QUESTIONS = 5;
    static final private int PLAYTIME_IN_SECONDS = 30;
    int secondsPassed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        database = FirebaseDatabase.getInstance("https://quizgame-fe94f-default-rtdb.europe-west1.firebasedatabase.app/");
        rootRef = database.getReference();
        question = findViewById(R.id.textQuestion);
        questionCount = findViewById(R.id.textQuestionCount);
        timerText = findViewById(R.id.textTimer);
        buttonAns1 = findViewById(R.id.buttonAns1);
        buttonAns2 = findViewById(R.id.buttonAns2);
        buttonAns3 = findViewById(R.id.buttonAns3);

        timer = new CountDownTimer(PLAYTIME_IN_SECONDS*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(Long.toString(millisUntilFinished/1000));
                secondsPassed += 1;
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Vrijeme isteklo!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra("playerScore", correctAnswerCount);
                startActivity(intent);
                cancelTimer();
                finish();
            }
        };

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        cancelTimer();
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        resetTimer(PLAYTIME_IN_SECONDS - secondsPassed);
                        break;
                }
            }
        };

        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        startGame();
    }


    private void startGame() {
        timer.start();
        questionsAnswers = new ArrayList<Question>();
        DatabaseReference myRef = rootRef.child("questions");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Question questionAnswers = dataSnapshot.getValue(Question.class);
                    questionsAnswers.add(questionAnswers);
                }
                loadQuestion();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("FirebaseMainActivity", "Failed to read from database", error.toException());
            }
        });

    }

    private void loadQuestion() {

        if (questionsAnswers.isEmpty()) {
            Intent intent = new Intent(this, GameOverActivity.class);
            int secondsLeft = PLAYTIME_IN_SECONDS - secondsPassed;
            intent.putExtra("playerScore", correctAnswerCount*secondsLeft);
            startActivity(intent);
            cancelTimer();
            finish();
        } else {
            int randomIndex = new Random().nextInt(questionsAnswers.size());
            Question randomQuestion = questionsAnswers.get(randomIndex);
            ArrayList shuffledAnswers = shuffleAnswers(randomQuestion);

            questionCount.setText(questionCounter + "/" + TOTAL_QUESTIONS);
            question.setText(randomQuestion.getQuestion());
            buttonAns1.setText(shuffledAnswers.get(0).toString());
            buttonAns2.setText(shuffledAnswers.get(1).toString());
            buttonAns3.setText(shuffledAnswers.get(2).toString());
            correctAnswer = randomQuestion.getCorrectAnswer();

            questionsAnswers.remove(randomIndex);
        }
    }

    private ArrayList shuffleAnswers(Question randomQuestion) {
        ArrayList answersList = new ArrayList();
        answersList.add(randomQuestion.getAnswer1());
        answersList.add(randomQuestion.getAnswer2());
        answersList.add(randomQuestion.getAnswer3());
        Collections.shuffle(answersList);

        return answersList;
    }

    public void checkAnswer(View view) {
        Button buttonClicked = findViewById(view.getId());
        String answerClicked = buttonClicked.getText().toString();

        if (answerClicked.equals(correctAnswer)) {
            correctAnswerCount++;
            buttonClicked.setBackground(ContextCompat.getDrawable(this, R.drawable.gradient_correct_answer));

        } else {
            buttonClicked.setBackground(ContextCompat.getDrawable(this, R.drawable.gradient_wrong_answer));
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonClicked.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gradient_buttons));
            }
        }, 1000);

        questionCounter++;
        (new Handler()).postDelayed(this::loadQuestion, 1000);
    }

    void resetTimer(int timeInSecs) {
        cancelTimer();
        timer = new CountDownTimer(timeInSecs*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(Long.toString(millisUntilFinished/1000));
                secondsPassed += 1;
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Vrijeme isteklo!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra("playerScore", correctAnswerCount);
                startActivity(intent);
                cancelTimer();
                finish();
            }
        };
        timer.start();
    }

    void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        } else {
            Log.d("heheh", "benz ili bimmaa");
        }

    }

}
