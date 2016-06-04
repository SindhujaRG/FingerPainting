package edu.niu.cs.sindhujagundreddy.fingerpainting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    //The drawing canvas
    private DrawingView drawingView;

    //current paint color
    private ImageButton currentColor;

    //Buttons for drawing, erasing, creating a new canvas, and saving a drawing
    Button drawBtn, eraseBtn, newBtn, saveBtn;

    //variables to hold the various brush sizes
    float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Connect variables and items on the screen
        drawingView = (DrawingView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.colors);

        //get the first paint color from the linear layout at bottom of screen and
        //change its border to make it look like it has been pressed
        currentColor = (ImageButton)paintLayout.getChildAt(0);
        currentColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));

        //set up the various brush sizes using values from dimens.xml
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        //initial color and brush size that will be used when the application starts
        drawingView.setPaintColor(currentColor.getTag().toString());
        drawingView.setBrushSize(mediumBrush);

        //Connect all of the buttons with an OnClickListener
        drawBtn = (Button)findViewById(R.id.brushButton);
        drawBtn.setOnClickListener(this);

        eraseBtn = (Button)findViewById(R.id.eraseButton);
        eraseBtn.setOnClickListener(this);

        newBtn = (Button)findViewById(R.id.newButton);
        newBtn.setOnClickListener(this);

        saveBtn = (Button)findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(this);
    }//end of onCreate


    //Listener for the four buttons
    //
    //v is the button that was clicked
    @Override
    public void onClick(View v)
    {
        //If the user wants to change the size of the paint brush
        if( v.getId() == R.id.brushButton )
        {
            //Create a pop-up dialog that displays the three options
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Select a Brush Size:");
            brushDialog.setContentView(R.layout.brush_choice);

            //display the dialog box
            brushDialog.show();

            //handle the small brush being selected
            Button smallBtn = (Button)brushDialog.findViewById(R.id.smallBrushButton);
            smallBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //change the brush size (also setting last brush size in case the previous
                    //operation was erasing
                    drawingView.setBrushSize(smallBrush);
                    drawingView.setLastBrushSize(smallBrush);

                    //don't erase! Color!
                    drawingView.setErase(false);

                    //get rid of the dialog box
                    brushDialog.dismiss();
                }
            });

            //handle the medium brush being selected
            Button mediumBtn = (Button)brushDialog.findViewById(R.id.mediumBrushButton);
            mediumBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawingView.setBrushSize(mediumBrush);
                    drawingView.setLastBrushSize(mediumBrush);
                    drawingView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            //handle the large brush being selected
            Button largeBtn = (Button)brushDialog.findViewById(R.id.largeBrushButton);
            largeBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawingView.setBrushSize(largeBrush);
                    drawingView.setLastBrushSize(largeBrush);
                    drawingView.setErase(false);
                    brushDialog.dismiss();
                }
            });
        }//end of clicking brush button

        //If the user wants to erase
        else if( v.getId() == R.id.eraseButton )
        {
            //Create a pop-up dialog that displays the three options
            final Dialog eraserDialog = new Dialog(this);
            eraserDialog.setTitle("Select an Eraser Size:");
            eraserDialog.setContentView(R.layout.eraser_choice);

            //display the dialog box
            eraserDialog.show();

            //handle the small eraser being selected
            Button smallBtn = (Button)eraserDialog.findViewById(R.id.smallEraseButton);
            smallBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawingView.setBrushSize(smallBrush);
                    //Erase! Don't color!
                    drawingView.setErase(true);
                    eraserDialog.dismiss();
                }
            });

            //handle the medium eraser being selected
            Button mediumBtn = (Button)eraserDialog.findViewById(R.id.mediumEraseButton);
            mediumBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawingView.setBrushSize(mediumBrush);
                    drawingView.setErase(true);
                    eraserDialog.dismiss();
                }
            });

            //handle the large eraser being selected
            Button largeBtn = (Button)eraserDialog.findViewById(R.id.largeEraseButton);
            largeBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawingView.setBrushSize(largeBrush);
                    drawingView.setErase(true);
                    eraserDialog.dismiss();
                }
            });
        }//end of erasing

        //If the user wants to start a new picture
        else if( v.getId() == R.id.newButton )
        {
            //Create a pop-up dialog box with positive/negative options
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New Drawing");
            newDialog.setMessage("Start a new drawing (you will lose the current drawing)?");

            //Set up the positive option
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    drawingView.newDrawing();
                    dialog.dismiss();
                }
            });

            //Set up the negative option
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });

            //display the dialog
            newDialog.show();
        }//end of new picture option

        //If the user wants to save their picture
        else if( v.getId() == R.id.saveButton )
        {
            //Create a pop-up dialog box with positive/negative options
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");

            //Set up the positive option
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    drawingView.setDrawingCacheEnabled(true);
                    String savedImageURI = MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(),
                            drawingView.getDrawingCache(),
                            UUID.randomUUID().toString() + ".png",
                            "drawing");
                    if (savedImageURI != null)
                    {
                        Toast.makeText(getApplicationContext(), "Drawing saved to gallery " + savedImageURI, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Sorry, drawing could not be saved " + savedImageURI, Toast.LENGTH_SHORT).show();

                    }
                }
            });

            //Set up the negative option
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });

            //display the dialog
            saveDialog.show();

            //Get rid of the drawing cache so it's not used for any other saved drawing
            drawingView.destroyDrawingCache();
        }//end of save option
    }//end of onClick



    //Handle a color selection
    //
    //view is the color button that was clicked
    public void paintClicked( View view )
    {
        //Make sure that erasing is turned off and update the brush size
        drawingView.setErase(false);
        drawingView.setBrushSize(drawingView.getLastBrushSize());

        //if the color needs to be updated
        if( view != currentColor )
        {
            ImageButton imageButton = (ImageButton)view;
            String color = view.getTag().toString();
            drawingView.setPaintColor(color);

            //Set the new button as pressed and the old one as normal
            imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
            currentColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_color, null));
            currentColor = (ImageButton)view;
        }
    }//end of paintClicked
}//end of MainActivity

