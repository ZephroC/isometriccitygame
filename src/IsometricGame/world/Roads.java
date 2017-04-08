/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IsometricGame.world;

import IsometricGame.GameParameters;
import IsometricGame.util.GraphEdge;
import IsometricGame.util.GraphNode;
import IsometricGame.util.GridPos;
import IsometricGame.util.SparseGraph;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * This class should both generate the road network and represent it
 * during execution. Generation relies on creating the actual navigation graph
 * and also producing the geometry with a minimum of polys.
 * 
 * @author Iain
 */
public class Roads {
    private static final Roads s_instance = new Roads();
    
    private SparseGraph navGraph;
    private List<GridPos> startingPoints;
    
    private Roads() {
        navGraph = new SparseGraph();
        startingPoints = new ArrayList<GridPos>();
    }
    
    public SparseGraph getRoadGraph() {
        return navGraph;
    }
    
    public static Roads getInstance() {
        return s_instance;
    }
    
    public List<GridPos> getStartPositions() {
        return startingPoints;
    }
    /**
     * Generates the road network for usage.
     */
    public void buildRoads() {
        Random randomGenerator = new Random(System.currentTimeMillis());
        
      
        
      int no_x_roads = randomGenerator.nextInt(GameParameters.max_x_roads 
                        - GameParameters.min_x_roads) +GameParameters.min_x_roads;
      
      int no_z_roads = randomGenerator.nextInt(GameParameters.max_z_roads 
                        - GameParameters.min_z_roads) + GameParameters.min_z_roads;
      int avg_x_interval = GameParameters.X_SIZE / no_x_roads;
      int avg_z_interval = GameParameters.Z_SIZE / no_z_roads;
      
      int[] x_places = new int[no_x_roads];
      int[] z_places = new int[no_z_roads];
      
      for(int i = 0; i< no_x_roads;i++)
      {
          int x_coord = randomGenerator.nextInt(avg_x_interval) + (i * avg_x_interval);
          x_places[i]=x_coord;
          startingPoints.add(new GridPos(x_coord,0));
          startingPoints.add(new GridPos(x_coord,GameParameters.X_SIZE));
      }
      
      // Y roads
      
      for(int i = 0; i< no_z_roads;i++)
      {
          int z_coord = randomGenerator.nextInt(avg_z_interval) + (i * avg_z_interval);
          z_places[i]=z_coord;    
          
          startingPoints.add(new GridPos(z_coord,0));
          startingPoints.add(new GridPos(z_coord,GameParameters.X_SIZE));
      }
      
      GraphNode here = null;
      GraphNode previous = null;
      // for each x road add Graph Nodes and edges
      for(int i: x_places)
      {
          for(int j=0; j< GameParameters.X_SIZE; j++)
          {
              previous = here;
              here = new GraphNode(i,j);
              navGraph.addNode(here);
              if(previous!=null)
              {
                  GraphEdge e = new GraphEdge(previous, here);
                  GraphEdge r = new GraphEdge(here,previous);
                  navGraph.addEdge(e);
                  navGraph.addEdge(r);
                  here.addEdge(r);
                  previous.addEdge(e);
              }
          }
      }
      previous = null;
      here = null;
      // for each z road add graph nodes and edges, linking to previous ones
      for(int i: z_places)
      {
          for(int j=0; j< GameParameters.Z_SIZE; j++)
          {
              previous = here;
              here = navGraph.nodeAt(j, i);
              if(here == null) //not an intersection
              {
                here = new GraphNode(j,i);
                navGraph.addNode(here);
              }
              if(previous!=null)
              {
                  GraphEdge e = new GraphEdge(previous, here);
                  GraphEdge r = new GraphEdge(here,previous);
                  navGraph.addEdge(e);
                  navGraph.addEdge(r);
                  here.addEdge(r);
                  previous.addEdge(e);
              }
          }
      }
    }
    
    public Node generateGeometry(SimpleApplication app) {
        
             Node roads = new Node("Roads");
      //this.app.getRootNode().attachChild(roads);
      Material d_grey = new Material(app.getAssetManager(), 
                            "Common/MatDefs/Misc/Unshaded.j3md");
                             // "Common/MatDefs/Light/Lighting.j3md");
      //d_grey.setColor("Specular", ColorRGBA.Black); 
      //d_grey.setColor("Diffuse", ColorRGBA.Black); 
      //d_grey.setColor("Ambient", ColorRGBA.Black); 
      d_grey.setColor("Color",ColorRGBA.DarkGray);
      
      for(GraphNode node: navGraph.nodes())
      {
        float x  = node.getX()+0.5f - 50f;
        float z = node.getY() + 0.5f - 50.0f;
        Vector3f position = new Vector3f(x,0.3f, z);
        Box road = new Box(position, 0.5f, 0.1f, 0.5f);
        Geometry g = new Geometry("Road Section",road);
        g.setMaterial(d_grey);
        roads.attachChild(g);
      }
      return roads;
//      Random randomGenerator = new Random(System.currentTimeMillis());
//      
//     
//      // X roads
//      int no_x_roads = randomGenerator.nextInt(5) +5;
//      for(int i = 0; i< no_x_roads;i++)
//      {
//          int x_coord = randomGenerator.nextInt(10) + (i * (GameParameters.X_SIZE / no_x_roads));
//          for(int j = 0; j< GameParameters.X_SIZE; j++)
//          {
//              float x  = x_coord+0.5f - 50f;
//              float z = j + 0.5f - 50.0f;
//              Vector3f position = new Vector3f(x,0.3f, z);
//              Box road = new Box(position, 0.5f, 0.1f, 0.5f);
//              Geometry g = new Geometry("Road Section",road);
//              g.setMaterial(d_grey);
//              roads.attachChild(g);
//              //grid[x_coord][j] = g;
//          }
//      }
//      
//      // Y roads
//      int no_z_roads = randomGenerator.nextInt(5) + 5;
//      for(int i = 0; i< no_z_roads;i++)
//      {
//          int z_coord = randomGenerator.nextInt(10) + (i * (GameParameters.Z_SIZE / no_z_roads));
//          for(int j = 0; j< GameParameters.Z_SIZE; j++)
//          {
//             // if( grid[j][z_coord] == null)
//              //{
//                float z  = z_coord+0.5f - 50f;
//                float x = j + 0.5f - 50.0f;
//                Vector3f position = new Vector3f(x,0.3f, z);
//                Box road = new Box(position, 0.5f, 0.1f, 0.5f);
//                Geometry g = new Geometry("Road Section",road);
//                g.setMaterial(d_grey);
//                roads.attachChild(g);
//               // grid[j][z_coord] = g;
//              //}
//          }
//      }
//      
//      return roads;
    }
}
