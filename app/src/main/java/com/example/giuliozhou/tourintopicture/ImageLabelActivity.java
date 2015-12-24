package com.example.giuliozhou.tourintopicture;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import java.util.ArrayList;

public class ImageLabelActivity extends AppCompatActivity {
    ArrayList<SelectionPoint> selectionPoints;
    ImageSelectView imageSelectionView;
    Bitmap currBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_label);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button submitPoints = (Button) findViewById(R.id.button3);
        submitPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                System.out.println("Bitmap size: " + currBitmap.getWidth() + " " + currBitmap.getHeight());
                ProgressBar spinner = (ProgressBar) findViewById(R.id.spinner);
                spinner.setVisibility(View.VISIBLE);
                Bitmap[] imgTextures = processWalls();
                // imageSelectionView.setImageBitmap(imgTextures[4]);
                // imageSelectionView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.berkeley));
                // imageSelectionView.invalidate();
                // startActivity(i);
                // System.out.println
            }
        });

        /* Load selected image into ImageView */
        String filename = getIntent().getStringExtra("filename");
        Uri selectedImage = Uri.parse(filename);
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        ImageSelectView imageview = (ImageSelectView) findViewById(R.id.img);
        Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
        imageview.setImageBitmap(imageBitmap);
        selectionPoints = new ArrayList<SelectionPoint>();
        imageview.selectionPoints = selectionPoints;
        imageview.srcImage = imageBitmap;
        imageSelectionView = imageview;
        currBitmap = imageBitmap;

        /* Initialize critical points */
        SelectionPoint sample = new SelectionPoint(this, 40, 40);
        SelectionPoint sample2 = new SelectionPoint(this, 80, 80);
        SelectionPoint sample3 = new SelectionPoint(this, 120, 120);
        sample.imageview = imageview;
        sample2.imageview = imageview;
        sample3.imageview = imageview;
        sample.neighbors.add(sample2);
        sample2.neighbors.add(sample);
        sample2.neighbors.add(sample3);
        sample3.neighbors.add(sample2);
        RelativeLayout.LayoutParams sample1Layout = new RelativeLayout.LayoutParams(120,120);
        RelativeLayout.LayoutParams sample2Layout = new RelativeLayout.LayoutParams(120,120);
        RelativeLayout.LayoutParams sample3Layout = new RelativeLayout.LayoutParams(120,120);
        sample.imageSizeOffset = 60;
        sample2.imageSizeOffset = 60;
        sample3.imageSizeOffset = 60;
        sample.point.x += sample.imageSizeOffset;
        sample.point.y += sample.imageSizeOffset;
        sample2.point.x += sample2.imageSizeOffset;
        sample2.point.y += sample2.imageSizeOffset;
        sample3.point.x += sample3.imageSizeOffset;
        sample3.point.y += sample3.imageSizeOffset;
        sample1Layout.leftMargin = 40;
        sample1Layout.topMargin = 40;
        sample2Layout.leftMargin = 80;
        sample2Layout.topMargin = 80;
        sample3Layout.leftMargin = 120;
        sample3Layout.topMargin = 120;
        sample.setLayoutParams(sample1Layout);
        sample2.setLayoutParams(sample2Layout);
        sample3.setLayoutParams(sample3Layout);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout);
        rl.addView(sample);
        rl.addView(sample2);
        rl.addView(sample3);
        selectionPoints.add(sample);
        selectionPoints.add(sample2);
        selectionPoints.add(sample3);

        /* Add in four points to get the four corners as a hack */
        SelectionPoint sample4 = new SelectionPoint(this, 0, 0);
        SelectionPoint sample5 = new SelectionPoint(this, 0, 0);
        selectionPoints.add(sample4);
        selectionPoints.add(sample5);
    }

    Bitmap[] processWalls() {
        int imgWidth = currBitmap.getWidth();
        int imgHeight = currBitmap.getHeight();
        int buffer_size = 512;
        /* Map across all five rectangles */
        Bitmap[] toReturn = new Bitmap[5];
        SelectionPoint centerTopLeft = selectionPoints.get(0);
        SelectionPoint vanishingPoint = selectionPoints.get(1);
        SelectionPoint centerBottomRight = selectionPoints.get(2);
        SelectionPoint imageTopLeft = selectionPoints.get(3);
        SelectionPoint imageBottomRight = selectionPoints.get(4);

        int[] coordinates = new int[2];
        imageSelectionView.getLocationOnScreen(coordinates);
        System.out.println("View coords: " + coordinates[0] + " " + coordinates[1] +
                " " + imageSelectionView.getRight() + " " + imageSelectionView.getBottom());
        System.out.println("Original coords: " + imageTopLeft.lastX + " " + imageTopLeft.lastY +
                " " + imageBottomRight.lastX + " " + imageBottomRight.lastY);
        imageTopLeft.lastX = coordinates[0];
        imageTopLeft.lastY = coordinates[1];
        imageBottomRight.lastX = imageTopLeft.lastX + imageSelectionView.getWidth();
        imageBottomRight.lastY =
            imageTopLeft.lastY + (imgHeight * imageSelectionView.getWidth())/imgWidth;
        System.out.println("Absolute coords: " + imageTopLeft.lastX + " " + imageTopLeft.lastY +
                " " + imageBottomRight.lastX + " " + imageBottomRight.lastY);
        System.out.println("Center coords: " + centerTopLeft.lastX + " " + centerTopLeft.lastY +
                " " + centerBottomRight.lastX + " " + centerBottomRight.lastY);
        int centerTopLeftX = (int) (imgWidth * (centerTopLeft.lastX - imageTopLeft.lastX) /
                (imageBottomRight.lastX - imageTopLeft.lastX));
        int centerTopLeftY = (int) (imgHeight * (centerTopLeft.lastY - imageTopLeft.lastY) /
                (imageBottomRight.lastY - imageTopLeft.lastY));
        int centerBottomRightX = (int) (imgWidth * (centerBottomRight.lastX - imageTopLeft.lastX) /
                (imageBottomRight.lastX - imageTopLeft.lastX));
        int centerBottomRightY = (int) (imgHeight * (centerBottomRight.lastY - imageTopLeft.lastY) /
                (imageBottomRight.lastY - imageTopLeft.lastY));
        float slope1 = (vanishingPoint.point.y - centerTopLeft.point.y) / (vanishingPoint.point.x - centerTopLeft.point.x);
        float slope2 = (vanishingPoint.point.y - centerBottomRight.point.y) / (vanishingPoint.point.x - centerTopLeft.point.x);
        float slope3 = (centerTopLeft.point.y - vanishingPoint.point.y) / (centerBottomRight.point.x - vanishingPoint.point.x );
        float slope4 = (centerBottomRight.point.y - vanishingPoint.point.y) / (centerBottomRight.point.x - vanishingPoint.point.x);

        /* Center */
        Bitmap center = Bitmap.createBitmap(buffer_size, buffer_size, Bitmap.Config.ARGB_8888);
        System.out.println("Stuff: " + imgHeight + " " + imgWidth);
        System.out.println("Values: " + centerTopLeftX + " " + centerTopLeftY + " " + centerBottomRightX + " " + centerBottomRightY);
        System.out.println("Values: " + imgHeight + " " + (centerBottomRight.lastY - imageTopLeft.lastY) + " " + (imageBottomRight.lastY - imageTopLeft.lastY));
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                int currX = centerTopLeftX + (i * (centerBottomRightX - centerTopLeftX)) / buffer_size;
                int currY = centerTopLeftY + (j * (centerBottomRightY - centerTopLeftY)) / buffer_size;
                // System.out.println("Looking up: " + currX + " " + currY + " " + i + " " + j);
                center.setPixel(i, j, currBitmap.getPixel(currX, currY));
                int value = currBitmap.getPixel(currX, currY);
                // System.out.println((value & 255) + " " + ((value >> 8) & 255) + " " + ((value >> 16) & 255) + " " + ((value >> 24) & 255));
            }
        }
        toReturn[0] = center;

        /* Up */
        Bitmap up = Bitmap.createBitmap(buffer_size, buffer_size, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                int currY = (j * centerTopLeftY) / buffer_size;
                int leftX = (int) Math.max(0, centerTopLeftX - (currY / slope1));
                int rightX = (int) Math.min(imgWidth - 1, centerBottomRightX -
                                                          (centerTopLeftY - currY) / slope2);
                int currX = leftX + (i*(rightX - leftX) / buffer_size);
                up.setPixel(i, j, currBitmap.getPixel(currX, currY));
            }
        }
        toReturn[1] = up;

        /* Down */
        Bitmap down = Bitmap.createBitmap(buffer_size, buffer_size, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                int currY = centerBottomRightY + (j * (imgHeight - centerBottomRightY) / buffer_size);
                int leftX = (int) Math.max(0, centerTopLeftX + (currY - centerBottomRightY) / slope3);
                int rightX = (int) Math.min(imgWidth - 1, centerBottomRightX +
                                                          (currY - centerBottomRightY) / slope4);
                int currX = leftX + (i*(rightX - leftX) / buffer_size);
                down.setPixel(i, j, currBitmap.getPixel(currX, currY));
            }
        }
        toReturn[2] = down;

        /* Left */
        Bitmap left = Bitmap.createBitmap(buffer_size, buffer_size, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                int currX = (i * centerTopLeftX) / buffer_size;
                int topY = (int) Math.max(0, centerTopLeftY - currX*slope1);
                int bottomY = (int) Math.min(imgHeight - 1, centerBottomRightY +
                                                            (centerTopLeftX - currX)*slope3);
                int currY = topY + (j*(bottomY - topY) / buffer_size);
                left.setPixel(i, j, currBitmap.getPixel(currX, currY));
            }
        }
        toReturn[3] = left;

        /* Right */
        Bitmap right = Bitmap.createBitmap(buffer_size, buffer_size, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                int currX = centerBottomRightX + (i * (imgWidth - centerBottomRightX) / buffer_size);
                int topY = (int) Math.max(0, centerTopLeftY + (currX - centerBottomRightX) * slope2);
                int bottomY = (int) Math.min(imgHeight - 1, centerBottomRightY +
                                                            (currX - centerBottomRightX)*slope4);
                int currY = topY + (j*(bottomY - topY) / buffer_size);
                right.setPixel(i, j, currBitmap.getPixel(currX, currY));
            }
        }
        toReturn[4] = right;

        return toReturn;
    }
}
