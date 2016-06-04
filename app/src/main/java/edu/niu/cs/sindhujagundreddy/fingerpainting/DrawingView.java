package edu.niu.cs.sindhujagundreddy.fingerpainting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sindhujagundreddy on 4/7/16.
 */
public class DrawingView extends View{
    //Drawing path - where the user has dragged their finger on the screen
    private Path path;

    //paint for drawing and for the canvas
    private Paint drawPaint, canvasPaint;

    //integer version of the paint color
    private int paintColor;

    //Canvas for the picture to be drawn
    private Canvas drawingCanvas;

    //Bitmap to be used by the canvas
    private Bitmap canvasBitmap;

    //hold the current brush size and the last brush size (used to get
    //back the brush size after erasing)
    private float brushSize, lastBrushSize;

    //Has the eraser been selected?
    private boolean erase = false;


    public DrawingView( Context context )
    {
        super(context);
    }//end of DrawingView constructor

    public DrawingView( Context context, AttributeSet attributeSet )
    {
        super(context, attributeSet);
        setUp();
    }//end of DrawingView constructor


    //This method sets up the drawing properties
    private void setUp()
    {
        //Create the path
        path = new Path();

        //the default brush size is a medium brush
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        //create the Paint object and set the initial color
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);

        //set the initial path properties - they should make the line smooth
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        //create second Paint object
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }//end of setUp


    //Handle erase mode
    public void setErase( boolean isErase )
    {
        //Update the data member
        erase = isErase;

        //If the user is erasing
        if( erase )
        {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        //the user is drawing
        else
        {
            drawPaint.setXfermode(null);
        }
    }//end of setErase


    //Handle creation of a brand new drawing
    public void newDrawing()
    {
        drawingCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }//end of newDrawing


    //Make a change to the brush size
    public void setBrushSize( float newBrushSize )
    {
        //Calculate the number of pixels for the specific brush size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newBrushSize, getResources().getDisplayMetrics());

        //Update the brushSize and use it to update the drawing paint
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }//end of setBrushSize


    //Make a change to the lastBrushSize variable
    public void setLastBrushSize( float lastSize )
    {
        lastBrushSize = lastSize;
    }

    //Retrieve the value that lastBrushSize is holding
    public float getLastBrushSize()
    {
        return lastBrushSize;
    }


    //Method to change the paint color
    public void setPaintColor( String newColor )
    {
        invalidate();

        //Parse the string to turn it into an integer color
        paintColor = Color.parseColor(newColor);

        //Update the color
        drawPaint.setColor(paintColor);
    }


    //This method has to be overridden to make the custom View function
    //as a drawing View. It is called when the custom View is assigned
    //a size
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        //call the super class version of the method
        super.onSizeChanged(w, h, oldw, oldh);

        //Create the bitmap and canvas using the updated width and height
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawingCanvas = new Canvas(canvasBitmap);
    }

    //This method has to be overridden to make the custom View function
    //as a drawing View. It is called to draw the canvas
    @Override
    protected void onDraw(Canvas canvas)
    {
        //Put the picture that has been drawn on the canvas
        canvas.drawBitmap(canvasBitmap,0,0,canvasPaint);
        canvas.drawPath(path,drawPaint);
    }

    //Handle the touch events that make the drawing
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //Get the coordinates where the touch occurred
        float touchX = event.getX(),
                touchY = event.getY();

        //Handle the different types of touches
        switch (event.getAction())
        {
            //When the user puts their finger on the screen, move to that position
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX,touchY);
                break;

            //When the user moves their finger that is touching the screen,
            //draw the path between the initial touch spot and where the finger
            //was moved
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX, touchY);
                break;

            //When the user lifts up their finger, draw the movement on the canvas
            //and reset the path to get ready for the next touch operation
            case MotionEvent.ACTION_UP:
                drawingCanvas.drawPath(path, drawPaint);
                path.reset();
                break;

            default:
                return false;
        }//end of switch

        //Draw the path onto the canvas
        invalidate();
        return true;
    }//end of onTouchEvent
}
