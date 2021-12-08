package vectorcastesterminmaxgen.Model;

/**
 * TestCase is a generic FATHER class template .
 * TestCaseXML is an extension of this class to store info for XML parsed TestCase.
 * TestCaseTST is an extension of this class to store info for a TST.
 * TestCaseVerifyExecutionResults is an extension of TestCase model for VerifyExecutionResults TableView.
 * 
 * @author Roberto Caputo
 * @version 1 08/04/2019
 */

public class TestCase {       
    protected String environment;
    protected String uut;
    protected String function;
    protected String testcaseName;
    //protected String notes;    

    public TestCase (){}

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getUut() {
        return uut;
    }

    public void setUut(String uut) {
        this.uut = uut;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getTestcaseName() {
        return testcaseName;
    }

    public void setTestcaseName(String testcaseName) {
        this.testcaseName = testcaseName;
    }
    
}


