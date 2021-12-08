package vectorcastesterminmaxgen.Model.XMLParams;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Model.TestCaseTST;

/**
 * This Extension is for Function Custom Parameters inserted by users
 *
 * @author  Roberto Caputo
 * @version 0.2.2a  10/04/2019  Created.
 */
public class FunctionUserCustomParam extends UserCustomParam{

    private String functionName;

    public FunctionUserCustomParam(UserCustomParam userCustomParam) {
        try {
            BeanUtils.copyProperties(this, userCustomParam);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LoggerBoy.logError(TestCaseTST.class.getName(), ex.getLocalizedMessage());
        }
    }
    
    public FunctionUserCustomParam(String name, long min, long max, long lockedValue, boolean isLockedValue, String functionName) {
        super(name, min, max, lockedValue, isLockedValue);
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
        
}
