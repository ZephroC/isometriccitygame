/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IsometricGame.util;

import IsometricGame.GameParameters;
import com.jme3.math.Vector3f;

/**
 *
 * @author Iain
 */
public class GridConverter {
    public static Vector3f gridToPosition(GridPos pos) {        
        // round down?
        float z = pos.y - (float)(GameParameters.Z_SIZE/2);
        float x = pos.x - (float)(GameParameters.X_SIZE/2);
        x+= (GameParameters.square_size/2); // convert to be the middle of the square
        z+= (GameParameters.square_size/2); // convert to be the middle of the square
        return new Vector3f(x,0,z);
    }

    public static GridPos positionToGrid(Vector3f f) {        
        // round down
        int x = (int) (f.x + (GameParameters.X_SIZE/2));
        int y = (int) (f.z + (GameParameters.Z_SIZE/2));
        return new GridPos(x,y);
    }
}

