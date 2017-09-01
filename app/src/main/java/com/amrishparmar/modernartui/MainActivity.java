package com.amrishparmar.modernartui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    /**
     * Tag for logcat
     */
    private static final String MAIN_TAG = "MainActivity";
    /**
     * Dialog which pops up when user requests more information
     */
    private Dialog moreInfoDialog;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(MAIN_TAG, "Entered MainActivity onCreate()");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // array that contains all of the panels for which we want to modify the colour
        final View[] panels = {
                findViewById(R.id.panel_00),
                findViewById(R.id.panel_01),
                findViewById(R.id.panel_10),
                findViewById(R.id.panel_12)
        };

        // store in an array, the colours of the panel backgrounds upon initial launch
        final int[] initialPanelColours = new int[panels.length];

        for (int i = 0; i < panels.length; ++i) {
            initialPanelColours[i] = ((ColorDrawable) panels[i].getBackground()).getColor();
        }

        // slider for controlling panel colours
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);

        // add a listener to the seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // iterate over the panels calling changeColour method to update the colours
                for (int j = 0; j < panels.length; ++j) {
                    changeColour(panels[j], i, initialPanelColours[j]);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // no code required, method stated to satisfy interface
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // no code required, method stated to satisfy interface
            }

            /**
             * Updates the background colour of a view
             * @param view          The view of which we which to change the colour
             * @param progress      The progress along the seekbar
             * @param initColour    The initial colour of the view at app launch represented an an int
             */
            private void changeColour(View view, int progress, int initColour) {
                // convert the colour to HSV, since we only want to change the hue component
                float[] initColourHSV = new float[3];
                Color.colorToHSV(initColour, initColourHSV);
                // update the hue by adding the progress to the initColour
                // mod 360 as 0 <= hue < 360
                initColourHSV[0] = (initColourHSV[0] + progress) % 360;
                // update the background colour, convert HSV back to int
                view.setBackgroundColor(Color.HSVToColor(initColourHSV));
            }
        });

        // if the user changed orientation while the dialog was displayed, restore it
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("DIALOG_SHOWING")) {
                Log.i(MAIN_TAG, "Restoring dialog");
                displayInfoDialog();
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(MAIN_TAG, "User opened options menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if the user tapped the more information item, show the more info dialog
        if (item.getItemId() == R.id.more_info_action) {
            Log.i(MAIN_TAG, "User requested more information");
            displayInfoDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays a dialog which shows more information
     */
    private void displayInfoDialog() {
        moreInfoDialog = new Dialog(this);
        // don't want the title to be visible as we are going to use a custom layout
        moreInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moreInfoDialog.setContentView(R.layout.info_dialog);
        moreInfoDialog.show();

        // create the button for dismissing the dialog
        Button notNow = (Button) moreInfoDialog.findViewById(R.id.not_now_button);
        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(MAIN_TAG, "User tapped 'not now' button to dismiss more info dialog");
                moreInfoDialog.dismiss();
            }
        });

        // create the button for visiting the MOMA website
        Button visitMoma = (Button) moreInfoDialog.findViewById(R.id.visit_moma_button);
        visitMoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(MAIN_TAG, "User tapped button to visit MOMA url");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.moma.org"));
                startActivity(intent);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ensure that the dialog is dismissed when the activity is destroyed
        // needed in particular when orientation changes to prevent error
        if (moreInfoDialog != null && moreInfoDialog.isShowing()) {
            moreInfoDialog.dismiss();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(MAIN_TAG, "saving instance state");

        // if the dialog is showing then want to record in savedInstanceState that this is the case
        if (moreInfoDialog != null && moreInfoDialog.isShowing()) {
            Log.i(MAIN_TAG, "Saving instance of dialog");
            savedInstanceState.putBoolean("DIALOG_SHOWING", true);
        }
        super.onSaveInstanceState(savedInstanceState);
    }
}


