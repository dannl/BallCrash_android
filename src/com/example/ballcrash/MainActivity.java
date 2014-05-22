
package com.example.ballcrash;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import java.util.HashMap;

public class MainActivity extends Activity {

    public static final int BALL_SIZE = 20;
    private static final RectF TEMP_RECT = new RectF();

    private Ball[] mLeftSideBalls;
    private Ball[] mRightSideBalls;
    private double mLeftBallVelocity = 4;
    private double mRightBallVelocity = 20;
    private double mLeftBallDistance = 130;
    private double mRightBallDistance = 210;
    private int mBallCount = 4;

    private BallPainter mBallPainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("TEST", String.valueOf(((26.666 + 26.666) * 4 + (20 + 13.33 - 6.66) * 8) / 12));
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.mainContainer);
        final Button startButton = (Button) findViewById(R.id.start);
        mBallPainter = new BallPainter(this);
        final SeekBar ballCount = (SeekBar) findViewById(R.id.ball_count);
        final SeekBar leftBallDistance = (SeekBar) findViewById(R.id.left_ball_dis);
        final SeekBar rightBallDistance = (SeekBar) findViewById(R.id.right_ball_dis);
        final SeekBar leftVelocity = (SeekBar) findViewById(R.id.left_ball_v);
        final SeekBar rightVelocity = (SeekBar) findViewById(R.id.right_ball_v);

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
                android.widget.RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE, R.id.start);
        params.addRule(RelativeLayout.BELOW, R.id.controller);
        layout.addView(mBallPainter, params);

        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mBallPainter.mMotionStarted) {
                    mBallPainter.stopMotion();
                } else {
                    startBallAnimation();
                }
            }
        });

        SeekBar.OnSeekBarChangeListener changeListener = new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                startBallAnimation();
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                mBallPainter.stopMotion();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUsr) {
                if (seekBar == ballCount) {
                    mBallCount = progress + 3;
                    ((TextView) ((ViewGroup) ballCount.getParent()).getChildAt(0))
                            .setText("ball count: " + mBallCount);
                } else if (seekBar == leftBallDistance) {
                    mLeftBallDistance = progress + BALL_SIZE * 2;
                    ((TextView) ((ViewGroup) leftBallDistance.getParent()).getChildAt(0))
                            .setText("left ball distance: " + mLeftBallDistance);
                } else if (seekBar == rightBallDistance) {
                    mRightBallDistance = progress + BALL_SIZE * 2;
                    ((TextView) ((ViewGroup) rightBallDistance.getParent()).getChildAt(0))
                            .setText("right ball distance: " + mRightBallDistance);
                } else if (seekBar == leftVelocity) {
                    mLeftBallVelocity = progress + 5;
                    ((TextView) ((ViewGroup) leftVelocity.getParent()).getChildAt(0))
                            .setText("left ball velocity: " + mLeftBallVelocity);
                } else if (seekBar == rightVelocity) {
                    mRightBallVelocity = progress + 5;
                    ((TextView) ((ViewGroup) rightVelocity.getParent()).getChildAt(0))
                            .setText("right ball velocity: " + mRightBallVelocity);
                }
            }
        };

        ballCount.setOnSeekBarChangeListener(changeListener);
        leftBallDistance.setOnSeekBarChangeListener(changeListener);
        rightBallDistance.setOnSeekBarChangeListener(changeListener);
        leftVelocity.setOnSeekBarChangeListener(changeListener);
        rightVelocity.setOnSeekBarChangeListener(changeListener);
    }

    private void startBallAnimation() {
        initBalls();
        mBallPainter.setBalls(mLeftSideBalls, mRightSideBalls);
        mBallPainter.startMotion();
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

    private void initBalls() {
        final Ball[] leftSideBalls = new Ball[mBallCount];
        final Ball[] rightSideBalls = new Ball[mBallCount];
        for (int i = 0; i < mBallCount; i++) {
            leftSideBalls[i] = new Ball(i, mBallCount, Side.LEFT, mLeftBallDistance);
        }

        for (int i = 0; i < mBallCount; i++) {
            rightSideBalls[i] = new Ball(i, mBallCount, Side.RIGHT, mRightBallDistance);
        }

        double velocitySum = mLeftBallVelocity + mRightBallVelocity;

        leftSideBalls[0].reverseVelocityTimes[0] = 0;
        leftSideBalls[0].reverseVelocityTimes[1] = mLeftBallDistance / velocitySum;
        rightSideBalls[0].reverseVelocityTimes[0] = 0;
        rightSideBalls[0].reverseVelocityTimes[1] = mRightBallDistance / velocitySum;
        for (int i = 1; i < mBallCount; i++) {
            leftSideBalls[i].reverseVelocityTimes[0] = leftSideBalls[i - 1].reverseVelocityTimes[1];
            rightSideBalls[i].reverseVelocityTimes[0] = rightSideBalls[i - 1].reverseVelocityTimes[1];
            if (i < mBallCount - 1) {
                leftSideBalls[i].reverseVelocityTimes[1] = leftSideBalls[i].reverseVelocityTimes[0]
                        + mLeftBallDistance / velocitySum;
                rightSideBalls[i].reverseVelocityTimes[1] = rightSideBalls[i].reverseVelocityTimes[0]
                        + mRightBallDistance / velocitySum;
            }
        }

        for (int j = 2; j < mBallCount * 2 - 1; j++) {
            for (int i = 0; i < mBallCount && j < (mBallCount - i) * 2 - 1; i++) {
                Ball righBallOfLeftSideBall = null;
                if (i == 0) {
                    righBallOfLeftSideBall = rightSideBalls[0];
                } else {
                    righBallOfLeftSideBall = leftSideBalls[i - 1];
                }
                Ball leftBallOfRightSideBall = null;
                if (i == 0) {
                    leftBallOfRightSideBall = leftSideBalls[0];
                } else {
                    leftBallOfRightSideBall = rightSideBalls[i - 1];
                }
                Ball leftBall = leftSideBalls[i];
                Ball rightBall = rightSideBalls[i];

                int k = j / 2;
                if (j % 2 == 0) {
                    if (i == 0) {
                        leftBall.reverseVelocityTimes[2 * k] = leftBall.reverseVelocityTimes[2 * k - 1]
                                + righBallOfLeftSideBall.reverseVelocityTimes[2 * k - 1]
                                - leftBall.reverseVelocityTimes[2 * k - 2];
                        rightBall.reverseVelocityTimes[2 * k] = rightBall.reverseVelocityTimes[2 * k - 1]
                                + leftBallOfRightSideBall.reverseVelocityTimes[2 * k - 1]
                                - rightBall.reverseVelocityTimes[2 * k - 2];
                    } else {
                        leftBall.reverseVelocityTimes[2 * k] = leftBall.reverseVelocityTimes[2 * k - 1]
                                + leftSideBalls[i - 1].reverseVelocityTimes[2 * k]
                                - leftBall.reverseVelocityTimes[2 * k - 2];
                        rightBall.reverseVelocityTimes[2 * k] = rightBall.reverseVelocityTimes[2 * k - 1]
                                + rightSideBalls[i - 1].reverseVelocityTimes[2 * k]
                                - rightBall.reverseVelocityTimes[2 * k - 2];
                    }
                } else {
                    leftBall.reverseVelocityTimes[2 * k + 1] = leftBall.reverseVelocityTimes[2 * k]
                            + leftSideBalls[i + 1].reverseVelocityTimes[2 * k - 1]
                            - leftBall.reverseVelocityTimes[2 * k - 1];
                    rightBall.reverseVelocityTimes[2 * k + 1] = rightBall.reverseVelocityTimes[2 * k]
                            + rightSideBalls[i + 1].reverseVelocityTimes[2 * k - 1]
                            - rightBall.reverseVelocityTimes[2 * k - 1];
                }

            }
        }

        System.out.println("left");
        for (int i = 0; i < leftSideBalls.length; i++) {
            leftSideBalls[i].buildEndingDistanceAndVelocitiesMap(Side.LEFT);
            Log.d("TEST", i + " : " + leftSideBalls[i].toString());
            Log.d("TEST", i + " velocities : " + leftSideBalls[i].printVelocities());
            Log.d("TEST", i + " ending distanc : " + leftSideBalls[i].printEndingDistance());
        }
        System.out.println("right");
        for (int i = 0; i < rightSideBalls.length; i++) {
            rightSideBalls[i].buildEndingDistanceAndVelocitiesMap(Side.RIGHT);
            Log.d("TEST", i + " : " + rightSideBalls[i].toString());
            Log.d("TEST", i + " velocities : " + rightSideBalls[i].printVelocities());
            Log.d("TEST", i + " ending distanc : " + rightSideBalls[i].printEndingDistance());
        }

        mLeftSideBalls = leftSideBalls;
        mRightSideBalls = rightSideBalls;
    }

    private final class Ball {

        private double[] reverseVelocityTimes;
        private HashMap<Double, Double> mEndingDistances;
        private HashMap<Double, Double> mVelocities;
        private int mIndex;
        private Side mSide;
        private double mBallDistance;
        private Paint mPaint;
        private int mCurrentTimeGapIndex;

        Ball(final int index, final int ballCount, Side side, double ballDistance) {
            mIndex = index;
            mSide = side;
            mBallDistance = ballDistance;
            final int inversedIndex = ballCount - index;
            reverseVelocityTimes = new double[inversedIndex * 2 - 1];
            mEndingDistances = new HashMap<Double, Double>();
            mVelocities = new HashMap<Double, Double>();
            mPaint = new Paint();
            if (mSide == Side.LEFT) {
                mPaint.setColor(Color.BLACK);
            } else {
                mPaint.setColor(Color.RED);
            }
        }

        public String printVelocities() {
            final double[] times = reverseVelocityTimes;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < times.length; i++) {
                builder.append(mVelocities.get(times[i])).append("....");
            }
            return builder.toString();
        }

        public String printEndingDistance() {
            final double[] times = reverseVelocityTimes;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < times.length; i++) {
                builder.append(mEndingDistances.get(times[i])).append("....");
            }
            return builder.toString();
        }

        public void buildEndingDistanceAndVelocitiesMap(final Side side) {
            final double[] timeGaps = this.reverseVelocityTimes;
            final HashMap<Double, Double> endingDistances = mEndingDistances;
            final HashMap<Double, Double> velocities = mVelocities;
            double startingVelocity;
            double inverseVelocity;
            if (side == Side.LEFT) {
                startingVelocity = mLeftBallVelocity;
                inverseVelocity = mRightBallVelocity;
            } else {
                startingVelocity = mRightBallVelocity;
                inverseVelocity = mLeftBallVelocity;
            }
            for (int i = 0; i < timeGaps.length; i++) {
                if (i % 2 == 0) {
                    double distance;
                    velocities.put(timeGaps[i], startingVelocity);
                    if (i > 0) {
                        distance = startingVelocity * (timeGaps[i] - timeGaps[i - 1]);
                        distance += endingDistances.get(timeGaps[i - 1]);
                    } else {
                        distance = startingVelocity * timeGaps[i];
                    }
                    endingDistances.put(timeGaps[i], distance);
                } else {
                    velocities.put(timeGaps[i], inverseVelocity);
                    double distance = endingDistances.get(timeGaps[i - 1]) - inverseVelocity
                            * (timeGaps[i] - timeGaps[i - 1]);
                    endingDistances.put(timeGaps[i], distance);
                }
            }

        }

        void draw(final Canvas canvas, final long drawingTime, int canvasWidth, int canvasHeight) {
            // if (mIndex != 1 || mSide != Side.LEFT) {
            // return;
            // }
            double drawingTimeInTimeGap = (double) drawingTime / 100;
            final int centerX = canvasWidth / 2;
            int startY = canvasHeight / 2;
            double startX = centerX;
            if (mSide == Side.LEFT) {
                startX -= mBallDistance * mIndex;
            } else {
                startX += mBallDistance * mIndex;
            }

            final double[] timeGaps = this.reverseVelocityTimes;
            int toIndex = -1;
            for (int i = 0; i < timeGaps.length; i++) {
                if (drawingTimeInTimeGap < timeGaps[i]) {
                    toIndex = i;
                    break;
                }
            }
            if (toIndex == -1) {
                toIndex = timeGaps.length;
            }
            if (toIndex != 0) {
                if (mSide == Side.LEFT) {
                    startX += mEndingDistances.get(timeGaps[toIndex - 1]);
                } else {
                    startX -= mEndingDistances.get(timeGaps[toIndex - 1]);
                }
            }
            if (toIndex != 0) {
                drawingTimeInTimeGap = drawingTimeInTimeGap - timeGaps[toIndex - 1];
            }
            final double distanceInTimeGap;
            if (toIndex == timeGaps.length) {
                if (mSide == Side.LEFT) {
                    distanceInTimeGap = mRightBallVelocity * drawingTimeInTimeGap;
                } else {
                    distanceInTimeGap = mLeftBallVelocity * drawingTimeInTimeGap;
                }
            } else {
                distanceInTimeGap = mVelocities.get(timeGaps[toIndex]) * drawingTimeInTimeGap;
            }
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
            if (startX < 0) {
                int multi = Math.abs((int) (startX / canvasWidth)) + 1;
                startX += multi * canvasWidth;
                startY -= multi * BALL_SIZE * 3;
            } else if (startX > canvasWidth) {
                int multi = (int) (startX / canvasWidth);
                startX -= multi * canvasWidth;
                startY += multi * BALL_SIZE * 3;
            }
            int color = mPaint.getColor();
            mPaint.setColor(Color.BLACK);
            canvas.drawLine(0, startY + BALL_SIZE, canvasWidth, startY + BALL_SIZE, mPaint);
            mPaint.setColor(color);
            TEMP_RECT.set((float) (startX - BALL_SIZE), startY - BALL_SIZE,
                    (float) (startX + BALL_SIZE), startY + BALL_SIZE);
            canvas.drawOval(TEMP_RECT, mPaint);
            color = mPaint.getColor();
            mPaint.setColor(Color.WHITE);
            canvas.drawText(String.valueOf(mIndex), (float) startX, startY, mPaint);
            mPaint.setColor(color);
            mCurrentTimeGapIndex = toIndex;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            final double[] timeGaps = this.reverseVelocityTimes;
            for (double d : timeGaps) {
                builder.append(d).append(",,,,");
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

        public BallPainter(Context context) {
            super(context);
        }

        public void setBalls(Ball[] leftSideBalls, Ball[] rightSideBalls) {
            mLeftSideBalls = leftSideBalls;
            mRightSideBalls = rightSideBalls;
            invalidate();
        }

        public void stopMotion() {
            mMotionStarted = false;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mLeftSideBalls == null || mRightSideBalls == null) {
                return;
            }
            final long drawingTime = System.currentTimeMillis() - mStartingTime;
            final Ball[] leftSideBalls = mLeftSideBalls;
            final Ball[] rightSideBalls = mRightSideBalls;
            for (Ball ball : rightSideBalls) {
                ball.draw(canvas, drawingTime, getWidth(), getHeight());
            }
            for (Ball ball : leftSideBalls) {
                ball.draw(canvas, drawingTime, getWidth(), getHeight());
            }
            if (mMotionStarted) {
                invalidate();
            }

        }

        void setMotionListener(final MotionListener listener) {
            mMotionListener = listener;
        }

        void startMotion() {
            // if (mMotionStarted) {
            // return;
            // }
            mMotionStarted = true;
            mStartingTime = System.currentTimeMillis() + 5000;
            if (mMotionListener != null) {
                mMotionListener.onMotionStarted();
            }
            invalidate();
        }

    }

}
