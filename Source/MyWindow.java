package com.company;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class MyWindow implements ComponentListener{
    private ArrayList<ImageButton> imageArray = new ArrayList<>();
    public ArrayList<PhotoComponent> pComArray = new ArrayList<>();
    public JPanel viewArea = new JPanel();
    public JScrollPane scrollArea = new JScrollPane();
    private ImageButton selectedImage;
    private JCheckBox vacation, family, school, work;
    public JLabel textLabel;//, imagePath;
    private JTextArea imagePath;
    public int selInd = -1;
    private JFrame frame;
    private int viewMode = 0;
    private JButton zoomIn, zoomOut, delete, previous, next;
    private double zScale = 1;
    private JMenuBar menuBar;
    private JMenu file, view, mode, colors;
    private JMenuItem importMenu, exit, delMenu;
    public JToggleButton mtag1, mtag2, mtag3, mtag4, dtag;
    private JRadioButtonMenuItem penTool, textTool, normalTool, pblack, pred, pblue, pgreen, photoView, gridView;
    private String importedPath = "";
    private boolean isPen = false;
    private boolean isText = false;
    private Color pColor = Color.BLACK;
    private GridComponent gridViewer;
    public DollarRecognizer dRecog = new DollarRecognizer();
    private ButtonGroup magnetGroup;
    private JButton eMags;

    public MyWindow() {

    }

    public void makeWindow(String[] inputPath) {

        frame = new JFrame("CS4470 Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        textLabel = new JLabel("Status", SwingConstants.CENTER);
        textLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        frame.getContentPane().add(textLabel, BorderLayout.SOUTH);
        imagePath = new JTextArea(1, SwingConstants.CENTER);
        imagePath.setEditable(false);
        imagePath.setDragEnabled(true);
        imagePath.setBorder(BorderFactory.createLoweredBevelBorder());
        frame.getContentPane().add(imagePath, BorderLayout.NORTH);
        viewArea.setLayout(new GridLayout(0, 3));
        viewArea.setPreferredSize(new Dimension(1, 1));
        scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollArea.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        frame.getContentPane().add(scrollArea, BorderLayout.CENTER);


        //-----Menu section-----



        menuBar = new JMenuBar();
        file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.getAccessibleContext().setAccessibleDescription("file menu");
        file.addActionListener((event) -> textLabel.setText("File menu opened"));
        menuBar.add(file);

        importMenu = new JMenuItem("Import", KeyEvent.VK_T);
        importMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK));
        importMenu.getAccessibleContext().setAccessibleDescription("import images");
        importMenu.addActionListener((event) -> importHelper());
        file.add(importMenu);

        delMenu = new JMenuItem("Delete", KeyEvent.VK_X);
        delMenu.addActionListener((event) -> delImage());
        delMenu.setEnabled(false);
        file.add(delMenu);

        exit = new JMenuItem("exit");
        exit.setMnemonic(KeyEvent.VK_E);
        exit.addActionListener((event) -> System.exit(0));
        file.add(exit);

        view = new JMenu("View");
        view.setMnemonic(KeyEvent.VK_V);
        view.getAccessibleContext().setAccessibleDescription("view menu");
        menuBar.add(view);

        ButtonGroup viewRGroup = new ButtonGroup();
        photoView = new JRadioButtonMenuItem("Photo View");
        photoView.setSelected(true);
        photoView.addActionListener((event) -> textLabel.setText("Photo View selected"));
        photoView.addActionListener((event) -> viewSwap(0));
        viewRGroup.add(photoView);
        view.add(photoView);

        gridView = new JRadioButtonMenuItem("Grid View");
        viewRGroup.add(gridView);
        gridView.addActionListener((event) -> textLabel.setText("Grid View selected"));
        gridView.addActionListener((event) -> viewSwap(1));
        view.add(gridView);

        mode = new JMenu("Mode");
        mode.getAccessibleContext().setAccessibleDescription("Cursor Mode");
        menuBar.add(mode);

        ButtonGroup cursorMode = new ButtonGroup();
        normalTool = new JRadioButtonMenuItem("Normal");
        normalTool.setSelected(true);
        normalTool.addActionListener((event) -> changeComMode(false, false));
        cursorMode.add(normalTool);
        mode.add(normalTool);

        penTool = new JRadioButtonMenuItem("Pen Tool");
        penTool.addActionListener((event) -> changeComMode(true, false));
        penTool.addActionListener((event) -> pComArray.get(selInd).repaint());
        cursorMode.add(penTool);
        mode.add(penTool);

        textTool = new JRadioButtonMenuItem("Text Tool");
        textTool.addActionListener((event) -> changeComMode(false, true));
        textTool.addActionListener((event) -> pComArray.get(selInd).repaint());
        cursorMode.add(textTool);
        mode.add(textTool);

        colors = new JMenu("Color");
        mode.add(colors);

        ButtonGroup colorSel = new ButtonGroup();
        pblack = new JRadioButtonMenuItem("Black");
        pblack.setSelected(true);
        pblack.addActionListener((event) -> pComArray.get(selInd).penColor = Color.BLACK);
        colorSel.add(pblack);
        colors.add(pblack);

        pred = new JRadioButtonMenuItem("Red");
        pred.addActionListener((event) -> pComArray.get(selInd).penColor = Color.RED);
        colorSel.add(pred);
        colors.add(pred);

        pgreen = new JRadioButtonMenuItem("Green");
        pgreen.addActionListener((event) -> pComArray.get(selInd).penColor = Color.GREEN);
        colorSel.add(pgreen);
        colors.add(pgreen);

        pblue = new JRadioButtonMenuItem("Blue");
        pblue.addActionListener((event) -> pComArray.get(selInd).penColor = Color.BLUE);
        colorSel.add(pblue);
        colors.add(pblue);


        //-----Left Buttons------

        JPanel controls = new JPanel();
        BoxLayout controlLayout = new BoxLayout(controls, BoxLayout.Y_AXIS);
        controls.setLayout(controlLayout);
        Border controlBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        controls.setBorder(controlBorder);
        controls.setPreferredSize(new Dimension(150, 200));
        frame.getContentPane().add(controls, BorderLayout.WEST);

        //Controls

        ImageIcon prevIcn = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/com/company/Icons/prev.png")
        ));
        previous = new JButton(prevIcn);
        previous.setText("Previous");
        previous.setVerticalTextPosition(AbstractButton.CENTER);
        previous.setHorizontalTextPosition(AbstractButton.TRAILING);
        previous.addActionListener((event) -> increment(-1));
        previous.setMinimumSize(new Dimension(140, 25));
        previous.setEnabled(false);
        controls.add(previous);
        controls.add(Box.createRigidArea(new Dimension(0,10)));


        ImageIcon nextIcn = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/com/company/Icons/next.png")
        ));
        next = new JButton(nextIcn);
        next.setText("Next");
        next.setVerticalTextPosition(AbstractButton.CENTER);
        next.setHorizontalTextPosition(AbstractButton.LEADING);
        next.addActionListener((event) -> increment(1));
        next.setMinimumSize(new Dimension(140, 25));
        next.setEnabled(false);
        controls.add(next);
        controls.add(Box.createRigidArea(new Dimension(0,10)));

        ImageIcon delIcn = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/com/company/Icons/del.png")
        ));
        delete = new JButton(delIcn);
        delete.setText("Delete");
        delete.setVerticalTextPosition(AbstractButton.CENTER);
        delete.setHorizontalTextPosition(AbstractButton.TRAILING);
        delete.addActionListener((event) -> delImage());
        delete.setMinimumSize(new Dimension(140, 25));
        delete.setEnabled(false);
        controls.add(delete);
        controls.add(Box.createRigidArea(new Dimension(0,40)));

        ImageIcon ziIcn = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/com/company/Icons/zoomIn.png")
        ));
        zoomIn = new JButton(ziIcn);
        zoomIn.setText("Zoom In");
        zoomIn.setEnabled(false);
        zoomIn.setVerticalTextPosition(AbstractButton.CENTER);
        zoomIn.setHorizontalTextPosition(AbstractButton.TRAILING);
        zoomIn.setMinimumSize(new Dimension(140, 25));
        zoomIn.addActionListener((event) -> zoomViewer(1));
        controls.add(zoomIn);
        controls.add(Box.createRigidArea(new Dimension(0,10)));

        ImageIcon zoIcn = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/com/company/Icons/zoomOut.png")
        ));
        zoomOut = new JButton(zoIcn);
        zoomOut.setText("Zoom Out");
        zoomOut.setEnabled(false);
        zoomOut.setVerticalTextPosition(AbstractButton.CENTER);
        zoomOut.setHorizontalTextPosition(AbstractButton.TRAILING);
        zoomOut.setMinimumSize(new Dimension(140, 25));
        zoomOut.addActionListener((event) -> zoomViewer(-1));
        controls.add(zoomOut);
        controls.add(Box.createRigidArea(new Dimension(0,40)));


        //Tags
        vacation = new JCheckBox("Vacation");
        vacation.setEnabled(false);
        vacation.addActionListener((event) -> textLabel.setText("Vacation toggled"));
        vacation.addActionListener((event) -> checkTag(1));
        controls.add(vacation);
        controls.add(Box.createRigidArea(new Dimension(0,10)));

        family = new JCheckBox("Family");
        family.setEnabled(false);
        family.addActionListener((event) -> textLabel.setText("Family toggled"));
        family.addActionListener((event) -> checkTag(2));
        controls.add(family);
        controls.add(Box.createRigidArea(new Dimension(0,10)));

        school = new JCheckBox("School");
        school.setEnabled(false);
        school.addActionListener((event) -> textLabel.setText("School toggled"));
        school.addActionListener((event) -> checkTag(3));
        controls.add(school);
        controls.add(Box.createRigidArea(new Dimension(0,10)));

        work = new JCheckBox("Work");
        work.setEnabled(false);
        work.addActionListener((event) -> textLabel.setText("Work toggled"));
        work.addActionListener((event) -> checkTag(4));
        controls.add(work);
        controls.add(Box.createRigidArea(new Dimension(0,10)));

        magnetGroup = new ButtonGroup();

        mtag1 = new JToggleButton("Vacation Magnet");
        mtag1.setEnabled(false);
        mtag1.setSelected(false);
        controls.add(mtag1);
        magnetGroup.add(mtag1);

        mtag2 = new JToggleButton("Family Magnet");
        mtag2.setEnabled(false);
        mtag2.setSelected(false);
        controls.add(mtag2);
        magnetGroup.add(mtag2);

        mtag3 = new JToggleButton("School Magnet");
        mtag3.setEnabled(false);
        mtag3.setSelected(false);
        controls.add(mtag3);
        magnetGroup.add(mtag3);

        mtag4 = new JToggleButton("Work Magnet");
        mtag4.setEnabled(false);
        mtag4.setSelected(false);
        controls.add(mtag4);
        magnetGroup.add(mtag4);

        dtag = new JToggleButton("Delete Magnet");
        dtag.setEnabled(false);
        dtag.setSelected(false);
        controls.add(dtag);
        magnetGroup.add(dtag);

        eMags = new JButton("Cease MagMode");
        eMags.setEnabled(false);
        eMags.addActionListener(event -> magnetGroup.clearSelection());
        controls.add(eMags);
        magnetGroup.add(eMags);


        frame.setPreferredSize(new Dimension(1080, 720));
        frame.setMinimumSize(new Dimension(560, 470));
        frame.addComponentListener(this);
        frame.setLocationRelativeTo(null);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setVisible(true);

        if (inputPath.length > 0) {
            loadImages(inputPath[0]);
            if (viewMode == 0) {
                redrawScrollViewer(scrollArea);
            } else if (viewMode == 1) {
                redrawViewer(scrollArea);
            }
        }
    }

    /**
     * Creats a file chooser and returns the path of the
     * chosen file or directory
     * @return
     */
    private String getFiles() {
        String outPath = "empty";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int sd = fileChooser.showSaveDialog(null);

        if (sd == JFileChooser.APPROVE_OPTION) {
            outPath = fileChooser.getSelectedFile().getAbsolutePath();
        }
        loadImages(outPath);
        if (imageArray.size() > 1) {
            next.setEnabled(true);
        }
        if (viewMode == 0) {
            redrawScrollViewer(scrollArea);
        } else if (viewMode == 1) {
            redrawViewer(scrollArea);
        }
        return outPath;

    }

    /**
     * Recursively adds images from the provided path
     * to the array of ImageButtons
     * @param path
     */
    private void loadImages(String path) {
        if (path.equals("empty")) {
            return;
        }
        if (path.toLowerCase().endsWith(".png") || path.toLowerCase().endsWith(".jpg")) {
            ImageIcon icon = new ImageIcon(path);
            PhotoComponent pomp = new PhotoComponent(icon, new Dimension(icon.getIconWidth(),
                    icon.getIconHeight()), scrollArea.getWidth(), scrollArea.getHeight(), dRecog, this);
            ImageButton loadedIm = new ImageButton(path, icon);
            imageArray.add(loadedIm);
            pComArray.add(pomp);
            return;
        }
        try {
            File directory = new File(path);
            File fileArr[] = directory.listFiles();
            for (File file : fileArr) {
                loadImages(file.getAbsolutePath());
            }
        }catch (Exception e) {

        }
        System.out.println("---" + imageArray.size());
        if (imageArray.size() > 0) {
            selInd = 0;
        }
        return;
    }

    /**
     * Redraws grid view mode, disables zoom
     * @param pane
     */
    private void redrawViewer(JScrollPane pane) {
        scrollArea.getViewport().removeAll();
        scrollArea.removeAll();
        scrollArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollArea.setViewport(new JViewport());
        viewArea.removeAll();
        if (imageArray.size() > 0) {
            vacation.setEnabled(true);
            family.setEnabled(true);
            school.setEnabled(true);
            work.setEnabled(true);
            delete.setEnabled(true);
            zoomIn.setEnabled(false);
            zoomOut.setEnabled(false);
            delMenu.setEnabled(true);
            mtag1.setEnabled(true);
            mtag2.setEnabled(true);
            mtag3.setEnabled(true);
            mtag4.setEnabled(true);
            dtag.setEnabled(true);
            eMags.setEnabled(true);
            gridViewer = new GridComponent(imageArray, pane.getSize(), selInd, this, mtag1, mtag2,
                    mtag3, mtag4, dtag, eMags, magnetGroup);
            gridViewer.setPreferredSize(pane.getSize());
            scrollArea.getViewport().add(gridViewer);
        } else {
            vacation.setEnabled(false);
            family.setEnabled(false);
            school.setEnabled(false);
            work.setEnabled(false);
            delete.setEnabled(false);
            delMenu.setEnabled(false);
            previous.setEnabled(false);
            next.setEnabled(false);
            mtag1.setEnabled(false);
            mtag2.setEnabled(false);
            mtag3.setEnabled(false);
            mtag4.setEnabled(false);
            dtag.setEnabled(false);
            eMags.setEnabled(false);

        }
        if (imageArray.size() > 1 && selInd != imageArray.size()-1) {
            next.setEnabled(true);
        }
        drawCheckBoxes();
        viewArea.revalidate();
        pane.revalidate();
        frame.revalidate();
        viewArea.repaint();
        pane.repaint();
    }

    /**
     * Redraws the photo view mode, makes sure all buttons are enabled
     * @param pane
     */
    private void redrawScrollViewer(JScrollPane pane) {
        scrollArea.getViewport().removeAll();
        scrollArea.removeAll();
        scrollArea.setViewport(new JViewport());
        scrollArea.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        //scrollArea.getViewport().setBackground(Color.lightGray);
        //scrollArea.getViewport().repaint();
        if (imageArray.size() > 0) {
            if (selInd == -1) {
                selInd = 0;
            }
            vacation.setEnabled(true);
            family.setEnabled(true);
            school.setEnabled(true);
            work.setEnabled(true);
            delete.setEnabled(true);
            zoomIn.setEnabled(true);
            zoomOut.setEnabled(true);
            delMenu.setEnabled(true);
            mtag1.setEnabled(false);
            mtag2.setEnabled(false);
            mtag3.setEnabled(false);
            mtag4.setEnabled(false);
            dtag.setEnabled(false);
            eMags.setEnabled(false);
            imagePath.setText(imageArray.get(selInd).path);
            ImageIcon icn = new ImageIcon(imageArray.get(selInd).path);
            if (zScale != 1) {
                icn.setImage(getScaledImage(icn.getImage(),
                        (int)(icn.getIconWidth() * zScale), (int)(icn.getIconHeight() * zScale)));
            }
            Dimension icnSz = new Dimension((icn.getIconWidth()), (icn.getIconHeight()));
            PhotoComponent pimg = pComArray.get(selInd);
            pimg.pic = icn;
            pimg.setSize(icnSz);
            //pimg.setMinimumSize(icnSz);
            pimg.setPreferredSize(icnSz);
            pimg.setEnabled(true);
            pimg.setVisible(true);
            scrollArea.getViewport().add(pimg);

        } else {
            vacation.setEnabled(false);
            family.setEnabled(false);
            school.setEnabled(false);
            work.setEnabled(false);
            delete.setEnabled(false);
            zoomIn.setEnabled(false);
            zoomOut.setEnabled(false);
            delMenu.setEnabled(false);
            previous.setEnabled(false);
            next.setEnabled(false);
            mtag1.setEnabled(false);
            mtag2.setEnabled(false);
            mtag3.setEnabled(false);
            mtag4.setEnabled(false);
            dtag.setEnabled(false);
            eMags.setEnabled(false);
        }
        if (imageArray.size() > 1 && selInd != imageArray.size()-1) {
            next.setEnabled(true);
        }
        if (pComArray.size() > 0) {
            pComArray.get(selInd).revalidate();
        }
        scrollArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollArea.revalidate();
        scrollArea.repaint();
        drawCheckBoxes();
        pane.revalidate();
        frame.revalidate();
        if (pComArray.size() > 0) {
            pComArray.get(selInd).repaint();
        }
        pane.repaint();
    }

    /**
     * Sets selected image to point to current selected image
     * and sets checkboxes to show image's tags
     * @param button
     */
    private void assignSelected(ImageButton button) {
        selectedImage = button;
        button.button.setSelected(true);
        for (ImageButton other : imageArray) {
            if (!other.equals(button)) {
                other.button.setSelected(false);
            }
        }
        imagePath.setText(selectedImage.path);
        viewArea.revalidate();
        viewArea.repaint();
    }

    /**
     * Moves to next or previous image and resets
     * scaling factor
     * @param direction
     */
    public void increment(int direction) {
        zScale = 1;
        if (direction == 1) {
            textLabel.setText("Next selected");
            if (selInd < imageArray.size()-1) {
                previous.setEnabled(true);
                imageArray.get(selInd).isSelected = false;
                selInd++;
                imageArray.get(selInd).isSelected = true;
                //drawCheckBoxes();
                if (selInd == imageArray.size()-1) {
                    next.setEnabled(false);
                }
            }
        } else if (direction == -1) {
            textLabel.setText("Previous selected");
            if (selInd > 0) {
                next.setEnabled(true);
                imageArray.get(selInd).isSelected = false;
                selInd--;
                imageArray.get(selInd).isSelected = true;
                //drawCheckBoxes();
                if (selInd == 0) {
                    previous.setEnabled(false);
                }
            } else {
                return;
            }
        }
        if (selInd == -1) {
            //System.out.println("index negative");
            return;
        }

        selectedImage = imageArray.get(selInd);
        assignSelected(selectedImage);
        //drawCheckBoxes();
        if (viewMode == 0) {
            pComArray.get(selInd).penColor = pColor;
            changeComMode(penTool.isSelected(), textTool.isSelected());
            if (pblack.isSelected()) {
                pComArray.get(selInd).penColor = Color.BLACK;
            }
            if (pblue.isSelected()) {
                pComArray.get(selInd).penColor = Color.BLUE;
            }
            if (pred.isSelected()) {
                pComArray.get(selInd).penColor = Color.RED;
            }
            if (pgreen.isSelected()) {
                pComArray.get(selInd).penColor = Color.GREEN;
            }
            redrawScrollViewer(scrollArea);
            pComArray.get(selInd).requestFocus();
        } else if (viewMode == 1) {
            redrawViewer(scrollArea);
        }
    }

    /**
     * Removes the currently selected image from the
     * array and removes it from view
     */
    public void delImage() {
        textLabel.setText("Delete selected");
        if (selInd < imageArray.size()-1 && selInd != -1) {
            imageArray.remove(selInd);
            pComArray.remove(selInd);
            selectedImage = imageArray.get(selInd);
            if (selInd == imageArray.size()-1) {
                next.setEnabled(false);
            }
            drawCheckBoxes();
        } else if (selInd == imageArray.size()-1 && selInd != -1) {
            imageArray.remove(selInd);
            pComArray.remove(selInd);
            next.setEnabled(false);
            selInd--;
            if (selInd == -1) {
                selectedImage = null;
                vacation.setSelected(false);
                family.setSelected(false);
                school.setSelected(false);
                work.setSelected(false);
                delete.setEnabled(false);
            } else {
                selectedImage = imageArray.get(selInd);
                imageArray.get(selInd).isSelected = true;
                drawCheckBoxes();
            }
        }
        if (viewMode == 0) {
            redrawScrollViewer(scrollArea);
        } else if (viewMode == 1) {
            redrawViewer(scrollArea);
        }
    }

    /**
     * Calls import method and redraws
     */
    public void importHelper() {
        textLabel.setText("Import selected");
        imagePath.setText(getFiles());
        if (viewMode == 0) {
            redrawScrollViewer(scrollArea);
        } else if (viewMode == 1) {
            redrawViewer(scrollArea);
        }
    }

    /**
     * Changes viewing mode between photo and grid view
     * by swapping the panel in the scrollpane
     * @param mode
     */
    public void viewSwap(int mode) {
        //0 = photo view, 1 = grid view
        if (mode == 0) {
            //scrollArea.getViewport().remove(viewArea);
            scrollArea.getViewport().add(pComArray.get(selInd));
            viewMode = 0;
            zoomIn.setEnabled(true);
            zoomOut.setEnabled(true);
            photoView.setSelected(true);
            gridView.setSelected(false);
            magnetGroup.clearSelection();
            redrawScrollViewer(scrollArea);
            //System.out.println("swapping to photo");
        } else if (mode == 1) {
            scrollArea.getViewport().remove(pComArray.get(selInd));
            //scrollArea.getViewport().add(viewArea);
            viewMode = 1;
            zoomIn.setEnabled(false);
            zoomOut.setEnabled(false);
            photoView.setSelected(false);
            gridView.setSelected(true);
            magnetGroup.clearSelection();
            redrawViewer(scrollArea);
            //System.out.println("swapping to grid");
        }
    }

    public void checkIncBut() {
        if (selInd == -1) {
            next.setEnabled(false);
            previous.setEnabled(false);
            delete.setEnabled(false);
        } else if (selInd == 0) {
            next.setEnabled(true);
            previous.setEnabled(false);
            delete.setEnabled(true);
        } else if (selInd < imageArray.size() - 1) {
            next.setEnabled(true);
            previous.setEnabled(true);
            delete.setEnabled(true);
        } else {
            next.setEnabled(false);
            previous.setEnabled(true);
            delete.setEnabled(true);
        }
        drawCheckBoxes();
        //redrawViewer(scrollArea);
    }

    /**
     * sets the scaling factor based on the current level
     * of zoom
     * @param direction
     */
    public void zoomViewer(int direction) {
        if (direction == 1) {
            textLabel.setText("Zoom In selected");
            if (zScale < 20) {
                zScale = (zScale * 1.2);
            }
        } else if (direction == -1) {
            textLabel.setText("Zoom Out selected");
            if (zScale > 0.1) {
                zScale = (zScale / 1.2);
            }
        }
        //.println(zScale + "++++++");
        redrawScrollViewer(scrollArea);
    }

    /**
     * Resizes an image to the specified dimensions
     * @param srcImg
     * @param inWidth
     * @param inHeight
     * @return
     */
    private Image getScaledImage(Image srcImg, int inWidth, int inHeight){
        BufferedImage resizedImg = new BufferedImage(inWidth, inHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D rendImg = resizedImg.createGraphics();

        rendImg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        rendImg.drawImage(srcImg, 0, 0, inWidth, inHeight, null);
        rendImg.dispose();

        return resizedImg;
    }

    private void changeComMode(boolean pen, boolean txt) {
        PhotoComponent comp = pComArray.get(selInd);
        if (!pen && !txt) {
            comp.penMode = false;
            comp.textMode = false;
        } else if (pen) {
            comp.penMode = true;
            comp.textMode = false;
        } else if (txt) {
            comp.penMode = false;
            comp.textMode = true;
        }
    }

    private void checkTag(int i) {
        PhotoComponent comp = pComArray.get(selInd);
        if (i == 1) {
            comp.tag1 = vacation.isSelected();
            System.out.println("tag " + comp.tag1);
        } else if (i == 2) {
            comp.tag2 = family.isSelected();
        } else if (i == 3) {
            comp.tag3 = school.isSelected();
        } else if (i == 4) {
            comp.tag4 = work.isSelected();
        }
    }

    public void flipTag(int i) {
        PhotoComponent comp = pComArray.get(selInd);
        if (i == 1) {
            comp.tag1 = !comp.tag1;
        } else if (i == 2) {
            comp.tag2 = !comp.tag2;
        } else if (i == 3) {
            comp.tag3 = !comp.tag3;
        } else if (i == 4) {
            comp.tag4 = !comp.tag4;
        }
        drawCheckBoxes();
    }

    public void drawCheckBoxes() {
        PhotoComponent comp = pComArray.get(selInd);
        vacation.setSelected(comp.tag1);
        family.setSelected(comp.tag2);
        school.setSelected(comp.tag3);
        work.setSelected(comp.tag4);
        viewArea.revalidate();
        viewArea.repaint();
        frame.revalidate();
        frame.repaint();
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        if (gridView.isSelected()) {
            redrawViewer(scrollArea);
        }
    }

    public void componentShown(ComponentEvent e) {

    }
}
