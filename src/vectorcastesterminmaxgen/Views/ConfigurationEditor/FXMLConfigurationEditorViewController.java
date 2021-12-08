package vectorcastesterminmaxgen.Views.ConfigurationEditor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import vectorcastesterminmaxgen.Configs.ConfigFileReader;
import vectorcastesterminmaxgen.Configs.Constants;
import vectorcastesterminmaxgen.CoreFunctions.VectorCastCLIUtils;
import vectorcastesterminmaxgen.LoggerBoy;

/**
 * FXML Controller class for Preference Editor Dialog.
 *
 * @author Roberto Caputo
 */
public class FXMLConfigurationEditorViewController implements Initializable {

    private ConfigFileReader configs;
    private File licenseServerScript;

    //  TAB 1   //
    public TextField vPref1Option1VectorCastFolder;
    public TextArea vPref1Option2LicenseScript;
    
    
    //  Buttons //
    public Button vButtonSave;
    public Button vButtonCancel;

    @FXML
    private void vButtonOnClick (ActionEvent event){
        switch (((Button)event.getSource()).getId()){
            case "vButtonSave":
                savePreferences(event);
                break;
            case "vButtonCancel":
                close(event);
                break;
            default:
                break;
        }
    }    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        licenseServerScript = new File (Constants.START_LICENSE_SERVER_SCRIPT_FILE_PATH);
        configs = ConfigFileReader.getInstance();    

        loadDefaultFields();
    }

    private void loadDefaultFields() {
            //  TAB 1   //
        vPref1Option1VectorCastFolder.setText(configs.getVECTORCAST_INSTALL_PATH());
        vPref1Option2LicenseScript.setText(new VectorCastCLIUtils().getStartServerLicenseScript());
    }
    
    /**
     * This Function save prefs in config file xml.
     * @param event 
     */
    private void savePreferences(ActionEvent event){        
        try {
            //  TAB 1   //
            configs.setVECTORCAST_INSTALL_PATH(vPref1Option1VectorCastFolder.getText());
            FileUtils.write(licenseServerScript, vPref1Option2LicenseScript.getText(), "UTF-8");

            close(event);
        } catch (IOException e) {
            LoggerBoy.logError(this.getClass().getSimpleName(), e.getLocalizedMessage());
        }
    }
    
    private void close(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
}
