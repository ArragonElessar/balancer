package com.pruparel.balancer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Maze {
    private static final String TAG = "MAZE";

    private float mMazeStartX, mMazeStartY;
    private int mMazeHeight, mMazeWidth; // The number of maze squares in Y and X direction.
    private float mMazeSquareSizePx; // Number of pixels that a square size exhibits, should be minimum 2x the Ball Size.
    private int [][] mMazeDescription;

    /**
     * Description for Maze
     * The value in ( row, col ), called val of mMazeDescription are to be interpreted as follows
     * Look at the first 4 bits of val  **** _ _ _ _
     * These signify if a wall is present in L R T B -> left right top bottom respectively
     * */

    private void SetSampleMaze(){
        // Generate a maze with all left walls -> see description
        for(int row = 0; row < mMazeHeight; row++){
            for(int col = 0; col < mMazeWidth; col++){
                mMazeDescription[row][col] = 2; // Left and top
            }
        }
    }
    public Maze(float mazeStartX, float mazeStartY, int mazeHeight, int mazeWidth, float mazeSquareSizePx) {
        // Initialize variables and log values
        this.mMazeStartX = mazeStartX;
        this.mMazeStartY = mazeStartY;
        this.mMazeHeight = mazeHeight;
        this.mMazeWidth = mazeWidth;
        this.mMazeSquareSizePx = mazeSquareSizePx;

        this.mMazeDescription = new int[mMazeHeight][mMazeWidth];
        // We need to initialize the Maze with some values to display
        SetSampleMaze();

        Log.d(TAG, "Maze: Maze Created");
    }

    public List<Line> calculateMazeLines(){
        List<Line> lines = new ArrayList<>();

        // We need to find all the lines needed to be drawn to represent our MazeDescription
        for(int row = 0; row < mMazeHeight; row++){
            for(int col = 0; col < mMazeWidth; col++){
                int val = mMazeDescription[row][col];

                // The starting point for this maze square
                float startX = mMazeStartX + col * mMazeSquareSizePx;
                float startY = mMazeStartY + row * mMazeSquareSizePx;

                // Check if we need a left line - shift right by 3 and check
                if( (( val >> 3 ) & 1 ) == 1 ){
                    Line leftLine = new Line(startX, startY, startX, startY + mMazeSquareSizePx);
                    lines.add(leftLine);
//                    Log.d(TAG, "calculateMazeLines: Adding Left Line: (" + startX + "," + startY + ") -> (" + startX + "," + (startY + mMazeSquareSizePx) + ")");
                }

                // Check if we need a right line
                if( (( val >> 2 ) & 1 ) == 1 ){
                    Line rightLine = new Line(startX + mMazeSquareSizePx, startY, startX + mMazeSquareSizePx, startY + mMazeSquareSizePx);
                    lines.add(rightLine);
//                    Log.d(TAG, "calculateMazeLines: Adding Right Line: (" + startX + "," + startY + ") -> (" + startX + "," + (startY + mMazeSquareSizePx) + ")");
                }

                // Check if we need a top line
                if( (( val >> 1 ) & 1 ) == 1 ){
                    Line topLine = new Line(startX, startY, startX + mMazeSquareSizePx, startY);
                    lines.add(topLine);
//                    Log.d(TAG, "calculateMazeLines: Adding Right Line: (" + startX + "," + startY + ") -> (" + startX + "," + (startY + mMazeSquareSizePx) + ")");
                }
                // Check if we need a bottom line

            }
        }

        return lines;
    }

    public void draw(Canvas canvas, Paint paint){
        /**
         * Steps: Calculate the StartX, StartY, EndX, EndY for all the lines that are required by the MazeSquares, per square.
         * Store them in a floating array and pass it to canvas.drawLines using paint.
         * */
        List<Line> lines = calculateMazeLines();
        int numLines = lines.size();
//        float[] points = new float[ 4 * numLines ];
        for(int i = 0; i < numLines; i++) {
            Line line = lines.get(i);
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint);
        }
    }
}
