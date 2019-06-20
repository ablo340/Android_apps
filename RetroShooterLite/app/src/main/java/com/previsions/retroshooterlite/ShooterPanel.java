package com.previsions.retroshooterlite;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Random;

public class ShooterPanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WIDTH = 477;
    public static final int HEIGHT = 233;
    public static final int MOVEDSPEED = -5;
    private long ennemiStartTime;
    private long missileStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Missile> missiles;
    private ArrayList<Ennemi> ennemis;
    private ArrayList<MissilePlayer> mplayer;
    private Random rand = new Random();
    private boolean newGameCreated;

    //increase to slow down difficulty progression, decrease to speed up difficulty progression
    private int progressDenom = 20;

    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean dissapear;
    private boolean started;
    private int best;
    private boolean missileplayer;


    public ShooterPanel(Context context)
    {
        super(context);


        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //make shooterPanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while(retry && counter<1000)
        {
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;

            }catch(InterruptedException e){e.printStackTrace();}

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player_alone), 46, 44, 4);
        missiles = new ArrayList<Missile>();
        ennemis = new ArrayList<Ennemi>();
        mplayer = new ArrayList<MissilePlayer>();

        ennemiStartTime = System.nanoTime();
        missileStartTime = System.nanoTime();

        thread = new MainThread(getHolder(), this);

        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.getPlaying() && newGameCreated && reset)
            {
                player.setPlaying(true);
                player.setUp(true);
            }
            if(player.getPlaying())
            {

                if(!started)started = true;
                reset = false;
                player.setUp(true);
            }
            return true;
        }

        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }



    public void update()
    {
        if(player.getPlaying()) {

            bg.update();
            player.update();

            //add ennemis on timer
            long ennemiElapsed = (System.nanoTime()-ennemiStartTime)/1000000; //elapsed time in millisecond
            if(ennemiElapsed >((int)(rand.nextDouble() + 2000 - player.getScore()/4))){

                //ennemi random apparition
                ennemis.add(new Ennemi(BitmapFactory.decodeResource(getResources(),R.drawable.ennemi),
                        WIDTH+20, (int)(rand.nextDouble()*(HEIGHT)),46,44, player.getScore(),3));

                //reset ennemi timer
                ennemiStartTime = System.nanoTime();
            }

            //add missile on timer
            long missileElapsed = (System.nanoTime()-missileStartTime)/1000000; //elapsed time in millisecond

            if(missileElapsed >((int)(rand.nextDouble()+ 2000 - player.getScore()))){

                for (Ennemi ennemi: ennemis){

                    if(ennemi.getX() > 100 + (int)(rand.nextDouble() )){

                        ennemi.setSpritesheet(BitmapFactory.decodeResource(getResources(),R.drawable.ennemi_tir));

                        missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missiles_tir),
                                ennemi.getX(), ennemi.getY(),30,25, player.getScore(),6));
                    }

                }

                //reset missile timer
                missileStartTime = System.nanoTime();
            }

            //loop through every ennemis and check collision and remove
            for(int i = 0; i<ennemis.size();i++)
            {
                //update ennemis
                ennemis.get(i).update();

                if(collision(ennemis.get(i),player))
                {
                    ennemis.remove(i);
                    player.setPlaying(false);
                    break;
                }
                //remove ennemi if it is way off the screen
                if(ennemis.get(i).getX()<-100)
                {
                    ennemis.remove(i);
                    break;
                }
            }

            //loop through every missile and check collision and remove
            for(int i = 0; i<missiles.size();i++)
            {
                //update missile
                missiles.get(i).update();

                if(collision(missiles.get(i),player))
                {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                //remove missile if it is way off the screen
                if(missiles.get(i).getX()<-100)
                {
                    missiles.remove(i);
                    break;
                }
            }

        }

        else{
            player.resetDY();
            if(!reset)
            {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                dissapear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosions),player.getX(),
                        player.getY()-30, 46, 46, 25);
            }

            explosion.update();
            long resetElapsed = (System.nanoTime()-startReset)/1000000;

            if(resetElapsed > 2500 && !newGameCreated)
            {
                newGame();
            }

        }
    }
    public boolean collision(GameObject a, GameObject b)
    {
        if(Rect.intersects(a.getRectangle(),b.getRectangle()))
        {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);
        final float scaleFactorX = getWidth() / (WIDTH * 1.f);
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);

        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            if (!dissapear) {
                player.draw(canvas);
            }

            //draw ennemis
            for (Ennemi e : ennemis) {
                e.draw(canvas);
            }

            //draw ennemis
            for (Missile m : missiles) {
                m.draw(canvas);
            }

            //draw explosion
            if (started) {
                explosion.draw(canvas);
            }
            drawText(canvas);
            canvas.restoreToCount(savedState);

        }
    }

    public void newGame()
    {
        dissapear = false;

        ennemis.clear();
        missiles.clear();

        player.resetDY();
        player.resetScore();
        player.setY(HEIGHT/2);

        if(player.getScore()>best)
        {
            best = player.getScore();

        }

        newGameCreated = true;


    }

    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(15);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + (player.getScore()*3), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + best, WIDTH - 150, HEIGHT - 10, paint);

        if(!player.getPlaying()&&newGameCreated&&reset)
        {
            Paint paint1 = new Paint();
            paint1.setTextSize(25);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Appuyer pour commencer", WIDTH/2-100, HEIGHT/2, paint1);

        }
    }

}
