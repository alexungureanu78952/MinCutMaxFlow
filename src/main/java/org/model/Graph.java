package org.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;
    private Map<String, List<Edge>> incidentEdges;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.incidentEdges = new HashMap<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
        
        incidentEdges.computeIfAbsent(edge.getSource().getId(), k -> new ArrayList<>()).add(edge);
        incidentEdges.computeIfAbsent(edge.getDestination().getId(), k -> new ArrayList<>()).add(edge);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Edge> getIncidentEdges(String nodeId) {
        return incidentEdges.getOrDefault(nodeId, new ArrayList<>());
    }

    public Node getNodeById(String id) {
        for (Node node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    public void resetFlow() {
        for (Edge edge : edges) {
            edge.setFlow(0);
        }
    }
}