package com.pearl.view.TimelyTextView.model;


import com.pearl.view.TimelyTextView.model.number.Eight;
import com.pearl.view.TimelyTextView.model.number.Five;
import com.pearl.view.TimelyTextView.model.number.Four;
import com.pearl.view.TimelyTextView.model.number.Nine;
import com.pearl.view.TimelyTextView.model.number.Null;
import com.pearl.view.TimelyTextView.model.number.One;
import com.pearl.view.TimelyTextView.model.number.Seven;
import com.pearl.view.TimelyTextView.model.number.Six;
import com.pearl.view.TimelyTextView.model.number.Three;
import com.pearl.view.TimelyTextView.model.number.Two;
import com.pearl.view.TimelyTextView.model.number.Zero;

import java.security.InvalidParameterException;

public class NumberUtils {

    public static float[][] getControlPointsFor(int start) {
        switch (start) {
            case (-1):
                return Null.getInstance().getControlPoints();
            case 0:
                return Zero.getInstance().getControlPoints();
            case 1:
                return One.getInstance().getControlPoints();
            case 2:
                return Two.getInstance().getControlPoints();
            case 3:
                return Three.getInstance().getControlPoints();
            case 4:
                return Four.getInstance().getControlPoints();
            case 5:
                return Five.getInstance().getControlPoints();
            case 6:
                return Six.getInstance().getControlPoints();
            case 7:
                return Seven.getInstance().getControlPoints();
            case 8:
                return Eight.getInstance().getControlPoints();
            case 9:
                return Nine.getInstance().getControlPoints();
            default:
                throw new InvalidParameterException("Unsupported number requested");
        }
    }
}
