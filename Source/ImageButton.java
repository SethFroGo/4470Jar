package com.company;

import javax.swing.*;

public class ImageButton extends JComponent{
    public JToggleButton button;
    public ImageIcon icon;
    public String path;
    public boolean tag1, tag2, tag3, tag4;
    public boolean isSelected = false;

    public ImageButton(String inPath, ImageIcon inIcon) {
        button = new JToggleButton(inIcon);
        icon = inIcon;
        path = inPath;
        tag1 = false;
        tag2 = false;
        tag3 = false;
        tag4 = false;

    }
}
