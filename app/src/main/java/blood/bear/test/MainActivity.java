package blood.bear.test;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import blood.bear.test.view.ChipAIAnimationView;

public class MainActivity extends AppCompatActivity {

    private ChipAIAnimationView aiView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aiView = findViewById(R.id.aiView);
        ((SeekBar) findViewById(R.id.seekCount)).setOnSeekBarChangeListener(seekListener);
        ((SeekBar) findViewById(R.id.seekLineWidth)).setOnSeekBarChangeListener(seekListener);
        ((SeekBar) findViewById(R.id.seekRadius)).setOnSeekBarChangeListener(seekListener);
        ((SeekBar) findViewById(R.id.seekAnimationLat)).setOnSeekBarChangeListener(seekListener);
        ((SeekBar) findViewById(R.id.seekAnimationIntensity)).setOnSeekBarChangeListener(seekListener);
    }

    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (seekBar.getId() == R.id.seekCount) {
                aiView.setCount(i);
            } else if (seekBar.getId() == R.id.seekLineWidth) {
                aiView.setLineWidth(i);
            } else if (seekBar.getId() == R.id.seekRadius) {
                aiView.setRadius(Math.max(i / 100f, 0.1f));
            } else if (seekBar.getId() == R.id.seekAnimationLat) {
                aiView.setAnimationLat(i);
            } else if (seekBar.getId() == R.id.seekAnimationIntensity) {
                aiView.setAnimationIntensity(i);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public void onAnimateClick(View v) {
        aiView.animateNeurons(!aiView.isAnimate());
    }

    public void onColorNeurons(View v) {
        ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                .setColor(aiView.getColorNeuron())
                .create();
        dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int color) {
                aiView.setColorNeuron(color);
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getFragmentManager(), "color-picker-dialog");
    }

    public void onColorLines(View v) {
        ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                .setColor(aiView.getColorLine())
                .create();
        dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int color) {
                aiView.setColorLine(color);
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getFragmentManager(), "color-picker-dialog");
    }

    public void onColorBG(View v) {
        ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                .setColor(aiView.getColorBg())
                .create();
        dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int color) {
                aiView.setColorBg(color);
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getFragmentManager(), "color-picker-dialog");
    }

}
