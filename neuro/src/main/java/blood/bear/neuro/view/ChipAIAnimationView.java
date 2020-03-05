package blood.bear.neuro.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
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

    private boolean animating = false;
    private int animationIntensity = -1;
    private int animationLat = -1;
    private int animationAccelerate = -1;

    private Neuron[][] neurons = null;
    private List<Triangle> triangles = new ArrayList<>();
    private Triangle[][] trianglesMatrix = null;
    private NeuroAnimator neuroAnimator = null;

    public ChipAIAnimationView(Context context) {
        super(context);
        init(Color.BLACK, Color.CYAN, Color.CYAN, 13, 0.4f, 1f);
    }

    public ChipAIAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(Color.BLACK, Color.CYAN, Color.CYAN, 13, 0.4f, 1f);
    }

    public ChipAIAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(Color.BLACK, Color.CYAN, Color.CYAN, 13, 0.4f, 1f);
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
        this.animating = animate;
        if (neuroAnimator != null)
            neuroAnimator.setAnimate(animate);
    }

    public void setAnimationLat(int animationLat) {
        this.animationLat = animationLat;
        if (neuroAnimator != null)
            neuroAnimator.setAnimationLat(animationLat);
    }

    public void setAnimationIntensity(int animationIntensity) {
        this.animationIntensity = animationIntensity;
        if (neuroAnimator != null)
            neuroAnimator.setAnimationIntensity(animationIntensity);
    }

    public void setAnimationAccelerate(int animationAccelerate) {
        this.animationAccelerate = animationAccelerate;
        if (neuroAnimator != null)
            neuroAnimator.setAccelerate(animationAccelerate);
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
        return neuroAnimator != null ? neuroAnimator.getAnimationLat() : 1;
    }

    public int getAnimationIntensity() {
        return neuroAnimator != null ? neuroAnimator.getAnimationIntensity() : 1;
    }

    public boolean isAnimate() {
        return neuroAnimator != null && neuroAnimator.isAnimate();
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
        Log.d("AI_BB", "colorBg=" + colorBg + " colorNeuron=" + colorNeuron + " colorLine=" + colorLine
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
        NeuroAnimator animator = new NeuroAnimator(triangles, trianglesMatrix, this);
        if (neuroAnimator != null) {
            animator.setAnimate(neuroAnimator.isAnimate());
            animator.setAnimationIntensity(neuroAnimator.getAnimationIntensity());
            animator.setAnimationLat(neuroAnimator.getAnimationLat());
        } else {
            if (animationIntensity != -1)
                animator.setAnimationIntensity(animationIntensity);
            if (animationLat != -1)
                animator.setAnimationLat(animationLat);
            if (animationAccelerate != -1)
                animator.setAccelerate(animationAccelerate);
            animator.setAnimate(animating);
        }
        neuroAnimator = animator;
    }
}
