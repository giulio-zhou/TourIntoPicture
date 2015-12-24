package com.example.giuliozhou.tourintopicture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by giuliozhou on 11/14/15.
 */
public class SelectionPoint extends ImageView {
    private Paint paint;
    PointF point;
    ArrayList<SelectionPoint> neighbors;
    boolean isDrawing;
    float lastX;
    float lastY;
    ImageSelectView imageview;
    int imageSizeOffset;
    /**
     * @param context
     */
    public SelectionPoint(Context context)
    {
        super(context);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        point = new PointF();
        neighbors = new ArrayList<SelectionPoint>();
        isDrawing = false;

        setImageResource(R.drawable.point);
        setScaleType(ScaleType.FIT_CENTER);
    }

    public SelectionPoint(Context context, float x, float y)
    {
        super(context);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        point = new PointF(x, y);
        neighbors = new ArrayList<SelectionPoint>();
        isDrawing = false;
        setImageResource(R.drawable.point);
        setScaleType(ScaleType.FIT_CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        if (isDrawing) {
            for (SelectionPoint neighbor: neighbors) {
                // System.out.println("Redrawing line " + point.x + " " + point.y + " " + neighbor.point.x +
                //                    " " + neighbor.point.y);
                // System.out.println(canvas);
                // canvas.drawLine(point.x, point.y, neighbor.point.x, neighbor.point.y, paint);
            }
        } */
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) getLayoutParams();
        // System.out.println("MARGINS: " + getPaddingLeft() + " " + getLeftPaddingOffset());
        // ImageSelectView imageview = (ImageSelectView) findViewById(R.id.img);
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                isDrawing = true;
                lastX = ev.getRawX();
                lastY = ev.getRawY();
                break;
            case (MotionEvent.ACTION_MOVE):
                if (isDrawing) {
                    System.out.println("Raw: " + ev.getRawX() + " " + ev.getRawY());
                    System.out.println("Margins: " + mParams.leftMargin + " " + mParams.topMargin);
                    System.out.println("Padding: " + getLeftPaddingOffset() + " " + getTopPaddingOffset());
                    point.x += ev.getRawX() - lastX;
                    point.y += ev.getRawY() - lastY;
                    mParams.leftMargin = (int) (point.x - imageSizeOffset);
                    mParams.topMargin = (int) (point.y - imageSizeOffset);
                    lastX = ev.getRawX();
                    lastY = ev.getRawY();
                    setLayoutParams(mParams);
                    imageview.invalidate();
                }
                break;
            case (MotionEvent.ACTION_UP):
                if (isDrawing) {
                    point.x += ev.getRawX() - lastX;
                    point.y += ev.getRawY() - lastY;
                    mParams.leftMargin = (int) (point.x - imageSizeOffset);
                    mParams.topMargin = (int) (point.y - imageSizeOffset);
                    setLayoutParams(mParams);
                    isDrawing = false;
                    imageview.invalidate();
                }
                break;
        }

        return true;
    }
}
