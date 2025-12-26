package org.gui;

import org.model.Edge;
import org.model.Graph;
import org.model.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {
    private Graph graph;
    private boolean showResidual;
    private List<Edge> highlightEdges;

    public GraphPanel() {
        this.highlightEdges = new ArrayList<>();
        this.showResidual = false;
        setBackground(Color.WHITE);
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        repaint();
    }

    public void setShowResidual(boolean showResidual) {
        this.showResidual = showResidual;
        repaint();
    }

    public void setHighlightEdges(List<Edge> highlightEdges) {
        this.highlightEdges = highlightEdges;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (showResidual) {
            drawResidualEdges(g2);
        } else {
            drawNormalEdges(g2);
        }

        drawNodes(g2);
    }

    private void drawNodes(Graphics2D g2) {
        for (Node node : graph.getNodes()) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillOval(node.getX() - 15, node.getY() - 15, 30, 30);
            g2.setColor(Color.BLACK);
            g2.drawOval(node.getX() - 15, node.getY() - 15, 30, 30);
            
            String label = node.getId();
            FontMetrics fm = g2.getFontMetrics();
            int txtWidth = fm.stringWidth(label);
            g2.drawString(label, node.getX() - txtWidth / 2, node.getY() + 5);
        }
    }

    private void drawNormalEdges(Graphics2D g2) {
        for (Edge edge : graph.getEdges()) {
            boolean isHighlighted = false;
            if (highlightEdges != null) {
                for (Edge he : highlightEdges) {
                    if (he.getSource().getId().equals(edge.getSource().getId()) &&
                        he.getDestination().getId().equals(edge.getDestination().getId())) {
                        isHighlighted = true;
                        break;
                    }
                }
            }

            if (isHighlighted) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3));
            } else {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1));
            }

            drawArrow(g2, edge.getSource(), edge.getDestination(), edge.getFlow() + "/" + edge.getCapacity());
        }
    }

    private void drawResidualEdges(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f));

        for (Edge edge : graph.getEdges()) {
            int remCap = edge.getCapacity() - edge.getFlow();
            if (remCap > 0) {
                drawArrow(g2, edge.getSource(), edge.getDestination(), String.valueOf(remCap));
            }

            int flow = edge.getFlow();
            if (flow > 0) {
                g2.setColor(Color.MAGENTA);
                drawArrow(g2, edge.getDestination(), edge.getSource(), String.valueOf(flow));
                g2.setColor(Color.BLUE);
            }
        }
        g2.setStroke(new BasicStroke(1));
    }

    private void drawArrow(Graphics2D g2, Node u, Node v, String label) {
        int x1 = u.getX();
        int y1 = u.getY();
        int x2 = v.getX();
        int y2 = v.getY();

        double angle = Math.atan2(y2 - y1, x2 - x1);
        int r = 15;
        int startX = (int) (x1 + r * Math.cos(angle));
        int startY = (int) (y1 + r * Math.sin(angle));
        int endX = (int) (x2 - r * Math.cos(angle));
        int endY = (int) (y2 - r * Math.sin(angle));

        g2.drawLine(startX, startY, endX, endY);

        int arrowSize = 8;
        
        AffineTransform tx = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, endX, endY);
        g2.transform(rotate);
        g2.fillPolygon(new int[]{endX, endX - arrowSize, endX - arrowSize},
                       new int[]{endY, endY - arrowSize / 2, endY + arrowSize / 2}, 3);
        g2.setTransform(tx);

        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;
        
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(label, midX + 5, midY - 5);
    }
}
