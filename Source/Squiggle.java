package com.company;

import java.awt.*;
import java.util.ArrayList;

public class Squiggle {
    public Color color;
    public ArrayList<Point> stroke;
    public boolean beautify = false;
    public String bShape;
    public int xub = 0, xlb = 99999, yub = 0, ylb = 99999;

    public Squiggle(ArrayList<Point> instroke, Color inCol) {
        color = inCol;
        stroke = instroke;
        //setBounds();
    }

    public void setBounds() {
        for (Point point : stroke) {
            if (point.getX() > xub) {
                xub = (int)point.getX();
            }
            if (point.getX() < xlb) {
                xlb = (int)point.getX();
            }
            if (point.getY() > yub) {
                yub = (int)point.getY();
            }
            if (point.getY() < ylb) {
                ylb = (int)point.getY();
            }
        }
        System.out.println("xub = " + xub + " xlb = " + xlb +
                " yub = " + yub + " ylb = " + ylb);
    }
}
