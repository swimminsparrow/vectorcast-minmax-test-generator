package vectorcastesterminmaxgen.Model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.apache.commons.beanutils.BeanUtils;
import vectorcastesterminmaxgen.LoggerBoy;

/**
 * TestCase is a generic FATHER class template .
 * TestCaseXML is an extension of this class to store info for XML parsed TestCase.
 * TestCaseTST is an extension of this class to store info for a TST.
 * TestCaseVerifyExecutionResults is an extension of TestCase model for VerifyExecutionResults TableView.
 * 
 * @author Roberto Caputo
 * @version 1 04/12/2018 Extended version of TestCase.
 * @version 2 05/04/2019 Now it is really a son class.
 */
public class TestCaseTST extends TestCase{
    private int START_LINE;
    private int ENVIRONMENT_LINE;
    private int UUT_LINE;
    private int FUNCTION_LINE;
    private int TESTCASENAME_LINE;
    private int END_LINE;
    
    private ArrayList <Parameter> parameterList;
    
    public TestCaseTST() {
        super();
    }
    
    /**
     * This Constructor copy all the attributes from an existing TestCase object.
     * @param testcase 
     */
    public TestCaseTST(TestCase testcase) {
        try {
            BeanUtils.copyProperties(this, testcase);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LoggerBoy.logError(TestCaseTST.class.getName(), ex.getLocalizedMessage());
        }
    }

    public int getSTART_LINE() {
        return START_LINE;
    }

    public void setSTART_LINE(int START_LINE) {
        this.START_LINE = START_LINE;
    }

    public int getENVIRONMENT_LINE() {
        return ENVIRONMENT_LINE;
    }

    public void setENVIRONMENT_LINE(int ENVIRONMENT_LINE) {
        this.ENVIRONMENT_LINE = ENVIRONMENT_LINE;
    }

    public int getUUT_LINE() {
        return UUT_LINE;
    }

    public void setUUT_LINE(int UUT_LINE) {
        this.UUT_LINE = UUT_LINE;
    }

    public int getFUNCTION_LINE() {
        return FUNCTION_LINE;
    }

    public void setFUNCTION_LINE(int FUNCTION_LINE) {
        this.FUNCTION_LINE = FUNCTION_LINE;
    }

    public int getTESTCASENAME_LINE() {
        return TESTCASENAME_LINE;
    }

    public void setTESTCASENAME_LINE(int TESTCASENAME_LINE) {
        this.TESTCASENAME_LINE = TESTCASENAME_LINE;
    }

    public int getEND_LINE() {
        return END_LINE;
    }

    public void setEND_LINE(int END_LINE) {
        this.END_LINE = END_LINE;
    }
    
    public ArrayList<Parameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(ArrayList<Parameter> parameterList) {
        this.parameterList = parameterList;
    }
    
    public void addParameter(Parameter parameter){
        if (this.parameterList == null)
            this.parameterList = new ArrayList();
        
        this.parameterList.add(parameter);
    }

}
