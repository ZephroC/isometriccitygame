package IsometricGame;

import IsometricGame.ui.StartScreen;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.Nifty;

/**
 * test
 * @author normenhansen
 */
public class TestGame extends SimpleApplication {
  
    private boolean left = false, right = false, up = false, down = false;
    private float cam_speed = 6f;
    private float low_z = -50, high_z = 50, low_x = -50, high_x = 50;
    
    private boolean paused = false;


    private Geometry selected;
    private Geometry previously_selected;
    
    private Material white; 
    private Material prevMat;
    
    private StartScreen startScreen;
    
    
    public TestGame() {
        super();
        
        selected=null;
        previously_selected=null;
        prevMat = null;
    }

    public static void main(String[] args) {
        TestGame app = new TestGame();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        
        white = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        initKeys();
        initCamera();
        //createCheckerBoard();
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(2,-2,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        AmbientLight daylight = new AmbientLight();
        daylight.setColor(new ColorRGBA(0.8f,0.8f,0.8f,1.0f));
        rootNode.addLight(daylight);
        
        startScreen = new StartScreen();
        stateManager.attach(startScreen);

        /**
         * Åctivate the Nifty-JME integration: 
         */
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.fromXml("Interface/Interface.xml", "start", startScreen);

    }

    @Override
    public void simpleUpdate(float tpf) {
        
        float this_speed = cam_speed * tpf;
        // 0.6 is the speed basically
        //Vector3f camDir = cam.getDirection().clone().multLocal(cam_speed);
        Vector3f camLeft = cam.getLeft().clone().multLocal(this_speed);
        Vector3f camPos = cam.getLocation().clone();
        
        Vector3f camFor = new Vector3f(this_speed,0,this_speed);
        
        if (left)  { camPos.addLocal(camLeft); }
        if (right) { camPos.addLocal(camLeft.negate()); }
        if (up)    { camPos.addLocal(camFor); }
        if (down)  { camPos.addLocal(camFor.negate()); }
        
        if(camPos.getX() > high_x)
            camPos.setX(high_x);
        if(camPos.getX() < low_x)
            camPos.setX(low_x);
        if(camPos.getZ() > high_z)
            camPos.setZ(high_z);
        if(camPos.getZ() < low_z)
            camPos.setZ(low_z);
        cam.setLocation(camPos);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void createCheckerBoard() {
        Material red = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        red.setColor("Color", ColorRGBA.Red);
        Material blue = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        blue.setColor("Color", ColorRGBA.Blue);
        
        for(int i=-50;i<50;i++)
        {
            for(int j=-50;j<50;j++)
            {
                Vector3f p = new Vector3f(i,0,j);
                Box b = new Box(p, 0.5f, 0.1f, 0.5f);
                Geometry g = new Geometry("blue cube", b);
            
                if( (i+j)%2 == 0)
                    g.setMaterial(red);
                else
                    g.setMaterial(blue);
                rootNode.attachChild(g);
            }
        }
    }
    
     /** Declaring the "Shoot" action and mapping to its triggers. */
  private void initKeys() {
    inputManager.setCursorVisible(true);
    inputManager.addMapping("Click",new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); 
    inputManager.addMapping("R_Click",new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); 
    inputManager.addListener(actionListener, "Click");
    inputManager.addListener(actionListener, "R_Click");
    
    
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addListener(actionListener, "Left");
    inputManager.addListener(actionListener, "Right");
    inputManager.addListener(actionListener, "Up");
    inputManager.addListener(actionListener, "Down");
  }
  /** Defining the "Shoot" action: Determine what was hit and how to respond. */
  private ActionListener actionListener = new ActionListener() {
 
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Left")) {
        left = keyPressed;
      } else if (name.equals("Right")) {
        right = keyPressed;
      } else if (name.equals("Up")) {
        up = keyPressed;
      } else if (name.equals("Down")) {
        down = keyPressed;
      } 
      if(name.equals("R_Click") && !keyPressed) {
          if(selected != null)
                selected.setMaterial(prevMat);
            previously_selected = null;
            selected = null;
            prevMat = null;
      }
      if (name.equals("Click") && !keyPressed) {
        // 1. Reset results list.
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        rootNode.collideWith(ray, results);

        if (results.size() > 0) {
            

        white.setColor("Color", ColorRGBA.White);
          CollisionResult closest = results.getClosestCollision();
          if(closest.getGeometry().getName() == "Floor")
          { // deselect
            if(selected != null)
                selected.setMaterial(prevMat);
            previously_selected = null;
            selected = null;
            prevMat = null;
          }
          else
          {
            previously_selected = selected;
            if(previously_selected != null)
              previously_selected.setMaterial(prevMat);
            selected = closest.getGeometry();
            prevMat = selected.getMaterial();
            selected.setMaterial(white);
          }
          
        } 
      }
    }
  };

    private void initCamera() {
        int cam_distance = 10;
        stateManager.detach( stateManager.getState(FlyCamAppState.class));
        cam.setLocation(new Vector3f(0,cam_distance,0));
        cam.lookAtDirection(new Vector3f(1,-1,1), new Vector3f(0,1,0));
        // if heigh if 20 and angle 45 then the x and z change should be 20...
        high_x -= cam_distance;
        low_x -= cam_distance;
        high_z -= cam_distance;
        low_z -= cam_distance;
    }
    
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    
}
