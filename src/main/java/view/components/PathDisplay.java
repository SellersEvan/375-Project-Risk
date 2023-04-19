package view.components;

import controller.Game;
import model.Map.Territory;
import model.PlayerColor;
import view.GameView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class PathDisplay extends JComponent {


    private final static int BUTTON_PADDING = 5;

    private Territory territoryFrom;
    private Territory territoryTo;


    public PathDisplay() {
        super();
        this.setBackground(null);
    }


    public void update(Territory territoryFrom, Territory territoryTo) {
        this.territoryFrom = territoryFrom;
        this.territoryTo   = territoryTo;
        this.repaint();
    }


    public void paint(Graphics g) {
        super.paint(g);
        if (this.territoryTo == null || this.territoryFrom == null) return;

        int alignCenterX = GameView.TERRITORY_BUTTON_WIDTH  / 2;
        int alignCenterY = GameView.TERRITORY_BUTTON_HEIGHT / 2;

        int fromX = (int) (territoryFrom.getPosX() * this.getWidth() + alignCenterX);
        int fromY = (int) (territoryFrom.getPosY() * this.getHeight() + alignCenterY);
        int toX = (int) (territoryTo.getPosX() * this.getWidth() + alignCenterX);
        int toY = (int) (territoryTo.getPosY() * this.getHeight() + alignCenterY);

        if (fromX < toX) { // from on left, to on right
            toX -= alignCenterX + BUTTON_PADDING;
        } else {
            toX += alignCenterX + BUTTON_PADDING;
        }

        if (fromY < toY) { // from on top, to on bottom
            toY -= alignCenterY + BUTTON_PADDING;
        } else {
            toY += alignCenterY + BUTTON_PADDING;
        }

        this.drawArrowLine(g, fromX, fromY, toX, toY, 20, 20);
    }


    // https://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
    private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {
        Graphics2D g2d = (Graphics2D) g;
        BasicStroke stroke = new BasicStroke(5);
        g2d.setStroke(stroke);
        g2d.setColor(Color.red);

        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx*dx + dy*dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy / D, cos = dx / D;

        x = xm*cos - ym*sin + x1;
        ym = xm*sin + ym*cos + y1;
        xm = x;

        x = xn*cos - yn*sin + x1;
        yn = xn*sin + yn*cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(xpoints, ypoints, 3);
    }


}
