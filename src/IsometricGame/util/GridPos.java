/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IsometricGame.util;

/**
 *
 * @author Iain
 */
public class GridPos {
    public int x = 0, y =0;
    
    // copy constructor
    public GridPos(GridPos p) {
        this(p.x,p.y);
    }
    
    public GridPos() {
        this(0,0);
    }
    
    public GridPos(int x, int y) {
        this.x=x;
        this.y=y;
    }
}
