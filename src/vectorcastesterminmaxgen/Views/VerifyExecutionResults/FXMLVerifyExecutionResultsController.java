package vectorcastesterminmaxgen.Views.VerifyExecutionResults;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import vectorcastesterminmaxgen.CoreFunctions.VerifyExecutionResultsThread;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Model.VectorCastEnvironment;
import vectorcastesterminmaxgen.Model.Parameter;
import vectorcastesterminmaxgen.Model.ParsedTST;
import vectorcastesterminmaxgen.Model.TestCaseComparison;
import vectorcastesterminmaxgen.Model.TestCaseTST;
import vectorcastesterminmaxgen.UIUtils.KeyShortcutHandler;
import vectorcastesterminmaxgen.UIUtils.UIDialogs;

/**
 * FXML Controller class
 *
 * @author Roberto Caputo
 */
public class FXMLVerifyExecutionResultsController implements Initializable {    
    @FXML
    private AnchorPane vMainAnchorPane;

    @FXML
    private TreeTableView<TestCaseComparison> vMainTable;

    @FXML
    private TreeTableColumn<TestCaseComparison, String> vTestCaseColumn;

    @FXML
    private TreeTableColumn<TestCaseComparison, String> vExpectedResultColumn;

    @FXML
    private TreeTableColumn<TestCaseComparison, String> vResultColumn;

    @FXML
    private TreeTableColumn<TestCaseComparison, String> vMatchingColumn;

    @FXML
    private TitledPane vTestCaseDetailsTitledPane;

    @FXML
    private Label vEnvironmentNameLabel;

    @FXML
    private Label vUUTNameLabel;

    @FXML
    private TextField vSubprogramTextField;

    @FXML
    private TextField vTestCaseNameTextField;

    @FXML
    private Label vInputLabel;

    @FXML
    private Label vExecutionResultLabel;

    @FXML
    private Label vExecutionDateLabel;

    @FXML
    private TableView<Parameter> vSubProgramInputsTable;

    @FXML
    private TableColumn<Parameter, String> vInputNameColumn;

    @FXML
    private TableColumn<Parameter, String> vInputValueColumn;
    
    
    private VectorCastEnvironment vcEnv; 
    private ParsedTST generatedParsedTST;
    ArrayList <TestCaseComparison> tableModelList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        KeyShortcutHandler.set_CTRL_C_ListenerOnTextView(vSubprogramTextField);
        KeyShortcutHandler.set_CTRL_C_ListenerOnTextView(vTestCaseNameTextField);
            // Set CTRL+C listener//
        
        vMainTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                LoggerBoy.logSilently(this.getClass().getName(), "MouseButton.PRIMARY PRESSED ON MAIN TABLE");
                if (vMainTable.getSelectionModel().getSelectedItem() != null) {
                    mainTableRowSelected(vMainTable.getSelectionModel().getSelectedItem().getValue());
                }
            }
        });
    }
    
    public void initVectorCastEnvironment(VectorCastEnvironment vcEnv){
        this.vcEnv = vcEnv;
    }
    
    public void startVerification() {
        String envPath = vcEnv.getEnvDir().getPath();
        vEnvironmentNameLabel.setText(vcEnv.getEnvName());


        if (envPath != null) {
            Alert confirmScan = UIDialogs.buildConfirmationDialog(this.getClass(),
                    "Do you want to scan Results for following environment?",
                    "• Path: " + envPath + "\n"
                    + "• Environment Name: " + vcEnv.getEnvName());
            Optional<ButtonType> result = confirmScan.showAndWait();
            if (result.get() == ButtonType.OK) {
                VerifyExecutionResultsThread verifyResultsThread
                        = new VerifyExecutionResultsThread(this, vcEnv);
                verifyResultsThread.start(); // Start THread    //
            } else {
                close();
            }
        } else {
            LoggerBoy.logError(this.getClass().getSimpleName(), "You must Run Parser before verifying results!");
        }

    }
    
    public void populateMainTable(ArrayList <TestCaseComparison> tableModelList, VectorCastEnvironment vcEnv,
            ParsedTST parsedTST) {
        this.vcEnv = vcEnv;
        this.generatedParsedTST = parsedTST;
        this.tableModelList = tableModelList;
        
        vMainTable.getColumns().clear();
        int numOfFailedComparisons = 0;

        if (tableModelList.size() > 0) {

            TestCaseComparison fakeEnvironmentTestCase = new TestCaseComparison("(Environment): " + vcEnv.getEnvName());
            TreeItem<TestCaseComparison> root = new TreeItem<>(fakeEnvironmentTestCase);
            root.setExpanded(true);
            
            for (String uutName : vcEnv.getSubProgramList().keySet()){
                //  For each uut create a root tree element //
                TestCaseComparison fakeUutTestCase = new TestCaseComparison("(UUT): " + uutName);
                
                TreeItem<TestCaseComparison> uutRoot = new TreeItem<>(fakeUutTestCase);  
                uutRoot.setExpanded(true);
                
                //  Now Take the subprogram list for the uut    // 
                ArrayList <String> subProgramList = vcEnv.getSubProgramList().get(uutName);
                for (String subProgram : subProgramList){
                    //  For each subprogram create child root element   //
                    TestCaseComparison fakeSubProgramTestCase = new TestCaseComparison("(Subprogram): " + subProgram);
                    
                    TreeItem <TestCaseComparison> subProgramChildRoot = new TreeItem <>(fakeSubProgramTestCase);
                    subProgramChildRoot.setExpanded(true);
                    
                    //  Add TC for subprogram   //
                    for (TestCaseComparison tc : tableModelList){
                        // for each tc of the subprogram considered //
                        if (tc.getUut().equals(uutName) && tc.getFunction().equals(subProgram)) {
                            subProgramChildRoot.getChildren().add(new TreeItem<>(tc));

                            if (tc.isExecutionAndExpectedResultsMatching().equals("NO")) {
                                numOfFailedComparisons++;
                            }
                        }
                    }
                    
                    uutRoot.getChildren().add(subProgramChildRoot);
                    //  add the subprogam list to the uut   //
                }
                root.getChildren().add(uutRoot);
            }

            vTestCaseColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TestCaseComparison, String> testcase) -> 
                new ReadOnlyStringWrapper(testcase.getValue().getValue().getTestcaseName())
            );
            
            vExpectedResultColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TestCaseComparison, String> testcase) -> new ReadOnlyStringWrapper(
                    testcase.getValue().getValue().getExpectedExecutionResult() != null
                            ? testcase.getValue().getValue().getExpectedExecutionResult().name()
                            : ""));
            
            vResultColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TestCaseComparison, String> testcase) -> new ReadOnlyStringWrapper(
                    testcase.getValue().getValue().getEXECUTION_RESULT() != null
                    ? testcase.getValue().getValue().getEXECUTION_RESULT().name()
                    : ""));
            
            vMatchingColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TestCaseComparison, String> testcase)
                    -> new ReadOnlyStringWrapper(testcase.getValue().getValue().isExecutionAndExpectedResultsMatching()!= null
                            ? testcase.getValue().getValue().isExecutionAndExpectedResultsMatching()
                            : ""));

            vMatchingColumn.setCellFactory((TreeTableColumn<TestCaseComparison, String> testcase) -> 
                    new TreeTableCell<TestCaseComparison, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    if (item != null && !item.isEmpty() && !empty) {
                        int currentIndex = indexProperty()
                                .getValue() < 0 ? 0
                                : indexProperty().getValue();
                        String cellValue = testcase.getTreeTableView().getTreeItem(currentIndex)
                                .getValue().isExecutionAndExpectedResultsMatching();
                        
                        if (cellValue.equals("NO")){
                            setTextFill(Color.WHITE);
                            setStyle("-fx-font-weight: bold; -fx-background-color: lightcoral; -fx-alignment: CENTER;"
                                    + "-fx-opacity: 0.9;");
                        }
                        
                        else if (cellValue.equals("YES")){
                            setTextFill(Color.NAVY);
                            setStyle("-fx-font-weight: normal; -fx-background-color: lightgreen; -fx-alignment: CENTER;"
                                    + "-fx-opacity: 0.9;");
                        }
                        
                        else setStyle("");
                        
                        setText(cellValue);
                    }
                    else {
                        setStyle("");
                        setText(null);
                    }
                }
            });

            vMainTable.setRoot(root);
            vMainTable.getColumns().setAll(vTestCaseColumn, vExpectedResultColumn, vResultColumn, vMatchingColumn);

            Stage stage = (Stage) vMainAnchorPane.getScene().getWindow();
            stage.setTitle("Verify Execution Results (Failed " + String.valueOf(numOfFailedComparisons) + " of "
                    + tableModelList.size() + ")");
            
        }
    }
    
    private void mainTableRowSelected(TestCaseComparison selectedTC) {
        if (selectedTC != null && selectedTC.getEXECUTIONDATE() != null) {
            //verify if it is a valid TestCase//
            vTestCaseDetailsTitledPane.setDisable(false);
            vEnvironmentNameLabel.setText(selectedTC.getEnvironment());
            vUUTNameLabel.setText(selectedTC.getUut());
            vSubprogramTextField.setText(selectedTC.getFunction());
            vTestCaseNameTextField.setText(selectedTC.getTestcaseName());
            vInputLabel.setText(String.valueOf(selectedTC.getParameterList().size()));
            vExecutionResultLabel.setText(selectedTC.getEXECUTION_RESULT().name());
            vExecutionDateLabel.setText(selectedTC.getEXECUTIONDATE());

            populateFunctionInputTable(selectedTC);
        } else {
            vTestCaseDetailsTitledPane.setDisable(true);
        }
    }
     
    private void populateFunctionInputTable(TestCaseComparison selectedTC) {
        for (TestCaseTST testcaseTST : generatedParsedTST.getTestcaseTSTList()) {
            if (testcaseTST.getTestcaseName().equals(selectedTC.getTestcaseName())
                    && testcaseTST.getFunction().equals(selectedTC.getFunction())) {
                ObservableList<Parameter> list = FXCollections.observableArrayList();
                list.addAll(testcaseTST.getParameterList());
                vInputNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<Parameter, String> c) -> {
                    return new SimpleStringProperty(c.getValue().getName());
                });

                vInputValueColumn.setCellValueFactory((TableColumn.CellDataFeatures<Parameter, String> c) -> {
                    return new SimpleStringProperty(c.getValue().getValue());
                });

                vSubProgramInputsTable.getSelectionModel().clearSelection();

                vSubProgramInputsTable.setItems(list);
            }
        }
    }
    
    public void close() {
        Stage stage = (Stage) vMainAnchorPane.getScene().getWindow();
        stage.close();
    }
}
