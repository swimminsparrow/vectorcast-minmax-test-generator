package vectorcastesterminmaxgen.Model;

/**
 * This Class is used by TestCase class.
 * @author Roberto Caputo
 * @version 1 03/12/2018
 * @version 2 04/12/2018
 * Added tstLine to store info from HanSolo 
 */
public class Parameter {
    private String name;
    private String value;
    private int tstLine;    
    /*
    tstLine contains the row index of the tst parsed where the param is defined.
    */

    /**
     * This Constructor is Used to store info from HanSolo TST PARSER.
     * So it is called when a TST IS PARSED.
     * @param name
     * @param value
     * @param tstLine 
     */
    public Parameter(String name, String value, int tstLine) {
        this.name = name;
        this.value = value;
        this.tstLine = tstLine;
    }

    /**
     * This Constructor is Used to store from Spock XML Parser.
     * So it is called when a XML IS PARSED.
     * @param name
     * @param value 
     */
    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getTstLine() {
        return tstLine;
    }

    public void setTstLine(int tstLine) {
        this.tstLine = tstLine;
    }
    
    

    
}
