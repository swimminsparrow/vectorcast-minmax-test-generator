package vectorcastesterminmaxgen.Model;

import java.io.File;
import java.util.ArrayList;


/**
 * This class stores information related to a VectorCastEnvironment.
 *
 * @author Roberto Caputo
 * @version 1 05/04/2019
 *
 */

public class VectorCastEnvironment extends AbstractParsedInfo{    
        
    private File envDir;
    
    private ArrayList <TestCaseXML> testcaseXMLList;
    
    public VectorCastEnvironment (File envDir){
        this.envDir = envDir;
    }

    public File getEnvDir() {
        return envDir;
    }

    public void setEnvDir(File envDir) {
        this.envDir = envDir;
    }

    public ArrayList<TestCaseXML> getTestcaseXMLList() {
        return testcaseXMLList;
    }

    public void setTestcaseXMLList(ArrayList<TestCaseXML> testcaseXMLList) {
        this.testcaseXMLList = testcaseXMLList;
    }

    public int indexOfTestCase (int uutID, int subProgramID, int testCaseID, String testCaseName){
        int retValue = -1;        
        for (TestCaseXML testcase : testcaseXMLList){
            if (testcase.getUUT_ID() == uutID
                    && testcase.getFUNCTION_ID() == subProgramID
                    && testcase.getTESTCASE_ID()== testCaseID
                    && testcase.getTestcaseName().equals(testCaseName)){
                retValue = testcaseXMLList.indexOf(testcase);
            }
        }
        
        return retValue;
    }

}
