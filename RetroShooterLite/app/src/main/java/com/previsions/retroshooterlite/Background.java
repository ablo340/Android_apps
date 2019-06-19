package com.previsions.retroshooterlite;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    private Bitmap image;
    private int x, y, dx;

    public Background(Bitmap res)
    {
        image = res;
        dx = ShooterPanel.MOVEDSPEED;
    }
    public void update()
    {
        x+=dx;
        if(x<-ShooterPanel.WIDTH){
            x=0;
        }
    }
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y,null);
        if(x<0)
        {
            canvas.drawBitmap(image, x+ShooterPanel.WIDTH, y, null);
        }
    }
}
