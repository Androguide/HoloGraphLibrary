package com.echo.holographlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

class Graph extends View {
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
