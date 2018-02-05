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

package com.echo.holographlibrarysample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.BarGraph.OnBarClickedListener;
import com.echo.holographlibrary.BarStackSegment;

import java.util.ArrayList;

public class BarFragment extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_bargraph, container, false);
        assert v != null;

		ArrayList<Bar> points = new ArrayList<Bar>();
		Bar d = new Bar();
		d.setColor(Color.parseColor("#99CC00"));
		d.setName("Test1");
		d.setValue(10);
		Bar d2 = new Bar();
		d2.setColor(Color.parseColor("#FFBB33"));
		d2.setName("Test2");
		d2.setValue(20);
        Bar d3 = new Bar();
        d3.setColor(Color.parseColor("#FFBB33"));
        d3.setName("Test3");
        d3.setStackedBar(true);
        d3.addStackValue(new BarStackSegment(2, Color.parseColor("#FFBB33")));
        d3.addStackValue(new BarStackSegment(4, Color.RED));
		points.add(d);
		points.add(d2);
        points.add(d3);

        BarGraph g = (BarGraph)v.findViewById(R.id.bargraph);
        assert g != null;
        g.setUnit("€");
        g.appendUnit(true);
		g.setBars(points);
		
		g.setOnBarClickedListener(new OnBarClickedListener(){

			@Override
			public void onClick(int index) {
				
			}
			
		});
		
		return v;
	}
}
