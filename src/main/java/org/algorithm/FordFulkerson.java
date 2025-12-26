package org.algorithm;

import org.model.Edge;
import org.model.Graph;
import org.model.Node;

import java.util.*;

public class FordFulkerson {

    public static class Result {
        public int maxFlow;
        public List<Graph> steps;
        public List<Edge> minCutEdges;
        public Graph finalGraph;

        public Result() {
            this.steps = new ArrayList<>();
            this.minCutEdges = new ArrayList<>();
        }
    }

    public static Result run(Graph inputGraph, String sourceId, String sinkId) {
        Result result = new Result();
        
        Graph graph = copyGraph(inputGraph);
        result.steps.add(copyGraph(graph));

        Node source = graph.getNodeById(sourceId);
        Node sink = graph.getNodeById(sinkId);

        if (source == null || sink == null) {
            throw new IllegalArgumentException("Source or Sink not found");
        }

        int maxFlow = 0;

        while (true) {
            Map<Node, Node> parentMap = new HashMap<>();
            Map<Node, Edge> edgeMap = new HashMap<>();
            Map<Node, Boolean> isForwardMap = new HashMap<>();

            if (!bfs(graph, source, sink, parentMap, edgeMap, isForwardMap)) {
                break;
            }

            int pathFlow = Integer.MAX_VALUE;
            Node curr = sink;
            while (curr != source) {
                Node prev = parentMap.get(curr);
                Edge edge = edgeMap.get(curr);
                boolean isForward = isForwardMap.get(curr);

                int residualCapacity;
                if (isForward) {
                    residualCapacity = edge.getCapacity() - edge.getFlow();
                } else {
                    residualCapacity = edge.getFlow();
                }
                pathFlow = Math.min(pathFlow, residualCapacity);
                curr = prev;
            }

            curr = sink;
            while (curr != source) {
                Node prev = parentMap.get(curr);
                Edge edge = edgeMap.get(curr);
                boolean isForward = isForwardMap.get(curr);

                if (isForward) {
                    edge.setFlow(edge.getFlow() + pathFlow);
                } else {
                    edge.setFlow(edge.getFlow() - pathFlow);
                }
                curr = prev;
            }

            maxFlow += pathFlow;
            result.steps.add(copyGraph(graph));
        }

        result.maxFlow = maxFlow;
        result.finalGraph = graph;
        result.minCutEdges = findMinCut(graph, source, inputGraph);

        return result;
    }

    private static boolean bfs(Graph graph, Node source, Node sink,
                               Map<Node, Node> parentMap,
                               Map<Node, Edge> edgeMap,
                               Map<Node, Boolean> isForwardMap) {
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        queue.add(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            Node u = queue.poll();

            if (u == sink) return true;

            for (Edge edge : graph.getIncidentEdges(u.getId())) {
                if (edge.getSource() == u) {
                    Node v = edge.getDestination();
                    if (!visited.contains(v) && edge.getCapacity() > edge.getFlow()) {
                        visited.add(v);
                        parentMap.put(v, u);
                        edgeMap.put(v, edge);
                        isForwardMap.put(v, true);
                        queue.add(v);
                    }
                } else if (edge.getDestination() == u) {
                    Node v = edge.getSource();
                    if (!visited.contains(v) && edge.getFlow() > 0) {
                        visited.add(v);
                        parentMap.put(v, u);
                        edgeMap.put(v, edge);
                        isForwardMap.put(v, false);
                        queue.add(v);
                    }
                }
            }
        }
        return false;
    }

    private static List<Edge> findMinCut(Graph finalGraph, Node source, Graph originalGraph) {
        Set<Node> reachable = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(source);
        reachable.add(source);

        while (!queue.isEmpty()) {
            Node u = queue.poll();
            for (Edge edge : finalGraph.getIncidentEdges(u.getId())) {
                if (edge.getSource() == u) {
                    Node v = edge.getDestination();
                    if (!reachable.contains(v) && edge.getCapacity() > edge.getFlow()) {
                        reachable.add(v);
                        queue.add(v);
                    }
                } else if (edge.getDestination() == u) {
                     Node v = edge.getSource();
                     if (!reachable.contains(v) && edge.getFlow() > 0) {
                         reachable.add(v);
                         queue.add(v);
                     }
                }
            }
        }

        List<Edge> minCut = new ArrayList<>();
        
        for (Edge e : originalGraph.getEdges()) {
             boolean uReachable = reachable.stream().anyMatch(n -> n.getId().equals(e.getSource().getId()));
             boolean vReachable = reachable.stream().anyMatch(n -> n.getId().equals(e.getDestination().getId()));

             if (uReachable && !vReachable) {
                 minCut.add(e);
             }
        }
        return minCut;
    }

    private static Graph copyGraph(Graph original) {
        Graph copy = new Graph();
        Map<String, Node> nodeMap = new HashMap<>();

        for (Node n : original.getNodes()) {
            Node newNode = new Node(n.getId(), n.getX(), n.getY());
            copy.addNode(newNode);
            nodeMap.put(newNode.getId(), newNode);
        }

        for (Edge e : original.getEdges()) {
            Node src = nodeMap.get(e.getSource().getId());
            Node dst = nodeMap.get(e.getDestination().getId());
            Edge newEdge = new Edge(src, dst, e.getCapacity());
            newEdge.setFlow(e.getFlow());
            copy.addEdge(newEdge);
        }
        return copy;
    }
}
