package vectorcastesterminmaxgen.CoreFunctions.Parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import vectorcastesterminmaxgen.Configs.ConfigFileReader;
import vectorcastesterminmaxgen.Configs.Constants;
import vectorcastesterminmaxgen.CoreFunctions.UserCustomParamHandler;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Model.Parameter;
import vectorcastesterminmaxgen.Model.ParsedTST;
import vectorcastesterminmaxgen.Model.TestCaseTST;
import vectorcastesterminmaxgen.UIUtils.UIDialogs;
import vectorcastesterminmaxgen.UIUtils.UIProgressDialogThread;

/**
 * This Class parses a TST containing Exported VectorCast TestCases Script for an Environment.
 * 
 * @author Roberto Caputo
 * @version 1 30/10/2018
 * @version 2 04/12/2018
 * @version 3 08/04/2019 Forked and rewritten.
 */
public class AnakinTSTParser{    

    private File sourceTSTFile;
    private UserCustomParamHandler paramListHandler;
    
    /**
     * Main Constructor
     * @param sourceTSTFile
     */
    public AnakinTSTParser(File sourceTSTFile) {
        this.sourceTSTFile = sourceTSTFile;
        this.paramListHandler = UserCustomParamHandler.getInstance();
    }
    
    public ParsedTST parseTST() {
        ParsedTST parsedTST = new ParsedTST (sourceTSTFile);
        
        try {
            SortedMap <String, ArrayList<String>> subProgramMapList = new TreeMap <>();// KEY: UUT, VALUE: SUBPROGRAM
            String uutName = new String();
            ArrayList <String> subProgramList = new ArrayList();
            ArrayList <TestCaseTST> parsedTCList = new ArrayList();
            File tstTCScriptParsed;

            int numInputsOfSubProgram = 0;  // this counter is updated wvery time i found a input value for a subprogram
            //  i use this to delete testcases for no input values  //
            int rowStartSubProgramTestCase = 0;
            //  This int contains the row index containing --SubProgram //

            List<String> scriptLines = FileUtils.readLines(sourceTSTFile, "UTF-8");
            
            UIProgressDialogThread progressDialogThread = new UIProgressDialogThread("Parsing Script ", 
                    scriptLines.size(), false);

            progressDialogThread.updateDialog(sourceTSTFile.getPath());
            //Update Dialog //

            LoggerBoy.logEverywhere(this.getClass(),
                    "Starting Parsing TST File: " + sourceTSTFile.getAbsolutePath());

            TestCaseTST parsedTC = new TestCaseTST();

            //  read File lines in a List of Strings //
            for (int i = 0; i < scriptLines.size(); i++) {
                //  for each line of the script file    //
                String[] lineElements = StringUtils.split(scriptLines.get(i), ":");
                //   separator in each line to qualify attribute and attribute content is ":"    //

                if (lineElements.length > 0) {
                    String element_0 = StringUtils.deleteWhitespace(StringUtils.chomp(lineElements[0].trim()));
                    String element_1 = new String();
                    String element_2 = new String();
                    if (lineElements.length > 1) {
                        element_1 = StringUtils.deleteWhitespace(StringUtils.chomp(lineElements[1].trim()));
                        if (lineElements.length > 2) {
                            element_2 = StringUtils.deleteWhitespace(StringUtils.chomp(lineElements[2].trim()));
                        }
                    }

                    if (StringUtils.equals(element_0, "--Environment")){
                        parsedTST.setEnvName(element_1);
                    } else if (StringUtils.equals(element_0, "--Unit")){
                        //  UUT START   //
                        uutName = element_1;
                    } else if (StringUtils.equals(element_0, "--Subprogram")){
                        //  SUBPROGRAM START   //
                        subProgramList.add(element_1);
                        rowStartSubProgramTestCase = i;
                    } else if (StringUtils.equals(element_0, "--TestCase")) {
                        //  TESTCASE START  //
                        parsedTC = new TestCaseTST();
                        parsedTC.setSTART_LINE(i);                        
                    } else if (StringUtils.equals(element_0, "TEST.END")) {
                        //  TESTCASE END    //
                        parsedTC.setEND_LINE(i);
                        
                        //delete TC if subprogram is of the type foo(void)//
                        if (numInputsOfSubProgram == 0 
                                && ConfigFileReader.getInstance().getBooleanProperty(ConfigFileReader.PARSER_OPTION_DELETE_VOID_FUNCTIONS)) {
                            int j = rowStartSubProgramTestCase;
                            while (j <= i){
                                scriptLines.remove(rowStartSubProgramTestCase);
                                j++;
                            }
                            i = rowStartSubProgramTestCase; //  move seek reader    //
                                    
                            LoggerBoy.logEverywhere(this.getClass().getSimpleName(),
                                    "Removed function void: " + parsedTC.getTestcaseName());
                        }
                        else {
                            parsedTCList.add(parsedTC);
                            subProgramMapList.put(uutName, subProgramList);
                            uutName = new String();
                            subProgramList.clear();
                        }

//                        int indexOfXMLTestCase = parsedTST.indexOfTestCase(parsedTC.getUUT_NAME(), parsedTC.getFUNCTION_NAME(), parsedTC.getTESTCASE_NAME());
//                        if (indexOfXMLTestCase >= 0) {
//                            TestCaseXML tc = parsedTST.getTestcaseXMLList().get(indexOfXMLTestCase);
//                            //tc.setPARAMETERS(parsedTC.getPARAMETERS());
//                            parsedTST.getTestcaseXMLList().set(indexOfXMLTestCase, tc);
//                        }
                    } else if (StringUtils.equals(element_0, "TEST.UNIT")) {
                        //  UUT //
                        parsedTC.setUUT_LINE(i);
                        parsedTC.setUut(element_1);
                    } else if (StringUtils.equals(element_0, "TEST.SUBPROGRAM")) {
                        // FUNCTION    //
                        parsedTC.setFUNCTION_LINE(i);
                        parsedTC.setFunction(element_1);
                        numInputsOfSubProgram = 0; //initialize counter for subprogram param input//
                    } else if (StringUtils.equals(element_0, "TEST.NAME")) {
                        //  TESTCASE NAME   //
                        parsedTC.setTESTCASENAME_LINE(i);
                        parsedTC.setTestcaseName(element_1);
                    } else if (StringUtils.equals(element_0, "TEST.VALUE")
                            && !(StringUtils.contains(element_1, "<<OPTIONS>>"))
                            && !(StringUtils.contains(StringUtils.deleteWhitespace(lineElements[2].trim()), "<<malloc"))) {
                        //  TESTCASE PARAM  //
                        String temp = element_1.trim().replace(".", "_");
                        List<String> testValueLine = Arrays.asList(temp.trim().split("_"));
                        //Split all elements referred to name of paramater (uut.subprogram.parameter)//

                        String paramName = new String();
                        if (ConfigFileReader.getInstance().getBooleanProperty(ConfigFileReader.PARSER_OPTION_EXPLODE_STRUCT)) {
                            int indexOfSubProgram = testValueLine.indexOf(parsedTC.getFunction());
                            //it should return -1 if no occurences found //
                            paramName = StringUtils.join(testValueLine.subList(indexOfSubProgram + 1, testValueLine.size()), ".");
                            // the name of the param should be struct.paramofthestruct  //
                        } else {
                            paramName = testValueLine.get(testValueLine.size() - 1).split(":")[0];
                            //Param Name should be the last of the list //
                        }

                        if (!StringUtils.equals(testValueLine.get(testValueLine.size() - 1).split(":")[0], "return")) {
                            // Now save the row number in the file  //
                            parsedTC.addParameter(new Parameter(paramName, element_2, i));
                            paramListHandler.addParsedFunctionParam(paramName, parsedTC.getFunction());
                            //  increment num of inputs for subprogram  only if it is not return value//
                            numInputsOfSubProgram++;
                        }
                        else if (StringUtils.equals(testValueLine.get(testValueLine.size() - 1).split(":")[0], "return")){
                            // if return value delete it    //
                            scriptLines.remove(i);
                            i--;//stay on the actual index at the nextcycle //
                        }
                        
                    }
                }
                
                progressDialogThread.updateDialog(sourceTSTFile.getPath());

            }
            
            parsedTST.setSubProgramList(subProgramMapList);
            parsedTST.setTestcaseTSTList(parsedTCList);
            
            paramListHandler.initCurrentSetUserParamList();
            
            //  Write the reparsed tst with unused testcases eventually (es. function void) removed    //
            tstTCScriptParsed = new File(parsedTST.getSourceTSTFile().getParent() + "\\" + 
                    FilenameUtils.getBaseName(parsedTST.getSourceTSTFile().getName()) + Constants.REPARSED_TST_FILE);
            parsedTST.setReparsedSourceTSTFile(tstTCScriptParsed);
            
            FileUtils.writeLines(tstTCScriptParsed, scriptLines);
        
            UIDialogs.showInfoDialog(this.getClass(),
                    "TST Successfully parsed!",
                    "Summary",
                    "TestCases: " + parsedTST.getTestcaseTSTList().size() + "\n"
                    + "Parameters: " + paramListHandler.getParsedUserParamList().size());
            
            progressDialogThread.closeDialog();
            //Close Progress Dialog // 
            
        } catch (IOException e) {
            LoggerBoy.logError(this.getClass().getSimpleName(), e.getLocalizedMessage());
        }
        
        return parsedTST;
    }
}
