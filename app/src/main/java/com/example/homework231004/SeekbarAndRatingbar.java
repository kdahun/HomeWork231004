package com.example.homework231004;

import android.widget.RatingBar;
import android.widget.SeekBar;

public class SeekbarAndRatingbar {
    RatingBar ratingBar;
    SeekBar seekBar;
    public SeekbarAndRatingbar(RatingBar r, SeekBar s){
        ratingBar = r;
        seekBar = s;
    }

    public void valuePlusMinus(int gameTime,int stage){

        seekBar.setMax(gameTime); // seekbar 최대 크기 감소
        ratingBar.setRating(stage);

    }

}
