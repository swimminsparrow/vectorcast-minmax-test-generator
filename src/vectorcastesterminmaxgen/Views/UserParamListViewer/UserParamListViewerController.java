package vectorcastesterminmaxgen.Views.UserParamListViewer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import org.apache.commons.lang3.StringUtils;
import vectorcastesterminmaxgen.CoreFunctions.UserCustomParamHandler;
import vectorcastesterminmaxgen.Model.XMLParams.FunctionUserCustomParam;

/**
 * FXML Controller class
 * 
 *
 * @author Roberto Caputo
 */

//TODO
public class UserParamListViewerController implements Initializable {

    @FXML
    private TreeTableView<FunctionUserCustomParam> vMainTable;

    @FXML
    private TreeTableColumn<FunctionUserCustomParam, String> vParamNameCol;

    @FXML
    private TreeTableColumn<FunctionUserCustomParam, String> vLockedValueCol;

    @FXML
    private TreeTableColumn<FunctionUserCustomParam, String> vMinCol;

    @FXML
    private TreeTableColumn<FunctionUserCustomParam, String> vMaxCol;

    @FXML
    private TreeTableColumn<FunctionUserCustomParam, String> vCustomFunctionCol;

    @FXML
    private CheckBox vSelectAllCBox;

    @FXML
    private Button vCancelButton;

    @FXML
    private Button vRunButton;

    @FXML
    private ProgressIndicator vProgressRun;
    
    
    private UserCustomParamHandler userParamHandler;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userParamHandler = UserCustomParamHandler.getInstance();
    }    
    
//    private void populateMainTable() {
//        vMainTable.getColumns().clear();
//        
//        for (String paramName : userParamHandler.getCurrentUserParamList().keySet()) {
//            //  For each param name of the XML //
//            FunctionUserCustomParam userCustomParam = new FunctionUserCustomParam(
//                    userParamHandler.getCurrentUserParamList().get(paramName));
//            userCustomParam.setFunctionName("[" 
//                    + String.valueOf(userCustomParam.getFunctionCustomParamList().size()) 
//                    + "]"); 
//                //it's a fake assignment
//            
//
//            if (userCustomParam.isCurrentlyUsing()) {
//                //  Add the param to the table <=> the param is parsed in current environment   //
//                TreeItem<FunctionUserCustomParam> paramRootTreeItem = new TreeItem<>(userCustomParam);
//                paramRootTreeItem.setExpanded(true);
//
//                for (String function : userCustomParam.getFunctionCustomParamList().keySet()) {
//                    //  For each subprogram create child root element   //
//                    FunctionUserCustomParam childCustomParam = userCustomParam.getFunctionCustomParamList().get(function);
//
//                    if (childCustomParam.isCurrentlyUsing()){
//                        //  Add the param to the table <=> the param is parsed in current environment   //
//                        TreeItem<FunctionUserCustomParam> childTreeItem = new TreeItem<>(childCustomParam);
//                        paramRootTreeItem.getChildren().add(childTreeItem);
//                    }
//                }
//            }
//
//            vParamNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FunctionUserCustomParam, String> cellValue)
//                    -> new ReadOnlyStringWrapper(cellValue.getValue().getValue().getName())
//            );
//
//            vLockedValueCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FunctionUserCustomParam, String> cellValue)
//                    -> new ReadOnlyStringWrapper(String.valueOf(cellValue.getValue().getValue().getLockedValue()))
//            );
//
//            vMinCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FunctionUserCustomParam, String> cellValue)
//                    -> new ReadOnlyStringWrapper(String.valueOf(cellValue.getValue().getValue().getMin())));
//
//            vMaxCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FunctionUserCustomParam, String> cellValue)
//                    -> new ReadOnlyStringWrapper(String.valueOf(cellValue.getValue().getValue().getMax())));
//
//            vCustomFunctionCol.setCellFactory((TreeTableColumn<FunctionUserCustomParam, String> cellValue)
//                    -> new TreeTableCell<FunctionUserCustomParam, String>() {
//                @Override
//                protected void updateItem(String item, boolean empty) {
//                    if (item != null && !empty) {
//                        String cellText = new String();
//                        
//                        int currentIndex = indexProperty()
//                                .getValue() < 0 ? 0
//                                        : indexProperty().getValue();
//                        
//                        cellText = String.valueOf(
//                                    cellValue.getTreeTableView().getTreeItem(currentIndex).getValue().getFunctionName());
//                        
//                        if (StringUtils.isNumeric(cellText.replace("]", cellText.replace("[", cellText))))
//                            setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");  
//
//                        setText(cellText);
//                    } else {
//                        setStyle("");
//                        setText(null);
//                    }
//                }
//            });
//
//            //vMainTable.ad(userCustomParam);
//            vMainTable.getColumns().setAll(vParamNameCol, vLockedValueCol, vMinCol, vMaxCol, vCustomFunctionCol);
//
//        }
//
//    }   
    
}
