/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IsometricGame.util;

/**
 *
 * @author Iain
 */
public class GraphEdge {
    
    public GraphEdge(GraphNode from, GraphNode to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
    
    public GraphEdge(GraphNode from, GraphNode to)
    {
        this(from,to,1);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public GraphNode getFrom() {
        return from;
    }

    public void setFrom(GraphNode from) {
        this.from = from;
    }

    public GraphNode getTo() {
        return to;
    }

    public void setTo(GraphNode to) {
        this.to = to;
    }
    private int weight;
    private GraphNode from;
    private GraphNode to;
}
