package vectorcastesterminmaxgen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.geom.Rectangle2D;
import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import vectorcastesterminmaxgen.Configs.Constants;

/**
 *
 * @author Roberto Caputo
 */
public class VectorCastEsterMinMaxGen extends Application {
    
    private SplashScreen mySplash;
    private Rectangle2D.Double splashTextArea;
    private Graphics2D splashGraphics;
    private Font font;
    
    @Override
    public void start(Stage stage) throws Exception {
        splashInit();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
                
        createMainDirs();
        
        Scene scene = new Scene(root);
        
        stage.setTitle(this.getClass().getPackage().getImplementationTitle() + " " + this.getClass().getPackage().getImplementationVersion());
        stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_64x64.png"));
        stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_32x32.png"));
        stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_16x16.png"));
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
        
    }

        /**
     * This Function create dirs for the software if they does not exist.
     */
    private void createMainDirs() {
        new File(Constants.APPDIR_CONFIG_PATH).mkdir();
        new File(Constants.APPDIR_CACHE_PATH).mkdir();
    }
    
     /**
     * Prepare the global variables for the other splash functions.
     */
    private void splashInit()
    {
        mySplash = SplashScreen.getSplashScreen();
        if (mySplash != null)
        {   // if there are any problems displaying the splash this will be null
            Dimension ssDim = mySplash.getSize();
            int height = ssDim.height;
            int width = ssDim.width;
            // stake out some area for our status information
            splashTextArea = new Rectangle2D.Double(15., height*0.88, width * .45, 32.);

            // create the Graphics environment for drawing status info
            splashGraphics = mySplash.createGraphics();
            font = new Font("Arial", Font.PLAIN, 14);
            splashGraphics.setFont(font);
            
            // initialize the status info
            splashText("Version: " + this.getClass().getPackage().getImplementationVersion()+"\n"
                    + "Author: Roberto Caputo");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                LoggerBoy.logError(this.getClass().getName(), ex.getMessage());
            }
            mySplash.close();
        }
        else LoggerBoy.logSilently(this.getClass().getName(), "Can't start splashscreen");
    }
    
     /**
     * Display text in status area of Splash.  Note: no validation it will fit.
     * @param str - text to be displayed
     */
    public void splashText(String str)
    {
        String appInfo []= StringUtils.split(str, "\n");
        if (mySplash != null && mySplash.isVisible())
        {   // important to check here so no other methods need to know if there
            // really is a Splash being displayed

            // erase the last status text
            Color bg=new Color(0f,0f,0f,0f );
            splashGraphics.setPaint(bg);
            splashGraphics.fill(splashTextArea);

            // draw the text
            splashGraphics.setPaint(Color.WHITE);
            splashGraphics.drawString(appInfo[0], (int)(mySplash.getSize().width/2),
                    (int)(splashTextArea.getY() + 0));
            splashGraphics.drawString(appInfo[1], (int)(mySplash.getSize().width/2), 
                    (int)(splashTextArea.getY() + 20));

            // make sure it's displayed
            mySplash.update();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
