package vectorcastesterminmaxgen.CoreFunctions;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import vectorcastesterminmaxgen.Configs.ConfigFileReader;
import vectorcastesterminmaxgen.Configs.Constants;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Model.VectorCastEnvironment;
import vectorcastesterminmaxgen.Utility.BrowseFileorFolder;
import vectorcastesterminmaxgen.Utility.BrowseFileorFolderVCExtension;
import vectorcastesterminmaxgen.Views.BuildTestCases.BuildTestCasesController;

/**
 * This Class contains Thread definition to finalize enviroment with the CLI of
 * Vectorcast. See run() description to understand the algo.
 *
 * @author Roberto Caputo
 * @version 0.1 08/11/2018
 *
 */
public class VectorCastFinalizeEnvironment extends Thread {

    private VectorCastCLIUtils vectorCastCLIUtils;
    private VectorCastEnvironment vcEnv;
    private File tstDirToImport;
    private BuildTestCasesController controller;

    public VectorCastFinalizeEnvironment(VectorCastEnvironment vcEnv, File tstDirToImport,
            BuildTestCasesController controller) {
        this.vectorCastCLIUtils = new VectorCastCLIUtils();
        this.vcEnv = vcEnv;
        this.tstDirToImport = tstDirToImport;
        this.controller = controller;
    }

    /**
     * This Function is called when parseEnvironment has been clicked. Algo: 1.
     * Verify VC License Server is correctly running (if not, start) 2. Verify
     * if folder contains an Environment 3. Delete all the testcase in the
     * Environment 4. Import the new TSTs generated into the Environment
     */
    @Override
    public void run() {
        ConfigFileReader configFileReader = ConfigFileReader.getInstance();
        File envDir = vcEnv.getEnvDir();
        String envName = vcEnv.getEnvName();
        
        Instant beginningTime = Instant.now();
        controller.lockOrUnlockUI();
        if (vectorCastCLIUtils.isVectorCastLicenseServerRunning()) {
            controller.vProgressRun.setVisible(true);
            //  If VectorCast License Server is ok  //
            if (BrowseFileorFolderVCExtension.verifyIfEnvironmentFolder(envDir)) {
                vectorCastCLIUtils.deleteEnvironmentTestCases(envDir, envName);
                //  Delete All testcases in the environment to avoid renaming of duplicates tc  //

                // NOW add import tst command for all the tsts in the directory //  
                Collection<File> tstFileList = BrowseFileorFolder.getListOfFilesInFolder(tstDirToImport,
                        Constants.TST_EXTENSIONS);
                for (File tstFile : tstFileList){
                    vectorCastCLIUtils.importEnvironmentTestCases(envDir, envName, tstFile);
                }

                if (configFileReader.getBooleanProperty(ConfigFileReader.GENERATE_OPTION_EXECUTE_TESTCASES)){
                    // do the user wants to execute tcs?//
                    vectorCastCLIUtils.executeTestCasesEnvironment(envDir, envName);
                }

                vectorCastCLIUtils.killVectorCastLicenseServer();

                controller.vProgressRun.setVisible(false);

                if (vectorCastCLIUtils.execCommand(envName, "finalizeEnv")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //  Refresh UI  //
                            Instant elapsedTime = Instant.now();
                            String elapsedTimeMessage = "Elapsed Time: ";

                            if (Duration.between(beginningTime, elapsedTime).toMinutes() > 0)
                                elapsedTimeMessage += Duration.between(beginningTime, elapsedTime).toMinutes() + " minutes";
                            else
                                elapsedTimeMessage += (Duration.between(beginningTime, elapsedTime).toMillis()/1000) + " seconds";
                            
                            LoggerBoy.logEverywhere(this.getClass().getName(), elapsedTimeMessage);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Job Done!");
                            alert.setHeaderText(elapsedTimeMessage);
                            alert.setContentText("I have imported all the new testcases in your environment!");
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                controller.close();
                            }
                        }
                    });
                }
            }
        }
        controller.lockOrUnlockUI();
    }

}
