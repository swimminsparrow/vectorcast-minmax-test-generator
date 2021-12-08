package vectorcastesterminmaxgen.Model;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import vectorcastesterminmaxgen.LoggerBoy;

/**
 * TestCase is a generic FATHER class template .
 * TestCaseXML is an extension of this class to store info for XML parsed TestCase.
 * TestCaseTST is an extension of this class to store info for a TST.
 * TestCaseVerifyExecutionResults is an extension of TestCase model for VerifyExecutionResults TableView.
 *
 * @author Roberto Caputo
 * @version 1
 * @version 2 29/11/2018
 * @version 3 05/04/2019
 */
public class TestCaseComparison extends TestCaseXML {

    /**
     * TABLE PARAMS (NOT PARSED FROM XML)
     */
    private EXECUTION_RESULTS expectedExecutionResult;
    private String isExecutionAndExpectedResultsMatching;

    // FAKE CONSTRUCTOR 
    public TestCaseComparison(String fakeName){
        this.testcaseName = fakeName;
    }
    
    /**
     * This Constructor copy all the attributes from an existing TestCaseXML
     * object.
     *
     * @param testcaseXML must be a valid parsed testcase xml
     */
    public TestCaseComparison(TestCaseXML testcaseXML) {
        try {
            BeanUtils.copyProperties(this, testcaseXML);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LoggerBoy.logError(TestCaseTST.class.getName(), ex.getLocalizedMessage());
        }
    }

    public EXECUTION_RESULTS getExpectedExecutionResult() {
        return expectedExecutionResult;
    }

    public void setExpectedExecutionResult(EXECUTION_RESULTS expectedExecutionResult) {
        this.expectedExecutionResult = expectedExecutionResult;
    }

    public String isExecutionAndExpectedResultsMatching() {
        return isExecutionAndExpectedResultsMatching;
    }

    public void setIsExecutionAndExpectedResultsMatching(String isExecutionAndExpectedResultsMatching) {
        this.isExecutionAndExpectedResultsMatching = isExecutionAndExpectedResultsMatching;
    }
}
