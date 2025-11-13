package com.pruparel.balancer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Maze {
    private static final String TAG = "Maze";

    private static final float COR = 0.3f; // Wall collision rebound energy constant
    private static final float SIMULATION_FACTOR = 0.1f; // scale down the accelerations
    private static final float FRICTION_SPEED_SLOWDOWN_FACTOR = 0.99f; // Slow down motion by 0.9 every frame
    private final List<Wall> mWalls;
    private final Vector3D<Integer> mNumMazeSquares; // x -> cols, y -> rows, z = unused.
    private int [][] mMazeDescription; // Last 4 bits -> L, R, T, B -> Indicate if these walls are present or not.
    private final float mMazeSquareSize; // in px;
    private final float mWallWidth; // in px
    private final Vector3D<Float> mMazeStartPos;
    private final Ball mBall;
    private final Paint mMazePaint;
    private final Paint mBallPaint;
    private Vector3D<Integer> mMazeStartSquare;
    private Vector3D<Integer> mMazeEndSquare;

    // Statistics for visuals
    private final Paint mBallDataPaint;
    private final Paint mAccDataPaint;
    private final Paint mElapsedTimePaint, mVictoryMessagePaint;
    private String mBallData, mAccelerationData, mElapsedTimeData, mVictoryMessageData;
    private long mGameStartTime;

    private boolean mDebugBall, mGameWon;

    public Maze( Vector3D<Float> mazeStartPos, Vector3D<Integer> numMazeSquares, float mazeSquareSize, int mazeColor, int ballColor, float wallWidth, boolean debugBall){

        // Game state should be not started yet
        Log.d(TAG, "Game initializing");
        this.mGameWon = false;

        // Initialize all the parameters
        this.mMazeStartPos = mazeStartPos;
        this.mNumMazeSquares = numMazeSquares;
        this.mMazeSquareSize = mazeSquareSize;
        this.mWallWidth = wallWidth;

        // Debug ball will be used to check the position, speed and accuracy of the ball
        mDebugBall = debugBall;

        // Fetch the sample maze, TODO we can fetch maze levels from other permanent storage locations
        this.SampleMaze();

        // Initialize the Ball Object
        float ballRadius = (mMazeSquareSize - wallWidth) / 2;

        Vector3D<Float> ballStart = new Vector3D<>( mMazeStartPos.x + mMazeStartSquare.x * mMazeSquareSize + ballRadius, mMazeStartPos.y + mMazeStartSquare.y * mMazeSquareSize + ballRadius, 0.0f);
        Log.d(TAG, "Ball Starting Location: " + ballStart);
        mBall = new Ball( ballRadius, ballStart );

        // Initialize the Maze Paint object
        mMazePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMazePaint.setColor(mazeColor);
        mMazePaint.setStrokeWidth(wallWidth);
        mMazePaint.setStyle(Paint.Style.STROKE);

        // Initialize the ball paint object
        mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBallPaint.setColor(ballColor);
        mBallPaint.setStyle(Paint.Style.FILL);

        // Initialize the statistics paints
        // Initialize the ball data paint
        mBallDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBallDataPaint.setColor(Color.GREEN);
        mBallDataPaint.setTextSize(50f);
        mBallDataPaint.setTextAlign(Paint.Align.CENTER);
        mBallData = "NOT INITIALIZED YET";

        // Initialize the acceleration data paint
        mAccDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAccDataPaint.setColor(Color.GREEN);
        mAccDataPaint.setTextSize(50f);
        mAccDataPaint.setTextAlign(Paint.Align.CENTER);
        mAccelerationData = "NOT INITIALIZED YET";

        // Initialize the elapsed time paint
        mElapsedTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mElapsedTimePaint.setColor(Color.GREEN);
        mElapsedTimePaint.setTextSize(50f);
        mElapsedTimePaint.setTextAlign(Paint.Align.CENTER);
        mElapsedTimeData = "00:00:00 s";

        // Victory Message Paint
        mVictoryMessagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mVictoryMessagePaint.setColor(Color.GREEN);
        mVictoryMessagePaint.setTextSize(75f);
        mVictoryMessagePaint.setTextAlign(Paint.Align.CENTER);
        mVictoryMessageData = "NOT INITIALIZED YET";

        // Initialize the Walls Container
        mWalls = new ArrayList<Wall>();

        // Add walls according to the description
        this.ConstructWalls();

        // Set the game to started
        Log.d(TAG, "user playing the game");

        // Start counting the time
        mGameStartTime = System.nanoTime();
    }

    private void ConstructWalls(){
        // Function to create walls from maze description and other parameters.

        int mazeRows = mNumMazeSquares.y;
        int mazeCols = mNumMazeSquares.x;

        // Iterate over the maze description
        for( int row = 0; row < mazeRows; row++ ){
            for ( int col = 0; col < mazeCols; col++ ){

                int mazeVal = this.mMazeDescription[row][col]; // Find the value of this maze square.

                // Calculate the coordinates of all the four corners of this square
                Vector3D<Float> leftTop = new Vector3D<>( this.mMazeStartPos.x + col * this.mMazeSquareSize, this.mMazeStartPos.y + row * this.mMazeSquareSize, 0.0f );
                Vector3D<Float> leftBottom = Vector3D.add(leftTop, new Vector3D<>(0f, this.mMazeSquareSize, 0f)); // Add one step down to left top
                Vector3D<Float> rightTop = Vector3D.add(leftTop, new Vector3D<>(this.mMazeSquareSize, 0f, 0f)); // Add one step right to left top
                Vector3D<Float> rightBottom = Vector3D.add(leftBottom, new Vector3D<>(this.mMazeSquareSize, 0f, 0f)); // Add one step right to left bottom

                // Check what all walls do we need.
                // TODO - here, we make sure walls are not repetitive by taking care in the description
                // Last 4 bits -> L, R, T, B -> Indicate if these walls are present or not.

                if ( ((mazeVal >> 3 ) & 1) == 1 ){
                    // This means we need a left wall
                    Wall leftWall = new Wall(leftTop, leftBottom);
                    mWalls.add(leftWall);
                }
                if ( ((mazeVal >> 2 ) & 1) == 1 ){
                    // This means we need a right wall
                    Wall rightWall = new Wall(rightTop, rightBottom);
                    mWalls.add(rightWall);
                }
                if ( ((mazeVal >> 1 ) & 1) == 1 ) {
                    // This means we need a top wall
                    Wall topWall = new Wall(leftTop, rightTop);
                    mWalls.add(topWall);
                }
                if ( (mazeVal & 1) == 1) {
                    Wall bottomWall = new Wall(leftBottom, rightBottom);
                    mWalls.add(bottomWall);
                }
            }
        }
        Log.d(TAG, "Added all walls per description.");
    }

    private void SampleMaze(){
        // Sample Maze for testing. 3x8
        // Last 4 bits -> L, R, T, B -> Indicate if these walls are present or not.
//        this.mMazeDescription = new int[][] {
//                { 11, 3, 3, 3, 3, 3, 3, 6 },
//                { 10, 3, 3, 3, 3, 3, 3, 5 },
//                { 9, 3, 3, 3, 3, 3, 3, 7 }
//        };
//
//        // Set the start and end squares for the ball
//        this.mMazeStartSquare = new Vector3D<>(0, 0, 0);
//        this.mMazeEndSquare = new Vector3D<>(7, 2, 0);

        this.mMazeDescription = new int[][] {
                { 11,  6, 11,  3,  3, 3,  6, 10, 7},
                { 10,  1,  3,  3,  3, 2,  5,  9, 6},
                {  9,  3,  3,  6, 14, 9,  7, 19, 5},
                { 10,  3,  3,  5,  9, 2,  3,  1, 6},
                {  8,  6, 10,  3,  3, 5, 10,  3, 4},
                { 12, 13, 12, 10,  3, 3,  5, 10, 4},
                {  9,  3,  5,  9,  7,11,  3,  5,13}
        };

        this.mMazeStartSquare = new Vector3D<>(4, 0, 0);
        this.mMazeEndSquare = new Vector3D<>(4, 6, 0);

        Log.d(TAG, "Added sample maze, of size y: " + this.mMazeDescription.length + "x" + this.mMazeDescription[0].length);
    }

    private long CalculateElapsedMilliSeconds(){
        return (System.nanoTime() - mGameStartTime) / 1_000_000;
    }

    @SuppressLint("DefaultLocale")
    public void draw(Canvas canvas, Bitmap mEndSquareSprite ){

        // NOTE Order is important here
        float textCenter = 510; // TODO we need a class member screen size to do this
        if(mGameWon){
            float textRowPx = mMazeStartPos.y + ( mNumMazeSquares.y + 2) * mMazeSquareSize;
            mVictoryMessageData = "CONGRATS, YOU WON ... ";
            canvas.drawText(mVictoryMessageData, textCenter, textRowPx, mVictoryMessagePaint);
            canvas.drawText(mElapsedTimeData, textCenter, textRowPx + 100, mVictoryMessagePaint);
        }
        else {
            long elapsedMs = CalculateElapsedMilliSeconds();
            mElapsedTimeData = String.format("%02d:%02d:%02d s", elapsedMs / 60000, elapsedMs / 1000, elapsedMs % 100);
        }
        // Draw the ball
        canvas.drawCircle( mBall.position.x, mBall.position.y, mBall.radius, mBallPaint );

        // Draw all the maze lines
        for( Wall wall : mWalls ){
            canvas.drawLine( wall.startPos.x, wall.startPos.y, wall.endPos.x, wall.endPos.y, mMazePaint );
        }
        // Draw the statistics - if debug is enabled
        if ( mDebugBall ){
            canvas.drawText(mBallData, textCenter, 200, mBallDataPaint);
            canvas.drawText(mAccelerationData, textCenter, 300, mAccDataPaint);
        }

        // Draw the time elapsed string here
        canvas.drawText(mElapsedTimeData, textCenter, 200, mElapsedTimePaint);

        // Draw the end square sprite
        if ( mEndSquareSprite != null ){
            Rect srcRect = new Rect(0, 0, mEndSquareSprite.getWidth(), mEndSquareSprite.getHeight());
            Rect destRect = new Rect(
                    (int) (mMazeStartPos.x + mMazeEndSquare.x * mMazeSquareSize),
                    (int) (mMazeStartPos.y + mMazeEndSquare.y * mMazeSquareSize),
                    (int) (mMazeStartPos.x + (mMazeEndSquare.x + 1) * mMazeSquareSize),
                    (int) (mMazeStartPos.y + (mMazeEndSquare.y + 1) * mMazeSquareSize)
            );
            // Draw the sprite
            canvas.drawBitmap(mEndSquareSprite, srcRect, destRect, null);
        }

    }

    // These functions will be called periodically by the Runnable animation -> whose time is related to the frame rate.
    public void updateBallAcceleration( Vector3D<Float> acceleration ){
        // Sensor values should be passed here to update the ball acceleration.
        // Also scale down by the factor
        mBall.acceleration = Vector3D.scalarProduct(acceleration, SIMULATION_FACTOR); // TODO Verify if this works.

        // Update the ball acceleration data
        if (mDebugBall)
        {
            mAccelerationData = "ACC: ( " + roundOff(mBall.acceleration.x, 1) + ", " + roundOff(mBall.acceleration.y, 1) + ")";
        }
    }

    public boolean isGameWon(){
        Vector3D<Integer> currentBallSquare = CurrentMazeSquarePos();
        // Check if we are in the end square
        if( Objects.equals(currentBallSquare.x, mMazeEndSquare.x) && Objects.equals(currentBallSquare.y, mMazeEndSquare.y) )
        {
            Log.d(TAG, "GameState: Player Won");
            mGameWon = true;
            return true;
        }

        return false;
    }

    private Vector3D<Integer> CurrentMazeSquarePos(){
        /**
         * From the current ball position, find which square we are in.
         * */

        // TODO handle the case where ball goes out of the maze bounds
        Vector3D<Integer> mazeSquarePos = new Vector3D<>(0, 0, 0);

        mazeSquarePos.x = (int) ( (mBall.position.x - mMazeStartPos.x) / mMazeSquareSize );
        mazeSquarePos.y = (int) ( (mBall.position.y - mMazeStartPos.y) / mMazeSquareSize );

        return mazeSquarePos;
    }
    public void updateBallVelocityAndPosition(){
        /**
         * Steps:
         * 1. With the current position of the ball, find if the ball is colliding with any wall, and its direction ( if yes ).
         * 2. If yes, apply restitution and reverse that component of the ball velocity
         * 3. With the updated ball acceleration received from sensor, find the final velocity.
         * */

        // Find collisions
        /**
         * From the current ball position, find which square we are in.
         * From the maze description, find out the walls that are present in this maze square.
         * If the ball center is closer to a wall than its radius, we are colliding, set that bit in result.
         * Order followed: Bit 3:Left, Bit 2:Right, Bit 1:Top, Bit 0:Bottom
         * */

        Vector3D<Integer> currSquareIndex = CurrentMazeSquarePos();

        // Find the square-walls description
        int squareDesc = mMazeDescription[currSquareIndex.y][currSquareIndex.x];

        // Find the absolute coordinates of the current maze square's boundaries
        float leftWallX = mMazeStartPos.x + currSquareIndex.x * mMazeSquareSize;
        float rightWallX = leftWallX + mMazeSquareSize;
        float topWallY = mMazeStartPos.y + currSquareIndex.y * mMazeSquareSize;
        float bottomWallY = topWallY + mMazeSquareSize;

        // Effective radius has a bigger margin for safety
        float effectiveRadius = mBall.radius + 2 * mWallWidth;

        // --- Find horizontal collisions ---
        boolean hasLeftWall = ((squareDesc >> 3) & 1) == 1;
        boolean hasRightWall = ((squareDesc >> 2) & 1) == 1;

        if (hasLeftWall && (mBall.position.x - mBall.radius < leftWallX)) {
            // Ball is colliding with the left wall
            // Update position and velocity according to collision
            mBall.velocity.x = -1 * COR * mBall.velocity.x;
            mBall.position.x = leftWallX + effectiveRadius;
        }
        if (hasRightWall && (mBall.position.x + mBall.radius > rightWallX)) {
            // Ball is colliding with the right wall
            // Update position and velocity according to collision
            mBall.velocity.x = -1 * COR * mBall.velocity.x;
            mBall.position.x = rightWallX - effectiveRadius;
        }

        // --- Find vertical collisions ---
        boolean hasTopWall = ((squareDesc >> 1) & 1) == 1;
        boolean hasBottomWall = (squareDesc & 1) == 1;

        if (hasTopWall && (mBall.position.y - mBall.radius < topWallY)) {
            // Ball is colliding with the top wall
            // Update position and velocity according to collision
            mBall.velocity.y = -1 * COR * mBall.velocity.y;
            mBall.position.y = topWallY + effectiveRadius;
        }
        if (hasBottomWall && (mBall.position.y + mBall.radius > bottomWallY)) {
            // Ball is colliding with the bottom wall
            // Update position and velocity according to collision
            mBall.velocity.y = -1 * COR * mBall.velocity.y;
            mBall.position.y = bottomWallY - effectiveRadius;
        }

        // Update the velocity according to acceleration
        mBall.velocity = Vector3D.add(mBall.velocity, mBall.acceleration);
        mBall.velocity = Vector3D.scalarProduct(mBall.velocity, FRICTION_SPEED_SLOWDOWN_FACTOR);

        // Update the ball position
        mBall.position = Vector3D.add(mBall.position, mBall.velocity);

        // Round the ball's position to perfect pixels - remove the floating points
        mBall.position.x = (float) Math.round(mBall.position.x);
        mBall.position.y = (float) Math.round(mBall.position.y);

        // Update the ball data text
        if ( mDebugBall ){
            mBallData = "POS: ( " + roundOff(mBall.position.x, 1) + ", " + roundOff(mBall.position.y, 1) + "), V: (" + roundOff(mBall.velocity.x, 1) + " , " + roundOff(mBall.velocity.y, 1) + ")";
        }

    }

    private String roundOff(float val, int digits){
        String formatString = "%." + digits + "f";
        return String.format(formatString, val);
    }
}
