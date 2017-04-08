/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IsometricGame.world;

import IsometricGame.GameParameters;
import IsometricGame.entities.VehicleControl;
import IsometricGame.util.GraphNode;
import IsometricGame.util.GridConverter;
import IsometricGame.util.GridPos;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Iain
 */
public class WorldGrid extends AbstractAppState  {
    
    private Geometry[][] grid;
    private SimpleApplication app;
    private int X_SIZE, Z_SIZE;
    
    private VehicleControl testVehicle;
    private Node vehicles;

    private static Material green = null;
    private static final Box car = new Box( 0.2f, 0.1f, 0.4f);
    
    private ArrayList<Geometry> cars = new ArrayList<Geometry>();
 
   @Override
    public void initialize(AppStateManager stateManager, Application app) {
      super.initialize(stateManager, app); 
      this.app = (SimpleApplication)app;   
      X_SIZE = GameParameters.X_SIZE;
      Z_SIZE = GameParameters.Z_SIZE;
      // draw floor
      grid  = new Geometry[X_SIZE][Z_SIZE];
      for(int i=0;i<X_SIZE;i++)
          for(int j=0;j<Z_SIZE;j++)
              grid[i][j] = null;
      Vector3f p = new Vector3f(0,0,0);
      Box floor = new Box(p, X_SIZE/2,0.1f,Z_SIZE/2);
      Geometry g = new Geometry("Floor", floor);
      g.setLocalTranslation(p);
        green = new Material(app.getAssetManager(), 
                                      "Common/MatDefs/Light/Lighting.j3md");

        green.setBoolean("UseMaterialColors", true);
        green.setColor("Ambient",  ColorRGBA.Green);
        green.setColor("Diffuse",  ColorRGBA.Green);
        green.setColor("Specular", ColorRGBA.Green); 
      
      
      Material grey = new Material(app.getAssetManager(),
                            "Common/MatDefs/Misc/Unshaded.j3md");
      grey.setColor("Color", ColorRGBA.Gray);
      g.setMaterial(grey);
      this.app.getRootNode().attachChild(g);
      
      testVehicle = null;
      //buildRoads();
      //placeBuildings();
   }
    
    @Override
    public void update(float tpf) {                              // call some methods...
        if(testVehicle == null)
        {
            GridPos start = Roads.getInstance().getStartPositions().get(0);
            GraphNode curr = Roads.getInstance().getRoadGraph().nodeAt(start.x,start.y);
            
            ArrayList<GridPos> myPlan = new ArrayList<GridPos>();
            for(int i =0; i < 30;i++)
            {
                myPlan.add(new GridPos(curr.getX(), curr.getY()));
                if(curr.getNeighbours().size() == 1)
                    curr = curr.getNeighbours().get(0).getTo();
                else
                    curr = curr.getNeighbours().get(1).getTo();
            }
            
            
              Vector3f position = GridConverter.gridToPosition(start);
              position.setY(0.5f);
              position.x+=0.2; // make it move to the lane?
              
              Geometry g = new Geometry("Vehicle",car);
              g.setLocalTranslation(position);
              g.setMaterial(green);
              g.addControl(new VehicleControl(myPlan));
              vehicles = new Node("Vehicles");
              vehicles.attachChild(g);
              app.getRootNode().attachChild(vehicles);
              testVehicle = g.getControl(VehicleControl.class);              
        }
        if(testVehicle.atFinalDestination())
        {
            
            Geometry geom = (Geometry)testVehicle.getSpatial();
            vehicles.detachChild(geom);
            geom.removeControl(testVehicle);
            testVehicle=null;            
        }
    }

    public Node buildRoads() {
      Roads.getInstance().buildRoads();  
      Node roads = Roads.getInstance().generateGeometry(this.app); //$new Node("Roads");
      return roads;
      //this.app.getRootNode().attachChild(roads);
//      Material d_grey = new Material(app.getAssetManager(), 
//                            "Common/MatDefs/Misc/Unshaded.j3md");
//                             // "Common/MatDefs/Light/Lighting.j3md");
//      //d_grey.setColor("Specular", ColorRGBA.Black); 
//      //d_grey.setColor("Diffuse", ColorRGBA.Black); 
//      //d_grey.setColor("Ambient", ColorRGBA.Black); 
//      d_grey.setColor("Color",ColorRGBA.DarkGray);
//      
//      Random randomGenerator = new Random();
//      
//     
//      // X roads
//      int no_x_roads = randomGenerator.nextInt(5) +5;
//      for(int i = 0; i< no_x_roads;i++)
//      {
//          int x_coord = randomGenerator.nextInt(10) + (i * (X_SIZE / no_x_roads));
//          for(int j = 0; j< X_SIZE; j++)
//          {
//              float x  = x_coord+0.5f - 50f;
//              float z = j + 0.5f - 50.0f;
//              Vector3f position = new Vector3f(x,0.3f, z);
//              Box road = new Box(position, 0.5f, 0.1f, 0.5f);
//              Geometry g = new Geometry("Road Section",road);
//              g.setMaterial(d_grey);
//              roads.attachChild(g);
//              grid[x_coord][j] = g;
//          }
//      }
//      
//      // Y roads
//      int no_z_roads = randomGenerator.nextInt(5) + 5;
//      for(int i = 0; i< no_z_roads;i++)
//      {
//          int z_coord = randomGenerator.nextInt(10) + (i * (Z_SIZE / no_z_roads));
//          for(int j = 0; j< Z_SIZE; j++)
//          {
//              if( grid[j][z_coord] == null)
//              {
//                float z  = z_coord+0.5f - 50f;
//                float x = j + 0.5f - 50.0f;
//                Vector3f position = new Vector3f(x,0.3f, z);
//                Box road = new Box(position, 0.5f, 0.1f, 0.5f);
//                Geometry g = new Geometry("Road Section",road);
//                g.setMaterial(d_grey);
//                roads.attachChild(g);
//                grid[j][z_coord] = g;
//              }
//          }
//      }
//      
//      return roads;
    }

    public Node placeBuildings() {
        Node buildings = new Node("Buildings");
        //this.app.getRootNode().attachChild(buildings);
        Material d_grey = new Material(app.getAssetManager(),
                             "Common/MatDefs/Light/Lighting.j3md");
      d_grey.setColor("Specular", ColorRGBA.LightGray); 
      d_grey.setColor("Diffuse", ColorRGBA.LightGray); 
      d_grey.setColor("Ambient", ColorRGBA.White); 
      
      
        Random randomGenerator = new Random();
        for(int i =0; i< X_SIZE; i++ )
            for(int j = 0; j < Z_SIZE; j++)
            {
              if( randomGenerator.nextInt(3) == 1)
                  continue;
                if(grid[i][j] == null) // empty add building
                {
                    float height= randomGenerator.nextFloat()+0.5f;
                    float width = (randomGenerator.nextFloat() /4f) +0.25f;
                    float depth = (randomGenerator.nextFloat() /4f) +0.25f;
                    
                    float z  = j - 50.0f + 0.5f;
                    float x = i - 50.0f + 0.5f;
                    float y = height / 2f + 0.4f;
                    Vector3f position = new Vector3f(x,y, z);
                    Box building = new Box(position, width, height, depth);
                    Geometry g = new Geometry("Building",building);
                    g.setMaterial(d_grey);
                    buildings.attachChild(g);
                    grid[i][j] = g;
                }
            }
        return buildings;
    }
}
