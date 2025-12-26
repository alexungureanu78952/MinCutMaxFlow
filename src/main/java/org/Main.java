package org;

import org.algorithm.FordFulkerson;
import org.gui.GraphPanel;
import org.model.Edge;
import org.model.Graph;
import org.model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {
    private static int currentStep = 0;
    private static boolean showResidual = false;
    private static FordFulkerson.Result result;
    private static GraphPanel graphPanel;
    private static JLabel infoLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Graph graph = createSampleGraph();
            try {
                result = FordFulkerson.run(graph, "S", "T");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            JFrame frame = new JFrame("Ford-Fulkerson Max Flow & Min Cut");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());

            graphPanel = new GraphPanel();
            frame.add(graphPanel, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel();
            JButton prevBtn = new JButton("Previous Step");
            JButton nextBtn = new JButton("Next Step");
            JToggleButton residualToggle = new JToggleButton("Show Residual Graph");
            infoLabel = new JLabel("Start");

            prevBtn.addActionListener(e -> {
                if (currentStep > 0) {
                    currentStep--;
                    updateView();
                }
            });

            nextBtn.addActionListener(e -> {
                if (currentStep < result.steps.size() - 1) {
                    currentStep++;
                    updateView();
                }
            });

            residualToggle.addActionListener(e -> {
                showResidual = residualToggle.isSelected();
                graphPanel.setShowResidual(showResidual);
            });

            controlPanel.add(prevBtn);
            controlPanel.add(nextBtn);
            controlPanel.add(residualToggle);
            controlPanel.add(infoLabel);

            frame.add(controlPanel, BorderLayout.SOUTH);

            updateView();

            frame.setVisible(true);
        });
    }

    private static void updateView() {
        Graph currentGraph = result.steps.get(currentStep);
        graphPanel.setGraph(currentGraph);
        
        if (currentStep == result.steps.size() - 1) {
            graphPanel.setHighlightEdges(result.minCutEdges);
            infoLabel.setText("Final Step. Max Flow: " + result.maxFlow + ". Min-Cut Edges Highlighted.");
        } else {
            graphPanel.setHighlightEdges(null);
            infoLabel.setText("Step " + (currentStep + 1) + " / " + result.steps.size());
        }
    }

    private static Graph createSampleGraph() {
        Graph g = new Graph();
        Node s = new Node("S", 50, 250);
        Node a = new Node("A", 200, 100);
        Node b = new Node("B", 200, 400);
        Node c = new Node("C", 400, 100);
        Node d = new Node("D", 400, 400);
        Node t = new Node("T", 600, 250);

        g.addNode(s); g.addNode(a); g.addNode(b);
        g.addNode(c); g.addNode(d); g.addNode(t);

        g.addEdge(new Edge(s, a, 10));
        g.addEdge(new Edge(s, b, 10));
        g.addEdge(new Edge(a, b, 2));
        g.addEdge(new Edge(a, c, 4));
        g.addEdge(new Edge(a, d, 8));
        g.addEdge(new Edge(b, d, 9));
        g.addEdge(new Edge(c, t, 10));
        g.addEdge(new Edge(d, t, 10));
        g.addEdge(new Edge(d, c, 6));

        return g;
    }
}