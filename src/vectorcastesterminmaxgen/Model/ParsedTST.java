package vectorcastesterminmaxgen.Model;

import java.io.File;
import java.util.ArrayList;


/**
 * This class stores information related to Parsed TST File.
 *
 * @author Roberto Caputo
 * @version 1 08/04/2019 
 *
 */

public class ParsedTST extends AbstractParsedInfo{    
        
    private File sourceTSTFile;
    private File reparsedSourceTSTFile;
    private ArrayList <TestCaseTST> testcaseTSTList;
    
    public ParsedTST (File sourceTSTFile){
        this.sourceTSTFile = sourceTSTFile;
    }

    public File getSourceTSTFile() {
        return sourceTSTFile;
    }

    public void setSourceTSTFile(File sourceTSTFile) {
        this.sourceTSTFile = sourceTSTFile;
    }

    public File getReparsedSourceTSTFile() {
        return reparsedSourceTSTFile;
    }

    public void setReparsedSourceTSTFile(File reparsedSourceTSTFile) {
        this.reparsedSourceTSTFile = reparsedSourceTSTFile;
    }

    public ArrayList<TestCaseTST> getTestcaseTSTList() {
        return testcaseTSTList;
    }

    public void setTestcaseTSTList(ArrayList<TestCaseTST> testcaseTSTList) {
        this.testcaseTSTList = testcaseTSTList;
    }
    
    public int getNumOfOccurencesForParamName(String paramName) {
        int numOccurences = 0;
        for (TestCaseTST testcaseTST : testcaseTSTList) {
            for (Parameter parameter : testcaseTST.getParameterList()) {
                if (parameter.getName().equals(paramName)) {
                    numOccurences++;
                }
            }
        }
        return numOccurences;
    }
    
    public boolean isFunctionNameInTST(String functionName) {
        boolean isFunctionNameInTST = false;

        for (TestCaseTST testcaseTST : testcaseTSTList) {
            if (testcaseTST.getFunction().equals(functionName)) {
                isFunctionNameInTST = true;
                break;
            }
        }
        return isFunctionNameInTST;
    }
    

}
