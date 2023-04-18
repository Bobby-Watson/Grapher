package grapher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Graph extends JPanel {

    float zoom = 0;
    double xOffset = 0;
    double yOffset = 0;

    int screenWidth = 1000;
    int screenHeight = 500;
    int pixelSize = 5;

    HashMap<UnaryOperator<Double>, Color> functions = new HashMap<>();

    public Graph() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);

        functions.put((x) -> 100 * Math.sin(Math.toRadians(x)), Color.red);
        functions.put((x) -> 50 * Math.sin(Math.toRadians(x)), Color.cyan);
        functions.put((x) -> x, Color.orange);
        functions.put((x) -> 100 % x, Color.magenta);
        functions.put((x) -> Math.abs(Math.sqrt(x)), Color.blue);
    }

    public void init() {
        KeyHandler keyHandler = new KeyHandler();
        SwingUtilities.windowForComponent(this).addKeyListener(keyHandler);

        MouseHandler mouseHandler = new MouseHandler();
        SwingUtilities.windowForComponent(this).addMouseListener(mouseHandler);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.fillRect(0, (int) (Math.exp(-zoom) * yOffset) + screenHeight / 2, screenWidth, 2);
        g.fillRect((int) (Math.exp(-zoom) * xOffset) + screenWidth / 2, 0, 2, screenHeight);

        /*
        Draws each function. For each function, loops over each "pixel" on the screen and finds its
        corresponding y-value, then draws the "pixel" to the screen
         */
        BiConsumer<Method, Color> bc = (Method m, Color c) -> {
            for (int x = 0; x < screenWidth / pixelSize; x++) {
                g.setColor(c);

                g.fillRect(getXValue(x), (int) (Math.exp(-zoom) * getValue(m, getXValue(x) - screenWidth / 2)) + screenHeight / 2, pixelSize, pixelSize);
            }
        };
        FunctionManager.forEach(bc);
    }

    private int getXValue(int x) {
        return x * pixelSize;
    }

    private double getValue(Method m, double x) {
        try {
            return yOffset - (double) m.invoke(null, Math.exp(zoom) * x - xOffset);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    class KeyHandler implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP ->
                    yOffset -= 1;
                case KeyEvent.VK_DOWN ->
                    yOffset += 1;
                case KeyEvent.VK_LEFT ->
                    xOffset -= 1;
                case KeyEvent.VK_RIGHT ->
                    xOffset += 1;
                case KeyEvent.VK_EQUALS ->
                    zoom += 0.1;
                case KeyEvent.VK_MINUS ->
                    zoom -= 0.1;
            }
            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

    }

    class MouseHandler implements MouseListener {

        int initialMouseX;
        int initialMouseY;
        double initialOffsetX;
        double initialOffsetY;

        boolean stopThread = false;
        Thread t;
        Runnable r = () -> {
            while (!stopThread) {
                int currentX = java.awt.MouseInfo.getPointerInfo().getLocation().x;
                int currentY = java.awt.MouseInfo.getPointerInfo().getLocation().y;

                //This black magic updates the offset according to how much the
                //cursor has moved since the click started
                xOffset = initialOffsetX + (currentX - initialMouseX) * Math.exp(zoom);
                yOffset = initialOffsetY + (currentY - initialMouseY) * Math.exp(zoom);

                repaint();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        };

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            initialOffsetX = xOffset;
            initialOffsetY = yOffset;
            initialMouseX = e.getXOnScreen();
            initialMouseY = e.getYOnScreen();
            stopThread = false;
            t = new Thread(r);
            t.start();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            stopThread = true;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

    }
}
