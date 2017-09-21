package blood.bear.test.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Blood Bear on 20-Sep-17.
 */

public class ChipAIAnimationView extends View {

    private Paint backgroundPaint;
    private Paint neuronPaint;
    private Paint linesPaint;

    private int count;
    private float radius;
    private int colorBg;
    private int colorNeuron;
    private int colorLine;
    private float lineWidth;
    private float rootWidth, rootHeigth;
    private float fieldSizeX, fieldSizeY;

    private Neuron[][] neurons = null;
    private List<Triangle> triangles = new ArrayList<>();
    private Triangle[][] trianglesMatrix = null;


    private int animationLat = 1;
    private int animationIntensity = 1;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private int currentPosition = 0;
    private boolean animate = false;
    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!animate)
                return;

            triangles.get(currentPosition).alpha = 255;
            for (int i = 0; i < triangles.size(); i++) {
                if (i != currentPosition)
                    triangles.get(i).alpha = Math.max(0, triangles.get(i).alpha - animationIntensity);
            }
            currentPosition++;

            if (currentPosition == triangles.size()) {
                currentPosition = 0;
            }
            invalidate();
            uiHandler.postDelayed(animationRunnable, animationLat);
        }
    };

    public ChipAIAnimationView(Context context) {
        super(context);
        init(Color.BLACK, Color.CYAN, Color.CYAN, 13, 0.3f, 1f);
    }

    public ChipAIAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(Color.BLACK, Color.CYAN, Color.CYAN, 13, 0.3f, 1f);
    }

    public ChipAIAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(Color.BLACK, Color.CYAN, Color.CYAN, 13, 0.3f, 1f);
    }

    public void init(int colorBg, int colorNeuron, int colorLine, int count, float radius, float lineWidth) {
        this.count = Math.max(count, 3);
        this.colorBg = colorBg;
        this.colorNeuron = colorNeuron;
        this.colorLine = colorLine;
        this.radius = Math.max(radius, 0.1f);
        this.radius = Math.min(this.radius, 1f);
        this.lineWidth = Math.max(lineWidth, 1f);
        neuronPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        neuronPaint.setStyle(Paint.Style.FILL);
        neuronPaint.setColor(colorNeuron);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(colorBg);

        linesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linesPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linesPaint.setColor(colorLine);
        linesPaint.setStrokeWidth(lineWidth);

        if (rootHeigth != 0 && rootWidth != 0) {
            createNetwork();
        }
        invalidate();
    }

    public void animateNeurons(boolean animate) {
        uiHandler.removeCallbacks(animationRunnable);
        this.animate = animate;
        for (int i = 0; i < triangles.size(); i++) {
            triangles.get(i).alpha = animate ? 0 : 255;
        }
        if (animate) {
            uiHandler.post(animationRunnable);
        }
    }

    public void setAnimationLat(int animationLat) {
        this.animationLat = Math.max(animationLat, 1);
    }

    public void setAnimationIntensity(int animationIntensity) {
        this.animationIntensity = Math.max(animationIntensity, 1);
    }

    public void setCount(int count) {
        this.count = Math.max(count, 3);
        createNetwork();
        invalidate();
    }

    public void setRadius(float radius) {
        this.radius = Math.max(radius, 0.1f);
        this.radius = Math.min(this.radius, 1f);
        createNetwork();
        invalidate();
    }

    public void setColorBg(int colorBg) {
        this.colorBg = colorBg;
        backgroundPaint.setColor(colorBg);
        invalidate();

    }

    public void setColorNeuron(int colorNeuron) {
        this.colorNeuron = colorNeuron;
        neuronPaint.setColor(colorNeuron);
        invalidate();
    }

    public void setColorLine(int colorLine) {
        this.colorLine = colorLine;
        linesPaint.setColor(colorLine);
        invalidate();
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = Math.max(lineWidth, 1f);
        linesPaint.setStrokeWidth(lineWidth);
        invalidate();
    }

    public int getCount() {
        return count;
    }

    public float getRadius() {
        return radius;
    }

    public int getColorBg() {
        return colorBg;
    }

    public int getColorNeuron() {
        return colorNeuron;
    }

    public int getColorLine() {
        return colorLine;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public int getAnimationLat() {
        return animationLat;
    }

    public int getAnimationIntensity() {
        return animationIntensity;
    }

    public boolean isAnimate() {
        return animate;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);
        int minh = getPaddingBottom() + getPaddingTop() + getSuggestedMinimumHeight();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 1);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());
        rootWidth = (float) w * 1.3f;
        rootHeigth = (float) h * 1.3f;
        createNetwork();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (neurons == null || neurons.length == 0)
            return;
        Log.i("AI_BB", "colorBg=" + colorBg + " colorNeuron=" + colorNeuron + " colorLine=" + colorLine
                + " count=" + count + " radius=" + radius + " lineWidth=" + lineWidth);
        canvas.drawColor(colorBg);
        canvas.translate(-fieldSizeX, -fieldSizeY);
        for (Triangle triangle : triangles) {
            if (triangle.active) {
                linesPaint.setAlpha(triangle.alpha);
                neuronPaint.setAlpha(triangle.alpha);
                if (triangle.neuron_1 != null && triangle.neuron_2 != null) {
                    canvas.drawLine(triangle.neuron_1.x, triangle.neuron_1.y, triangle.neuron_2.x, triangle.neuron_2.y, linesPaint);
                }
                if (triangle.neuron_1 != null && triangle.neuron_3 != null) {
                    canvas.drawLine(triangle.neuron_1.x, triangle.neuron_1.y, triangle.neuron_3.x, triangle.neuron_3.y, linesPaint);
                }
                if (triangle.neuron_2 != null && triangle.neuron_3 != null) {
                    canvas.drawLine(triangle.neuron_2.x, triangle.neuron_2.y, triangle.neuron_3.x, triangle.neuron_3.y, linesPaint);
                }


                if (triangle.neuron_1 != null) {
                    canvas.drawCircle(triangle.neuron_1.x, triangle.neuron_1.y, triangle.neuron_1.radius, neuronPaint);
                }
                if (triangle.neuron_2 != null) {
                    canvas.drawCircle(triangle.neuron_2.x, triangle.neuron_2.y, triangle.neuron_2.radius, neuronPaint);
                }
                if (triangle.neuron_3 != null) {
                    canvas.drawCircle(triangle.neuron_3.x, triangle.neuron_3.y, triangle.neuron_3.radius, neuronPaint);
                }
            }
        }
    }

    private void createNetwork() {
        if (count <= 0)
            return;
        fieldSizeX = rootWidth / (float) count;
        fieldSizeY = rootHeigth / (float) count;
        float neuroRadius = Math.min(fieldSizeX, fieldSizeY) / 3f * radius;
        neurons = new Neuron[count][count];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                float x = (fieldSizeX * i) + fieldSizeX / 2f;
                float y = (fieldSizeY * j) + fieldSizeY / 2f;
                int deltaX = new Random().nextInt((int) (fieldSizeX / 1.5f));
                int deltaY = new Random().nextInt((int) (fieldSizeY / 1.5f));
                x += ((fieldSizeX / 1.5f) / 2) - deltaX;
                y += ((fieldSizeY / 1.5f) / 2) - deltaY;
                neurons[i][j] = new Neuron(i, j, x, y, neuroRadius - new Random().nextInt((int) (neuroRadius / 3)));
            }
        }
        triangles.clear();
        fill();
    }

    private void fill() {
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                Neuron neuron_1 = neurons[i][j];
                Neuron neuron_2 = null, neuron_3 = null;
                if (i < count - 1)
                    neuron_2 = neurons[i + 1][j];
                if (j < count - 1)
                    neuron_3 = neurons[i][j + 1];
                triangles.add(new Triangle(neuron_1, neuron_2, neuron_3));
            }
        }

        int n = count;
        trianglesMatrix = new Triangle[n][n];
        int pos = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                trianglesMatrix[i][j] = triangles.get(pos++);
            }
        }
        triangles.clear();
        for (int i = 1; i < n + n; i++) {
            for (int j = i - n + 1 > 0 ? i - n + 1 : 0; j < Math.min(n, i + 1); j++) {
                triangles.add(trianglesMatrix[j][i - j]);
            }
        }

    }


    class Neuron {
        final int i, j;
        final float x, y;
        final float radius;

        Neuron(int i, int j, float x, float y, float radius) {
            this.i = i;
            this.j = j;
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    class Triangle {
        final Neuron neuron_1, neuron_2, neuron_3;
        boolean active = true;
        int alpha = 255;

        Triangle(Neuron neuron_1, Neuron neuron_2, Neuron neuron_3) {
            this.neuron_1 = neuron_1;
            this.neuron_2 = neuron_2;
            this.neuron_3 = neuron_3;
        }
    }
}
