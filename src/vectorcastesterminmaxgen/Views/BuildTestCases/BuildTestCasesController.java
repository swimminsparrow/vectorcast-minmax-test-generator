package vectorcastesterminmaxgen.Views.BuildTestCases;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vectorcastesterminmaxgen.Configs.ConfigFileReader;
import vectorcastesterminmaxgen.Configs.Constants;
import vectorcastesterminmaxgen.CoreFunctions.UserCustomParamHandler;
import vectorcastesterminmaxgen.CoreFunctions.VectorCastFinalizeEnvironment;
import vectorcastesterminmaxgen.CoreFunctions.YodaTestGenerator;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Model.ParsedTST;
import vectorcastesterminmaxgen.Model.VectorCastEnvironment;
import vectorcastesterminmaxgen.Utility.BrowseFileorFolderVCExtension;

/**
 * FXML Controller class
 *
 * @author Roberto Caputo
 * @version 0.2a    10/12/2018      View Created.
 * @version 0.2.1a  22/01/2019      UI fixes.
 * @version 0.2.2a  09/04/2019      Fixed for new YodaGenerator
 */
public class BuildTestCasesController implements Initializable {

    private UserCustomParamHandler paramListHandler;
    private ParsedTST parsedTST;
    private VectorCastEnvironment vcEnv;
    private ConfigFileReader configFileReader;

    //  UI DECLARATIONS     //
    @FXML
    private AnchorPane vMainPane;

    @FXML
    private Button vCancelButton;

    @FXML
    private Button vRunButton;

    @FXML
    public ProgressIndicator vProgressRun;

    @FXML
    private CheckBox vBuildCombinationTestsCheckBox;

    @FXML
    private CheckBox vImportTestCaseCheckBox;

    @FXML
    private VBox vVectorCastOptionsVBox;

    @FXML
    private HBox vEnvironmentDirHBox;

    @FXML
    private TextField vTextFieldEnvironmentPath;

    @FXML
    private Button vBrowseEnvironmentPath;

    @FXML
    private HBox vExecuteOptionHBox;

    @FXML
    private CheckBox vExecuteTCCheckBox;
    

    @FXML
    private void vButtonClicked(ActionEvent event) {
        switch (((Button) event.getSource()).getId()) {
            case "vBrowseEnvironmentPath":
                File envDir = BrowseFileorFolderVCExtension.browseFolderDialog(event, new File("C:\\"));
                if (BrowseFileorFolderVCExtension.verifyIfEnvironmentFolder(envDir)) {
                    vTextFieldEnvironmentPath.setText(envDir.getPath());
                    //  Take Path if it contains a valid environment    //
                    configFileReader.setENVIRONMENT_DIR_PATH(envDir.getPath());
                    vcEnv = new VectorCastEnvironment(envDir);
                    vcEnv.setEnvName(BrowseFileorFolderVCExtension.getEnvironmentNameFromFolder(envDir));
                }
                break;
            case "vRunButton":
//                Alert alert = UIDialogs.buildConfirmationDialog(this.getClass(), "Do you confirm these values?",
//                        paramListHandler.getParamListFormatted());
//                Optional<ButtonType> result = alert.showAndWait();
//                if (result.get() == ButtonType.OK) {
                if (!vImportTestCaseCheckBox.isSelected() || 
                        (vImportTestCaseCheckBox.isSelected() && isValidEnvironmentInfo())){
                    YodaTestGenerator yodaGenerator = new YodaTestGenerator(parsedTST, paramListHandler);
                    paramListHandler.saveParamListToXML(parsedTST);
                    //  save cached copy of paramlist to XML file   //

                    //  save configFileReader  //
                    configFileReader.storeBooleanProperty(ConfigFileReader.GENERATE_OPTION_EXECUTE_TESTCASES, vExecuteTCCheckBox.isSelected());
                    configFileReader.storeBooleanProperty(ConfigFileReader.GENERATE_OPTION_TEST_COMBINATORI, vBuildCombinationTestsCheckBox.isSelected());

                    File destinationGeneratedTestCasesDir = yodaGenerator.buildNewTestCases();

                    if (vImportTestCaseCheckBox.isSelected() && isValidEnvironmentInfo()) {
                        //  Start Finalization Environment Thread    //
                        new VectorCastFinalizeEnvironment(vcEnv, destinationGeneratedTestCasesDir, this).start();
                    } else {
                        showBuiltTestCaseDir();    //show only tst dir//
                    }
                }
//                }
                break;
            case "vCancelButton":
                close();
                break;

            default:
                break;
        }
    }

    private void showBuiltTestCaseDir() {
        ButtonType openDirButton = new ButtonType("Open Folder", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                "I cannot import TST in the Environment... because you didn't specify"
                + " any valid Environment Dir.\nHowever, you can see the generated TSTs",
                openDirButton);
        alert.setTitle("Operation Finished Successfully");
        alert.setHeaderText("Do you want to see Generated Scripts?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == openDirButton) {
            BrowseFileorFolderVCExtension.openFolder(Constants.APPDIR_CACHE_PATH);
            close();
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paramListHandler = UserCustomParamHandler.getInstance();
        configFileReader = ConfigFileReader.getInstance();
        
        vProgressRun.setVisible(false);
                
        vImportTestCaseCheckBox.setSelected(true);
        
        vImportTestCaseCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            vExecuteTCCheckBox.setDisable(!vImportTestCaseCheckBox.isSelected());
        });
    }
    
    public void initParsedTST (ParsedTST parsedTST){
        this.parsedTST = parsedTST;
    }
    
    public void initVectorCastEnvironment (VectorCastEnvironment vcEnv){
        this.vcEnv = vcEnv;
        
        vTextFieldEnvironmentPath.setText(vcEnv != null ? vcEnv.getEnvDir().getPath() : "");
    }

    private boolean isValidEnvironmentInfo() {
        if (vcEnv != null && vcEnv.getEnvDir() != null && vcEnv.getEnvName() != null
                && vcEnv.getEnvName().equals(parsedTST.getEnvName())) {
            //  Disable or enable containers if import has been disabled/enabled    //
            return true;
        } else {
            LoggerBoy.logError(this.getClass(),
                    "Parsed Environment in TST does not match with Specified Environment Dir!");
            return false;
        }
    }
    
    public void lockOrUnlockUI() {
        vMainPane.setDisable(!vMainPane.isDisabled());
    }
    
    public void close() {
        Stage stage = (Stage) vMainPane.getScene().getWindow();
        stage.close();
    }
}
