/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IsometricGame.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Iain
 */
public class SparseGraph {
    private List<GraphNode> nodes;
    private List<GraphEdge> edges;
    
    public SparseGraph() {
        nodes = new ArrayList<GraphNode>();
        edges = new ArrayList<GraphEdge>();
    }
    
    public List<GraphNode> nodes() {
        return nodes;
    }
    
    public void addNode(GraphNode node) {
        nodes.add(node);
    }
    
    public void addEdge(GraphEdge edge) {
        edges.add(edge);
    }
    
    /*
     * can return null!
     */
    public GraphNode nodeAt(int x, int y) {
        for(GraphNode n: nodes)
            if(n.getX() == x && n.getY() == y)
                return n;
        
        return null;
    }
}
