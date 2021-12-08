package vectorcastesterminmaxgen;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This Class contains functions to show UI objects (ex: dialogs) for Help Menu Items.
 * @author Roberto Caputo
 */
public class MenuHelp {
    
    public static void showAbout(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About " + MenuHelp.class.getPackage().getImplementationTitle());
        alert.setHeaderText("Version: " + MenuHelp.class.getPackage().getImplementationVersion());
        alert.setContentText("Author: Roberto Caputo (roberto.caputo@intecs.it)\nCredits:\n"
                + "- Image Source Attributions:\n"
                + "\thttps://www.freeiconspng.com/img/2271\n"
                + "\tRemove Simple Png (https://www.freeiconspng.com/img/7117)\n"
                + "\tAdd Icon (https://www.freeiconspng.com/img/2468)\n"
                +"\n-Special Thanks to: \n\tLuca Nastro");

        alert.showAndWait();
    }
    
    public static void showUserManual(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MenuRun.class.getResource(
                    "Views/Help/FXMLHelpView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("User Manual");
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_64x64.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_32x32.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_16x16.png"));
            stage.setX(0);
            stage.setY(0);
            stage.setResizable(false);
            stage.setScene(new Scene(root1));
            stage.setOnHiding((WindowEvent event) -> {
                Platform.runLater(() -> {
                    LoggerBoy.logEverywhere(MenuHelp.class.getSimpleName(), "Stage Closed");
                });
            });
            stage.show();
            stage.centerOnScreen();
        } catch (Exception e) {
            LoggerBoy.logError(MenuRun.class.getSimpleName(), e.getLocalizedMessage());
        }
    }
}
