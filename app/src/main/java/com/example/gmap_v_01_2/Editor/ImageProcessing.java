package com.example.gmap_v_01_2.Editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

public class ImageProcessing {

    FollowerProcessing followerProcessing = new FollowerProcessing();

    //This method takes users bitmap and remake it with prefered width and height
    public Bitmap getResizedBitmap(Bitmap bitmap, int followers){

        int[] PicWidthHeight = followerProcessing.picSizeViaFollower(followers);
        Bitmap finalbitmap = Bitmap.createScaledBitmap(bitmap,PicWidthHeight[0],PicWidthHeight[1], false);
        return finalbitmap;

    }


    //This method makes standard image to circle image as Instagram have
    public Bitmap getCroppedBitmap(Bitmap bitmap){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth()/2,bitmap.getHeight()/2,bitmap.getWidth()/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

}
