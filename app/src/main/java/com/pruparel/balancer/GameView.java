package com.pruparel.balancer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GameView extends View {

    private static final String TAG = "GameView";
    private static final int FRAME_RATE = 30; // Desired frames per second
    private static final long FRAME_TIME_MS = 1000 / FRAME_RATE;

    private Paint mBallPaint;
    private int mBallRadius;
    private float mBallX, mBallY; // Use float for smoother positioning

    // Velocities for movement, to be updated by sensor data later
    private float mVelocityX = 10.0f;
    private float mVelocityY = 10.0f;

    public GameView(Context context) {
        super(context);
        init(null);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Basic definition of the ball
        mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Enable anti-aliasing for smooth edges
        mBallPaint.setColor(Color.RED);
        mBallPaint.setStyle(Paint.Style.FILL); // A solid ball is usually desired

        mBallRadius = 50; // Use a more manageable initial radius
    }

    // Called when the view's size is first determined or changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Initialize ball position to the center of the view
        // This is the correct place to get final dimensions
        mBallX = w / 2f;
        mBallY = h / 2f;
        Log.d(TAG, "onSizeChanged: View dimensions are " + w + "x" + h);
    }

    private final Runnable animationLoop = new Runnable() {
        @Override
        public void run() {
            // Update ball position based on velocity
            mBallX += mVelocityX;
            mBallY += mVelocityY;

            // Check for collisions with view boundaries
            // Left boundary
            if (mBallX - mBallRadius < 0) {
                mBallX = mBallRadius;
                mVelocityX *= -1; // Reverse direction
            }
            // Right boundary
            if (mBallX + mBallRadius > getWidth()) {
                mBallX = getWidth() - mBallRadius;
                mVelocityX *= -1; // Reverse direction
            }
            // Top boundary
            if (mBallY - mBallRadius < 0) {
                mBallY = mBallRadius;
                mVelocityY *= -1; // Reverse direction
            }
            // Bottom boundary
            if (mBallY + mBallRadius > getHeight()) {
                mBallY = getHeight() - mBallRadius;
                mVelocityY *= -1; // Reverse direction
            }

            // Request a redraw
            invalidate();

            // Schedule the next frame
            postDelayed(this, FRAME_TIME_MS);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Start the animation loop when the view is attached to a window
        post(animationLoop);
    }

    @Override
    protected void onDetachedFromWindow() {
        // Stop the animation loop when the view is detached
        removeCallbacks(animationLoop);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // Draw the ball at its current position
        canvas.drawCircle(mBallX, mBallY, mBallRadius, mBallPaint);
    }

    /**
     * Public method to update the ball's velocity based on external input (e.g., sensors).
     * @param newVelocityX The new velocity for the x-axis.
     * @param newVelocityY The new velocity for the y-axis.
     */
    public void updateVelocity(float newVelocityX, float newVelocityY) {
        this.mVelocityX = newVelocityX;
        this.mVelocityY = newVelocityY;
    }
}
