package com.echo.holographlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Arrays;
import java.util.List;

class Graph extends View {
    static final List<Integer> DEFAULT_COLORS = Arrays.asList(0xffcc5c57, 0xff5f6ec2, 0xfff9db00, 0xffb7cf47, 0xfff48935, 0xff4ba5e2, 0xff99cc00, 0xffffbb33, 0xffaa66cc);
    static final int DP = TypedValue.COMPLEX_UNIT_DIP;
    static final int SP = TypedValue.COMPLEX_UNIT_SP;

    public Graph(Context context){
        super(context);
    }

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    float convertToPx(int value, int unit) {
        return TypedValue.applyDimension(unit, value, getContext().getResources().getDisplayMetrics());
    }
}
