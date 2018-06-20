package com.djekgrif.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LineChartView extends View {

    private static final float MAX_SMOOTH_VALUE = 0.5f;
    private static final float DEFAULT_SMOOTH_SIZE = 0.3f;
    private static final int DEFAULT_CIRCLE_SIZE = 8;
    private static final int DEFAULT_STROKE_SIZE = 2;
    private static final int DEFAULT_TEXT_SIZE = 40;
    private static final int DEFAULT_BORDER = 10;

    private static final int DEFAULT_CHART_COLOR = 0xFF0099CC;

    private Paint chartLinePaint;
    private Paint textPaint;
    private Paint axisPaint;
    private Path chartLinePath;
    private List<Float> values;
    private List<String> axisXLabels;
    private List<String> axisYLabels;
    private float minY;
    private float maxY;

    private float circleSize;
    private float strokeSize;
    private float leftXOffset;
    private float bottomYOffset;
    private float rightXOffset;
    private float topYOffset;
    private float smoothSize = DEFAULT_SMOOTH_SIZE;
    private boolean isFillBottom = true;
    private boolean isShowPoints = true;
    private boolean isShowXAxis = true;
    private boolean isShowYAxis = true;

    public LineChartView(Context context) {
        super(context);
        init(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float scale = context.getResources().getDisplayMetrics().density;
        circleSize = scale * DEFAULT_CIRCLE_SIZE;
        leftXOffset = circleSize / 2;
        bottomYOffset = leftXOffset;
        rightXOffset = leftXOffset;
        topYOffset = leftXOffset;
        strokeSize = scale * DEFAULT_STROKE_SIZE;

        chartLinePaint = new Paint();
        chartLinePaint.setAntiAlias(true);
        chartLinePaint.setStrokeWidth(strokeSize);

        textPaint = new Paint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(DEFAULT_TEXT_SIZE);

        axisPaint = new Paint();
        axisPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.DKGRAY);

        chartLinePath = new Path();
    }

    public void setChartLineData(List<Float> values) {
        this.values = values;
        if (values.size() > 0) {
            maxY = values.get(0);
            minY = values.get(0);
            for (float y : values) {
                if (y > maxY) {
                    maxY = y;
                }
                if (y < minY) {
                    minY = y;
                }
            }
        }

        invalidate();
    }

    public void setSmoothSize(float smoothSize) {
        if (smoothSize > MAX_SMOOTH_VALUE || smoothSize <= 0) {
            Log.e("LineChartView", "Incorrect smooth value please use: (value > 0 && value < 0.5f) ");
        } else {
            this.smoothSize = smoothSize;
        }
    }

    public void setAxisXLabels(List<String> axisXLabels) {
        this.axisXLabels = axisXLabels;
        if (axisXLabels != null && axisXLabels.size() > 0) {
            Rect labelRect = calculateTextSize(axisXLabels.get(0), textPaint);
            bottomYOffset = labelRect.height() * 2;
            rightXOffset = labelRect.width() / 2;
        }
    }

    public void setAxisYLabels(List<String> axisYLabels) {
        this.axisYLabels = axisYLabels;
        if (axisYLabels != null && axisYLabels.size() > 0) {
            Rect labelRect = calculateTextSize(axisYLabels.get(0), textPaint);
            leftXOffset = labelRect.width() + ((labelRect.width() / axisYLabels.get(0).length()) * 2);
            topYOffset = labelRect.height();
//            float step = (maxY - minY) / axisYLabels.length;
//            maxY += step;
//            minY = minY > 0 ? minY - step : 0;
        }
    }

    public void setFillBottom(boolean fillBottom) {
        isFillBottom = fillBottom;
    }

    public void setShowPoints(boolean showPoints) {
        isShowPoints = showPoints;
    }

    public void setShowXAxis(boolean showXAxis) {
        isShowXAxis = showXAxis;
    }

    public void setShowYAxis(boolean showYAxis) {
        isShowYAxis = showYAxis;
    }

    public void setAxisTextSize(@DimenRes int textSizeRes) {
        textPaint.setTextSize(getResources().getDimensionPixelSize(textSizeRes));
    }

    public void setAxisTextColor(@ColorRes int colorRes) {
        axisPaint.setColor(getResources().getColor(colorRes));
    }

    private Rect calculateTextSize(String text, Paint textPaint) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds;
    }

    private List<PointF> calculatePoints(List<Float> values) {
        int valueSize = values.size();
        final float widthWithoutBorders = getMeasuredWidth() - leftXOffset - rightXOffset;
        final float height = getMeasuredHeight() - bottomYOffset - topYOffset;
        final float dX = valueSize > 1 ? valueSize - 1 : 2;
        final float dY = (maxY - minY) > 0 ? (maxY - minY) : 2;
        List<PointF> points = new ArrayList<PointF>(valueSize);
        for (int i = 0; i < valueSize; i++) {
            float x = leftXOffset + i * widthWithoutBorders / (axisXLabels != null && axisXLabels.size() > valueSize ? axisXLabels.size() - 1 : dX);
            float y = topYOffset + height - (values.get(i) - minY) * height / dY;
            points.add(new PointF(x, y));
        }
        return points;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (values != null && values.size() > 0) {
            List<PointF> points = calculatePoints(values);
            drawChartLine(canvas, points);
            drawPointCircles(canvas, points);
            drawAxisYLabels(canvas);
            drawAxisXLabels(canvas);
        }
    }

    private void drawChartLine(Canvas canvas, List<PointF> points) {
        int size = points.size();
        final float height = getMeasuredHeight() - bottomYOffset;

        chartLinePath.reset();

        // Calculate smooth chart line
        float lX = 0, lY = 0;
        chartLinePath.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 0; i < size; i++) {
            // We have to skip first point
            PointF currentPoint = points.get(i);
            if(i > 0) {
                // first control point
                PointF previousPoint = points.get(i - 1);    // previous point
                float x1 = previousPoint.x + lX;
                float y1 = previousPoint.y + lY;

                // second control point
                PointF p1 = points.get(i + 1 < size ? i + 1 : i);    // next point
                lX = (p1.x - previousPoint.x) / 2 * smoothSize;        // (lX,lY) is the slope of the reference line
                lY = (p1.y - previousPoint.y) / 2 * smoothSize;
                float x2 = currentPoint.x - lX;
                float y2 = currentPoint.y - lY;

                chartLinePath.cubicTo(x1, y1, x2, y2, currentPoint.x, currentPoint.y);
            }

            if (isShowYAxis) {
                // Y Axis lines
                canvas.drawLine(currentPoint.x, getMeasuredHeight() - bottomYOffset, currentPoint.x, topYOffset, axisPaint);
            }
        }


        // Draw chart line path
        chartLinePaint.setColor(DEFAULT_CHART_COLOR);
        chartLinePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(chartLinePath, chartLinePaint);

        // Draw bottom area
        if (isFillBottom) {
            chartLinePaint.setStyle(Paint.Style.FILL);
            chartLinePaint.setColor((DEFAULT_CHART_COLOR & 0xFFFFFF) | 0x10000000);
            chartLinePath.lineTo(points.get(size - 1).x, height);
            chartLinePath.lineTo(points.get(0).x, height);
            chartLinePath.close();
            canvas.drawPath(chartLinePath, chartLinePaint);
        }

    }

    private void drawPointCircles(Canvas canvas, List<PointF> points) {
        if (isShowPoints) {
            chartLinePaint.setColor(DEFAULT_CHART_COLOR);
            chartLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            for (PointF point : points) {
                canvas.drawCircle(point.x, point.y, circleSize / 2, chartLinePaint);
            }
            chartLinePaint.setStyle(Paint.Style.FILL);
            chartLinePaint.setColor(Color.WHITE);
            for (PointF point : points) {
                canvas.drawCircle(point.x, point.y, (circleSize - strokeSize) / 2, chartLinePaint);
            }
        }
    }

    private void drawAxisYLabels(Canvas canvas) {
        if (axisYLabels != null && axisYLabels.size() > 0) {
            final float heightWithoutBorders = getMeasuredHeight() - bottomYOffset - topYOffset;
            final float widthWithoutRightBorder = getMeasuredWidth() - rightXOffset;
            int yParts = axisYLabels.size() - 1;
            //TODO add spaces after max and before min values
//            yParts += 2; // Add one part upper and one part above of min and max
            textPaint.setTextAlign(Paint.Align.LEFT);
            for (int i = 0; i < axisYLabels.size(); i++) {
                float y = ((heightWithoutBorders / yParts) * (yParts - i)) + topYOffset;
                if (isShowXAxis) {
                    // X Axis lines
                    canvas.drawLine(leftXOffset, y, widthWithoutRightBorder, y, axisPaint);
                }
                canvas.drawText(axisYLabels.get(i), 0, y + textPaint.getTextSize() / 2, textPaint);
            }
        }
    }

    private void drawAxisXLabels(Canvas canvas) {
        if (axisXLabels != null && axisXLabels.size() > 0) {
            final float heightWithoutBottomBorder = getMeasuredHeight() - bottomYOffset;
            final float widthWithoutBorders = getMeasuredWidth() - leftXOffset - rightXOffset;
            int dY = axisXLabels.size() - 1;
            textPaint.setTextAlign(Paint.Align.CENTER);
            for (int i = 0; i < axisXLabels.size(); i++) {
                float x = ((widthWithoutBorders / dY) * i) + leftXOffset;
                canvas.drawText(axisXLabels.get(i), x, getMeasuredHeight(), textPaint);
            }
        }
    }

}
