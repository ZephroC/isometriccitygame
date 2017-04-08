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
public class GraphNode {
    private GridPos pos;
    private List<GraphEdge> edges;
    
    public GraphNode(GridPos pos) {
        this(pos.x,pos.y);
    }
    
    public GraphNode(int x, int y) {
        pos = new GridPos(x,y);
        edges = new ArrayList<GraphEdge>();
    }
    
    public int getX() {
        return pos.x;
    }
    
    public int getY() {
        return pos.y;
    }
    
    public GridPos getPos() {
        return new GridPos(pos);
    }
    
    public void addEdge(GraphEdge e) {
        edges.add(e);        
    }
    
    public List<GraphEdge> getNeighbours() {
        return edges;
    }
}
