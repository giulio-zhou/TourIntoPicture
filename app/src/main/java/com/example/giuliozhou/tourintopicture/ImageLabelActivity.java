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
                float[][][] imgTextures = processWalls();
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
        RelativeLayout.LayoutParams sample4Layout = new RelativeLayout.LayoutParams(120,120);
        RelativeLayout.LayoutParams sample5Layout = new RelativeLayout.LayoutParams(120,120);
        SelectionPoint sample4 = new SelectionPoint(this, 140, 40);
        SelectionPoint sample5 = new SelectionPoint(this, 180, 80);
        sample4Layout.leftMargin = 140;
        sample4Layout.topMargin = 40;
        sample5Layout.leftMargin = 180;
        sample5Layout.topMargin = 80;
        sample4.setLayoutParams(sample4Layout);
        sample5.setLayoutParams(sample5Layout);
        sample4.setImageResource(R.drawable.green_dot);
        sample5.setImageResource(R.drawable.green_dot);
        sample4.imageview = imageview;
        sample5.imageview = imageview;
        rl.addView(sample4);
        rl.addView(sample5);
        selectionPoints.add(sample4);
        selectionPoints.add(sample5);
    }

    float[][][] processWalls() {
        int imgWidth = currBitmap.getWidth();
        int imgHeight = currBitmap.getHeight();
        int buffer_size = 512;
        /* Map across all five rectangles */
        float[][][] toReturn = new float[5][buffer_size][buffer_size];
        SelectionPoint centerTopLeft = selectionPoints.get(0);
        SelectionPoint vanishingPoint = selectionPoints.get(1);
        SelectionPoint centerBottomRight = selectionPoints.get(2);
        SelectionPoint imageTopLeft = selectionPoints.get(3);
        SelectionPoint imageBottomRight = selectionPoints.get(4);

        /* Center */
        float[][] center = toReturn[0];
        int currTopLeftX = (int) (imgWidth * (centerTopLeft.point.x - imageTopLeft.point.x) /
                                 (imageBottomRight.point.x - imageTopLeft.point.x));
        int currTopLeftY = (int) (imgHeight * (centerTopLeft.point.y - imageTopLeft.point.y) /
                                 (imageBottomRight.point.y - imageTopLeft.point.y));
        int currBottomRightX = (int) (imgWidth * (centerBottomRight.point.x - imageTopLeft.point.x) /
                                     (imageBottomRight.point.x - imageTopLeft.point.x));
        int currBottomRightY = (int) (imgHeight * (centerBottomRight.point.y - imageTopLeft.point.y) /
                                     (imageBottomRight.point.y - imageTopLeft.point.y));
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                int currX = currTopLeftX + (i * (currBottomRightX - currTopLeftX)) / buffer_size;
                int currY = currTopLeftY + (j * (currBottomRightY - currTopLeftY)) / buffer_size;
                center[i][j] = currBitmap.getPixel(currX, currY);
            }
        }

        /* Up */
        currTopLeftX = (int) (imgWidth * (centerTopLeft.point.x - imageTopLeft.point.x) /
                             (imageBottomRight.point.x - imageTopLeft.point.x));
        currTopLeftY = (int) (imgHeight * (centerTopLeft.point.y - imageTopLeft.point.y) /
                             (imageBottomRight.point.y - imageTopLeft.point.y));
        currBottomRightX = (int) (imgWidth * (centerBottomRight.point.x - imageTopLeft.point.x) /
                                 (imageBottomRight.point.x - imageTopLeft.point.x));
        currBottomRightY = (int) (imgHeight * (centerBottomRight.point.y - imageTopLeft.point.y) /
                                 (imageBottomRight.point.y - imageTopLeft.point.y));
        float[][] up = toReturn[1];
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                up[i][j] = currBitmap.getPixel(i, j);
            }
        }

        /* Down */
        float[][] down = toReturn[2];
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                down[i][j] = currBitmap.getPixel(i, j);
            }
        }

        /* Left */
        float[][] left = toReturn[3];
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                left[i][j] = currBitmap.getPixel(i, j);
            }
        }

        /* Right */
        float[][] right = toReturn[4];
        for (int i = 0; i < buffer_size; i++) {
            for (int j = 0; j < buffer_size; j++) {
                right[i][j] = currBitmap.getPixel(i, j);
            }
        }

        return toReturn;
    }

    /* Points are given to represent a quadrilateral
       clockwise from the top left corner
     */
    float[] selectRelevantPoints(SelectionPoint topLeft, SelectionPoint topRight,
                                 SelectionPoint bottomRight, SelectionPoint bottomLeft) {
        ArrayList<PointF> listOfPoints = new ArrayList<PointF>();
        for (int i = 0; i < 500; i++) {
            for (int j = 0; j < 500; j++) {
                float slope1 = (topRight.point.y - topLeft.point.y) / (topRight.point.x - topLeft.point.x);
                float slope2 = (bottomRight.point.y - bottomLeft.point.y) / (bottomRight.point.x - bottomLeft.point.x);
                float slope3 = (topLeft.point.y - bottomLeft.point.y) / (topLeft.point.x - bottomLeft.point.x);
                float slope4 = (topRight.point.y - bottomRight.point.y) / (topRight.point.x - bottomRight.point.x);

                if (j <= slope1*i && j > slope2*i &&
                        i > slope2) {
                    listOfPoints.add(new PointF(i, j));
                }
            }
        }
        float[] toReturn = new float[500];
        return toReturn;
    }
}
