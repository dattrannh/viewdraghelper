package com.dattran.viewdraghelper.MyDragHelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by DatTran on 09/01/2018.
 */

public class CustomView extends View {
    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.drawPath(path,paint);
    }
    private Paint paint;
    private Path path = new Path();

    private void init(){
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);//ko rang cua
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(5f);//do rong net ve
        paint.setStyle(Paint.Style.STROKE);//tao duong tron giong
        paint.setStrokeJoin(Paint.Join.ROUND);
    }
    float x,y;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x=event.getX();
        y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x,y);
                break;
            default:
                return false;
        }
        invalidate();//ve lai ondraw
        return true;
    }
}
