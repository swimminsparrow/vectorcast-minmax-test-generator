package vectorcastesterminmaxgen.Views.AddCustomValues;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;
import vectorcastesterminmaxgen.CoreFunctions.UserCustomParamHandler;
import vectorcastesterminmaxgen.FXMLDocumentController;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.MenuEdit;
import vectorcastesterminmaxgen.Model.XMLParams.FunctionUserCustomParam;
import vectorcastesterminmaxgen.Model.XMLParams.UserCustomParam;
import vectorcastesterminmaxgen.UIUtils.UIDialogs;

/**
 * FXML Controller class
 *
 * @author Roberto Caputo
 */
public class AddCustomValuesController implements Initializable {
    @FXML
    private AnchorPane vMainPane;

    @FXML
    private Button vCancelButton;

    @FXML
    private Button vAddButton;

    @FXML
    private ComboBox<String> vParameterListCombo;

    @FXML
    private CheckBox vLockValueCheckBox;

    @FXML
    private TextField vLockedValueTextField;

    @FXML
    private TextField vMinTextField;

    @FXML
    private TextField vMaxTextField;

    @FXML
    private CheckBox vFunctionSelectedCheckBox;

    @FXML
    private ComboBox<String> vFunctionListCombo;

    private UserCustomParamHandler paramListHandler;

    
    @FXML
    void vButtonAction(ActionEvent event) {
        switch (((Button) event.getSource()).getId()) {
            case "vAddButton":
                addParam();
                break;
            case "vCancelButton":
                close();
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
        this.paramListHandler = UserCustomParamHandler.getInstance();

        vParameterListCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            UserCustomParam userCustomParam = paramListHandler.getCurrentSetUserParamList().get(newValue);
            
            setUIValues(userCustomParam);

            populateFunctionListCombo(newValue);
        }
        );
        
        vFunctionListCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String paramName = vParameterListCombo.getSelectionModel().getSelectedItem();
            
            UserCustomParam functionUserCustomParam = paramListHandler.getCurrentSetUserParamList().get(paramName)
                    .getFunctionCustomParamList().get(newValue);
            
            setUIValues(functionUserCustomParam);
        }
        );
        
        vLockValueCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, 
                Boolean oldValue, Boolean newValue) -> {
            vLockedValueTextField.setDisable(!vLockValueCheckBox.isSelected());
            vMinTextField.setDisable(vLockValueCheckBox.isSelected());
            vMaxTextField.setDisable(vLockValueCheckBox.isSelected());
        });
        
        vFunctionSelectedCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable,
                Boolean oldValue, Boolean newValue) -> {
            vFunctionListCombo.setDisable(!vFunctionSelectedCheckBox.isSelected());

        });
        
        initParameterListCombo();
        vFunctionListCombo.setDisable(true);
    }
    
    private void initParameterListCombo() {
        vParameterListCombo.getItems().clear();
        vParameterListCombo.getItems().addAll(paramListHandler.getParsedUserParamList().keySet());
    }
    
    private void populateFunctionListCombo(String paramName){
        vFunctionListCombo.getItems().clear();
        vFunctionListCombo.getItems().add("");
        vFunctionListCombo.getItems().addAll(
                paramListHandler.getParsedUserParamList().get(paramName));
    }
    
    private void setUIValues (UserCustomParam userCustomParam){
        if (userCustomParam != null){
            vLockValueCheckBox.setSelected(userCustomParam.isIsLockedValue());
            vLockedValueTextField.setText(String.valueOf(userCustomParam.getLockedValue()));
            vMinTextField.setText(String.valueOf(userCustomParam.getMin()));
            vMaxTextField.setText(String.valueOf(userCustomParam.getMax()));
        }
        else{
            vLockValueCheckBox.setSelected(false);
            vLockedValueTextField.setText("");
            vMinTextField.setText("");
            vMaxTextField.setText("");
        }
    }
    
    private void addParam() {
        String selectedParamName = vParameterListCombo.getSelectionModel().getSelectedItem();
        String selectedFunction = vFunctionListCombo.getSelectionModel().getSelectedItem();
        String lockedValueString = vLockedValueTextField.getText();
        String minValueString = vMinTextField.getText();
        String maxValueString = vMaxTextField.getText();

        if (selectedParamName == null || selectedParamName.isEmpty()) {
            LoggerBoy.logError(this.getClass(), "Please Select a Parameter");
        } else if ((vLockValueCheckBox.isSelected() &&  !StringUtils.isNumeric(lockedValueString)) 
                || (!vLockValueCheckBox.isSelected() && (!StringUtils.isNumeric(minValueString)
                    || !StringUtils.isNumeric(maxValueString)))) {
            LoggerBoy.logError(this.getClass(), "Please insert valid numeric values");
        } else if (!vLockValueCheckBox.isSelected() && Long.parseLong(minValueString) >= Long.parseLong(maxValueString)) {
            LoggerBoy.logError(this.getClass(), "Please insert a valid range (Min should be > Max)");
        } else if (vFunctionSelectedCheckBox.isSelected()
                && (selectedFunction == null || selectedFunction.isEmpty())) {
            LoggerBoy.logError(this.getClass(), "Please Select a Function");
        } else if (vFunctionSelectedCheckBox.isSelected()
                && !selectedFunction.isEmpty()
                && paramListHandler.getCurrentSetUserParamList().containsKey(selectedParamName)
                && paramListHandler.getCurrentSetUserParamList().get(selectedParamName).getFunctionCustomParamList().containsKey(selectedFunction)) {
            LoggerBoy.logError(this.getClass(), "You already inserted values for\n"
                    + "Parameter: " + selectedParamName + "\n"
                    + "Function: " + selectedFunction + "\n"
                    + "Please Delete it from the main view before.");
        } else {
            //  OMG finally i can save it   //
            if (!vFunctionSelectedCheckBox.isSelected()) {
                if (paramListHandler.getCurrentSetUserParamList().containsKey(selectedParamName)) {
                    //  OLD SO OVERWRITE   //
                    UIDialogs.showInfoDialog(this.getClass(), 
                            "Info", 
                            "Values Overwritten", 
                            "You already inserted values for " + selectedParamName + ". They have been overwritten.");
                    UserCustomParam oldCustomParam = paramListHandler.getCurrentSetUserParamList().get(selectedParamName);

                    oldCustomParam.setIsLockedValue(vLockValueCheckBox.isSelected());
                    oldCustomParam.setLockedValue(parseLong(lockedValueString));
                    oldCustomParam.setMin(parseLong(minValueString));
                    oldCustomParam.setMax(parseLong(maxValueString));
                    paramListHandler.getCurrentSetUserParamList().replace(selectedParamName, oldCustomParam);
                } else {
                    //  NEW SO ADD  //
                    UserCustomParam newCustomParam = new UserCustomParam(
                            selectedParamName,
                            parseLong(minValueString),
                            parseLong(maxValueString),
                            parseLong(lockedValueString),
                            vLockValueCheckBox.isSelected()
                    );

                    paramListHandler.getCurrentSetUserParamList().put(selectedParamName, newCustomParam);
                }
            } else {
                //  FUNCTION PARAM MUST BE NEW //
                FunctionUserCustomParam newFunctionCustomParam = new FunctionUserCustomParam(
                        selectedParamName,
                        parseLong(minValueString),
                        parseLong(maxValueString),
                        parseLong(lockedValueString),
                        vLockValueCheckBox.isSelected(),
                        selectedFunction
                );

                if (paramListHandler.getCurrentSetUserParamList().containsKey(selectedParamName)) {
                    //  If param is already set in the list add only the customization for the function //
                    paramListHandler.getCurrentSetUserParamList().get(selectedParamName)
                            .getFunctionCustomParamList().put(selectedFunction, newFunctionCustomParam);
                }
                else{
                    UserCustomParam newCustomParam = new UserCustomParam(
                            selectedParamName,
                            0,
                            0,
                            0,
                            false
                    );
                    
                    newCustomParam.getFunctionCustomParamList().put(selectedFunction, newFunctionCustomParam);
                        
                    paramListHandler.getCurrentSetUserParamList().put(selectedParamName, newCustomParam);
                }
            }
            close();

        }

    }
    
    private long parseLong (String value){
        if (StringUtils.isNumeric(value))
            return Long.parseLong(value);
        else return 0;
    }
    
    public static void startView(FXMLDocumentController mainController) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MenuEdit.class.getResource(
                    "Views/AddCustomValues/AddCustomValuesView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Parameter");
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_64x64.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_32x32.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_16x16.png"));
            stage.setResizable(false);
            stage.setScene(new Scene(root1));
            stage.setOnHiding((WindowEvent event1) -> {
                Platform.runLater(() -> {
                    mainController.populateMainTable();
                    //mainController.mainTableRowSelected();
                });
            });
            stage.show();
        } catch (IOException e) {
            LoggerBoy.logError(AddCustomValuesController.class, e);
        }
    }
    
    public void close() {
        Stage stage = (Stage) vMainPane.getScene().getWindow();
        stage.close();
    }
}