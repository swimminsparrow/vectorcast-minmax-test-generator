package vectorcastesterminmaxgen.Model.XMLParams;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This Model represent a param inserted in Param List by the user and that must
 * be parsed in the Test Script TST File.
 *
 * @author  Roberto Caputo
 * @version 0.1a     21/11/2018     Added nested paramElement list for custom values for every single functionspecified
 * @version 0.2a     07/12/2018     Converted min max from int to long.
 * @version 0.2.1a   23/01/2019     Added isParamSetByUserForDefaultOrCustom().
 * @version 0.2.2a   09/04/2019     Forked and rewritten.
 */
public class UserCustomParam {

    protected String name;
    protected long min;
    protected long max;
    protected long lockedValue;
    protected boolean isLockedValue;

    private SortedMap<String, FunctionUserCustomParam> functionCustomParamList = new TreeMap <>(); 
        //  KEY: subprogram name, VALUE: custom value for subprogram (NULL if no custom value specified for that subprogram)        

    public UserCustomParam(String name, long min, long max, long lockedValue, boolean isLockedValue) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.lockedValue = lockedValue;
        this.isLockedValue = isLockedValue;
    }
    
    protected UserCustomParam(){}

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getLockedValue() {
        return lockedValue;
    }

    public void setLockedValue(long lockedValue) {
        this.lockedValue = lockedValue;
    }

    public boolean isIsLockedValue() {
        return isLockedValue;
    }

    public void setIsLockedValue(boolean isLockedValue) {
        this.isLockedValue = isLockedValue;
    }

    public SortedMap<String, FunctionUserCustomParam> getFunctionCustomParamList() {
        return functionCustomParamList;
    }

    public void setFunctionCustomParamList(SortedMap<String, FunctionUserCustomParam> functionCustomParamList) {
        this.functionCustomParamList = functionCustomParamList;
    }
    
    //region    OTHER FUNCTIONS
    
    /**
     * This Function set the policy to decide if a param is set by user or not
     * for the min and max value. If Min=Max the Input Value will be
     * automatically ignored and isLockedValue = fALSE
     *
     * @return TRUE if min = max OR isLockedValue = TRUE, FALSE instead
     */
    public boolean isParamSetByUser() {
        return ((max != min) || isLockedValue);
    }

    //endregion OTHER FUNCTIONS
    
    
}
