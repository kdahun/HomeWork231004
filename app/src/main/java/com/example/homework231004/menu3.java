package com.example.homework231004;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class menu3 extends AppCompatActivity {

    static int dx=0,dy=0;
    static int cx,r;
    static int cy, layoutWidth,layoutHeight;
    static int w;
    static int cirSpeed=2;
    static int wValue=0;
    LinearLayout layout;
    SensorManager sensorManager;
    Sensor acc;
    static TextView textView,textViewStage;
    menu3.MyGrapicView myGrapicView;
    Button btn1,btn2;
    RatingBar ratingBar;
    SeekBar seekBar;

    static boolean togle=true; // 메시지 박스가 떳을때 원이 움직이지 않도록
    static boolean timeGo = true; // 시간 초과했을때 메시지 박스가 계속 중첩되는 것을 막기위해
    static int gameTime = 30; // seekbar 최대 크기
    public Timer timerCall;
    private int nCnt;
    static SeekbarAndRatingbar seekbarAndRatingbar;
    static Rect rect[] = new Rect[6];
    static int Hp = 10;
    static boolean hpZero = true;

    static boolean nextStage = false;

    static boolean inside;
    static int threadTime = 5000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu3);

        // 화면 꺼짐 막기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 센서 메니저 만들어 놓기
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        textViewStage= (TextView) findViewById(R.id.menu3_textView2);

        acc=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        layout = (LinearLayout) findViewById(R.id.menu3_viewlayout);
        textView = (TextView) findViewById(R.id.menu3_textView);


        btn1 = (Button) findViewById(R.id.menu3_button);
        btn2 = (Button) findViewById(R.id.menu3_button2);

        ratingBar = (RatingBar) findViewById(R.id.menu3_ratingBar);
        seekBar = (SeekBar) findViewById(R.id.menu3_seekBar);

        seekbarAndRatingbar = new SeekbarAndRatingbar(ratingBar,seekBar);


        myGrapicView= new menu3.MyGrapicView(this);
        layout.addView(myGrapicView);

        seekBar.setProgress(10);

        btn1.setOnClickListener(new View.OnClickListener() {// 이전 스테이지 버튼
            @Override
            public void onClick(View v) {

                Random random = new Random();
                for(int i=0;i<7-cirSpeed/2;i++){
                    rect[i].setX(random.nextInt(layoutWidth-2*w)+w);
                    rect[i].setY(random.nextInt(layoutHeight-2*w)+w);
                }
                if(cirSpeed>2){
                    cirSpeed-=2;

                    wValue+=10;
                    gameTime+=5;// 시간 감소

                    ratingBar.setRating(cirSpeed/2);
                    textViewStage.setText(Integer.toString(cirSpeed/2)+"단계");
                    nCnt=0;
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() { // 다음 스테이지 버튼
            @Override
            public void onClick(View v) {
                for(int i=0;i<7-cirSpeed/2;i++){

                    Random random = new Random();
                    rect[i].setX(random.nextInt(layoutWidth-2*w)+w);
                    rect[i].setY(random.nextInt(layoutHeight-2*w)+w);
                }
                if(cirSpeed<12){
                    cirSpeed+=2;

                    wValue-=10;
                    gameTime-=5;// 시간 감소

                    ratingBar.setRating(cirSpeed/2);
                    textViewStage.setText(Integer.toString(cirSpeed/2)+"단계");
                    nCnt=0;
                }
            }
        });


        for(int i=0;i<6;i++){
            rect[i] = new Rect();
        }
        MyThread myThread = new MyThread();
        myThread.start();
    }



    private class MyThread extends Thread{
        public void run(){ // 동작할 내용 기술
            while(true){
                if(timeGo){
                    try {
                        Thread.sleep(threadTime);

                        layout.setBackgroundColor(Color.RED);
                        if (!inside) {
                            Hp -= 2;
                            seekBar.setProgress(Hp);

                        }

                        if (threadTime != 1000) {
                            threadTime -= 1000;
                        } else {
                            nextStage = true;
                        }
                        Thread.sleep(100);
                        layout.setBackgroundColor(Color.WHITE);

                        Random random = new Random();
                        for (int i = 0; i < 7 - cirSpeed / 2; i++) {
                            rect[i].setX(random.nextInt(layoutWidth - 2 * w) + w);
                            rect[i].setY(random.nextInt(layoutHeight - 2 * w) + w);
                        }

                        myGrapicView.invalidate();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener,acc,sensorManager.SENSOR_DELAY_NORMAL);
    }

    final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //sensorEvent.valuse는 기본적으로 float이고
            //여러개(거의3개)의 정보가 넘어와 배열로 넘어온다
            if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

                dx=-(int)(sensorEvent.values[0]*cirSpeed*1.5);
                dy=(int)(sensorEvent.values[1]*cirSpeed*1.5);

                if((cx-40+dx)>0&&(cx+40+dx<layoutWidth)&&togle){
                    cx=cx+dx;
                }
                if((cy-40+dy)>0&&(cy+40+dy<layoutHeight)&&togle){
                    cy=cy+dy;
                }

                myGrapicView.invalidate();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        layoutHeight = layout.getHeight();
        layoutWidth = layout.getWidth();
        cx = layout.getWidth()/2;
        cy = layout.getHeight()/2;
        r = (int)(layoutHeight*0.02);

        Random random = new Random();

        w=r*2;

        for(int i=0;i<7-cirSpeed/2;i++){
            rect[i].setX(random.nextInt(layoutWidth-2*w)+w);
            rect[i].setY(random.nextInt(layoutHeight-2*w)+w);
        }

        myGrapicView.invalidate();
    }

    private class MyGrapicView extends View {
        public MyGrapicView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint rectPaint[] = new Paint[6];

            for(int i=0;i<7-cirSpeed/2;i++){
                rectPaint[i] = new Paint();
                rectPaint[i].setColor(Color.BLUE);
                canvas.drawCircle(rect[i].getX(),rect[i].getY(),w,rectPaint[i]);
            }

            Paint paint = new Paint();
            paint.setColor(Color.RED);

            canvas.drawCircle(cx,cy,r,paint);



            for(int j=0;j<7-cirSpeed/2;j++){
                if((cx-rect[j].getX())*(cx-rect[j].getX())+(cy-rect[j].getY())*(cy-rect[j].getY())<=r*r){
                    inside = true;
                    textView.setText(j+"도착함");
                    break;

                }else{
                    textView.setText("안들어옴");
                    inside = false;
                }
            }
            if(Hp==0&&hpZero){
                hpZero=false;
                AlertDialog.Builder dlg = new AlertDialog.Builder(menu3.this);
                dlg.setTitle("게임 오버");
                dlg.setMessage("잘 좀 피해보세요.");
                dlg.setPositiveButton("다시하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Hp=10;
                        seekBar.setProgress(Hp);
                        hpZero=true;
                        threadTime=5000;
                    }
                });
                dlg.show();
            }


            if(nextStage&&timeGo){
                nextStage=false;
                timeGo=false;

                if ((cirSpeed / 2) == 6) { // 6단계일때 massageBox
                    AlertDialog.Builder dlg = new AlertDialog.Builder(menu3.this);
                    dlg.setTitle("도착!!");
                    dlg.setMessage("목적지에 도착하셨습니다.");
                    dlg.setPositiveButton("종료하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            timeGo = true;

                            cirSpeed=2;
                            wValue=0;
                            finish();
                        }
                    });
                    dlg.setNegativeButton("다시하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Hp=10;
                            seekBar.setProgress(Hp);
                            threadTime=5000;
                            timeGo = true;
                        }
                    });

                }else{
                    AlertDialog.Builder dlg = new AlertDialog.Builder(menu3.this);
                    dlg.setTitle("성공!");
                    dlg.setMessage("성공하셨어요!");
                    dlg.setNegativeButton("다시하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Hp=10;
                            seekBar.setProgress(Hp);
                            threadTime=5000;
                            timeGo = true;
                        }
                    });
                    dlg.setPositiveButton("다음 스테이지", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cirSpeed+=2;

                            wValue-=10;
                            gameTime-=5;// 시간 감소
                            threadTime=5000;
                            ratingBar.setRating(cirSpeed/2);
                            textViewStage.setText(Integer.toString(cirSpeed/2)+"단계");
                            Hp=10;
                            seekBar.setProgress(Hp);
                            timeGo = true;
                        }
                    });
                    dlg.show();
                }

            }

        }
    }
}