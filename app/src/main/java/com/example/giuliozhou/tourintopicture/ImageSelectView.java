package com.example.giuliozhou.tourintopicture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.lang.Math;
import java.util.ArrayList;

/**
 * Created by giuliozhou on 11/14/15.
 */
public class ImageSelectView extends ImageView {
    ArrayList<SelectionPoint> selectionPoints;
    Paint paint;

    public ImageSelectView(Context context) {
        super(context);
        selectionPoints = new ArrayList<SelectionPoint>();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
    }

    public ImageSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        selectionPoints = new ArrayList<SelectionPoint>();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
    }

    public ImageSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selectionPoints = new ArrayList<SelectionPoint>();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (SelectionPoint point: selectionPoints) {
            for (SelectionPoint neighbor: point.neighbors) {
                System.out.println("Redrawing line " + point.point.x + " " + point.point.y + " " + neighbor.point.x +
                                   " " + neighbor.point.y);
                canvas.drawLine(point.point.x, point.point.y, neighbor.point.x, neighbor.point.y, paint);
            }
        }
        SelectionPoint topLeft = selectionPoints.get(0);
        SelectionPoint vanishingPoint = selectionPoints.get(1);
        SelectionPoint bottomRight = selectionPoints.get(2);
        /* Draw in the rectangle */
        canvas.drawLine(topLeft.point.x, topLeft.point.y, topLeft.point.x, bottomRight.point.y, paint);
        canvas.drawLine(topLeft.point.x, topLeft.point.y, bottomRight.point.x, topLeft.point.y, paint);
        canvas.drawLine(topLeft.point.x, bottomRight.point.y, bottomRight.point.x, bottomRight.point.y, paint);
        canvas.drawLine(bottomRight.point.x, topLeft.point.y, bottomRight.point.x, bottomRight.point.y, paint);
        /* And additional connections to the corners */
        canvas.drawLine(topLeft.point.x, bottomRight.point.y, vanishingPoint.point.x, vanishingPoint.point.y, paint);
        canvas.drawLine(vanishingPoint.point.x, vanishingPoint.point.y, bottomRight.point.x, topLeft.point.y, paint);

        /* Draw the outer frame edges */
        int[] topLeftCoordinates = new int[2];
        getLocationOnScreen(topLeftCoordinates);
        int topLeftX = topLeftCoordinates[0];
        int topLeftY = topLeftCoordinates[1];
        int bottomRightX = getRight();
        int bottomRightY = getBottom();
        System.out.println(topLeftX + " " + topLeftY + " " + getLeft() + " " + getTop() + " " + getBottom());
        getLocationInWindow(topLeftCoordinates);
        System.out.println("Window: " + topLeftCoordinates[0] + " " + topLeftCoordinates[1]);
        float slope1 = (vanishingPoint.point.y - topLeft.point.y) / (vanishingPoint.point.x - topLeft.point.x);
        float slope2 = (vanishingPoint.point.y - bottomRight.point.y) / (vanishingPoint.point.x - topLeft.point.x);
        float slope3 = (topLeft.point.y - vanishingPoint.point.y) / (bottomRight.point.x - vanishingPoint.point.x );
        float slope4 = (bottomRight.point.y - vanishingPoint.point.y) / (bottomRight.point.x - vanishingPoint.point.x);
        /* If the truth conditions are satisfied, then the left/right borders are where the points collide */
        System.out.println("Current: " + (vanishingPoint.point.y - slope1*(vanishingPoint.point.x)) + " " + topLeft);
        if (vanishingPoint.point.y - slope1*(vanishingPoint.point.x) >= topLeftY) {
            canvas.drawLine(0, vanishingPoint.point.y - slope1 * (vanishingPoint.point.x),
                    topLeft.point.x, topLeft.point.y, paint);
        } else {
            float intersectionX = (vanishingPoint.point.y / slope1) - vanishingPoint.point.x;
            canvas.drawLine(intersectionX, 0, topLeft.point.x, topLeft.point.y, paint);
        }

        if (vanishingPoint.point.y - slope2*(vanishingPoint.point.x) < bottomRightY) {
            canvas.drawLine(0, vanishingPoint.point.y - slope2 * (vanishingPoint.point.x),
                            topLeft.point.x, bottomRight.point.y, paint);
        } else {
            float intersectionX = (vanishingPoint.point.y / slope2) - vanishingPoint.point.x;
            canvas.drawLine(intersectionX, bottomRightY, topLeft.point.x, bottomRight.point.y, paint);
        }

        if (vanishingPoint.point.y + slope3*(bottomRightX - vanishingPoint.point.x) >= topLeftY) {
            canvas.drawLine(bottomRightX, vanishingPoint.point.y + slope3*(bottomRightX - vanishingPoint.point.x),
                            bottomRight.point.x, topLeft.point.y, paint);
        } else {
            float intersectionX = -(vanishingPoint.point.y / slope3) + vanishingPoint.point.x;
            canvas.drawLine(bottomRight.point.x, topLeft.point.y, intersectionX, topLeftY, paint);
        }

        if (vanishingPoint.point.y + slope4*(bottomRightX - vanishingPoint.point.x) < bottomRightY) {
            canvas.drawLine(bottomRightX, vanishingPoint.point.y + slope4*(bottomRightX - vanishingPoint.point.x),
                            bottomRight.point.x, bottomRight.point.y, paint);
        } else {
            float intersectionX = -(vanishingPoint.point.y / slope4) + vanishingPoint.point.x;
            canvas.drawLine(bottomRight.point.x, bottomRight.point.y, intersectionX, bottomRightY, paint);
        }
    }

}
