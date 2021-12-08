package vectorcastesterminmaxgen.Views.RunParser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import vectorcastesterminmaxgen.Configs.ConfigFileReader;
import vectorcastesterminmaxgen.CoreFunctions.Parsers.AnakinTSTParser;
import vectorcastesterminmaxgen.CoreFunctions.UserCustomParamHandler;
import vectorcastesterminmaxgen.CoreFunctions.VectorCastPrepareEnvironmentTask;
import vectorcastesterminmaxgen.Model.ParsedTST;
import vectorcastesterminmaxgen.Model.VectorCastEnvironment;
import vectorcastesterminmaxgen.Utility.BrowseFileorFolderVCExtension;

/**
 * FXML Controller class
 *
 * @author Roberto Caputo
 * @version 0.2     20/11/2018 Created.
 * @version 0.2.2   08/04/2019 Adapted to new models.
 */
public class FXMLRunParserController implements Initializable {
    
    public VectorCastEnvironment vcEnv;
    public ParsedTST parsedTST;
    private AnakinTSTParser anakin;
    private UserCustomParamHandler paramListHandler;
    private ConfigFileReader configFileReader;
    private ArrayList<String> uutList;

    
    @FXML
    private TitledPane vTitledPane1;

    @FXML
    private TextField vTextFieldSrcPath;

    @FXML
    private Button vBrowseSrcPath;

    @FXML
    private Label vUutNumLabel;

    @FXML
    public CheckBox vCompleteParamNamesCheckbox;

    @FXML
    public CheckBox vVoidFunctionCheckBox;

    @FXML
    private RadioButton vRadioButton1;

    @FXML
    private TextField vTextFieldEnvironmentPath;

    @FXML
    private Button vBrowseEnvironmentPath;

    @FXML
    private Button vParseEnvironmentButton;

    @FXML
    public ProgressIndicator vProgressEnvironment;

    @FXML
    private RadioButton vRadioButton2;

    @FXML
    private TextField vSourcetstTextField;

    @FXML
    private Button vBrowseTSTButton;

    @FXML
    private Button vParseTSTButton;

    @FXML
    public ProgressIndicator vProgressTST;
    
    @FXML
    private HBox vEnvironmentParseHBox;
    @FXML
    private HBox vTSTParseHBox;

    @FXML
    private void vButtonClicked(ActionEvent event) {
        switch (((Button) event.getSource()).getId()) {
            case "vBrowseSrcPath":
                File srcDir = BrowseFileorFolderVCExtension.browseFolderDialog(event, new File("C:\\"));
                if (srcDir != null && srcDir.isDirectory()) {
                    uutList = BrowseFileorFolderVCExtension.getUUTList(srcDir);
                    vUutNumLabel.setText(String.valueOf(uutList.size()));
                    vTextFieldSrcPath.setText(srcDir.getPath());
                }
                break;
            case "vBrowseEnvironmentPath":
                enableParseEnvironmentFuncs(BrowseFileorFolderVCExtension.browseFolderDialog(event, new File("C:\\")));
                break;
            case "vParseEnvironmentButton":
                File envDir = new File(vTextFieldEnvironmentPath.getText());
                enableParseEnvironmentFuncs(envDir);
                String environmentName = BrowseFileorFolderVCExtension.getEnvironmentNameFromFolder(envDir);
                vcEnv.setEnvName(environmentName);
                storeConfigurations();
                //  Start prepare Environment Thread    //
                new VectorCastPrepareEnvironmentTask(uutList, this, event, vcEnv).start();
                break;
            case "vBrowseTSTButton":
                File tstFile = BrowseFileorFolderVCExtension.browseTSTFileDialog(event, new File("C:\\"));
                enableParseTSTFuncs(tstFile);
                break;
            case "vParseTSTButton":
                File sourceTSTFile = new File(vSourcetstTextField.getText());
                if (BrowseFileorFolderVCExtension.isTSTFile(sourceTSTFile)) {
                    vProgressTST.setVisible(true);
                    storeConfigurations();
                    anakin = new AnakinTSTParser(sourceTSTFile);
                    parsedTST = anakin.parseTST();
                    vProgressTST.setVisible(false);
                    close(event);
                }
                break;
                
            default:
                break;
        }
    }

    private void enableParseEnvironmentFuncs(File envDir) {
        if (BrowseFileorFolderVCExtension.verifyIfEnvironmentFolder(envDir)) {
            vTextFieldEnvironmentPath.setText(envDir.getPath());
            //  Take Path if it contains a valid environment    //
            vParseEnvironmentButton.setDisable(false);
            configFileReader.setENVIRONMENT_DIR_PATH(envDir.getPath());
            vcEnv = new VectorCastEnvironment(envDir);
        }
    }

    private void enableParseTSTFuncs(File tstFile) {
        if (tstFile != null && FilenameUtils.isExtension(tstFile.getName(), "tst")) {
            vSourcetstTextField.setText(tstFile.getPath());
            vParseTSTButton.setDisable(false);
            configFileReader.setTST_FILE_PATH(tstFile.getPath());
        }
    }
    
    /**
     * This function stores the configuration for the parser in the xml.
     */
    private void storeConfigurations(){
        configFileReader.storeBooleanProperty(ConfigFileReader.PARSER_OPTION_EXPLODE_STRUCT, 
                vCompleteParamNamesCheckbox.isSelected());
        configFileReader.storeBooleanProperty(ConfigFileReader.PARSER_OPTION_DELETE_VOID_FUNCTIONS, 
                vVoidFunctionCheckBox.isSelected());
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        uutList = new ArrayList();
        configFileReader = ConfigFileReader.getInstance();
        paramListHandler = UserCustomParamHandler.getInstance();
        
        final ToggleGroup radioGroup = new ToggleGroup();
        vRadioButton1.setToggleGroup(radioGroup);
        vRadioButton2.setToggleGroup(radioGroup);
               
        radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle> (){
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                vEnvironmentParseHBox.setDisable(!vRadioButton1.isSelected());
                vTSTParseHBox.setDisable(!vRadioButton2.isSelected());
            }
        
        });
                        
        vRadioButton1.setSelected(true);
        vTSTParseHBox.setDisable(!vRadioButton2.isSelected());
        vProgressEnvironment.setVisible(false);
        vProgressTST.setVisible(false);
        
        //  Load Configurations //
        if (configFileReader.getENVIRONMENT_DIR_PATH() != null) {
            File envDir = new File(configFileReader.getENVIRONMENT_DIR_PATH());
            vTextFieldEnvironmentPath.setText(envDir.getPath());
            enableParseEnvironmentFuncs(envDir);
        }
        if (configFileReader.getTST_FILE_PATH() != null) {
            File tstFile = new File(configFileReader.getTST_FILE_PATH());
            vSourcetstTextField.setText(tstFile.getPath());
            enableParseTSTFuncs(tstFile);
        }        
    }
 
    public void close(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
