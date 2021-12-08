package vectorcastesterminmaxgen;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vectorcastesterminmaxgen.Views.BuildTestCases.BuildTestCasesController;
import vectorcastesterminmaxgen.Views.RunParser.FXMLRunParserController;
import vectorcastesterminmaxgen.Views.VerifyExecutionResults.FXMLVerifyExecutionResultsController;

/**
 * This Class contains functions to show UI objects (ex: dialogs) for Edit Menu
 * Items.
 *
 * @author Roberto Caputo
 */
public class MenuRun {

    public static void showRunParser(FXMLDocumentController controller) {
        try {
            controller.lockUI();
            FXMLLoader fxmlLoader = new FXMLLoader(MenuRun.class.getResource(
                    "Views/RunParser/FXMLRunParserView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Run Parser");
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_64x64.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_32x32.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_16x16.png"));
            stage.setX(0);
            stage.setY(0);
            stage.setResizable(false);
            stage.setScene(new Scene(root1));
            stage.setOnShown((WindowEvent event) -> {
                Platform.runLater(() -> {
                    controller.lockUI();
                });
            });  
            stage.setOnHiding((WindowEvent event) -> {
                Platform.runLater(() -> {
                    LoggerBoy.logEverywhere(MenuRun.class.getSimpleName(), "Stage Closed");
                    FXMLRunParserController childController = fxmlLoader.<FXMLRunParserController>getController();
                    
                    //  verify if something has been parsed //
                    controller.onCloseRunParserController(childController.parsedTST, childController.vcEnv);
                    controller.unLockUI();
                });
            });
            stage.setOnCloseRequest((WindowEvent event) -> {
                Platform.runLater(() -> {
                    //LoggerBoy.logEverywhere(MenuRun.class.getSimpleName(), "Stage Closed");
                    controller.unLockUI();
                });
            });
            stage.show();
            stage.centerOnScreen();
        } catch (IOException e) {
            LoggerBoy.getInstance().logError(MenuRun.class.getSimpleName(), e.getLocalizedMessage());
        }
    }

    public static void showBuildTestCasesView(FXMLDocumentController controller) {
        try {
            controller.lockUI();
            FXMLLoader fxmlLoader = new FXMLLoader(MenuRun.class.getResource(
                    "Views/BuildTestCases/BuildTestCasesView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Generate Boundary Test Cases");
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_64x64.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_32x32.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_16x16.png"));
            stage.setResizable(false);
            stage.setScene(new Scene(root1));
            BuildTestCasesController childController = fxmlLoader.<BuildTestCasesController>getController();
            childController.initParsedTST(controller.parsedTST);
            childController.initVectorCastEnvironment(controller.vcEnv);
            stage.setOnShown((WindowEvent event) -> {
                Platform.runLater(() -> {
                    controller.lockUI();
                });
            });  
            stage.setOnHiding((WindowEvent event) -> {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //LoggerBoy.logEverywhere(MenuRun.class.getSimpleName(), "Stage Closed");
                        controller.unLockUI();
                    }
                });
            });
            stage.show();
        } catch (IOException e) {
            LoggerBoy.logError(MenuRun.class.getSimpleName(), e.getLocalizedMessage());
        }
    }

    public static void showVerifyExecutionResults(FXMLDocumentController controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MenuRun.class.getResource(
                    "Views/VerifyExecutionResults/FXMLVerifyExecutionResultsView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Verify Execution Results");
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_64x64.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_32x32.png"));
            stage.getIcons().add(new Image("resources/icon/VectorCastAssistant_16x16.png"));
            stage.setScene(new Scene(root1));
            FXMLVerifyExecutionResultsController childController = fxmlLoader.<FXMLVerifyExecutionResultsController>getController();
            childController.initVectorCastEnvironment(controller.vcEnv);
            childController.startVerification();
            stage.setOnShown((WindowEvent event) -> {
                Platform.runLater(() -> {
                    controller.lockUI();
                });
            });            
            stage.setOnHiding((WindowEvent event) -> {
                Platform.runLater(() -> {
                    //LoggerBoy.logSilently(MenuRun.class.getName(), "Stage Closed");
                    controller.unLockUI();
                });
            });
            stage.show();
            stage.setMaximized(true);

        } catch (IOException e) {
            LoggerBoy.logError(MenuRun.class.getSimpleName(), e.getLocalizedMessage());
        }
    }

}
