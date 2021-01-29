package com.company;

import java.awt.*;
import java.util.ArrayList;

public class TextRect {
    public Color color;
    public int height, width, ph, pw;
    public Point position;
    public ArrayList word = new ArrayList<>();
    public ArrayList<Point> charSpots = new ArrayList<>();
    boolean newLine = true;


    public TextRect(Color incol, int inh, int inw, Point inp) {
        color = incol;
        height = inh;
        width = inw;
        position = inp;
    }

    public void checkBackwards() {
        if (width < 0) {
            position.x += width;
            width = - width;
        }
        if (height < 0) {
            position.y += height;
            height = - height;
        }
    }

    public void delChar() {
        if (charSpots.size() > 0) {
            charSpots.remove(charSpots.size() - 1);
            word.remove(word.size() - 1);
        }
    }

    public void addChar(char input) {
        if (newLine) {
            Point spot = new Point();
            spot.x = position.x + 5;
            spot.y = position.y + 15;
            charSpots.add(spot);
            newLine = false;
        } else {
            Point spot = new Point();
            if (charSpots.get(charSpots.size() - 1).x > (position.x + width - 15)) {
                if (input == ' ' || (char) word.get(word.size() - 1) == ' ') {
                    spot.x = position.x + 5;
                    spot.y = charSpots.get(charSpots.size() - 1).y + 15;
                } else {
                    char temp = (char) word.get(word.size() - 1);
                    word.set(word.size() - 1, '-');
                    charSpots.add(new Point(position.x + 5, charSpots.get(charSpots.size() - 1).y + 15));
                    word.add(temp);
                    spot.x = charSpots.get(charSpots.size() - 1).x + 7;
                    spot.y = charSpots.get(charSpots.size() - 1).y;
                }
            } else {
                spot.x = charSpots.get(charSpots.size() - 1).x + 7;
                spot.y = charSpots.get(charSpots.size() - 1).y;
            }
            if (spot.y > height + position.y - 15) {
                height += 15;
            }
            charSpots.add(spot);
        }
        word.add(input);
    }
}
