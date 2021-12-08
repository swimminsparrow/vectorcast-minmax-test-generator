package vectorcastesterminmaxgen;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This Class contains functions to show UI objects (ex: dialogs) for Edit Menu
 * Items.
 *
 * @author Roberto Caputo
 */
public class MenuEdit {

    public static void showPreferences(FXMLDocumentController controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MenuEdit.class.getResource(
                    "Views/ConfigurationEditor/FXMLConfigurationEditorView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Edit Preferences");
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_64x64.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_32x32.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_16x16.png"));
            stage.setResizable(false);
            stage.setScene(new Scene(root1));
            stage.setOnHiding(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Stage Closed");
                            controller.loadConfigFile();                            
                        }
                    });
                }
            });
            stage.show();
        } catch (Exception e) {
            LoggerBoy.getInstance().logError(MenuEdit.class.getSimpleName(), e.getLocalizedMessage());
        }
    }
}
