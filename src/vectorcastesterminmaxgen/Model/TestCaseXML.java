package vectorcastesterminmaxgen.Model;

import java.util.ArrayList;

/**
 * TestCase is a generic FATHER class template .
 * TestCaseXML is an extension of this class to store info for XML parsed TestCase.
 * TestCaseTST is an extension of this class to store info for a TST.
 * TestCaseVerifyExecutionResults is an extension of TestCase model for VerifyExecutionResults TableView.
 * 
 * @author Roberto Caputo
 * @version 1 08/04/2019
 */

public class TestCaseXML extends TestCase{   
    public static enum EXECUTION_RESULTS {
        NONE,//0
        FAILED,//1
        PASSED//2
    };

    public static enum TESTCASE_TYPE {
        MCDC,
        BASIS,
        PARTITIONED,
        NORMAL
    };
    
    protected int TESTCASE_ID;
    protected int UUT_ID;
    protected int FUNCTION_ID;
    protected EXECUTION_RESULTS EXECUTION_RESULT;
    protected TESTCASE_TYPE TESTCASE_TYPE;
    protected String EXECUTIONDATE;    
    protected ArrayList <Parameter> parameterList;
    

    public TestCaseXML (){}   

    public int getTESTCASE_ID() {
        return TESTCASE_ID;
    }

    public void setTESTCASE_ID(int TESTCASE_ID) {
        this.TESTCASE_ID = TESTCASE_ID;
    }

    public int getUUT_ID() {
        return UUT_ID;
    }

    public void setUUT_ID(int UUT_ID) {
        this.UUT_ID = UUT_ID;
    }

    public int getFUNCTION_ID() {
        return FUNCTION_ID;
    }

    public void setFUNCTION_ID(int FUNCTION_ID) {
        this.FUNCTION_ID = FUNCTION_ID;
    }

    public EXECUTION_RESULTS getEXECUTION_RESULT() {
        return EXECUTION_RESULT;
    }

    public void setEXECUTION_RESULT(EXECUTION_RESULTS EXECUTION_RESULT) {
        this.EXECUTION_RESULT = EXECUTION_RESULT;
    }

    public TESTCASE_TYPE getTESTCASE_TYPE() {
        return TESTCASE_TYPE;
    }

    public void setTESTCASE_TYPE(TESTCASE_TYPE TESTCASE_TYPE) {
        this.TESTCASE_TYPE = TESTCASE_TYPE;
    }

    public String getEXECUTIONDATE() {
        return EXECUTIONDATE;
    }

    public void setEXECUTIONDATE(String EXECUTIONDATE) {
        this.EXECUTIONDATE = EXECUTIONDATE;
    }

    public ArrayList<Parameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(ArrayList<Parameter> parameterList) {
        this.parameterList = parameterList;
    }

}


