package vectorcastesterminmaxgen;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;
import vectorcastesterminmaxgen.CoreFunctions.UserCustomParamHandler;
import vectorcastesterminmaxgen.Model.ParsedTST;

/**
 * This Class contains functions to show UI objects (ex: dialogs) for Help Menu
 * Items.
 *
 * @author Roberto Caputo
 */
public class MenuView {

    public static void showReports(ParsedTST parsedTST) {
        UserCustomParamHandler paramListHandler = UserCustomParamHandler.getInstance();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //  Refresh UI  //
// Create the custom dialog.
                Dialog<Pair<String, String>> dialog = new Dialog<>();
                dialog.setTitle("Reports Window");
                dialog.setHeaderText("Environment: " + parsedTST.getEnvName());
                dialog.setWidth(300);

                VBox vBox = new VBox();

                Font boldFont = Font.font(null, FontWeight.BOLD, 12);
                        
                HBox hBox1 = new HBox();
                Label l1 = new Label();
                l1.setText("Functions: ");
                l1.setFont(boldFont);
                Label l2 = new Label();
                l2.setText(String.valueOf(parsedTST.getSubProgramList().size()));
                hBox1.getChildren().addAll(l1, l2);
                
                HBox hBox2 = new HBox();
                Label l3 = new Label();
                l3.setText("TestCases: ");
                l3.setFont(boldFont);
                Label l4 = new Label();
                l4.setText(String.valueOf(parsedTST.getTestcaseTSTList().size()));
                hBox2.getChildren().addAll(l3, l4);    
                
                HBox hBox3 = new HBox();
                Label l5 = new Label();
                l5.setText("Input Values (DISTINCT): ");
                l5.setFont(boldFont);
                Label l6 = new Label();
                l6.setText(String.valueOf(paramListHandler.getCurrentSetUserParamList().size()));
                hBox3.getChildren().addAll(l5, l6);    

                
                vBox.getChildren().addAll(hBox1, hBox2, hBox3);
                vBox.fillWidthProperty();
                vBox.setSpacing(5);

                dialog.getDialogPane().setContent(vBox);
                dialog.getDialogPane().getButtonTypes().addAll(
                        ButtonType.CLOSE
                );
                dialog.show();
            }
        });
    }

    public static void showLogWindow(FXMLDocumentController controller) {
        controller.vLoggerTitledPane.setExpanded(!controller.vLoggerTitledPane.isExpanded());
    }
}
