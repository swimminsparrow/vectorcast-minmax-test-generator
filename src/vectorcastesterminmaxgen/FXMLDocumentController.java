package vectorcastesterminmaxgen;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import vectorcastesterminmaxgen.Configs.ConfigFileReader;
import vectorcastesterminmaxgen.CoreFunctions.UserCustomParamHandler;
import vectorcastesterminmaxgen.Model.VectorCastEnvironment;
import vectorcastesterminmaxgen.Model.ParsedTST;
import vectorcastesterminmaxgen.Model.XMLParams.FunctionUserCustomParam;
import vectorcastesterminmaxgen.Model.XMLParams.UserCustomParam;
import vectorcastesterminmaxgen.UIUtils.KeyShortcutHandler;
import vectorcastesterminmaxgen.UIUtils.UIDialogs;
import vectorcastesterminmaxgen.Utility.BrowseFileorFolderVCExtension;
import vectorcastesterminmaxgen.Views.AddCustomValues.AddCustomValuesController;

/**
 * This is the Main App Controller.
 * @author Roberto Caputo
 */
public class FXMLDocumentController implements Initializable {
    public VectorCastEnvironment vcEnv;
    public ParsedTST parsedTST;
    public UserCustomParamHandler userParamHandler;
    private ConfigFileReader configFileReader;
    private LoggerBoy logger;
    
    @FXML
    private AnchorPane vMainAnchorPane;
    
        //  MenuBar //
    @FXML
    private Menu vMenuRun;
    @FXML
    private MenuItem vMenuView_Reports;    
    @FXML
    public MenuItem vMenuRun_Generate;
    @FXML
    private MenuItem vMenuRun_VerifyExecutionResults;
    
    public MenuItem vMenuInfo_About;
    @FXML
    private MenuItem vMenuEdit_Settings;
    @FXML
    private MenuItem vMenuInfo_Help;    

    @FXML
    private TableView<UserCustomParam> vMainTable;

    @FXML
    private TableColumn<UserCustomParam, String> vParamNameColumn;

    @FXML
    private TableColumn<UserCustomParam, String> vOccurencesColumn;
        
    @FXML
    private TableColumn<UserCustomParam, String> vLockedValueColumn;

    @FXML
    private TableColumn<UserCustomParam, String> vMinColumn;

    @FXML
    private TableColumn<UserCustomParam, String> vMaxColumn;
    
    @FXML
    private TableColumn<UserCustomParam, String> vCustomSubProgramValuesColumn;

    @FXML
    private Label vEnvironmentNameLabel;
    
    @FXML
    private Label vNumInputFoundLabel;
    
    //  Single Param Pane Details   //
    @FXML 
    private TitledPane vParamDetailsTitledPane;
    
    @FXML
    private Label vParamNameLabel;
        
    @FXML
    private ListView <String> vFunctionsListView;

    @FXML
    private TableView<FunctionUserCustomParam> vFunctionCustomParamTable;

    @FXML
    private TableColumn<FunctionUserCustomParam, String> vFunctionCustomTableColumn;

    @FXML
    private TableColumn<FunctionUserCustomParam, String> vLockedValueCustomTableColumn;

    @FXML
    private TableColumn<FunctionUserCustomParam, String> vMinCustomTableColumn;

    @FXML
    private TableColumn<FunctionUserCustomParam, String> vMaxCustomTableColumn;
    
    @FXML
    private HBox vSelectedParamActionsHBox;

    @FXML
    private Button vDeleteParamButton;

    @FXML
    private Button vAddParamButton;

    @FXML
    private Button vDeleteCustomValuesButton;

    
    @FXML 
    public TitledPane vLoggerTitledPane;
    public TextArea vLoggerTextArea;
    

    @FXML
    private void vMenuItemClicked(ActionEvent event) {
        switch (((MenuItem) event.getSource()).getId()) {
            case "vMenuFile_Close":
                saveAndClose(event);
                break;
            case "vMenuView_Reports":
                MenuView.showReports(parsedTST);
                break;
            case "vMenuView_LogWindow":
                MenuView.showLogWindow(this);
                break;
            case "vMenuEdit_Settings":
                MenuEdit.showPreferences(this);
                break;
            case "vMenuRun_RunParser":
                MenuRun.showRunParser(this);
                break;
            case "vMenuRun_Generate":
                MenuRun.showBuildTestCasesView(this);
                break;
            case "vMenuRun_VerifyExecutionResults":
                MenuRun.showVerifyExecutionResults(this);
                break;
            case "vMenuInfo_Help":
                MenuHelp.showUserManual();
                break;
            case "vMenuInfo_About":
                MenuHelp.showAbout();
                break;
            default:
                break;
        }
    }
    
    @FXML
    private void vButtonClicked(ActionEvent event) {
        switch (((Button) event.getSource()).getId()) {
            case "vDeleteParamButton":
                UserCustomParam parentSelectedParam = vMainTable.getSelectionModel().getSelectedItem();
                if (parentSelectedParam != null) {
                    Alert confirmDeleteDialog = UIDialogs.buildConfirmationDialog(this.getClass(),
                            "Are You Sure?",
                            "Do you really want to remove all configurations for the following Parameter?\n"
                            + "(ATTENTION: configuration will not be removed from the Database)\n"
                            + "\t" + parentSelectedParam.getName());
                    Optional<ButtonType> result = confirmDeleteDialog.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        userParamHandler.getCurrentSetUserParamList().remove(parentSelectedParam.getName());
                        populateMainTable();
                    }
                } else {
                    LoggerBoy.logError(this.getClass(), "Please Select a Parameter from the list");
                }
                break;
            case "vAddParamButton":
                AddCustomValuesController.startView(this);
                break;

            default:
                break;
        }
    }
    
    /**
    *   Button bar actions
    */
    @FXML
    private void vSaveButtonAction(ActionEvent event) {
        userParamHandler.saveParamListToXML(parsedTST);
            //  save cached copy of paramlist to XML file   //
    }  
    
    /**
     * This Function handles Selected Param Button actions.
     * @param event 
     */
    @FXML
    private void vFunctionCustomParamTableButtonActions(ActionEvent event) {
        switch (((Button) event.getSource()).getId()) {
            case "vDeleteCustomValuesButton":
                UserCustomParam parentSelectedParam = vMainTable.getSelectionModel().getSelectedItem();
                FunctionUserCustomParam selectedChildParam = vFunctionCustomParamTable.getSelectionModel().getSelectedItem();
                if (selectedChildParam != null) {
                    userParamHandler.getCurrentSetUserParamList().get(parentSelectedParam.getName())
                            .getFunctionCustomParamList().remove(selectedChildParam.getFunctionName());

                    fillVParamDetailsTitledPane(parentSelectedParam);
                    updateMainTableRow(parentSelectedParam.getName());
                } else {
                    LoggerBoy.logError(this.getClass(), "Please Select a Function from the list");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userParamHandler = UserCustomParamHandler.getInstance();
        loadConfigFile();
        logger = LoggerBoy.getInstance();
        logger.setLoggerTextArea(vLoggerTextArea);
        
        // Initialize Views //
        initializeMainTable();
                      
    }    
    
    //  MAIN TABLE METHODS  //
    private void initializeMainTable() {
        populateMainTable();

        //  Commented: MUST BE FIXED!
//        vMainTable.setOnKeyPressed((KeyEvent event) -> {
//            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP){
//                LoggerBoy.logSilently(this.getClass().getName(), "KEY DOWN || KEY UP PRESSED ON MAIN TABLE");
//                MainTableRowSelected();
//            }
//        });
        vMainTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                //LoggerBoy.logSilently(this.getClass().getName(), "MouseButton.PRIMARY PRESSED ON MAIN TABLE");
                mainTableRowSelected();
            }
        });

    }
    
    public void mainTableRowSelected() {
        UserCustomParam selectedParam = vMainTable.getSelectionModel().getSelectedItem();
        if (selectedParam != null) {
            vParamDetailsTitledPane.setDisable(false);
            fillVParamDetailsTitledPane(selectedParam);
        } else {
            vParamDetailsTitledPane.setDisable(true);
        }
    }
    
    public void populateMainTable() {
        ObservableList<UserCustomParam> list = FXCollections.observableArrayList();
        
        list.addAll(userParamHandler.getCurrentSetUserParamList().values());

        if (list.size() > 0) {
            vParamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            vOccurencesColumn.setCellValueFactory((CellDataFeatures<UserCustomParam, String> c) -> {
                return new SimpleStringProperty(
                        String.valueOf(parsedTST.getNumOfOccurencesForParamName(c.getValue().getName())));
            });          
            vLockedValueColumn.setCellValueFactory((CellDataFeatures<UserCustomParam, String> c) -> {
                return new SimpleStringProperty((c.getValue().isIsLockedValue() && c.getValue().isParamSetByUser()) ?
                        String.valueOf(c.getValue().getLockedValue()) : "");
            });

            vMinColumn.setCellValueFactory((CellDataFeatures<UserCustomParam, String> c) -> {                
                return new SimpleStringProperty((!c.getValue().isIsLockedValue() && c.getValue().isParamSetByUser()) ?
                        String.valueOf(c.getValue().getMin()) : "");
            });

            vMaxColumn.setCellValueFactory((CellDataFeatures<UserCustomParam, String> c) -> {
                return new SimpleStringProperty((!c.getValue().isIsLockedValue() && c.getValue().isParamSetByUser()) ?
                        String.valueOf(c.getValue().getMax()) : "");
            });
            vCustomSubProgramValuesColumn.setCellValueFactory((CellDataFeatures<UserCustomParam, String> c) -> {
                return new SimpleStringProperty(String.valueOf(c.getValue().getFunctionCustomParamList().size()));
            });
            vMainTable.getItems().clear();
            vMainTable.setItems(list);
        }
    }
    
    /**
     * This Function updates a single row values of the main table.
     * @param paramName
     */
    public synchronized void updateMainTableRow(String paramName){
        Platform.runLater(() -> {
            vMainTable.getItems().set(vMainTable.getSelectionModel().getSelectedIndex(),
                    userParamHandler.getCurrentSetUserParamList().get(paramName));
        });
    }    
    
    
    //  PARAMETER DETAILS TABLE //
    private void populateVSelectedParamTable(UserCustomParam selectedParam) {
        LoggerBoy.logSilently(this.getClass().getName(), selectedParam.getName() + " has been selected");
        vParamNameLabel.setText(selectedParam.getName());

        //  Populate function list  //       
        vFunctionsListView.getItems().clear();
        vFunctionsListView.setItems(FXCollections.observableArrayList(
                userParamHandler.getParsedUserParamList().get(selectedParam.getName())));
        
        KeyShortcutHandler.set_CTRL_C_ListenerOnListView(vFunctionsListView);
        //  Set CTRL_C KEY COMBINATION LISTENER//
    }
    
    private void fillVParamDetailsTitledPane(UserCustomParam selectedParam) {
        populateVSelectedParamTable(selectedParam);
        
        vFunctionCustomParamTable.getItems().clear();

        ObservableList<FunctionUserCustomParam> list = FXCollections.observableArrayList();
        list.addAll(selectedParam.getFunctionCustomParamList().values());

        if (list.size() > 0) {
            vFunctionCustomTableColumn.setCellValueFactory(new PropertyValueFactory<>("functionName"));
            vLockedValueCustomTableColumn.setCellValueFactory((CellDataFeatures<FunctionUserCustomParam, String> c) -> {
                return new SimpleStringProperty((c.getValue().isIsLockedValue() && c.getValue().isParamSetByUser()) ?
                        String.valueOf(c.getValue().getLockedValue()) : "");
            });

            vMinCustomTableColumn.setCellValueFactory((CellDataFeatures<FunctionUserCustomParam, String> c) -> {
                return new SimpleStringProperty((!c.getValue().isIsLockedValue() && c.getValue().isParamSetByUser()) ?
                        String.valueOf(c.getValue().getMin()) : "");
            });

            vMaxCustomTableColumn.setCellValueFactory((CellDataFeatures<FunctionUserCustomParam, String> c) -> {
                return new SimpleStringProperty((!c.getValue().isIsLockedValue() && c.getValue().isParamSetByUser()) ?
                        String.valueOf(c.getValue().getMax()) : "");
            });

            vFunctionCustomParamTable.setItems(list);
        }

    }          

    /**
     * This Function loads configuration from config xml file.
     */
    public void loadConfigFile (){
        configFileReader = ConfigFileReader.getInstance();    
        LoggerBoy.logSilently(this.getClass().getName(), "Configurations in XML file loaded");
    }
    
    public void onCloseRunParserController (ParsedTST parsedTST, VectorCastEnvironment vcEnv){
        this.parsedTST = parsedTST;
        this.vcEnv = vcEnv;
        if (vcEnv != null && vcEnv.getEnvDir() != null){
            vcEnv.setEnvName(BrowseFileorFolderVCExtension.getEnvironmentNameFromFolder(vcEnv.getEnvDir()));
        }
        
        if (userParamHandler.getParsedUserParamList().size() > 0){
            vEnvironmentNameLabel.setText(parsedTST.getEnvName());
            vNumInputFoundLabel.setText(String.valueOf(userParamHandler.getCurrentSetUserParamList().size()));
            populateMainTable();
            vMenuRun_Generate.setDisable(false);
            vMenuView_Reports.setDisable(false);
        }
        else {
            vMenuRun_Generate.setDisable(true);
            vMenuView_Reports.setDisable(true);
            LoggerBoy.logError(this.getClass().getSimpleName(), "No input value parsed");
        }
    }
    
    private void saveAndClose (ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save before exit?");
        //alert.setHeaderText("Values to save:");
        alert.setContentText("Do you want to save the values before exit?");
        alert.setHeight(500);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            vSaveButtonAction(event);
            close();
        }
    }
            
    public void close() {
        Stage stage = (Stage) vMainAnchorPane.getScene().getWindow();
        stage.close();
    }
    
    public void lockUI() {
        vMenuRun.setDisable(true);
    }

    public void unLockUI() {
        vMenuRun.setDisable(false);
    }
    
}
