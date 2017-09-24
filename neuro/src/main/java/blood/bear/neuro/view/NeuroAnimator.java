package blood.bear.neuro.view;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.List;

/**
 * Created by Blood Bear on 21-Sep-17.
 */

public final class NeuroAnimator {

    private final View root;
    private final List<Triangle> triangles;
    private final Triangle[][] trianglesMatrix;

    private int animationLat = 1;
    private int animationIntensity = 1;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean animate = false;
    private int mode = 0;
    private int accelerate = 0;

    private int currentPosition = 0;


    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!animate)
                return;
            switch (mode) {
                case 0:
                    anim_0();
                    break;
                case 1:
                    anim_1();
                    break;
                default:
                    setAnimate(false);
                    break;
            }
            root.invalidate();
            uiHandler.postDelayed(animationRunnable, animationLat);
        }
    };

    public NeuroAnimator(List<Triangle> triangles, Triangle[][] trianglesMatrix, View root) {
        this.triangles = triangles;
        this.trianglesMatrix = trianglesMatrix;
        this.root = root;
    }

    public int getAnimationLat() {
        return animationLat;
    }

    public void setAnimationLat(int animationLat) {
        this.animationLat = Math.max(animationLat, 1);
    }

    public int getAnimationIntensity() {
        return animationIntensity;
    }

    public void setAnimationIntensity(int animationIntensity) {
        this.animationIntensity = Math.max(animationIntensity, 1);
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setAnimate(boolean animate) {
        uiHandler.removeCallbacks(animationRunnable);
        this.animate = animate;
        for (int i = 0; i < triangles.size(); i++) {
            triangles.get(i).alpha = animate ? 0 : 255;
            triangles.get(i).increment = true;
        }
        if (animate) {
            uiHandler.post(animationRunnable);
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        for (int i = 0; i < triangles.size(); i++) {
            triangles.get(i).alpha = animate ? 0 : 255;
        }
    }

    public int getAccelerate() {
        return accelerate;
    }

    public void setAccelerate(int accelerate) {
        this.accelerate = Math.min(0, accelerate);
    }

    private void anim_0() {
        for (int i = 0; i < triangles.size(); i++) {
            Triangle triangle = triangles.get(i);
            if (i == currentPosition) {
                for (int j = currentPosition; j <= currentPosition + accelerate && j < triangles.size(); j++) {
                    triangles.get(j).alpha = 255;
                }
            } else {
                triangle.alpha = Math.max(0, triangle.alpha - animationIntensity);
            }
        }
        currentPosition++;
        currentPosition += accelerate;
        if (currentPosition >= triangles.size()) {
            currentPosition = 0;
        }
    }

    private void anim_1() {
    }


}
