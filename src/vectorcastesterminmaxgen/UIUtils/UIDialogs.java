package vectorcastesterminmaxgen.UIUtils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import vectorcastesterminmaxgen.LoggerBoy;

/**
 * This class offers common functions to show simple dialogs.
 *
 * @author Roberto Caputo
 * @version 1 15/11/2018
 */
public class UIDialogs {

    /**
     * This Functions shows an info dialog with logging.
     * @param callingClass
     * @param Title
     * @param Header
     * @param Message
     */
    public static void showInfoDialog(Class callingClass, String Title, String Header, String Message) {
        Platform.runLater(() -> {
            //  Refresh UI  //
            LoggerBoy.logEverywhere(callingClass.getSimpleName(), Header + Message.replace("\n", ", "));
            Label contentLabel = new Label();
            contentLabel.setText(Message);
            VBox dialogContent = new VBox();
            ScrollPane scrollPaneContent = new ScrollPane();
            scrollPaneContent.setContent(contentLabel);
            scrollPaneContent.setMaxHeight(300);
            scrollPaneContent.setMinWidth(300);
            dialogContent.getChildren().add(scrollPaneContent);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Title);
            alert.setHeaderText(Header);
            alert.setContentText(Message);
            alert.getDialogPane().setContent(dialogContent);
            alert.show();
        });
    }
    
    public static void showErrorDialog(Class callingClass, String Header, String Message) {
        Platform.runLater(() -> {
            //  Refresh UI  //
            LoggerBoy.logEverywhere(callingClass.getSimpleName(), Header + Message.replace("\n", ", "));
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(Header);
            alert.setContentText(Message);
            alert.show();
        });
    }
            
    /**
     * This Function build a Confirmation Alert and return it to handle actions.
     * @param callingClass
     * @param Header
     * @param Message
     * @return dialog built
     */
    public static Alert buildConfirmationDialog(Class callingClass, String Header, String Message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Action Required");
        Label contentLabel = new Label();
        contentLabel.setText(Message);
        VBox dialogContent = new VBox();
        ScrollPane scrollPaneContent = new ScrollPane();
        scrollPaneContent.setContent(contentLabel);
        scrollPaneContent.setMaxHeight(300);
        dialogContent.getChildren().add(scrollPaneContent);
        alert.setHeaderText(Header);
        alert.getDialogPane().setContent(dialogContent);
        LoggerBoy.logEverywhere(callingClass.getSimpleName(), Header + Message.replace("\n", ", "));
        return alert;

    }

}
