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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MultiSeriesDonutGraph extends View
{
    private List<List<MultiSeriesDonutSlice>> seriesList = new ArrayList<>();
    private Paint paint = new Paint();
    private Path path = new Path();

    private Pair<Integer, Integer> indexSelected = Pair.create(-1, -1);
    private int thickness = 200;
    private OnSeriesSliceClickedListener listener;


    public MultiSeriesDonutGraph(Context context)
    {
        super(context);
    }

    public MultiSeriesDonutGraph(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void update()
    {
        postInvalidate();
    }

    public void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.TRANSPARENT);
        paint.reset();
        paint.setAntiAlias(true);
        float midX, midY, chartRadius, chartInnerRadius;
        path.reset();

        float padding = 2;

        midX = getWidth() / 2;
        midY = getHeight() / 2;
        if (midX < midY)
        {
            chartRadius = midX;
        }
        else
        {
            chartRadius = midY;
        }
        chartRadius -= padding;
        chartInnerRadius = chartRadius - thickness;

        float radialPadding = 2 * padding;
        float totalRadialPadding = (seriesList.size() - 1) * radialPadding;
        float sliceRadialThickness = (chartRadius - chartInnerRadius - totalRadialPadding) / seriesList.size();

        for (int seriesIndex = 0; seriesIndex < seriesList.size(); seriesIndex++)
        {
            List<MultiSeriesDonutSlice> series = seriesList.get(seriesIndex);

            float radius = chartRadius - (sliceRadialThickness + radialPadding) * seriesIndex;
            float innerRadius = radius - sliceRadialThickness;

            float currentAngle = 270;
            float currentSweep;
            int totalValue = 0;

            for (MultiSeriesDonutSlice slice : series)
            {
                totalValue += slice.getValue();
            }

            int count = 0;
            for (MultiSeriesDonutSlice slice : series)
            {
                Path p = new Path();
                paint.setColor(slice.getColor());
                currentSweep = (slice.getValue() / totalValue) * (360);
                p.arcTo(new RectF(midX - radius, midY - radius, midX + radius, midY + radius), currentAngle + padding, currentSweep - padding);
                p.arcTo(new RectF(midX - innerRadius, midY - innerRadius, midX + innerRadius, midY + innerRadius), (currentAngle + padding) + (currentSweep - padding), -(currentSweep - padding));
                p.close();

                slice.setPath(p);
                slice.setRegion(new Region((int) (midX - radius), (int) (midY - radius), (int) (midX + radius), (int) (midY + radius)));
                canvas.drawPath(p, paint);

                if (indexSelected.first == seriesIndex && indexSelected.second == count && listener != null)
                {
                    path.reset();
                    paint.setColor(slice.getColor());
                    paint.setColor(Color.parseColor("#33B5E5"));
                    paint.setAlpha(100);

                    if (seriesList.size() > 1)
                    {
                        path.arcTo(new RectF(midX - radius - (padding * 2), midY - radius - (padding * 2), midX + radius + (padding * 2), midY + radius + (padding * 2)), currentAngle, currentSweep + padding);
                        path.arcTo(new RectF(midX - innerRadius + (padding * 2), midY - innerRadius + (padding * 2), midX + innerRadius - (padding * 2), midY + innerRadius - (padding * 2)), currentAngle + currentSweep + padding, -(currentSweep + padding));
                        path.close();
                    }
                    else
                    {
                        path.addCircle(midX, midY, radius + padding, Direction.CW);
                    }

                    canvas.drawPath(path, paint);
                    paint.setAlpha(255);
                }

                currentAngle = currentAngle + currentSweep;

                count++;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        int seriesCount = 0;
        for (List<MultiSeriesDonutSlice> series : seriesList)
        {
            int sliceCount = 0;
            for (MultiSeriesDonutSlice slice : series)
            {
                Region r = new Region();
                r.setPath(slice.getPath(), slice.getRegion());
                if (r.contains(point.x, point.y) && event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    indexSelected = Pair.create(seriesCount, sliceCount);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (r.contains(point.x, point.y) && listener != null)
                    {
                        if (indexSelected.first > -1)
                        {
                            listener.onClick(indexSelected.first, indexSelected.second);
                        }
                        indexSelected = Pair.create(-1, -1);
                    }

                }
                sliceCount++;
            }
            seriesCount++;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP)
        {
            postInvalidate();
        }

        return true;
    }

    public List<List<MultiSeriesDonutSlice>> getSeriesList()
    {
        return seriesList;
    }

    public void setSeriesList(List<List<MultiSeriesDonutSlice>> seriesList)
    {
        this.seriesList = seriesList;
        postInvalidate();
    }

    public MultiSeriesDonutSlice getSlice(int series, int index)
    {
        return seriesList.get(series).get(index);
    }

    public void addSlice(int series, MultiSeriesDonutSlice slice)
    {
        while (seriesList.size() < series + 1)
        {
            seriesList.add(new ArrayList<MultiSeriesDonutSlice>());
        }
        this.seriesList.get(series).add(slice);
        postInvalidate();
    }

    public void setOnSliceClickedListener(OnSeriesSliceClickedListener listener)
    {
        this.listener = listener;
    }

    public int getThickness()
    {
        return thickness;
    }

    public void setThickness(int thickness)
    {
        this.thickness = thickness;
        postInvalidate();
    }

    public void removeSlices()
    {
        for (int i = seriesList.size() - 1; i >= 0; i--)
        {
            for (int j = seriesList.get(i).size() - 1; j >= 0; j--)
            {
                seriesList.get(i).remove(j);
            }
            seriesList.remove(i);
        }
        postInvalidate();
    }

    public static interface OnSeriesSliceClickedListener
    {
        public abstract void onClick(int series, int index);
    }
}
