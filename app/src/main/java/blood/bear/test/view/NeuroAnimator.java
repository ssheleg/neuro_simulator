package blood.bear.test.view;

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

    private int currentPosition_0 = 0;

    private void anim_0() {
        for (int i = 0; i < triangles.size(); i++) {
            triangles.get(i).alpha = Math.max(0, triangles.get(i).alpha - animationIntensity);
        }

        triangles.get(currentPosition_0).alpha = 255;
        for (int i = 1; i < animationIntensity; i++) {
            if (currentPosition_0 + i < triangles.size()) {
                triangles.get(currentPosition_0 + i).alpha = Math.max(0, 255 - (i * animationIntensity));
            }
            if (currentPosition_0 - i >= 0) {
                triangles.get(currentPosition_0 - i).alpha = Math.max(0, 255 - (i * animationIntensity));
            }
        }

        currentPosition_0++;
        if (currentPosition_0 == triangles.size()) {
            currentPosition_0 = 0;
        }
    }

    private int currentPosition_1 = 0;

    private void anim_1() {
        trianglesMatrix[currentPosition_1][currentPosition_1].alpha = 255;
        if (currentPosition_1 + 1 < trianglesMatrix.length) {
            trianglesMatrix[currentPosition_1 + 1][currentPosition_1].alpha = 128;
            trianglesMatrix[currentPosition_1][currentPosition_1 + 1].alpha = 128;
        }
        for (int i = 0; i < trianglesMatrix.length; i++) {
            for (int j = 0; j < trianglesMatrix.length; j++) {
                trianglesMatrix[i][j].alpha = Math.max(0, trianglesMatrix[i][j].alpha - animationIntensity);
                if (i + 1 < trianglesMatrix.length) {
                    trianglesMatrix[i + 1][j].alpha = Math.max(0, trianglesMatrix[i + 1][j].alpha - animationIntensity);
                }
                if (j + 1 < trianglesMatrix.length) {
                    trianglesMatrix[i][j + 1].alpha = Math.max(0, trianglesMatrix[i][j + 1].alpha - animationIntensity);
                }
            }
        }
        currentPosition_1++;

        if (currentPosition_1 == trianglesMatrix.length) {
            currentPosition_1 = 0;
        }
    }


}
