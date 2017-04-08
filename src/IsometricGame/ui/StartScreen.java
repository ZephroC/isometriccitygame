/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IsometricGame.ui;

import IsometricGame.TestGame;
import IsometricGame.TestGame;
import IsometricGame.world.WorldGrid;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.button.ButtonControl;
import de.lessvoid.xml.xpp3.Attributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author Iain
 */
public class StartScreen extends AbstractAppState implements ScreenController {

  private Nifty nifty;
  private TestGame app;
  private Screen screen;
  private boolean load = false;
  private Future loadFuture;
  private Element progressBarElement;
  private TextRenderer textRenderer;
  private WorldGrid myGrid;
  private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);

  private GregorianCalendar myDate;
  /** custom methods */
  public StartScreen() {
    /** You custom constructor, can accept arguments */
    myDate = new GregorianCalendar(2048, GregorianCalendar.JANUARY, 1, 12, 0);
  }

  public void pauseGame() {
      ButtonControl pauseButton;
      pauseButton = nifty.getScreen("hud").findControl("PauseButton",ButtonControl.class);
      this.app.setPaused(!app.isPaused());
      myGrid.setEnabled(!app.isPaused());
      
      if(app.isPaused())
        pauseButton.setText("Play");
      else
          pauseButton.setText("Pause");
  }
  public void showLoadingMenu() {
    load = true;    
    myGrid = new WorldGrid();
    app.getStateManager().attach(myGrid);
    myGrid.setEnabled(false);
    nifty.gotoScreen("loadlevel");  // switch to another screen
  }

  public void quitGame() {
    app.stop();
  }

  public String getPlayerName() {
    return System.getProperty("user.name");
  }

  /** Nifty GUI ScreenControl methods */
  public void bind(Nifty nifty, Screen screen) {
    this.nifty = nifty;
    this.screen = screen;
    progressBarElement = nifty.getScreen("loadlevel").findElementByName("progressbar");
  }
  
  public void bind(Nifty nifty, Screen screen, Element elmnt, Properties prprts, Attributes atrbts) {
        progressBarElement = elmnt.findElementByName("progressbar");
  }

  public void onStartScreen() {
  }

  public void onEndScreen() {
  }

  /** jME3 AppState methods */
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    this.app = (TestGame)app;
  }

  @Override
  public void update(float tpf) {
    if (screen.getScreenId().equals("hud") && !app.isPaused()) {
      myDate.add(Calendar.MILLISECOND, (int)(tpf*1000f));
      SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
      String date = format.format(myDate.getTime());
      Element niftyElement = nifty.getCurrentScreen().findElementByName("timer");
      // Display the time-per-frame -- this field could also display the score etc...
      
      niftyElement.getRenderer(TextRenderer.class).setLineWrapping(true);
      niftyElement.getRenderer(TextRenderer.class).setText(date); 
    }
    if (load) {
        if (loadFuture == null) {
            //if we have not started loading yet, submit the Callable to the executor
            loadFuture = exec.submit(loadingCallable);
        }
        //check if the execution on the other thread is done
        if (loadFuture.isDone()) {
            //these calls have to be done on the update loop thread, 
            //especially attaching the terrain to the rootNode
            //after it is attached, it's managed by the update loop thread 
            // and may not be modified from any other thread anymore!
            nifty.gotoScreen("hud");


            load = false;
        }
    }
  }
  
   Callable<Void> loadingCallable = new Callable<Void>() {
 
        public Void call() {
 
            Element element = nifty.getScreen("loadlevel").findElementByName("loadingtext");
            textRenderer = element.getRenderer(TextRenderer.class);
            
            setProgress(0.2f, "Drawing floor");
            setProgress(0.3f, "Drawing roads");
            Node roads = myGrid.buildRoads();
            setProgress(0.5f, "Adding buildings");
            Node buildings = myGrid.placeBuildings();
            setProgress(0.9f, "Attaching to scene");
            app.getRootNode().attachChild(roads);
            //app.getRootNode().attachChild(buildings);
            setProgress(1f, "Loading complete");
 
            myGrid.setEnabled(true);
            return null;
        }
    };
    
    public void setProgress(final float progress, final String loadingText) {
        //since this method is called from another thread, we enqueue the changes to the progressbar to the update loop thread
        app.enqueue(new Callable() {
 
            public Object call() throws Exception {
                final int MIN_WIDTH = 32;
                int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
                progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
                progressBarElement.getParent().layoutElements();
 
                textRenderer.setText(loadingText);
                return null;
            }
        });
 
    }
    @Override
    public void cleanup() {
        exec.shutdown();
        
    }
}
