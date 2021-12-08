package vectorcastesterminmaxgen.CoreFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import vectorcastesterminmaxgen.Configs.ConfigFileReader;
import vectorcastesterminmaxgen.Configs.Constants;
import static vectorcastesterminmaxgen.Configs.Constants.TESTCASE_NAME_INTORNO_DESTRO;
import static vectorcastesterminmaxgen.Configs.Constants.TESTCASE_NAME_INTORNO_SINISTRO;
import static vectorcastesterminmaxgen.Configs.Constants.TESTCASE_NAME_MAX;
import static vectorcastesterminmaxgen.Configs.Constants.TESTCASE_NAME_MIN;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Model.Parameter;
import vectorcastesterminmaxgen.Model.ParsedTST;
import vectorcastesterminmaxgen.Model.TestCaseTST;
import vectorcastesterminmaxgen.Model.XMLParams.UserCustomParam;

/**
 * This Class generate new TestCase starting from specified params.
 * 
 * @author Roberto Caputo
 * @version 1 05/04/2019
 */
public class YodaTestGenerator{    

    private ParsedTST parsedTST;
    private UserCustomParamHandler userCustomParamHandler;
    
    /**
     * Main Constructor
     * @param parsedTST
     */
    public YodaTestGenerator(ParsedTST parsedTST, UserCustomParamHandler userCustomParamHandler) {
        this.parsedTST = parsedTST;
        this.userCustomParamHandler = userCustomParamHandler;
    }
    
    /**
     * This Function builds all the new TestCases.
     * @return the Dir containing the new testcases
     */
    public File buildNewTestCases() {
        File generatedEnvironmentTSTDir = null;
        try {
            File sourceTSTFile = parsedTST.getReparsedSourceTSTFile();
            
            if (sourceTSTFile.exists()) {
                List<String> sourceTSTFileLines = FileUtils.readLines(sourceTSTFile, "UTF-8");

                generatedEnvironmentTSTDir = new File(sourceTSTFile.getParent() + "\\" + parsedTST.getEnvName());
                if (generatedEnvironmentTSTDir.exists()){
                    FileUtils.deleteDirectory(generatedEnvironmentTSTDir);
                }
                    
                    generatedEnvironmentTSTDir.mkdir();

                //  Generate MIN testcase and write it  //
                List<String> minTSTFileLines = generateMinTST(sourceTSTFileLines);
                FileUtils.writeLines(new File(generatedEnvironmentTSTDir.getPath() + "\\" + parsedTST.getEnvName() 
                        + Constants.TESTCASE_NAME_MIN + "." + Constants.TST_EXTENSIONS[0]),
                        minTSTFileLines);
                //  Generate MAX testcase and write it  //
                List<String> maxTSTFileLines = generateMaxTST(sourceTSTFileLines);
                FileUtils.writeLines(new File(generatedEnvironmentTSTDir.getPath() + "\\" + parsedTST.getEnvName() 
                        + Constants.TESTCASE_NAME_MAX + "." + Constants.TST_EXTENSIONS[0]),
                        maxTSTFileLines);

                //  Generate INTORNO SINISTRO testcase and write it  //
                FileUtils.writeLines(new File(generatedEnvironmentTSTDir.getPath() + "\\" + parsedTST.getEnvName() 
                        + Constants.TESTCASE_NAME_INTORNO_SINISTRO + "." + Constants.TST_EXTENSIONS[0]),
                        generateIntornoSinistroTST(minTSTFileLines));
                //  Generate INTORNO DESTRO testcase and write it  //
                FileUtils.writeLines(new File(generatedEnvironmentTSTDir.getPath() + "\\" + parsedTST.getEnvName() 
                        + Constants.TESTCASE_NAME_INTORNO_DESTRO + "." + Constants.TST_EXTENSIONS[0]),
                        generateIntornoDestroTST(maxTSTFileLines));
            }
            else {
                LoggerBoy.logError(this.getClass(), "Something went wrong during Reparsed TST file");
            }
        } catch (IOException ex) {
            Logger.getLogger(YodaTestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return generatedEnvironmentTSTDir;
    }
    
    /**
     * This Method generate the MIN TST.
     * @param sourceTSTFileLines
     * @return 
     */
    private List<String> generateMinTST(List<String> sourceTSTFileLines) {
        List<String> minTSTFileLines = new ArrayList<>(sourceTSTFileLines);//create a copy of the source list//

        for (int i = 0; i < parsedTST.getTestcaseTSTList().size(); i++) {
            TestCaseTST testcaseTST = parsedTST.getTestcaseTSTList().get(i);

            //   Set new TestCase Name    //
            String newTestCaseNameLine = parseNewTestCaseName(sourceTSTFileLines.get(testcaseTST.getTESTCASENAME_LINE()), testcaseTST, TESTCASE_NAME_MIN);
            minTSTFileLines.set(testcaseTST.getTESTCASENAME_LINE(), newTestCaseNameLine);

            //   Set new Param Value Line for each param    //
            for (Parameter parameter : testcaseTST.getParameterList()) {
                UserCustomParam currentCustomParam = userCustomParamHandler.getCurrentSetUserParamList().get(parameter.getName());
                String newParamValue = Constants.MIN_VALUE;  // use default <<MIN>> value if not specified by the user   //

                if (currentCustomParam != null) {
                    if (currentCustomParam.getFunctionCustomParamList().get(testcaseTST.getFunction()) != null) //  get function custom values  //
                    {
                        currentCustomParam = currentCustomParam.getFunctionCustomParamList().get(testcaseTST.getFunction());
                    }
                    //  If param has been specified by user //
                    //  if param is locked, get param locked, else get param min    //
                    newParamValue = String.valueOf(currentCustomParam.isIsLockedValue() ? currentCustomParam.getLockedValue() : currentCustomParam.getMin());
                }
                
                String newParamValueLine = parseNewParamValueLine(sourceTSTFileLines.get(parameter.getTstLine()), newParamValue);
                minTSTFileLines.set(parameter.getTstLine(), newParamValueLine);
            }
        }
        
        return minTSTFileLines;
    }
    
    /**
     * This Method generate the MAX TST.
     * @param sourceTSTFileLines
     * @return 
     */
    private List<String> generateMaxTST(List<String> sourceTSTFileLines) {
        List<String> maxTSTFileLines = new ArrayList<>(sourceTSTFileLines);//create a copy of the source list//

        for (int i = 0; i < parsedTST.getTestcaseTSTList().size(); i++) {
            TestCaseTST testcaseTST = parsedTST.getTestcaseTSTList().get(i);

            //   Set new TestCase Name    //
            String newTestCaseNameLine = parseNewTestCaseName(sourceTSTFileLines.get(testcaseTST.getTESTCASENAME_LINE()), testcaseTST, TESTCASE_NAME_MAX);
            maxTSTFileLines.set(testcaseTST.getTESTCASENAME_LINE(), newTestCaseNameLine);

            //   Set new Param Value Line for each param    //
            for (Parameter parameter : testcaseTST.getParameterList()) {
                UserCustomParam currentCustomParam = userCustomParamHandler.getCurrentSetUserParamList().get(parameter.getName());
                String newParamValue = Constants.MAX_VALUE;  // use default <<MAX>> value if not specified by the user   //

                if (currentCustomParam != null) {
                    if (currentCustomParam.getFunctionCustomParamList().get(testcaseTST.getFunction()) != null) //  get function custom values  //
                    {
                        currentCustomParam = currentCustomParam.getFunctionCustomParamList().get(testcaseTST.getFunction());
                    }

                    //  If param has been specified by user //
                    //  if param is locked, get param locked, else get param max    //
                    newParamValue = String.valueOf(currentCustomParam.isIsLockedValue() ? currentCustomParam.getLockedValue() : currentCustomParam.getMax());
                }
                
                String newParamValueLine = parseNewParamValueLine(sourceTSTFileLines.get(parameter.getTstLine()), newParamValue);
                maxTSTFileLines.set(parameter.getTstLine(), newParamValueLine);
            }
        }
        
        return maxTSTFileLines;
    }
    
    /**
     * This Method generate the INTORNO SINISTRO testcase.
     * NB: Intorno TestCases will be generated only and only if there are specified params for the specified function
     * @param minTSTFileLines
     * @return 
     */
    private List<String> generateIntornoSinistroTST(List<String> minTSTFileLines) {
        List <String> tempMinTSTFileLines = new ArrayList <>();
        tempMinTSTFileLines.addAll(minTSTFileLines);
            //temporary copy//
        
        List<String> intornoSXTSTFileLines = new ArrayList();
        boolean testCombinatori = ConfigFileReader.getInstance().getBooleanProperty(ConfigFileReader.GENERATE_OPTION_TEST_COMBINATORI);
        int TSTHeaderSize = parsedTST.getTestcaseTSTList().get(0).getSTART_LINE();
        
        //  Copy only the header    //
        for (int i = 0; i < TSTHeaderSize; i++){
            intornoSXTSTFileLines.add(minTSTFileLines.get(i));
        }
        
        for (int i = 0; i < parsedTST.getTestcaseTSTList().size(); i++) {
            boolean generateIntornoTestCaseForCurrentFunction = false;
            TestCaseTST testcaseTST = parsedTST.getTestcaseTSTList().get(i);

            //   Set new Param Value Line for each param    //
            for (Parameter parameter : testcaseTST.getParameterList()) {
                UserCustomParam currentCustomParam = userCustomParamHandler.getCurrentSetUserParamList().get(parameter.getName());
                String newParamValue = new String();
                try {
                    if (currentCustomParam != null) {
                        if (currentCustomParam.getFunctionCustomParamList().get(testcaseTST.getFunction()) != null)
                            //  get function custom values  //
                            currentCustomParam = currentCustomParam.getFunctionCustomParamList().get(testcaseTST.getFunction());
                        
                        generateIntornoTestCaseForCurrentFunction = true;
                        //  If param has been specified by user calculate the Intorno value//
                        //  if param is locked, get param locked, else get param min    //
                        Long minValue = currentCustomParam.isIsLockedValue() ? currentCustomParam.getLockedValue() : currentCustomParam.getMin();
                        newParamValue = String.valueOf(minValue - 1);

                        String newParamValueLine = parseNewParamValueLine(minTSTFileLines.get(parameter.getTstLine()), newParamValue);
                        tempMinTSTFileLines.set(parameter.getTstLine(), newParamValueLine);

                        if (testCombinatori) {
                            //   Set new TestCase Name    //
                            String newTestCaseNameLine = parseNewTestCaseName(
                                    tempMinTSTFileLines.get(testcaseTST.getTESTCASENAME_LINE()), testcaseTST,
                                    "_" + parameter.getName() + TESTCASE_NAME_INTORNO_SINISTRO);
                            tempMinTSTFileLines.set(testcaseTST.getTESTCASENAME_LINE(), newTestCaseNameLine);
                            
                            //  Se devo generare test combinatori (cioè un test per ogni parametro spefcificato)    //
                            
                            for (int j = testcaseTST.getSTART_LINE(); j <= testcaseTST.getEND_LINE(); j++) {
                                //  append the new testcase script to the list  //
                                intornoSXTSTFileLines.add(tempMinTSTFileLines.get(j));
                            }
                            
                            //  Reput the correct list
                            tempMinTSTFileLines = new ArrayList();
                            tempMinTSTFileLines.addAll(minTSTFileLines);
                        }
                    }
                } catch (NumberFormatException e) {
                    LoggerBoy.logError(this.getClass().getSimpleName(), e.getLocalizedMessage());
                }
            }
            
            if (generateIntornoTestCaseForCurrentFunction && !testCombinatori) {
                //  if there are custom parameters set for this function but i dont want test combinatori   //
                //   Set new TestCase Name    //
                String newTestCaseNameLine = parseNewTestCaseName(minTSTFileLines.get(
                        testcaseTST.getTESTCASENAME_LINE()), testcaseTST, TESTCASE_NAME_INTORNO_SINISTRO);
                tempMinTSTFileLines.set(testcaseTST.getTESTCASENAME_LINE(), newTestCaseNameLine);

                for (int j = testcaseTST.getSTART_LINE(); j <= testcaseTST.getEND_LINE(); j++) {
                    //  append the new testcase script to the list  //
                    intornoSXTSTFileLines.add(tempMinTSTFileLines.get(j));
                }
                
                //  Reput the correct list
                tempMinTSTFileLines = new ArrayList();
                tempMinTSTFileLines.addAll(minTSTFileLines);
            }
        }

        return intornoSXTSTFileLines;
    }
    
    /**
     * This Method generate the INTORNO DESTRO testcase.
     * NB: Intorno TestCases will be generated only and only if there are specified params for the specified function
     * @param maxTSTFileLines
     * @return 
     */
    private List<String> generateIntornoDestroTST(List<String> maxTSTFileLines) {
        List<String> tempMaxTSTFileLines = new ArrayList<>();
        tempMaxTSTFileLines.addAll(maxTSTFileLines);
        //temporary copy//
            
        List<String> intornoDXTSTFileLines = new ArrayList();
        boolean testCombinatori = ConfigFileReader.getInstance().getBooleanProperty(ConfigFileReader.GENERATE_OPTION_TEST_COMBINATORI);
        int TSTHeaderSize = parsedTST.getTestcaseTSTList().get(0).getSTART_LINE();
        
        //  Copy only the header    //
        for (int i = 0; i < TSTHeaderSize; i++){
            intornoDXTSTFileLines.add(maxTSTFileLines.get(i));
        }
        
        for (int i = 0; i < parsedTST.getTestcaseTSTList().size(); i++) {
            boolean generateIntornoTestCaseForCurrentFunction = false;
            TestCaseTST testcaseTST = parsedTST.getTestcaseTSTList().get(i);

            //   Set new Param Value Line for each param    //
            for (Parameter parameter : testcaseTST.getParameterList()) {
                UserCustomParam currentCustomParam = userCustomParamHandler.getCurrentSetUserParamList().get(parameter.getName());
                String newParamValue = new String();
                try {
                    if (currentCustomParam != null) {
                        if (currentCustomParam.getFunctionCustomParamList().get(testcaseTST.getFunction()) != null)
                            //  get function custom values  //
                            currentCustomParam = currentCustomParam.getFunctionCustomParamList().get(testcaseTST.getFunction());
                        
                        generateIntornoTestCaseForCurrentFunction = true;
                        //  If param has been specified by user calculate the Intorno value//
                        //  if param is locked, get param locked, else get param MAX    //
                        Long maxValue = currentCustomParam.isIsLockedValue() ? currentCustomParam.getLockedValue() : currentCustomParam.getMax();
                        newParamValue = String.valueOf(maxValue + 1);

                        String newParamValueLine = parseNewParamValueLine(maxTSTFileLines.get(parameter.getTstLine()), newParamValue);
                        tempMaxTSTFileLines.set(parameter.getTstLine(), newParamValueLine);

                        if (testCombinatori) {
                            //   Set new TestCase Name    //
                            String newTestCaseNameLine = parseNewTestCaseName(tempMaxTSTFileLines.get(testcaseTST.getTESTCASENAME_LINE()), testcaseTST,
                                    "_" + parameter.getName() + TESTCASE_NAME_INTORNO_DESTRO);
                            tempMaxTSTFileLines.set(testcaseTST.getTESTCASENAME_LINE(), newTestCaseNameLine);
                            
                            //  Se devo generare test combinatori (cioè un test per ogni parametro spefcificato)    //
                            
                            for (int j = testcaseTST.getSTART_LINE(); j <= testcaseTST.getEND_LINE(); j++) {
                                //  append the new testcase script to the list  //
                                intornoDXTSTFileLines.add(tempMaxTSTFileLines.get(j));
                            }
                            
                            //  Reput the correct list
                            tempMaxTSTFileLines = new ArrayList();
                            tempMaxTSTFileLines.addAll(maxTSTFileLines);                            
                        }
                    }
                } catch (NumberFormatException e) {
                    LoggerBoy.logError(this.getClass().getSimpleName(), e.getLocalizedMessage());
                }
            }
            
            if (generateIntornoTestCaseForCurrentFunction && !testCombinatori) {
                //  if there are custom parameters set for this function but i dont want test combinatori   //
                //   Set new TestCase Name    //
                String newTestCaseNameLine = parseNewTestCaseName(maxTSTFileLines.get(
                        testcaseTST.getTESTCASENAME_LINE()), testcaseTST, TESTCASE_NAME_INTORNO_DESTRO);
                tempMaxTSTFileLines.set(testcaseTST.getTESTCASENAME_LINE(), newTestCaseNameLine);

                for (int j = testcaseTST.getSTART_LINE(); j <= testcaseTST.getEND_LINE(); j++) {
                    //  append the new testcase script to the list  //
                    intornoDXTSTFileLines.add(tempMaxTSTFileLines.get(j));
                }
                
                //  Reput the correct list
                tempMaxTSTFileLines = new ArrayList();
                tempMaxTSTFileLines.addAll(maxTSTFileLines);  
            }
        }

        return intornoDXTSTFileLines;
    }
    
    /**
     * This method generate the new testcase name in the script
     *
     * @param testcaseTSTLine
     * @param testcaseTST
     * @param testcaseNameSuffix
     * @return
     */
    private String parseNewTestCaseName(String testcaseTSTLine, TestCaseTST testcaseTST, String testcaseNameSuffix) {
        String[] lineElements = testcaseTSTLine.split(":");

        return lineElements[0] + ":" + testcaseTST.getFunction() + testcaseNameSuffix;
    }
    
    /**
     * This method generate the new testcase name in the script
     *
     * @param testcaseTSTLine
     * @param testcaseTST
     * @param testcaseNameSuffix
     * @return
     */
    private String parseNewParamValueLine(String testcaseTSTLine, String newValue) {
        String[] lineElements = testcaseTSTLine.split(":");
            //  Search for param if it is a critical value specified by the user //
        
        if (lineElements.length >1)
            return lineElements[0] + ":" + lineElements[1] + ":" + newValue;
        else {
            LoggerBoy.logError(this.getClass(), "Errore di parserizzazione...(TST LINE: " + testcaseTSTLine + ")");
            return null;
        }
    }

}
