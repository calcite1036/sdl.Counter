package jp.ac.titech.itpro.sdl.walkcounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

class RotationView extends View {
    private final static String TAG = jp.ac.titech.itpro.sdl.walkcounter.RotationView.class.getSimpleName();

    private double direction = 0;

    private final Paint paint = new Paint();

    public RotationView(Context context) {
        this(context, null);
    }

    public RotationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: w=" + w + " h=" + h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        int r = Math.min(cx, cy) - 20;
        paint.setColor(Color.LTGRAY);
        canvas.drawCircle(cx, cy, r, paint);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(cx, cy, 20, paint);
        paint.setStrokeWidth(10);
        float x = (float) (cx + r * Math.cos(direction));
        float y = (float) (cy + r * Math.sin(direction));
        canvas.drawLine(cx, cy, x, y, paint);
    }

    void setDirection(double th) {
        direction = th - Math.PI / 2;
        this.getRotation();
        invalidate();
    }
}
