package vectorcastesterminmaxgen.Model;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Roberto Caputo
 */
public abstract class AbstractParsedInfo {
        
    protected String envName; 
    
    protected SortedMap <String, ArrayList <String>> subProgramList = new TreeMap <>();
        // KEY: UUT, VALUE: List <SUBPROGRAM>
    
    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }  
 
    public SortedMap<String, ArrayList<String>> getSubProgramList() {
        return subProgramList;
    }

    public void setSubProgramList(SortedMap<String, ArrayList<String>> subProgramList) {
        this.subProgramList = subProgramList;
    }
}
