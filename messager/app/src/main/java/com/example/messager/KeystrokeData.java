package com.example.messager;

import java.io.Serializable;

public class KeystrokeData implements Serializable {
    private long dwellTime;
    private float pressureDifference;
    private float touchArea;

    public KeystrokeData(long dwellTime, float pressureDifference, float touchArea) {
        this.dwellTime = dwellTime;
        this.pressureDifference = pressureDifference;
        this.touchArea = touchArea;
    }


    public long getDwellTime() {
        return dwellTime;
    }

    public void setDwellTime(long dwellTime) {
        this.dwellTime = dwellTime;
    }

    public float getPressureDifference() {
        return pressureDifference;
    }

    public void setPressureDifference(float pressureDifference) {
        this.pressureDifference = pressureDifference;
    }

    public float getTouchArea() {
        return touchArea;
    }

    public void setTouchArea(float touchArea) {
        this.touchArea = touchArea;
    }
}
