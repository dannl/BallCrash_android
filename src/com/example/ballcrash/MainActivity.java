
package com.example.ballcrash;

import com.example.ballcrash.MainActivity.BallPainter.MotionListener;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends Activity {

    private Ball[] mLeftSideBalls;
    private Ball[] mRightSideBalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FrameLayout layout = new FrameLayout(this);
        setContentView(layout);
        initBalls();
        final BallPainter ballPainter = new BallPainter(this, mLeftSideBalls, mRightSideBalls);
        layout.addView(ballPainter);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        final Button startButton = new Button(this);
        layout.addView(startButton, params);
        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ballPainter.startMotion();
            }
        });
        ballPainter.setMotionListener(new MotionListener() {

            @Override
            public void onMotionStarted() {
                startButton.setEnabled(false);
            }

            @Override
            public void onMotionEnded() {
                startButton.setEnabled(true);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    private enum Side {
        LEFT, RIGHT
    }

    private static final double BALL_VELOCITY_LEFT = 4;

    private static final double BALL_VELOCITY_RIGHT = 8;

    private static final double LEFT_BALL_DISTANCE = 80;

    private static final double RIGHT_BALL_DISTANCE = 160;

    private static final int BALL_COUNT = 3;

    public static final int BALL_SIZE = 20;

    private void initBalls() {
        final Ball[] leftSideBalls = new Ball[BALL_COUNT];
        final Ball[] rightSideBalls = new Ball[BALL_COUNT];
        for (int i = 0; i < BALL_COUNT; i++) {
            leftSideBalls[i] = new Ball(i, BALL_COUNT, Side.LEFT, LEFT_BALL_DISTANCE);
        }

        for (int i = 0; i < BALL_COUNT; i++) {
            rightSideBalls[i] = new Ball(i, BALL_COUNT, Side.RIGHT, RIGHT_BALL_DISTANCE);
        }

        double velocitySum = BALL_VELOCITY_LEFT + BALL_VELOCITY_RIGHT;
        leftSideBalls[0].mTimeGaps[0] = 0;
        rightSideBalls[0].mTimeGaps[0] = 0;
        for (int i = 0; i < BALL_COUNT - 1; i++) {
                leftSideBalls[i].mTimeGaps[1] = LEFT_BALL_DISTANCE / velocitySum;
                rightSideBalls[i].mTimeGaps[1] = RIGHT_BALL_DISTANCE / velocitySum;
        }

        for (int j = 0; j < BALL_COUNT * 2 - 1; j++) {
            if (j == 1) {
                continue;
            }
            for (int i = 0; i < BALL_COUNT; i++) {
                if (j >= (BALL_COUNT - i) * 2 - 1 || (j == 0 && i == 0)) {
                    continue;
                }
                if (i == 0) {
                    int k = j / 2;
                    if (j % 2 == 0) {
                        leftSideBalls[i].mTimeGaps[2 * k] = (leftSideBalls[i].mTimeGaps[2 * k - 1] * BALL_VELOCITY_RIGHT + rightSideBalls[i].mTimeGaps[2 * k - 1] * BALL_VELOCITY_LEFT) / velocitySum;
                        rightSideBalls[i].mTimeGaps[2 * k] = (rightSideBalls[i].mTimeGaps[2 * k - 1] * BALL_VELOCITY_LEFT + leftSideBalls[i].mTimeGaps[2 * k - 1] * BALL_VELOCITY_RIGHT) / velocitySum;
                    } else {
                        leftSideBalls[i].mTimeGaps[2 * k + 1] = (leftSideBalls[i + 1].mTimeGaps[2 * k] * BALL_VELOCITY_LEFT + leftSideBalls[i].mTimeGaps[2 * k - 2] * BALL_VELOCITY_RIGHT) / velocitySum;
                        rightSideBalls[i].mTimeGaps[2 * k + 1] = (rightSideBalls[i + 1].mTimeGaps[2 * k] * BALL_VELOCITY_RIGHT + rightSideBalls[i].mTimeGaps[2 * k - 2] * BALL_VELOCITY_LEFT) / velocitySum;
                    }
                } else {
                    if (j == 0) {
                        leftSideBalls[i].mTimeGaps[j] = leftSideBalls[i - 1].mTimeGaps[1] + leftSideBalls[i - 1].mTimeGaps[0];
                        rightSideBalls[i].mTimeGaps[j] = rightSideBalls[i - 1].mTimeGaps[1] + rightSideBalls[i - 1].mTimeGaps[0];
                    } else {
                        int k = j / 2;
                        if (j % 2 == 0) {
                            leftSideBalls[i].mTimeGaps[2 * k] = (leftSideBalls[i].mTimeGaps[2 * k - 1] * BALL_VELOCITY_RIGHT + leftSideBalls[i - 1].mTimeGaps[2 * k - 1] * BALL_VELOCITY_LEFT) / velocitySum;
                            rightSideBalls[i].mTimeGaps[2 * k] = (rightSideBalls[i].mTimeGaps[2 * k - 1] * BALL_VELOCITY_LEFT + rightSideBalls[i - 1].mTimeGaps[2 * k - 1] * BALL_VELOCITY_RIGHT) / velocitySum;
                        } else {
                            leftSideBalls[i].mTimeGaps[2 * k + 1] = (leftSideBalls[i + 1].mTimeGaps[2 * k] * BALL_VELOCITY_LEFT + leftSideBalls[i].mTimeGaps[2 * k - 2] * BALL_VELOCITY_RIGHT) / velocitySum;
                            rightSideBalls[i].mTimeGaps[2 * k + 1] = (rightSideBalls[i + 1].mTimeGaps[2 * k] * BALL_VELOCITY_RIGHT + rightSideBalls[i].mTimeGaps[2 * k - 2] * BALL_VELOCITY_LEFT) / velocitySum;
                        }
                    }
                }
            }
        }
        System.out.println("left");
        for (int i = 0; i < leftSideBalls.length; i++) {
            Log.d("TEST", leftSideBalls[i].toString());
            leftSideBalls[i].buildTimeGaps();
            leftSideBalls[i].buildEndingDistanceAndVelocitiesMap(Side.LEFT);
        }
        System.out.println("right");
        for (int i = 0; i < rightSideBalls.length; i++) {
            rightSideBalls[i].buildTimeGaps();
            rightSideBalls[i].buildEndingDistanceAndVelocitiesMap(Side.RIGHT);
            Log.d("TEST", rightSideBalls[i].toString());
        }

        mLeftSideBalls = leftSideBalls;
        mRightSideBalls = rightSideBalls;
    }


    private static final class Ball {

        private static final RectF TEMP_RECT = new RectF();

        private double[] mTimeGaps;
        private HashMap<Double, Double> mEndingDistances;
        private HashMap<Double, Double> mVelocities;
        private int mIndex;
        private Side mSide;
        private double mBallDistance;
        private Paint mPaint;
        private int mCurrentTimeGapIndex;

        Ball (final int index, final int ballCount, Side side, double ballDistance) {
            mIndex = index;
            mSide = side;
            mBallDistance = ballDistance;
            final int inversedIndex = ballCount - index;
            mTimeGaps = new double[inversedIndex * 2 - 1];
            mEndingDistances = new HashMap<Double, Double>();
            mVelocities = new HashMap<Double, Double>();
            mPaint = new Paint();
            if (mSide == Side.LEFT) {
                mPaint.setColor(Color.BLACK);
            } else {
                mPaint.setColor(Color.RED);
            }
        }

        public void buildTimeGaps() {
            final double[] timeGaps = mTimeGaps;
            for (int i = 1; i < timeGaps.length; i++) {
                timeGaps[i] = timeGaps[i] + timeGaps[i - 1];
            }
        }

        public void buildEndingDistanceAndVelocitiesMap(final Side side) {
            final double[] timeGaps = mTimeGaps;
            final HashMap<Double, Double> endingDistances = mEndingDistances;
            final HashMap<Double, Double> velocities = mVelocities;
            double startingVelocity;
            double inverseVelocity;
            if (side == Side.LEFT) {
                startingVelocity = BALL_VELOCITY_LEFT;
                inverseVelocity = BALL_VELOCITY_RIGHT;
            } else {
                startingVelocity = BALL_VELOCITY_RIGHT;
                inverseVelocity = BALL_VELOCITY_LEFT;
            }
            for (int i = 0; i < timeGaps.length; i++) {
                if (i % 2 == 0) {
                    double distance;
                    velocities.put(timeGaps[i], startingVelocity);
                    if (i  > 0) {
                        distance = startingVelocity * (timeGaps[i] - timeGaps[i - 1]);
                        distance += endingDistances.get(timeGaps[i - 1]);
                    } else {
                        distance = startingVelocity * timeGaps[i];
                    }
                    endingDistances.put(timeGaps[i], distance);
                } else {
                    velocities.put(timeGaps[i], inverseVelocity);
                    double distance = endingDistances.get(timeGaps[i - 1]) - inverseVelocity * (timeGaps[i] - timeGaps[i - 1]);
                    endingDistances.put(timeGaps[i], distance);
                }
            }

        }

        void draw(final Canvas canvas, final long drawingTime, int canvasWidth, int canvasHeight) {
            double drawingTimeInTimeGap = (double) drawingTime / 500;
            final int centerX = canvasWidth / 2;
            final int centerY = canvasHeight / 2;
            double startX;
            if (mSide == Side.LEFT) {
                startX = centerX - mBallDistance * mIndex;
            } else {
                startX = centerX + mBallDistance * mIndex;
            }
            final double[] timeGaps = mTimeGaps;
            int toIndex = -1;
            for (int i = 0; i < timeGaps.length; i++) {
                if (drawingTimeInTimeGap < timeGaps[i]) {
                    toIndex = i;
                    break;
                }
            }
            if (toIndex == -1) {
                toIndex = timeGaps.length;
                mCurrentTimeGapIndex = toIndex;
                return;
            }
            for (int i = 0; i < toIndex; i++) {
                if (mSide == Side.LEFT) {
                    if (i % 2 == 0) {
                        startX += mEndingDistances.get(timeGaps[i]);
                    } else {
                        startX -= mEndingDistances.get(timeGaps[i]);
                    }
                } else {
                    if (i % 2 == 0) {
                        startX -= mEndingDistances.get(timeGaps[i]);
                    } else {
                        startX += mEndingDistances.get(timeGaps[i]);
                    }
                }
            }
            if (toIndex != 0) {
                 drawingTimeInTimeGap = drawingTimeInTimeGap - timeGaps[toIndex - 1];
            }
            final double distanceInTimeGap = mVelocities.get(timeGaps[toIndex]) * drawingTimeInTimeGap;
            if (mSide == Side.LEFT) {
                if (toIndex % 2 == 0) {
                    startX += distanceInTimeGap;
                } else {
                    startX -= distanceInTimeGap;
                }
            } else {
                if (toIndex % 2 == 0) {
                    startX -= distanceInTimeGap;
                } else {
                    startX += distanceInTimeGap;
                }
            }
            TEMP_RECT.set((float) (startX - BALL_SIZE), centerY - BALL_SIZE, (float) (startX + BALL_SIZE), centerY + BALL_SIZE);
            canvas.drawOval(TEMP_RECT, mPaint);
            mCurrentTimeGapIndex = toIndex;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            final double[] timeGaps = mTimeGaps;
            for (double d : timeGaps) {
                builder.append(d).append(",");
            }
            return builder.toString();
        }

    }

    public static final class BallPainter extends View {

        public interface MotionListener {
            public void onMotionStarted();
            public void onMotionEnded();
        }

        private Ball[] mLeftSideBalls;
        private Ball[] mRightSideBalls;
        private boolean mMotionStarted;
        private MotionListener mMotionListener;
        private long mStartingTime;

        public BallPainter(Context context, final Ball[] leftSideBalls, final Ball[] rightSideBalls) {
            super(context);
            mLeftSideBalls = leftSideBalls;
            mRightSideBalls = rightSideBalls;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final long drawingTime = System.currentTimeMillis() - mStartingTime;
            final Ball[] leftSideBalls = mLeftSideBalls;
            final Ball[] rightSideBalls = mRightSideBalls;
            for (Ball ball : rightSideBalls) {
                ball.draw(canvas, drawingTime, getWidth(), getHeight());
            }
            for (Ball ball : leftSideBalls) {
                ball.draw(canvas, drawingTime, getWidth(), getHeight());
            }
            if (leftSideBalls[0].mCurrentTimeGapIndex == leftSideBalls[0].mTimeGaps.length) {
                mMotionStarted = false;
                post(new Runnable() {

                    @Override
                    public void run() {
                        if (mMotionListener != null) {
                            mMotionListener.onMotionEnded();
                        }
                    }
                });
            }
            if (mMotionStarted) {
                invalidate();
            }

        }

        void setMotionListener(final MotionListener listener) {
            mMotionListener = listener;
        }

        void startMotion() {
            if (mMotionStarted) {
                return;
            }
            mMotionStarted = true;
            mStartingTime = System.currentTimeMillis();
            if (mMotionListener != null) {
                mMotionListener.onMotionStarted();
            }
            invalidate();
        }





    }





}
