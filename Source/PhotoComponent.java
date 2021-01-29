package com.company;

import org.w3c.dom.Text;

import javax.naming.Context;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class PhotoComponent extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
    public ImageIcon pic;
    public boolean isFlipped = false;
    private boolean midStroke = false;
    public boolean gestMode = false;
    private ArrayList<TextRect> textBoxes = new ArrayList<>();
    private ArrayList<Squiggle> strokes = new ArrayList<>();
    private ArrayList<String> itemQ = new ArrayList<>();
    public ArrayList<Point2D> gestPoints = new ArrayList<>();
    public boolean penMode = false;
    public boolean textMode = false;
    public Color penColor = Color.BLACK;
    public boolean tag1 = false, tag2 = false, tag3 = false, tag4 = false;
    public int conX, conY;
    public DollarRecognizer drec;
    public Result gestRes;
    public boolean gestAcc = false;
    public MyWindow scont;

    public PhotoComponent(ImageIcon inImg, Dimension inDim, int ix, int iy, DollarRecognizer indr, MyWindow incont) {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        this.setSize(new Dimension(inImg.getIconWidth(), inImg.getIconHeight()));
        pic = inImg;
        this.setOpaque(true);
        conX = ix;
        conY = iy;
        this.setFocusable(true);
        this.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 12));
        drec = indr;
        scont = incont;

    }

    @Override
    public void setFocusable(boolean b) {
        super.setFocusable(b);
    }

    /**
     * Paints either continuously as user is drawing
     * or paints entire frame based on the order
     * objects were created
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RenderingHints rh2 = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        if (isFlipped) {
            if (!midStroke) {
                g2.setColor(Color.WHITE);
                g2.fillRect(((conX/2) - (pic.getIconWidth()/2)), 0,
                        pic.getIconWidth() + 1, pic.getIconHeight() + 1);
                int sqc = 0;
                int trc = 0;
                for (String iType : itemQ) {
                    //for (Squiggle stroke : strokes) {
                    if (iType.equals("stroke")) {
                        Squiggle stroke = strokes.get(sqc);
                        if (stroke.beautify) {
                            g2.setColor(stroke.color);
                            System.out.println("beautifying");
                            if (stroke.bShape.equals("rectangle")) {
                                System.out.println("brec");
                                g2.drawRect((int)stroke.stroke.get(0).getX(), (int)stroke.stroke.get(0).getY(),
                                        stroke.xub - stroke.xlb, stroke.yub - stroke.ylb);
                            } else if (stroke.bShape.equals("circle")) {
                                g2.draw(new Ellipse2D.Double(stroke.xlb, stroke.ylb,
                                        stroke.xub - stroke.xlb, stroke.yub - stroke.ylb));
                            }
                        } else {
                            for (int i = 0; i < stroke.stroke.size() - 1; i++) {
                                g2.setColor(stroke.color);
                                g2.drawLine(stroke.stroke.get(i).x, stroke.stroke.get(i).y,
                                        stroke.stroke.get(i + 1).x, stroke.stroke.get(i + 1).y);
                            }
                        }
                        sqc++;
                    }
                    //for (TextRect trex : textBoxes) {
                    if (iType.equals("textbox")) {
                        TextRect trex = textBoxes.get(trc);
                        if (trex.equals(textBoxes.get(textBoxes.size() - 1)) && textMode) {
                            g2.setColor(trex.color);
                            g2.drawRect(trex.position.x, trex.position.y, trex.width, trex.height);
                            g2.setColor(Color.WHITE);
                            g2.fillRect(trex.position.x + 1, trex.position.y + 1,
                                    trex.width - 2, trex.height - 2);
                        }
                        g2.setRenderingHints(rh2);
                        g2.setColor(trex.color);
                        for (int i = 0; i < trex.word.size(); i++) {
                            g2.drawString(String.valueOf(trex.word.get(i)),
                                    trex.charSpots.get(i).x, trex.charSpots.get(i).y);
                        }
                        g2.setRenderingHints(rh);
                        trc++;
                    }
                }
            } else {
                if (penMode) {
                    ArrayList<Point> stroke = strokes.get(strokes.size() - 1).stroke;
                    g2.setColor(strokes.get(strokes.size() - 1).color);

                        if (stroke.size() >= 2) {
                            g2.drawLine(stroke.get(stroke.size() - 2).x, stroke.get(stroke.size() - 2).y,
                                    stroke.get(stroke.size() - 1).x, stroke.get(stroke.size() - 1).y);
                        }

                }
                if (textMode) {
                    TextRect cRec = textBoxes.get(textBoxes.size() - 1);
                    if (cRec.width < 0 && cRec.height >= 0) {
                        g2.setColor(Color.WHITE);
                        g2.fillRect(cRec.position.x + cRec.pw, cRec.position.y, - cRec.pw, cRec.ph);
                        g2.setColor(cRec.color);
                        g2.drawRect(cRec.position.x + cRec.width, cRec.position.y,
                                - cRec.width, cRec.height);
                    } else if (cRec.height < 0 && cRec.width >= 0) {
                        g2.setColor(Color.WHITE);
                        g2.fillRect(cRec.position.x, cRec.position.y + cRec.ph,
                                cRec.pw, - cRec.ph);
                        g2.setColor(cRec.color);
                        g2.drawRect(cRec.position.x, cRec.position.y + cRec.height,
                                cRec.width, - cRec.height);
                    } else if (cRec.height < 0 && cRec.width < 0) {
                        g2.setColor(Color.WHITE);
                        g2.fillRect(cRec.position.x + cRec.pw, cRec.position.y + cRec.ph,
                                - cRec.pw, - cRec.ph);
                        g2.setColor(cRec.color);
                        g2.drawRect(cRec.position.x + cRec.pw, cRec.position.y + cRec.height,
                                - cRec.width, - cRec.height);
                    } else {
                        g2.setColor(Color.WHITE);
                        g2.fillRect(cRec.position.x, cRec.position.y, cRec.pw, cRec.ph);
                        g2.setColor(cRec.color);
                        g2.drawRect(cRec.position.x, cRec.position.y, cRec.width, cRec.height);
                    }
                }
            }
        } else {
            g2.drawImage(pic.getImage(), ((conX/2) - (pic.getIconWidth()/2)), 0, this);
        }
        if (gestMode) {
            g2.setColor(Color.ORANGE);
            for (int i = 0; i < gestPoints.size() - 1; i++) {
                    g2.drawLine((int) gestPoints.get(i).getX(),
                            (int) gestPoints.get(i).getY(),
                            (int) gestPoints.get(i + 1).getX(),
                            (int) gestPoints.get(i + 1).getY());

            }
        }
    }

    private void gestAction() {
        if (gestAcc) {
            if (gestRes.getName().equals("v")) {
                scont.increment(1);
                scont.textLabel.setText("v, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("caret")) {
                scont.increment(-1);
                scont.textLabel.setText("caret, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("delete")) {
                scont.delImage();
                scont.textLabel.setText("delete, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("check")) {
                tag1 = !tag1;
                scont.drawCheckBoxes();
                scont.textLabel.setText("check, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("star")) {
                tag2 = !tag2;
                scont.drawCheckBoxes();
                scont.textLabel.setText("star, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("pigtail")) {
                tag3 = !tag3;
                scont.drawCheckBoxes();
                scont.textLabel.setText("pigtail, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("x")) {
                tag4 = !tag4;
                scont.drawCheckBoxes();
                scont.textLabel.setText("x, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("right square bracket")) {
                scont.zoomViewer(1);
                scont.textLabel.setText("right square bracket, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("left square bracket")) {
                scont.zoomViewer(-1);
                scont.textLabel.setText("left square bracket, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("circle")) {
                scont.importHelper();
                scont.textLabel.setText("circle, score: " + gestRes.getScore());
            }
            if (gestRes.getName().equals("arrow")) {
                scont.viewSwap(1);
                scont.textLabel.setText("arrow, score: " + gestRes.getScore());
            }
        }
    }


    /**
     * Adds new squiggle or textrect to their
     * respective arrays and adds item to
     * the itemQ depending on which tool
     * is selected
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        requestFocus();
        if (SwingUtilities.isRightMouseButton(e)) {
            gestPoints.clear();
            gestMode = true;
            gestPoints.add(e.getPoint());
            this.repaint();
        } else {
            if (penMode && isFlipped && inBounds(e.getPoint())) {
                ArrayList<Point> curStroke = new ArrayList<>();
                Squiggle curSquig = new Squiggle(curStroke, penColor);
                strokes.add(curSquig);
                itemQ.add("stroke");
                midStroke = true;
            }
            if (textMode && isFlipped && inBounds(e.getPoint())) {
                TextRect curRec = new TextRect(penColor, 0, 0, e.getPoint());
                textBoxes.add(curRec);
                itemQ.add("textbox");
                midStroke = true;
            }
        }

    }

    public void mouseReleased(MouseEvent e) {
        if (textMode) {
            textBoxes.get(textBoxes.size() - 1).checkBackwards();
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            gestRes = drec.recognize(gestPoints);
            if (gestRes.getScore() >= 0.77) {
                gestAcc = true;
            } else {
                gestAcc = false;
            }
            gestMode = false;
            this.repaint();
            System.out.println(gestRes.toString());
            System.out.println(gestRes.getScore());
            scont.scrollArea.repaint();
            gestAction();
        }
        if (SwingUtilities.isLeftMouseButton(e) && isFlipped && strokes.size() > 0) {
            Squiggle bstroke = strokes.get(strokes.size() - 1);
            ArrayList<Point2D> tempStr = new ArrayList<>();
            for (Point point : bstroke.stroke) {
                tempStr.add((Point2D)point);
            }
            Result tempRes = drec.recognize(tempStr);
            if (tempRes.getScore() >= 0.77) {
                if (tempRes.getName().equals("rectangle")) {
                    bstroke.beautify = true;
                    bstroke.bShape = "rectangle";
                    bstroke.setBounds();
                    scont.textLabel.setText("rectangle, score: " + tempRes.getScore());
                }
                if (tempRes.getName().equals("circle")) {
                    bstroke.beautify = true;
                    bstroke.bShape = "circle";
                    bstroke.setBounds();
                    scont.textLabel.setText("circle, score: " + tempRes.getScore());
                }
            }
        }
        for (Squiggle stroke : strokes) {
            ArrayList<Point2D> ddstr = new ArrayList<>();
            for (Point pt : stroke.stroke) {
                ddstr.add(pt);
            }
            System.out.println(drec.recognize(ddstr).toString());
        }
        midStroke = false;
        this.repaint();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        if (!penMode && !textMode) {
            if (e.getClickCount() == 2) {
                isFlipped = !isFlipped;
                this.repaint();
            }
        }
        System.out.println("clicked");
    }

    /**
     * Updates the currently in use squiggle or textrect
     * with current mouse position
     * @param e
     */
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            gestMode = true;
            gestPoints.add(e.getPoint());
            this.repaint();
        } else {
            if (penMode && isFlipped) {
                if (inBounds(e.getPoint())) {
                    strokes.get(strokes.size() - 1).stroke.add(e.getPoint());
                    this.repaint();
                }
            }
            if (textMode && isFlipped && inBounds(e.getPoint())) {
                Point origin = textBoxes.get(textBoxes.size() - 1).position;
                textBoxes.get(textBoxes.size() - 1).pw =
                        textBoxes.get(textBoxes.size() - 1).width;
                textBoxes.get(textBoxes.size() - 1).ph =
                        textBoxes.get(textBoxes.size() - 1).height;
                textBoxes.get(textBoxes.size() - 1).width = e.getPoint().x - origin.x;
                textBoxes.get(textBoxes.size() - 1).height = e.getPoint().y - origin.y;
                this.repaint();
            }
        }
    }

    public void mouseMoved(MouseEvent e) {

    }

    /**
     * Checks to make sure that the given point
     * is within the bounds of the flipped image size
     * @param inPoi
     * @return true if inside, false if outside
     */
    private boolean inBounds(Point inPoi) {
        int right = (conX/2) + (pic.getIconWidth()/2);
        int left = (conX/2) - (pic.getIconWidth()/2);
        if (inPoi.x <= right && inPoi.x >= left) {
            if (inPoi.y >= 0 && inPoi.y <= pic.getIconHeight()) {
                return true;
            }
        }
        return false;
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (textMode && textBoxes.size() > 0) {
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                textBoxes.get(textBoxes.size() - 1).delChar();
                //System.out.println("backspace");
            } else {
                textBoxes.get(textBoxes.size() - 1).addChar(e.getKeyChar());
            }
            this.repaint();
        }
    }

    public void keyReleased(KeyEvent e) {

    }
}
