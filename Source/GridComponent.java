package com.company;

import javax.naming.Context;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.*;

public class GridComponent extends JComponent implements MouseListener, MouseMotionListener {
    public ArrayList<ImageButton> imageArray = null;
    public Dimension frameSize;
    public double mDim, imDim;
    public int imInd, selInd, dragInd;
    private int animPerc = 1;
    private ArrayList<Rectangle2D> rectArr = new ArrayList<>();
    public MyWindow above;
    private Color selCol = new Color(184, 249, 255);
    private JToggleButton mtag1, mtag2, mtag3, mtag4, dtag;
    private JButton eMag;
    private ButtonGroup magButs;
    private boolean magMode = false;
    private boolean magDrag = false;
    private ArrayList<JButton> magnets = new ArrayList<>();
    private ArrayList<Rectangle2D> recLocs = new ArrayList<>();
    private ArrayList<ImageIcon> recImgs = new ArrayList<>();
    private ArrayList<Rectangle2D> magRecs = new ArrayList<>();
    private ArrayList<Rectangle2D> startLocs = new ArrayList<>();
    private ArrayList<Point2D> destPoints = new ArrayList<>();
    private Timer perTime;
    private Button perBut;

    public GridComponent (ArrayList<ImageButton> inImArr, Dimension superSize, int curSel, MyWindow caller,
                           JToggleButton m1, JToggleButton m2, JToggleButton m3, JToggleButton m4,
                           JToggleButton dm, JButton endMag, ButtonGroup mbut) {
        addMouseListener(this);
        addMouseMotionListener(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        imageArray = inImArr;
        frameSize = superSize;
        mDim = frameSize.width / 5;
        imDim = mDim - 10;
        selInd = curSel;
        imageArray.get(selInd).isSelected = true;
        above = caller;
        mtag1 = m1;
        mtag2 = m2;
        mtag3 = m3;
        mtag4 = m4;
        dtag = dm;
        eMag = endMag;
        magButs = mbut;
        mtag1.addActionListener(event -> toggleMagMode());
        mtag2.addActionListener(event -> toggleMagMode());
        mtag3.addActionListener(event -> toggleMagMode());
        mtag4.addActionListener(event -> toggleMagMode());
        eMag.addActionListener(event -> etoggleMagMode());
        dtag.addActionListener(event -> toggleMagMode());
        perBut = new Button();
        perBut.addActionListener(event -> changePerc());
        perTime = new Timer(10, perBut.getActionListeners()[0]);
        perTime.setRepeats(true);

    }

    private void changePerc() {
        if (animPerc == 100) {
            animPerc = 1;
            perTime.stop();
            this.repaint();
            return;
        }
        if (animPerc < 100) {
            if (animPerc > 20 && animPerc < 40) {
                animPerc = animPerc + 2;
            } else if (animPerc >= 40 && animPerc < 60) {
                animPerc = animPerc + 3;
            } else if (animPerc >= 60 && animPerc < 80) {
                animPerc = animPerc + 2;
            }
            animPerc = animPerc + 1;
        }
        if (animPerc > 100) {
            animPerc = 100;
        }
        this.repaint();
        return;
    }

    private void toggleMagMode() {
        if (mtag1.isSelected() || mtag2.isSelected() || mtag3.isSelected() || mtag4.isSelected()) {
            magMode = true;
        } else {
            magMode = false;
        }
        System.out.println(magMode);
    }
    private void etoggleMagMode() {
        magMode = false;
        System.out.println(magMode);
    }

    private Image getScaledImage(Image srcImg, int inWidth, int inHeight){
        BufferedImage resizedImg = new BufferedImage(inWidth, inHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D rendImg = resizedImg.createGraphics();

        rendImg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        rendImg.drawImage(srcImg, 0, 0, inWidth, inHeight, null);
        rendImg.dispose();

        return resizedImg;
    }

    private Point2D getDestPos(int index, int xt1, int yt1, int xt2, int yt2, int xt3, int yt3,
                            int xt4, int yt4) {
        boolean tt1 = above.pComArray.get(index).tag1;
        boolean tt2 = above.pComArray.get(index).tag2;
        boolean tt3 = above.pComArray.get(index).tag3;
        boolean tt4 = above.pComArray.get(index).tag4;
        if (xt1 == -1) {
            tt1 = false;
        }
        if (xt2 == -1) {
            tt2 = false;
        }
        if (xt3 == -1) {
            tt3 = false;
        }
        if (xt4 == -1) {
            tt4 = false;
        }
        int tx = 0;
        int ty = 0;
        int tagcount = 0;
        if (tt1) {
            tx = tx + xt1;
            ty = ty + yt1;
            tagcount++;
        }
        if (tt2) {
            tx = tx + xt2;
            ty = ty + yt2;
            tagcount++;
        }
        if (tt3) {
            tx = tx + xt3;
            ty = ty + yt3;
            tagcount++;
        }
        if (tt4) {
            tx = tx + xt4;
            ty = ty + yt4;
            tagcount++;
        }
        //System.out.println(tagcount);
        if (tagcount == 0) {
            return new Point2D.Double(-1, -1);
        }
        return new Point2D.Double(tx/tagcount, ty/tagcount);

    }

    private Point2D findOnLine(int x1, int y1, int x2, int y2, int percent) {
        int a = y2 - y1;
        int b = x2 - x1;
        //int c = (a * x1) + (b * y1);
        int xval = (int)( b * ((double)percent/100)) + x1;
        int yval = (int)( a * ((double)percent/100)) + y1;
        return new Point2D.Double(xval, yval);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (magnets.size() > 0) {
            magRecs.clear();
            destPoints.clear();
            ArrayList<JButton> mag1 = new ArrayList<>();
            ArrayList<JButton> mag2 = new ArrayList<>();
            ArrayList<JButton> mag3 = new ArrayList<>();
            ArrayList<JButton> mag4 = new ArrayList<>();
            for (JButton mag : magnets) {
                if (mag.getText().equals("Vacation")) {
                    mag1.add(mag);
                } else if (mag.getText().equals("Family")) {
                    mag2.add(mag);
                } else if (mag.getText().equals("School")) {
                    mag3.add(mag);
                } else if (mag.getText().equals("Work")) {
                    mag4.add(mag);
                }
            }
            int mx1 = -1;
            int my1 = 0;
            if (mag1.size() > 0) {
                mx1 = 0;
                for (JButton mag : mag1) {
                    mx1 = mx1 + mag.getX();
                    my1 = my1 + mag.getY();
                }
                mx1 = mx1 / mag1.size();
                my1 = my1 / mag1.size();
            }
            int mx2 = -1;
            int my2 = 0;
            if (mag2.size() > 0) {
                mx2 = 0;
                for (JButton mag : mag2) {
                    mx2 = mx2 + mag.getX();
                    my2 = my2 + mag.getY();
                }
                mx2 = mx2 / mag2.size();
                my2 = my2 / mag2.size();
            }
            int mx3 = -1;
            int my3 = 0;
            if (mag3.size() > 0) {
                mx3 = 0;
                for (JButton mag : mag3) {
                    mx3 = mx3 + mag.getX();
                    my3 = my3 + mag.getY();
                }
                mx3 = mx3 / mag3.size();
                my3 = my3 / mag3.size();
            }
            int mx4 = -1;
            int my4 = 0;
            if (mag4.size() > 0) {
                mx4 = 0;
                for (JButton mag : mag4) {
                    mx4 = mx4 + mag.getX();
                    my4 = my4 + mag.getY();
                }
                mx4 = mx4 / mag4.size();
                my4 = my4 / mag4.size();
            }


            this.removeAll();
            rectArr.clear();
            recImgs.clear();
            //recLocs.clear();
            Rectangle2D tempRect;
            for (int i = 0; i < imageArray.size(); i++) {
                Point2D ep = getDestPos(i, mx1, my1, mx2, my2, mx3, my3, mx4, my4);
                destPoints.add(ep);
                Rectangle2D oRec = startLocs.get(i);

                int xpos;
                int ypos;
                if ((int)ep.getX() == (int)oRec.getX() && (int)ep.getY() == (int)oRec.getY()) {
                    xpos = (int) ep.getX();
                    ypos = (int) ep.getY();
                    //perTime.stop();
                } else {
                    Point2D pp = findOnLine((int) oRec.getX(), (int) oRec.getY(),
                            (int) ep.getX(), (int) ep.getY(), animPerc);
                    xpos = (int) pp.getX();
                    ypos = (int) pp.getY();
                }
                if (ep.getX() == -1 && ep.getY() == -1) {
                    xpos = 0;
                    if (i != 0) {
                        xpos = (int) mDim * (i % 5);
                    }
                    ypos = (int) (mDim * floor(i / 5));
                }
                /*if (i == 0) {
                    System.out.println("startix: " + oRec.getX() + ", startiy: "
                            + oRec.getY() + ", percent: " + animPerc);
                    System.out.println("currentx: " + xpos + ", currenty "
                            + ypos);
                    System.out.println("endix: " + ep.getX() + ", endiy: "
                            + ep.getY());
                }*/
                if (imageArray.get(i).isSelected) {
                    g.setColor(selCol);
                    Rectangle2D rect = new Rectangle2D.Double(xpos, ypos, mDim, mDim);
                    tempRect = rect;
                    g2.fill(rect);
                    rectArr.add(rect);
                } else {
                    g.setColor(Color.GRAY);
                    Rectangle2D rect = new Rectangle2D.Double(xpos, ypos, mDim, mDim);
                    tempRect = rect;
                    g2.draw(rect);
                    rectArr.add(rect);
                }
                JToggleButton tempButt = imageArray.get(i).button;
                this.add(tempButt);
                ImageIcon tempIm = new ImageIcon(imageArray.get(i).path);
                recLocs.set(i, tempRect);
                if (animPerc == 100) {
                    startLocs.set(i, tempRect);
                }
                recImgs.add(tempIm);
                double tempBound = 0;
                if (tempIm.getIconHeight() > tempIm.getIconWidth()) {
                    tempBound = tempIm.getIconHeight() / imDim;
                } else {
                    tempBound = tempIm.getIconWidth() / imDim;
                }
                int imxpos = xpos + (int) (mDim / 2) - (int) ((tempIm.getIconWidth() / tempBound) / 2);
                int imypos = ypos + (int) (mDim / 2) - (int) ((tempIm.getIconHeight() / tempBound) / 2);
                tempButt.setIcon(new ImageIcon(getScaledImage(tempIm.getImage(),
                        (int) (tempIm.getIconWidth() / tempBound),
                        (int) (tempIm.getIconHeight() / tempBound))));
                g.drawImage(new ImageIcon(getScaledImage(tempIm.getImage(),
                        (int) (tempIm.getIconWidth() / tempBound),
                        (int) (tempIm.getIconHeight() / tempBound))).getImage(),
                        imxpos, imypos, this);
            }



            for (JButton mag : magnets) {
                g2.setColor(Color.DARK_GRAY);
                Rectangle2D tmrec = new Rectangle2D.Double(mag.getX(), mag.getY(), 60, 25);
                g2.fill(tmrec);
                magRecs.add(tmrec);
                g2.setColor(Color.WHITE);
                g2.drawString(mag.getText(), mag.getX() + 5, mag.getY() + 15);
            }

        } else {
            this.removeAll();
            rectArr.clear();
            recImgs.clear();
            recLocs.clear();
            startLocs.clear();
            Rectangle2D tempRect;
            for (int i = 0; i < imageArray.size(); i++) {
                int xpos = 0;
                if (i != 0) {
                    xpos = (int) mDim * (i % 5);
                }
                int ypos = (int) (mDim * floor(i / 5));
                if (imageArray.get(i).isSelected) {
                    g.setColor(selCol);
                    Rectangle2D rect = new Rectangle2D.Double(xpos, ypos, mDim, mDim);
                    tempRect = rect;
                    g2.fill(rect);
                    rectArr.add(rect);
                } else {
                    g.setColor(Color.GRAY);
                    Rectangle2D rect = new Rectangle2D.Double(xpos, ypos, mDim, mDim);
                    tempRect = rect;
                    g2.draw(rect);
                    rectArr.add(rect);
                }
                JToggleButton tempButt = imageArray.get(i).button;
                this.add(tempButt);
                ImageIcon tempIm = new ImageIcon(imageArray.get(i).path);
                recLocs.add(tempRect);
                startLocs.add(tempRect);
                recImgs.add(tempIm);
                double tempBound = 0;
                if (tempIm.getIconHeight() > tempIm.getIconWidth()) {
                    tempBound = tempIm.getIconHeight() / imDim;
                } else {
                    tempBound = tempIm.getIconWidth() / imDim;
                }
                int imxpos = xpos + (int) (mDim / 2) - (int) ((tempIm.getIconWidth() / tempBound) / 2);
                int imypos = ypos + (int) (mDim / 2) - (int) ((tempIm.getIconHeight() / tempBound) / 2);
                tempButt.setIcon(new ImageIcon(getScaledImage(tempIm.getImage(),
                        (int) (tempIm.getIconWidth() / tempBound),
                        (int) (tempIm.getIconHeight() / tempBound))));
                g.drawImage(new ImageIcon(getScaledImage(tempIm.getImage(),
                        (int) (tempIm.getIconWidth() / tempBound),
                        (int) (tempIm.getIconHeight() / tempBound))).getImage(),
                        imxpos, imypos, this);
            }
            int minheight = frameSize.height;
            if ((int) ((ceil(imageArray.size() / 5)) * mDim) > minheight) {
                minheight = (int) ((ceil(imageArray.size() / 5)) * mDim);
            }

            this.setSize(frameSize.width, minheight);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (int i = 0; i < magRecs.size(); i++) {
                if (magRecs.get(i).contains(e.getX(), e.getY())) {
                    magDrag = true;
                    dragInd = i;
                    System.out.println("clicked mag");
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            for (int i = 0; i < magRecs.size(); i++) {
                if (magRecs.get(i).contains(e.getX(), e.getY())) {
                    magRecs.remove(i);
                    magnets.remove(i);
                }
            }
        }
        if (magnets.size() > 0) {
            perTime.start();
        }
        this.repaint();
    }

    public void mouseReleased(MouseEvent e) {
        magDrag = false;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        //if (e.getClickCount() == 2) {
        if (!magMode) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                int count = 0;
                for (Rectangle2D rect : rectArr) {
                    if (rect.contains(e.getX(), e.getY())) {
                        imageArray.get(selInd).isSelected = false;
                        selInd = count;
                        imageArray.get(selInd).isSelected = true;
                        System.out.println("click thing at " + selInd);
                        above.selInd = selInd;
                        above.checkIncBut();
                        if (e.getClickCount() == 2) {
                            above.viewSwap(0);
                        }
                        break;
                    } else {
                        //System.out.println("click nothing");
                    }
                    count++;
                }
                if (dtag.isSelected()) {
                    for (int i = 0; i < magRecs.size(); i++) {
                        if (magRecs.get(i).contains(e.getX(), e.getY())) {
                            magRecs.remove(i);
                            magnets.remove(i);
                        }
                    }
                }
            }
            this.repaint();
        } else {

            if (SwingUtilities.isLeftMouseButton(e)) {
                if (!magDrag) {
                    JButton newMag = new JButton();
                    newMag.setLocation(e.getX(), e.getY());
                    if (mtag1.isSelected()) {
                        newMag.setText("Vacation");
                    } else if (mtag2.isSelected()) {
                        newMag.setText("Family");
                    } else if (mtag3.isSelected()) {
                        newMag.setText("School");
                    } else if (mtag4.isSelected()) {
                        newMag.setText("Work");
                    }
                    magnets.add(newMag);
                    perTime.start();
                    this.repaint();
                }
            }
        }
        //}
    }

    public void mouseDragged(MouseEvent e) {
        if (magDrag) {
            magnets.get(dragInd).setLocation(e.getX(), e.getY());
            this.repaint();
        }
        perTime.start();
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void setSelect(int i) {
        if (i == selInd) {
            return;
        }
        imageArray.get(selInd).isSelected = false;
        selInd = i;
        imageArray.get(selInd).isSelected = true;
        this.repaint();
    }
}
