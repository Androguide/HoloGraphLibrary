/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 * 
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.echo.holographlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LineGraph extends Graph {
    private ArrayList<Line> lines = new ArrayList<Line>();
    private Paint paint = new Paint();
    private Paint txtPaint = new Paint();
    private Paint numPaint = new Paint();
    private float minY = 0, minX = 0;
    private float maxY = 0, maxX = 0;
    private boolean isRangeSet = false;
    private boolean isDomainSet = false;
    private int lineToFill = -1;
    private int indexSelected = -1;
    private OnPointClickedListener listener;
    private Bitmap fullImage;
    private boolean shouldUpdate = false;
    private int gridColor = 0xffffffff;
    private String yAxisTitle = null;
    private String xAxisTitle = null;
    private boolean showAxisValues = true;

    boolean debug = false;

    public LineGraph(Context context) {
        this(context, null);
    }

    public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        txtPaint.setColor(0xdd000000);
        txtPaint.setTextSize(convertToPx(20, SP));
        numPaint.setColor(0xdd000000);
        numPaint.setTextSize(convertToPx(16, SP));
    }

    public void setGridColor(int color) {
        gridColor = color;
    }

    public void showAxisValues(boolean show) {
        showAxisValues = show;
    }

    public void setTextColor(int color) {
        txtPaint.setColor(color);
    }

    public void setTextSize(float s) {
        txtPaint.setTextSize(s);
    }

    public void setYAxisTitle(String title) {
        yAxisTitle = title;
    }

    public void setXAxisTitle(String title) {
        xAxisTitle = title;
    }

    public void update() {
        shouldUpdate = true;
        postInvalidate();
    }

    public void removeAllLines() {
        while (lines.size() > 0) {
            lines.remove(0);
        }
        shouldUpdate = true;
        postInvalidate();
    }

    public void addLine(Line line) {
        lines.add(line);
        shouldUpdate = true;
        postInvalidate();
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public int getLineToFill() {
        return lineToFill;
    }

    public void setLineToFill(int indexOfLine) {
        this.lineToFill = indexOfLine;
        shouldUpdate = true;
        postInvalidate();
    }

    public Line getLine(int index) {
        return lines.get(index);
    }

    public int getSize() {
        return lines.size();
    }

    public void setRange(float min, float max) {
        minY = min;
        maxY = max;
        isRangeSet = true;
    }

    public void setDomain(float min, float max) {
        minX = min;
        maxX = max;
        isDomainSet = true;
    }

    public float getMaxY() {
        if (isRangeSet) return maxY;

        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                maxY = point.getY();
                break;
            }
        }
        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                if (point.getY() > maxY) maxY = point.getY();
            }
        }
        return maxY;

    }

    public float getMinY() {
        if (isRangeSet) return minY;

        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                minY = point.getY();
                break;
            }
        }
        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                if (point.getY() < minY) minY = point.getY();
            }
        }
        return minY;
    }

    public float getMaxX() {
        if(isDomainSet) return maxX;

        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                maxX = point.getX();
                break;
            }
        }
        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                if (point.getX() > maxX) maxX = point.getX();
            }
        }
        return maxX;

    }

    public float getMinX() {
        if(isDomainSet) return minX;

        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                minX = point.getX();
                break;
            }
        }
        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                if (point.getX() < minX) minX = point.getX();
            }
        }
        return minX;
    }

    public void onDraw(Canvas ca) {
        if (fullImage == null || shouldUpdate) {
            fullImage = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(fullImage);
            getMaxY();
            getMinY();
            getMaxX();
            getMinX();
            paint.reset();
            Path path = new Path();

            float topPadding = 0, bottomPadding = 0, leftPadding = 0, rightPadding = 0;
            if (showAxisValues) {
                bottomPadding = leftPadding = numPaint.getTextSize() * 1.5f;
                rightPadding = numPaint.measureText(maxX+"") / 2;
                topPadding = numPaint.measureText(maxY+"") / 2;
                System.out.println("Right p: "+rightPadding);
                System.out.println("Top p: "+topPadding);
            }

            float usableHeight = getHeight() - bottomPadding - topPadding;
            float usableWidth = getWidth() - leftPadding - rightPadding;

            if(debug) {
                txtPaint.setColor(0xffff0000);
                canvas.drawRect(0, 0, getWidth(), getHeight(), txtPaint);
                txtPaint.setColor(0xff00ff00);
                canvas.drawRect(leftPadding, topPadding, usableWidth + leftPadding, usableHeight + topPadding, txtPaint);
                txtPaint.setColor(0xdd000000);
            }

            int lineCount = 0;
            for (Line line : lines) {
                int count = 0;
                float lastXPixels = 0, newYPixels;
                float lastYPixels = 0, newXPixels;

                if (lineCount == lineToFill) {
                    paint.setColor(Color.BLACK);
                    paint.setAlpha(30);
                    paint.setStrokeWidth(2);
                    for (int i = (int) convertToPx(5, DP); i - getWidth() < getHeight(); i += convertToPx(10, DP)) {
                        canvas.drawLine(i, getHeight() - bottomPadding, 0, getHeight() - bottomPadding - i, paint);
                    }

                    paint.reset();

                    paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
                    for (LinePoint p : line.getPoints()) {
                        float yPercent = (p.getY() - minY) / (maxY - minY);
                        float xPercent = (p.getX() - minX) / (maxX - minX);
                        if (count == 0) {
                            lastXPixels = leftPadding + (xPercent * usableWidth);
                            lastYPixels = getHeight() - bottomPadding - (usableHeight * yPercent);
                            path.moveTo(lastXPixels, lastYPixels);
                        } else {
                            newXPixels = leftPadding + (xPercent * usableWidth);
                            newYPixels = getHeight() - bottomPadding - (usableHeight * yPercent);
                            path.lineTo(newXPixels, newYPixels);
                            Path pa = new Path();
                            pa.moveTo(lastXPixels, lastYPixels);
                            pa.lineTo(newXPixels, newYPixels);
                            pa.lineTo(newXPixels, 0);
                            pa.lineTo(lastXPixels, 0);
                            pa.close();
                            canvas.drawPath(pa, paint);
                            lastXPixels = newXPixels;
                            lastYPixels = newYPixels;
                        }
                        count++;
                    }

                    path.reset();

                    path.moveTo(0, getHeight() - bottomPadding);
                    path.lineTo(leftPadding, getHeight() - bottomPadding);
                    path.lineTo(leftPadding, 0);
                    path.lineTo(0, 0);
                    path.close();
                    canvas.drawPath(path, paint);

                    path.reset();

                    path.moveTo(getWidth(), getHeight() - bottomPadding);
                    path.lineTo(getWidth() - leftPadding, getHeight() - bottomPadding);
                    path.lineTo(getWidth() - leftPadding, 0);
                    path.lineTo(getWidth(), 0);
                    path.close();

                    canvas.drawPath(path, paint);

                }

                lineCount++;
            }

            paint.reset();

            paint.setColor(this.gridColor);
            paint.setAlpha(50);
            paint.setAntiAlias(true);
            canvas.drawLine(leftPadding, getHeight() - bottomPadding, getWidth(), getHeight() - bottomPadding, paint);
            paint.setAlpha(255);

            for (Line line : lines) {
                int count = 0;
                float lastXPixels = 0, newYPixels;
                float lastYPixels = 0, newXPixels;

                paint.setColor(line.getColor());
                paint.setStrokeWidth(convertToPx(3, DP));

                for (LinePoint p : line.getPoints()) {
                    float yPercent = (p.getY() - minY) / (maxY - minY);
                    float xPercent = (p.getX() - minX) / (maxX - minX);
                    if (count == 0) {
                        lastXPixels = leftPadding + (xPercent * usableWidth);
                        lastYPixels = getHeight() - bottomPadding - (usableHeight * yPercent);
                    } else {
                        newXPixels = leftPadding + (xPercent * usableWidth);
                        newYPixels = getHeight() - bottomPadding - (usableHeight * yPercent);
                        canvas.drawLine(lastXPixels, lastYPixels, newXPixels, newYPixels, paint);
                        lastXPixels = newXPixels;
                        lastYPixels = newYPixels;
                    }
                    count++;
                }
            }

            int pointCount = 0;

            for (Line line : lines) {
                paint.setColor(line.getColor());
                paint.setStrokeWidth(convertToPx(6, DP));
                paint.setStrokeCap(Paint.Cap.ROUND);

                if (line.isShowingPoints()) {
                    for (LinePoint p : line.getPoints()) {
                        float yPercent = (p.getY() - minY) / (maxY - minY);
                        float xPercent = (p.getX() - minX) / (maxX - minX);
                        float xPixels = leftPadding + (xPercent * usableWidth);
                        float yPixels = getHeight() - bottomPadding - (usableHeight * yPercent);

                        paint.setColor(Color.GRAY);
                        canvas.drawCircle(xPixels, yPixels, convertToPx(6, DP), paint);
                        paint.setColor(Color.WHITE);
                        canvas.drawCircle(xPixels, yPixels, convertToPx(3, DP), paint);

                        Path path2 = new Path();
                        path2.addCircle(xPixels, yPixels, convertToPx(30, DP), Direction.CW);
                        p.setPath(path2);
                        p.setRegion(new Region((int) (xPixels - convertToPx(30, DP)), (int) (yPixels - convertToPx(30, DP)), (int) (xPixels + convertToPx(30, DP)), (int) (yPixels + convertToPx(30, DP))));

                        if (indexSelected == pointCount && listener != null) {
                            paint.setColor(Color.parseColor("#33B5E5"));
                            paint.setAlpha(100);
                            canvas.drawPath(p.getPath(), paint);
                            paint.setAlpha(255);
                        }

                        pointCount++;
                    }
                }
            }

            if (showAxisValues) {
                int minSize = (int) convertToPx(50, DP);

                // Find unique integers to display on the x axis
                List<Integer> values = new LinkedList<Integer>();
                int prevNum = Integer.MIN_VALUE;
                int numbersToShow = (int) usableWidth / minSize + 1;
                float step = (maxX - minX) / (numbersToShow - 1);
                for(int i=0; i<numbersToShow; i++) {
                    int num = (int) (minX + i * step);
                    if(num != prevNum) {
                        values.add(num);
                    }
                    prevNum = num;
                }

                // Draw the x axis
                for(int i=0; i<values.size(); i++) {
                    String num = values.get(i).toString();

                    // Find the proper position for the text
                    float pos = i * usableWidth / (values.size() - 1);
                    // Add padding for the y axis
                    pos += leftPadding;
                    // Center text
                    pos -= numPaint.measureText(num) / 2;

                    // Draw text
                    canvas.drawText(num, pos, usableHeight + topPadding + bottomPadding - numPaint.getTextSize() / 3, numPaint);
                }

                // Rotate the canvas for the y axis
                canvas.save();
                canvas.rotate(-90, getWidth() / 2, getHeight() / 2);
                canvas.translate(0, getHeight() / 2);
                canvas.translate(0, -getWidth() / 2);
                canvas.translate(-getHeight() / 2, 0);
                canvas.translate(getWidth() / 2, 0);

                // Find unique integers to display on the y axis
                values = new LinkedList<Integer>();
                prevNum = Integer.MIN_VALUE;
                numbersToShow = (int) usableHeight / minSize + 1;
                step = (maxY - minY) / (numbersToShow - 1);
                for(int i=0; i<numbersToShow; i++) {
                    int num = (int) (minY + i * step);
                    if(num != prevNum) {
                        values.add(num);
                    }
                    prevNum = num;
                }

                // Draw the y axis
                for(int i=0; i<values.size(); i++) {
                    String num = values.get(i).toString();

                    // Find the proper position for the text
                    float pos = i * usableHeight / (values.size() - 1);
                    // Add padding for the x axis
                    pos += bottomPadding;
                    // Center text
                    pos -= numPaint.measureText(num) / 2;

                    // Draw text
                    canvas.drawText(num, pos, numPaint.getTextSize(), numPaint);
                }

                // Restore canvas upright
                canvas.restore();
            }

            if(xAxisTitle != null) {
                ca.drawText(xAxisTitle, (getWidth() - txtPaint.measureText(xAxisTitle)) / 2, getHeight() - txtPaint.getTextSize() / 3, txtPaint);
            }

            if(yAxisTitle != null) {
                ca.save();
                ca.rotate(-90, getWidth() / 2, getHeight() / 2);
                ca.translate(0, getHeight() / 2);
                ca.translate(0, -getWidth() / 2);
                ca.drawText(yAxisTitle, (getWidth() - txtPaint.measureText(yAxisTitle)) / 2 , txtPaint.getTextSize() * 2 / 3, txtPaint);
                ca.restore();
            }

            shouldUpdate = false;
        }


        Matrix m = new Matrix();

        if(xAxisTitle != null) {
            m.preScale(1, (getHeight() - txtPaint.getTextSize()) / getHeight());
        }

        if(yAxisTitle != null) {
            m.postTranslate(txtPaint.getTextSize(), 0);
            m.preScale((getWidth() - txtPaint.getTextSize()) / getWidth(), 1);
        }

        ca.drawBitmap(fullImage, m, null);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        int count = 0;
        int lineCount = 0;
        int pointCount;

        Region r = new Region();
        for (Line line : lines) {
            pointCount = 0;
            for (LinePoint p : line.getPoints()) {
                if (p.getPath() != null && p.getRegion() != null) {
                    r.setPath(p.getPath(), p.getRegion());
                    if (r.contains(point.x, point.y) && event.getAction() == MotionEvent.ACTION_DOWN) {
                        indexSelected = count;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (r.contains(point.x, point.y) && listener != null) {
                            listener.onClick(lineCount, pointCount);
                        }
                        indexSelected = -1;
                    }
                }

                pointCount++;
                count++;
            }
            lineCount++;

        }

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
            shouldUpdate = true;
            postInvalidate();
        }

        return true;
    }

    public void setOnPointClickedListener(OnPointClickedListener listener) {
        this.listener = listener;
    }

    public interface OnPointClickedListener {
        abstract void onClick(int lineIndex, int pointIndex);
    }
}
