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

public class menu2 extends AppCompatActivity {


    static int dx=0,dy=0;
    static int cx,r;
    static int cy, layoutWidth,layoutHeight;
    static int rx, ry,w;
    static int cirSpeed=2;
    static int wValue=0;
    static int enemyHight;
    LinearLayout layout;
    SensorManager sensorManager;
    Sensor acc;
    static TextView textView,textViewStage;
    menu2.MyGrapicView myGrapicView;
    Button btn1,btn2;
    RatingBar ratingBar;
    SeekBar seekBar;

    static boolean togle=true; // 메시지 박스가 떳을때 원이 움직이지 않도록
    static boolean timeGo = true; // 시간 초과했을때 메시지 박스가 계속 중첩되는 것을 막기위해
    static boolean arrivalDialogShown = false;
    static int gameTime = 40; // seekbar 최대 크기
    public Timer timerCall;
    private int nCnt;
    static EnemyCircle enemyCircle[] = new EnemyCircle[6];
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
        setContentView(R.layout.activity_menu2);

        // 화면 꺼짐 막기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 센서 메니저 만들어 놓기
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        textViewStage= (TextView) findViewById(R.id.menu2_textView2);

        acc=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        layout = (LinearLayout) findViewById(R.id.menu2_viewlayout);
        textView = (TextView) findViewById(R.id.menu2_textView);


        btn1 = (Button) findViewById(R.id.menu2_button);
        btn2 = (Button) findViewById(R.id.menu2_button2);

        ratingBar = (RatingBar) findViewById(R.id.menu2_ratingBar);
        seekBar = (SeekBar) findViewById(R.id.menu2_seekBar);

        seekbarAndRatingbar = new SeekbarAndRatingbar(ratingBar,seekBar);


        myGrapicView= new menu2.MyGrapicView(this);
        layout.addView(myGrapicView);

        btn1.setOnClickListener(new View.OnClickListener() {// 이전 스테이지 버튼
            @Override
            public void onClick(View v) {

                if(cirSpeed>2){
                    cirSpeed-=2;

                    for(int i=0;i<cirSpeed / 2;i++){
                        enemyCircle[i].setX(layoutWidth/2);
                        enemyCircle[i].setY(layoutHeight/(cirSpeed / 2+1)*(i+1));
                    }

                    wValue+=3;
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

                    for(int i=0;i<cirSpeed / 2;i++){
                        enemyCircle[i].setX(layoutWidth/2);
                        enemyCircle[i].setY(layoutHeight/(cirSpeed / 2+1)*(i+1));
                    }

                    wValue-=3;
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


        for(int i=0;i<6;i++) {
            enemyCircle[i] = new EnemyCircle();
        }

        MyThread myThread = new MyThread();
        myThread.start();
    }

    private class MyThread extends Thread{
        public void run(){ // 동작할 내용 기술
            while(true){
                try {
                    Thread.sleep(100);
                    for(int i=0;i<6;i++){

                        // 화면 벽에 닿았을 때 팅기도록 처리
                        if (enemyCircle[i].getX() - r <= 0 || enemyCircle[i].getX() + r >= layoutWidth) {
                            if(enemyCircle[i].getWay()==0){
                                enemyCircle[i].setWay(1);
                            }else{
                                enemyCircle[i].setWay(0);
                            }
                        }

                        if(enemyCircle[i].getWay()==0){
                            enemyCircle[i].setX(enemyCircle[i].getX()-8*(i+1));
                        }else{
                            enemyCircle[i].setX(enemyCircle[i].getX()+8*(i+1));
                        }
                    }

                    myGrapicView.invalidate();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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

                dx=-(int)(sensorEvent.values[0]*cirSpeed*2);
                dy=(int)(sensorEvent.values[1]*cirSpeed*2);

                if((cx-r+dx)>0&&(cx+r+dx<layoutWidth)&&togle){
                    cx=cx+dx;
                }
                if((cy-r+dy)>0&&(cy+r+dy<layoutHeight)&&togle){
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

        Random random = new Random();

        r = (int)(layoutHeight*0.02);

        cx = random.nextInt(layoutWidth-r);
        cy = layout.getHeight()-r;

        for(int i=0;i<cirSpeed / 2;i++){
            enemyCircle[i].setX(layoutWidth/2);
            enemyCircle[i].setY(layoutHeight/(cirSpeed / 2+1)*(i+1));
        }

        w=r*4;
        rx = random.nextInt(layoutWidth-w);
        ry = 0;

        myGrapicView.invalidate();
    }

    private class MyGrapicView extends View {
        public MyGrapicView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);



            Paint enemyPaint[] = new Paint[6];

            for(int i = 0; i<cirSpeed / 2;i++){
                enemyPaint[i] = new Paint();
                enemyPaint[i].setColor(Color.BLACK);
                canvas.drawCircle(enemyCircle[i].getX(),enemyCircle[i].getY(),r,enemyPaint[i]);

                if (cx > enemyCircle[i].getX() - r && cx < enemyCircle[i].getX() + r
                        && cy > enemyCircle[i].getY() - r && cy < enemyCircle[i].getY() + r) {
                    timeGo = false;
                    togle = false;
                    AlertDialog.Builder dlg = new AlertDialog.Builder(menu2.this);
                    dlg.setTitle("게임 오버");
                    dlg.setMessage("적과 만남");
                    dlg.setPositiveButton("다시하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            togle = true;
                            timeGo = true;
                            nCnt = 0;
                        }
                    });
                    dlg.show();
                    break;
                }
            }


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
                AlertDialog.Builder dlg = new AlertDialog.Builder(menu2.this);
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

                togle = false;
                timeGo=false;
                textView.setText("도착함");
                if(!arrivalDialogShown) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(menu2.this);
                    dlg.setTitle("도착!!");
                    dlg.setMessage("목적지에 도착하셨습니다.");

                    if ((cirSpeed / 2) == 6) { // 6단계일때 massageBox
                        dlg.setPositiveButton("종료하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                timeGo = true;
                                togle = true;
                                arrivalDialogShown = false;
                                gameTime = 30;
                                cirSpeed=2;
                                wValue=0;
                                finish();
                            }
                        });
                        dlg.setNegativeButton("다시하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                togle = true;
                                timeGo = true;
                                arrivalDialogShown = false;
                                nCnt = 0;
                            }
                        });

                    } else { // 6단계전 massageBox
                        dlg.setPositiveButton("다음 스테이지", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (cirSpeed < 12) { // 5단계 이하 일때
                                    cirSpeed += 2;// 속도 증가


                                    gameTime -= 5;// 시간 감소

                                    wValue -= 3;

                                    seekbarAndRatingbar.valuePlusMinus(gameTime, cirSpeed / 2);

                                    textViewStage.setText(Integer.toString(cirSpeed / 2) + "단계"); // 몇 단계 인지
                                    nCnt = 0;
                                }

                                timeGo = true;
                                togle = true;
                                arrivalDialogShown = false;
                            }
                        });
                        dlg.setNegativeButton("다시하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                timeGo = true;
                                togle = true;
                                arrivalDialogShown = false;
                                nCnt = 0;
                            }
                        });
                    }
                    dlg.show();
                    arrivalDialogShown=true;
                }
            }else{
                textView.setText("안들어옴");
            }

        }
    }
}