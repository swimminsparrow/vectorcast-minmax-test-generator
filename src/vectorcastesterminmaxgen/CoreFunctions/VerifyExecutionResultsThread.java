package vectorcastesterminmaxgen.CoreFunctions;

import vectorcastesterminmaxgen.CoreFunctions.Parsers.SpockXMLTCParser;
import vectorcastesterminmaxgen.CoreFunctions.Parsers.AnakinTSTParser;
import vectorcastesterminmaxgen.CoreFunctions.Parsers.KirkTestDataXMLParser;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Platform;
import vectorcastesterminmaxgen.Configs.Constants;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Model.ParsedTST;
import vectorcastesterminmaxgen.Model.TestCaseComparison;
import vectorcastesterminmaxgen.Model.TestCaseXML;
import vectorcastesterminmaxgen.Model.VectorCastEnvironment;
import vectorcastesterminmaxgen.UIUtils.UIDialogs;
import vectorcastesterminmaxgen.UIUtils.UIProgressDialogThread;
import vectorcastesterminmaxgen.Utility.BrowseFileorFolderVCExtension;
import vectorcastesterminmaxgen.Views.VerifyExecutionResults.FXMLVerifyExecutionResultsController;

/**
 * This Thread simply start a thread for SpockXMLTCParser.
 *
 * @author Roberto Caputo
 * @created 29/11/2018
 * @version 0.2
 *
 */
public class VerifyExecutionResultsThread extends Thread {

    private final VectorCastCLIUtils vectorCastCLIUtils;
    private final FXMLVerifyExecutionResultsController controller;
    private VectorCastEnvironment vcEnv;
    private ArrayList <TestCaseComparison> comparedTestcaseList;

    
    public VerifyExecutionResultsThread(FXMLVerifyExecutionResultsController controller, VectorCastEnvironment vcEnv) {
        this.controller = controller;
        this.vectorCastCLIUtils = new VectorCastCLIUtils();

        this.vcEnv = vcEnv;
    }

    @Override
    public void run() {
        
        File envDir = vcEnv.getEnvDir();
        if (BrowseFileorFolderVCExtension.verifyIfEnvironmentFolder(envDir)) {
            SpockXMLTCParser spock = new SpockXMLTCParser(vcEnv);
            vcEnv = spock.scanEnvironment();
            KirkTestDataXMLParser kirk = new KirkTestDataXMLParser(vcEnv);
            vcEnv = kirk.scanEnvironmentForTestCaseDataXML();

            compareExecutionResults();
            
            UIProgressDialogThread progressDialogThread = new UIProgressDialogThread("Exporting TST for: ", 0, true);
            //indeterminate spinning

            progressDialogThread.updateDialog(vcEnv.getEnvName());
            //Update Dialog //


            if (vectorCastCLIUtils.isVectorCastLicenseServerRunning()) {
                //  If VectorCast License Server is ok  //
                if (BrowseFileorFolderVCExtension.verifyIfEnvironmentFolder(envDir)) {
                    String tstFileName = vcEnv.getEnvName() + "_to_verify";
                    File tstFile = vectorCastCLIUtils.exportTST(vcEnv.getEnvDir(), vcEnv.getEnvName(), tstFileName);

                    vectorCastCLIUtils.killVectorCastLicenseServer();
                    if (vectorCastCLIUtils.execCommand(vcEnv.getEnvName(), "exportTST")) {
                        LoggerBoy.logEverywhere(this.getClass(), "TST Correctly exported to " + tstFile.getPath());
                        
                        AnakinTSTParser anakin = new AnakinTSTParser(tstFile);
                        ParsedTST parsedTST = anakin.parseTST();
                        
                        if (vcEnv != null) {
                            Platform.runLater(() -> {
                                controller.populateMainTable(comparedTestcaseList, vcEnv, parsedTST);
                            });
                        } else {
                            UIDialogs.showErrorDialog(this.getClass(), "No TestCase Found in:", envDir.getPath());
                        }
                    }
                }
            }
            progressDialogThread.closeDialog();
        }
    }
    
    /**
     * This Function compares effective execution results with my expected execution results.
     */
    private void compareExecutionResults() {
        comparedTestcaseList = new ArrayList ();
        for (TestCaseXML testCaseXML : vcEnv.getTestcaseXMLList()) {
            TestCaseComparison comparedTestCase = new TestCaseComparison(testCaseXML);
            
            //  EXPECTED EXECUTION RESULT   //
            if (comparedTestCase.getTestcaseName().endsWith(Constants.TESTCASE_NAME_INTORNO_DESTRO)
                    || comparedTestCase.getTestcaseName().endsWith(Constants.TESTCASE_NAME_INTORNO_SINISTRO)) {
                    comparedTestCase.setExpectedExecutionResult(TestCaseComparison.EXECUTION_RESULTS.FAILED);
            }
            else comparedTestCase.setExpectedExecutionResult(TestCaseComparison.EXECUTION_RESULTS.PASSED);

            //  Compare expected results with effective execution results   //
            if (comparedTestCase.getEXECUTION_RESULT() != comparedTestCase.getExpectedExecutionResult())
                comparedTestCase.setIsExecutionAndExpectedResultsMatching("NO");
            else comparedTestCase.setIsExecutionAndExpectedResultsMatching("YES");
            
            comparedTestcaseList.add(comparedTestCase);
        }
    }
    
}
