/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IsometricGame.entities;

import IsometricGame.util.GridConverter;
import IsometricGame.util.GridPos;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.List;

/**
 *
 * @author Iain
 */
public class VehicleControl extends AbstractControl {
    
    private GridPos destination;
    private List<GridPos> plan;
    private Vector3f facing;
    private float velocity = 1000f;
    private boolean atFinalDestination = false;
 
    
    public VehicleControl(List<GridPos> plan) {
        facing = new Vector3f();
        this.plan = plan;
        this.destination = plan.get(0);
        
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(atDestination())
        {
            plan.remove(0);
            if(plan.size() > 0)
                destination = plan.get(0);
        }
        //Not doing anything here right now
        steerToNextDestination(tpf);
        seekToNextDestination(tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Don't do anything here for now
    }

    public Control cloneForSpatial(Spatial spatial) {
        final VehicleControl clone = new VehicleControl(plan);
        clone.setSpatial(spatial);
        return clone;
    }
    
    private void steerToNextDestination(float tpf) {
        
    }
    
    private void seekToNextDestination(float tpf) {
        Spatial spat = this.getSpatial();
        Vector3f current = spat.getLocalTranslation();
        Vector3f dest = GridConverter.gridToPosition(destination);
        dest.x+=0.2f;
        dest.y+=0.5f;
        
        Vector3f direction = dest.subtract(current);
        direction.normalizeLocal();
        direction.multLocal(((float)tpf/ 1000f)*(float)velocity);
        //current.add(direction);
        spat.move(direction);
    }
    
    private boolean atDestination() {
        Spatial spat = this.getSpatial();
        Vector3f current = spat.getLocalTranslation();
        GridPos currPos = GridConverter.positionToGrid(current);
        
        if(currPos.x == destination.x && currPos.y == destination.y)
            return true;
        else 
            return false;
    }
    
    public boolean atFinalDestination() {
        if(atDestination() && plan.isEmpty())
            return true;
        else 
            return false;
    }
}
