package com.echo.holographlibrary;

import android.graphics.Path;
import android.graphics.Region;

/**
 * Created by Aaron on 19/10/2014.
 */
public class BarStackSegment {
    public float Value;
    public int Color;
    private Path path;
    private Region region;

    public BarStackSegment(int val, int color){
        Value = val;
        Color = color;
    }
    public Path getPath() {
        return path;
    }
    public void setPath(Path path) {
        this.path = path;
    }
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
}
