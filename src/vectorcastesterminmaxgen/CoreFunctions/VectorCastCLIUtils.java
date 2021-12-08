package vectorcastesterminmaxgen.CoreFunctions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import vectorcastesterminmaxgen.Configs.ConfigFileReader;
import vectorcastesterminmaxgen.Configs.Constants;
import vectorcastesterminmaxgen.LoggerBoy;

/**
 * This class contains functions to use VectorCast cli commands.
 *
 * @author Roberto Caputo
 * @version 1 24/07/2018
 * @version 2 07/11/2018 Adapted to MINMAXGEN project.
 */
public class VectorCastCLIUtils {

    private String executionResult = new String();
    private boolean isVCLicenseRunning = true;
    private ArrayList<String> commandListToExecute = new ArrayList(); //  This string contains the list of the command to execute in the .bat file//
    private boolean executionSuccess;
    ConfigFileReader configs;

    public VectorCastCLIUtils (){
        configs = ConfigFileReader.getInstance();
    }
    
    /**
     * This Function returns the custom script to start vector cast license server, if the script has been 
     * defined by the user. 
     * Else loads the default script in the resource.
     * @return 
     */
    public String getStartServerLicenseScript() {
        String retValue = new String();
        File customLicenseScript = new File (Constants.START_LICENSE_SERVER_SCRIPT_FILE_PATH);
        
        if (customLicenseScript.exists()){
            try {
                retValue = FileUtils.readFileToString(customLicenseScript, "UTF-8");
            } catch (IOException e) {
                LoggerBoy.logError(this.getClass(), e.getLocalizedMessage());
            }
        }
        if (retValue == null || retValue.isEmpty()) {
        ClassLoader classLoader = getClass().getClassLoader();
        //File customCommandFile = new File(classLoader.getResource("/resources/startvectorcastlicenseserver.txt").getFile());
        
        InputStream inputStream
                = classLoader.getResourceAsStream("startvectorcastlicenseserver.bat");
        //    Convert input stream to string
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        retValue = s.hasNext() ? s.next() : "";
        }
        return retValue;
    }

    public void killVectorCastLicenseServer(){
        commandListToExecute.add("taskkill /IM lmgrd.exe");
    }
    
    /**
     * This is a Thread because license server could require to remain active.
     * TODO: exectuionResult does not work
     *
     * @return
     */
    public boolean isVectorCastLicenseServerRunning() {

        commandListToExecute.add(configs.getVECTORCAST_INSTALL_PATH() + "\\FLEXlm\\lmutil lmstat -A");

        execCommand("", "checkVectorCastLicense");
        if (executionResult.contains("Users") || executionResult.contains("license in use")) {
            //  If i found some string that indicates vector cast license is actually in use    //
            LoggerBoy.logError(VectorCastCLIUtils.class.getName(),
                    "It seems VectorCast is actually opened. Close every instance"
                    + " of VectorCast before running!");
            isVCLicenseRunning = false;
        } else if (!executionResult.contains("server UP") && executionResult.contains("not running")) {
            //  If server not UP and not running    //
            LoggerBoy.logError(VectorCastCLIUtils.class.getName(),
                    "VectorCast license server not running!"
                    + "\nI will try to restart server and reread license. After, you have to rerun latest command");
            commandListToExecute = new ArrayList();
            commandListToExecute.add(configs.getVECTORCAST_INSTALL_PATH() + "\\FLEXlm\\lmgrd");
            commandListToExecute.add(configs.getVECTORCAST_INSTALL_PATH() + "\\FLEXlm\\lmutil reread");
            //  Start Server   adn Reread License //
            execCommand("", "RereadLicense");
            //   Reread License  //
            isVCLicenseRunning = false;
        }

        //custom additions//
        commandListToExecute = new ArrayList();
        commandListToExecute.add(getStartServerLicenseScript());

        return isVCLicenseRunning;

    }

    /**
     * This Function generate inside the VectorCastEnvironment Partition testCases for the specified uuts or the entire
 environment if not specified.
     *
     * @param envDir VectorCast VectorCastEnvironment considered
     * @param EnvironmentName
     * @param uutList
     */
    public void generatePartitionTestCase(File envDir, String EnvironmentName, ArrayList<String> uutList) {
        commandListToExecute.add("cd " + envDir.toString());

        if (!uutList.isEmpty()) {
            for (String uutFile : uutList) {
                commandListToExecute.add(configs.getVECTORCAST_INSTALL_PATH() + Constants.VC_CLI_EXE
                        + Constants.VC_CLI_ENVIRONMENT + EnvironmentName
                        + Constants.VC_CLI_UUT + uutFile
                        + " TESt Partition");
            }
        } else {
            commandListToExecute.add(configs.getVECTORCAST_INSTALL_PATH() + Constants.VC_CLI_EXE
                    + Constants.VC_CLI_ENVIRONMENT + EnvironmentName
                    + " TESt Partition");
        }
    }

    /**
     * This Function export TST for the entire VectorCastEnvironment.
     *
     * @param envDir
     * @param envName
     * @param tstName Name of the tst file to export
     * @return TST exported
     */
    public File exportTST(File envDir, String envName, String tstName){
        File retValue = new File(".");
        try {
            String tstExportedAbsPath = new File(".").getCanonicalPath() + "\\"
                    + new File(Constants.APPDIR_CACHE_PATH + "\\" + tstName + ".tst");
            
            // get current working dir and new tst file //
            
            retValue = new File(tstExportedAbsPath);

            commandListToExecute.add("cd " + envDir.getPath());

            LoggerBoy.logEverywhere(VectorCastCLIUtils.class.getName(), "TST will be exported on: " 
                    + retValue.getPath());

            commandListToExecute.add(configs.getVECTORCAST_INSTALL_PATH() + Constants.VC_CLI_EXE + Constants.VC_CLI_ENVIRONMENT
                    + envName
                    + " TESt Script Create "
                    + tstExportedAbsPath);

        } catch (IOException e) {
            LoggerBoy.logError(this.getClass().getSimpleName(), e.getLocalizedMessage());
        }

        return retValue;

    }

    /**
     * This Function delete TestCases for the entire environment
     *
     * @param envDir VectorCast VectorCastEnvironment considered
     * @param EnvironmentName
     */
    public void deleteEnvironmentTestCases(File envDir, String EnvironmentName) {
        commandListToExecute.add("cd " + envDir.toString());
        commandListToExecute.add(configs.getVECTORCAST_INSTALL_PATH() + Constants.VC_CLI_EXE + Constants.VC_CLI_ENVIRONMENT
                + EnvironmentName
                + " TESt Delete yes");
    }

    /**
     * This Function import TestCases for the entire environment
     *
     * @param envDir VectorCast VectorCastEnvironment considered
     * @param EnvironmentName
     * @param tstToImport
     */
    public void importEnvironmentTestCases(File envDir, String EnvironmentName, File tstToImport) {
//        try {
            String tstToImportAbsPath = /*new File(".").getCanonicalPath() + "\\"
                    + */tstToImport.getPath();
            commandListToExecute.add("cd " + envDir.toString());
            commandListToExecute.add(configs.getVECTORCAST_INSTALL_PATH() + Constants.VC_CLI_EXE + Constants.VC_CLI_ENVIRONMENT
                    + EnvironmentName
                    + " TESt Script Run "
                    + tstToImportAbsPath);
//        } catch (IOException e) {
//            LoggerBoy.logError(this.getClass().getSimpleName(), e.getLocalizedMessage());
//        }
    }

    /**
     * This Function executes TestCases for the entire environment
     *
     * @param envDir VectorCast VectorCastEnvironment considered
     * @param EnvironmentName
     */
    public void executeTestCasesEnvironment(File envDir, String EnvironmentName) {
            commandListToExecute.add("cd " + envDir.toString());
            commandListToExecute.add(configs.getVECTORCAST_INSTALL_PATH() + Constants.VC_CLI_EXE + Constants.VC_CLI_ENVIRONMENT
                    + EnvironmentName
                    + " EXecute Batch [--update_coverage_data]");

    }    
    
    /**
     * This Function execute a VC command and write a log in the cache folder.
     *
     * @param envName VectorCastEnvironment Name used to naming bat file
     * @param cmdType Command Type (Full Report, Scripts ecc...) used to naming
     * .bat file
     * @return True if execution is Successfully else False
     */
    public boolean execCommand(String envName, String cmdType) {
        executionSuccess = true;
        executionResult = new String();
        String commandListForLogging = new String();
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy__HH_mm");
        String dateTimeFormatted = now.format(formatter);
        
        File logFile = new File( Constants.APPDIR_CACHE_PATH + "\\log_" + cmdType + "_"+dateTimeFormatted + ".txt");

        commandListToExecute.add("exit");   //add end command   //
        
//        commandListForLogging = ("@Echo off\r\n"
//                + "SET LOGFILE=" + logFile.getPath()+"\r\n"
//                        + "call :Logit >> %LOGFILE%\r\n"
//                        + "exit /b 0\r\n"
//                        + ":Logit");
//        
//        commandListToExecute.add(0, commandListForLogging);
        
        String batFileContent = StringUtils.join(commandListToExecute, "\r\n");

        commandListToExecute = new ArrayList(); //empty the list    //

        String batFile = Constants.APPDIR_CACHE_PATH + "\\VCGenerator_" + envName + "_" + cmdType + ".bat";
        // Path of the bat file that contains commands that will be executed    //

        try {
            String line;
            FileUtils.write(new File(batFile), batFileContent, "UTF-8");
            String dosCommand = "cmd /c start " + batFile;
            Process p = Runtime.getRuntime().exec(dosCommand);
            LoggerBoy.logEverywhere(this.getClass().getName(), dosCommand);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                FileUtils.write(logFile, line, "UTF-8", true);//append to log file//
                executionResult += (line + "\n");
            }
            input.close();
//            FileUtils.writeLines(logFile, Arrays.asList(executionResult.split("\n")));
        } catch (IOException e) {
            executionSuccess = false;
            LoggerBoy.logError(this.getClass().getSimpleName(), e.getLocalizedMessage());
        }
        return executionSuccess;
    }

}
