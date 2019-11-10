package com.example.gmap_v_01_2.editor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageProcessing {

    private FollowerProcessing followerProcessing;

    public ImageProcessing(FollowerProcessing followerProcessing) {
        this.followerProcessing = followerProcessing;
    }

    //This method takes users bitmap and remake it with prefered width and height
    public Bitmap getResizedBitmap(Bitmap bitmap, int followers) {

        int[] PicWidthHeight = followerProcessing.picSizeViaFollower(followers);
        return Bitmap.createScaledBitmap(bitmap,PicWidthHeight[0],PicWidthHeight[1], false);

    }

    //This Method resizing bitmap to UserList Fragment size type
    public Bitmap getResizedBitmapForUserListFragment(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap,100,100,false);
    }

    //CONVERT BITMAP TO STRING
    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] b = baos.toByteArray();
        String result = Base64.encodeToString(b,Base64.DEFAULT);
        return result;
    }

    //CONVERT STRING TO BITMAP
    public Bitmap stringToBitmap(String string) {
        try{
            byte[] encodeByte = Base64.decode(string,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
            return bitmap;
        }catch(Exception e) {
            e.getMessage();
            return null;
        }
    }


    //This method makes standard image to circle image as Instagram have
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
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
