package vectorcastesterminmaxgen.CoreFunctions;

import vectorcastesterminmaxgen.CoreFunctions.Parsers.AnakinTSTParser;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import vectorcastesterminmaxgen.Model.ParsedTST;
import vectorcastesterminmaxgen.Model.VectorCastEnvironment;
import vectorcastesterminmaxgen.UIUtils.UIDialogs;
import vectorcastesterminmaxgen.Views.RunParser.FXMLRunParserController;

/**
 * This Class contains Thread definition to prepare enviroment with the CLI of Vectorcast. See run() description to understand the algo.
 *
 * @author Roberto Caputo
 * @version 0.1 08/11/2018
 * 
 */
public class VectorCastPrepareEnvironmentTask extends Thread {

    private File tstExportedFile;
    private VectorCastCLIUtils vectorCastCLIUtils;
    private FXMLRunParserController controller;
    private ArrayList <String> uutList;
    private ActionEvent event;
    
    private ParsedTST parsedTST;
    private VectorCastEnvironment vcEnv;
    private UserCustomParamHandler paramListHandler;

    public VectorCastPrepareEnvironmentTask(ArrayList <String> uutList,
            FXMLRunParserController controller, ActionEvent event, VectorCastEnvironment vcEnv) {
        this.vectorCastCLIUtils = new VectorCastCLIUtils();
        this.controller = controller;
        this.uutList = uutList;
        this.event = event;
        this.paramListHandler = UserCustomParamHandler.getInstance();
        this.vcEnv = vcEnv;
    }

    /**
     * This Function is called when parseEnvironment has been clicked. Algo: 1.
     * Verify VC License Server is correctly running (if not, start) 2. Delete
     * all the testcase in the VectorCastEnvironment 3. Generate Partition
     * TestCases 4. Export TST Script
     */
    @Override
    public void run() {
        String environmentName = vcEnv.getEnvName();
        File envDir = vcEnv.getEnvDir();
        //mainController.lockOrUnlockUI();
        controller.vProgressEnvironment.setVisible(true);
        if (vectorCastCLIUtils.isVectorCastLicenseServerRunning()) {
            //  If VectorCast License Server is ok  //
            vectorCastCLIUtils.deleteEnvironmentTestCases(envDir, environmentName);
            //  Delete All testcases in the environment to avoid renaming of duplicates tc  //
            vectorCastCLIUtils.generatePartitionTestCase(envDir, environmentName, uutList);
            //Generate TESTCASES inside VectorCastEnvironment//

            tstExportedFile = vectorCastCLIUtils.exportTST(envDir, environmentName, environmentName);
            // Export TSTs  //
            vectorCastCLIUtils.killVectorCastLicenseServer();

            if (vectorCastCLIUtils.execCommand(environmentName, "prepareEnv")
                    && tstExportedFile.exists()) {
                AnakinTSTParser anakin = new AnakinTSTParser(tstExportedFile);
                this.parsedTST = anakin.parseTST();
            } else if (!tstExportedFile.exists()) {
                UIDialogs.showErrorDialog(this.getClass(), "Impossibile generare TST da VectorCast",
                         "Potrebbe essere necessario ricompilare l'Environment");
            }

            controller.vProgressEnvironment.setVisible(false);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.parsedTST = parsedTST;
                controller.vcEnv = vcEnv;
                controller.close(event);
            }
        }
        );
    }
}

