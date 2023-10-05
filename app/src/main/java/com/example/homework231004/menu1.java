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

public class menu1 extends AppCompatActivity {


    static int dx=0,dy=0;
    static int cx,r;
    static int cy, layoutWidth,layoutHeight;
    static int rx, ry,w;
    static int cirSpeed=2;
    static int wValue=0;
    LinearLayout layout;
    SensorManager sensorManager;
    Sensor acc;
    static TextView textView,textViewStage;
    MyGrapicView myGrapicView;
    Button btn1,btn2;
    RatingBar ratingBar;
    SeekBar seekBar;

    static boolean togle=true; // 메시지 박스가 떳을때 원이 움직이지 않도록
    static boolean timeGo = true; // 시간 초과했을때 메시지 박스가 계속 중첩되는 것을 막기위해
    static int gameTime = 30; // seekbar 최대 크기
    public Timer timerCall;
    private int nCnt;
    static SeekbarAndRatingbar seekbarAndRatingbar;

    public void someWork(){
        Log.d("test==>",nCnt+"work!!");
        if(timeGo) {
            nCnt++;
            seekBar.setProgress(nCnt);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);

        // 화면 꺼짐 막기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 센서 메니저 만들어 놓기
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        textViewStage= (TextView) findViewById(R.id.menu1_textView2);

        acc=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        layout = (LinearLayout) findViewById(R.id.menu1_viewlayout);
        textView = (TextView) findViewById(R.id.menu1_textView);


        btn1 = (Button) findViewById(R.id.menu1_button);
        btn2 = (Button) findViewById(R.id.menu1_button2);

        ratingBar = (RatingBar) findViewById(R.id.menu1_ratingBar);
        seekBar = (SeekBar) findViewById(R.id.menu1_seekBar);

        seekbarAndRatingbar = new SeekbarAndRatingbar(ratingBar,seekBar);


        myGrapicView= new MyGrapicView(this);
        layout.addView(myGrapicView);

        btn1.setOnClickListener(new View.OnClickListener() {// 이전 스테이지 버튼
            @Override
            public void onClick(View v) {
                if(cirSpeed>2){
                    cirSpeed-=2;

                    wValue+=10;
                    gameTime+=5;// 시간 감소
                    seekbarAndRatingbar.valuePlusMinus(gameTime,cirSpeed/2);

                    textViewStage.setText(Integer.toString(cirSpeed/2)+"단계");
                    nCnt=0;
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() { // 다음 스테이지 버튼
            @Override
            public void onClick(View v) {
                if(cirSpeed<12){
                    cirSpeed+=2;

                    wValue-=10;
                    gameTime-=5;// 시간 감소
                    seekbarAndRatingbar.valuePlusMinus(gameTime,cirSpeed/2);

                    textViewStage.setText(Integer.toString(cirSpeed/2)+"단계");
                    nCnt=0;
                }
            }
        });
        // 타이머 1초마다 timerTask메서드로 들어감
        nCnt=0;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                someWork();
            }
        };
        timerCall = new Timer();
        timerCall.schedule(timerTask, 0, 1000);
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

        w=r*4;
        rx = random.nextInt(layoutWidth-w);
        ry = random.nextInt(layoutHeight-w);

        myGrapicView.invalidate();
    }

    private class MyGrapicView extends View {
        public MyGrapicView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint1 = new Paint();
            paint1.setColor(Color.BLUE);

            Paint paint = new Paint();
            paint.setColor(Color.RED);

            canvas.drawRect(rx,ry,rx+w+wValue,ry+w+wValue,paint1);
            canvas.drawCircle(cx,cy,r,paint);

            // 시간 초과 했을 때
            if(nCnt>gameTime&&timeGo){
                timeGo = false;
                togle = false;
                AlertDialog.Builder dlg = new AlertDialog.Builder(menu1.this);
                dlg.setTitle("시간 초과");
                dlg.setMessage("시간을 초과하셨습니다.\n\n주의 : PositiveButton을 누르지 않으면 작동 안됨.");
                dlg.setPositiveButton("다시하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        togle = true;
                        timeGo = true;
                        nCnt=0;
                    }
                });
                dlg.show();
            }

            // 정사각형 안으로 들어왔을 때
            if(cx-r>rx&&cx+r<rx+w&&cy-r>ry&&cy+r<ry+w){
                timeGo=false;
                togle = false;
                textView.setText("도착함");
                AlertDialog.Builder dlg = new AlertDialog.Builder(menu1.this);
                dlg.setTitle("도착!!");
                dlg.setMessage("목적지에 도착하셨습니다.");

                if((cirSpeed/2) == 6) { // 6단계일때 massageBox
                    dlg.setPositiveButton("종료하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            timeGo = true;
                            togle = true;

                            gameTime = 30;
                            cirSpeed=2;
                            wValue=0;

                            finish();
                        }
                    });
                    dlg.setNegativeButton("다시하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            togle=true;
                            timeGo=true;
                            nCnt=0;
                        }
                    });

                }else{ // 6단계전 massageBox
                    dlg.setPositiveButton("다음 스테이지", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (cirSpeed < 12) { // 5단계 이하 일때
                                cirSpeed += 2;// 속도 증가


                                gameTime-=5;// 시간 감소
                                wValue-=10;
                                seekbarAndRatingbar.valuePlusMinus(gameTime,cirSpeed/2);

                                textViewStage.setText(Integer.toString(cirSpeed / 2) + "단계"); // 몇 단계 인지
                                nCnt=0;
                            }

                            timeGo=true;
                            togle=true;
                        }
                    });
                    dlg.setNegativeButton("다시하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            timeGo=true;
                            togle=true;
                            nCnt=0;
                        }
                    });
                }
                dlg.show();
            }else{
                textView.setText("안들어옴");
            }
        }
    }
}