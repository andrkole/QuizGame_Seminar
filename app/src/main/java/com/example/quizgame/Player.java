package com.example.quizgame;

public class Player implements Comparable<Player>{

    private String email;
    private Long bestScore;

    public Player() {}

    public Player(String email, Long bestScore) {
        this.email = email;
        this.bestScore = bestScore;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getBestScore() {
        return bestScore;
    }

    public void setBestScore(Long bestScore) {
        this.bestScore = bestScore;
    }

    @Override
    public int compareTo(Player other) {
        return Long.compare(bestScore, other.bestScore);
    }
}
