package vectorcastesterminmaxgen.CoreFunctions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;
import vectorcastesterminmaxgen.Configs.Constants;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Model.ParsedTST;
import vectorcastesterminmaxgen.Model.XMLParams.FunctionUserCustomParam;
import vectorcastesterminmaxgen.Model.XMLParams.UserCustomParam;

/**
 * Pattern: SINGLETON 
 * This Class Manages params inserted by the user and handles the XML db of the params.
 *
 * @author Roberto Caputo
 * @version 0.1 1   5/11/2018
 * @version 0.2.1a  23/01/2019      getParamListFormatted() -> Custom value was not used if no default value had been specified.
 *                                  setParamElementMin(), setParamElementMax() -> fixed param is set by user to show in main table of Main Controller.
 * @version 0.2.2a  10/04/2019      Forked and rewritten.
 * 
 */
public class UserCustomParamHandler {
    private static UserCustomParamHandler currentInstance;

    private SortedMap<String, UserCustomParam> currentSetUserParamList = new TreeMap <>(); 
        /**
         * The list contains the paramlist currently used for the Parsed TST
         * The list is populated both with parameters parsed from the XML database of parameters
         * AND with the parameters parsed from the TST
         * KEY: param name
         * VALUE: usercustomparam obj
         */
    
    private SortedMap<String, ArrayList <String>> parsedUserParamList = new TreeMap <>(); 
        /**
         * The list contains all the param list parsed.
         * KEY: parameter name
         * VALUE: list of function referencing param name
         */
    
    private UserCustomParamHandler() {}

    public static synchronized UserCustomParamHandler getInstance() {
        if (currentInstance == null) {
            currentInstance = new UserCustomParamHandler();
        }
        return currentInstance;
    }

    public void initCurrentSetUserParamList() {        
        currentSetUserParamList.clear();
        
        fillUserParamListWithXMLData();
        //fillUserParamListWithTSTParams(parsedTST);
    }
    

    public SortedMap<String, UserCustomParam> getCurrentSetUserParamList() {
        return currentSetUserParamList;
    }

    public SortedMap<String, ArrayList<String>> getParsedUserParamList() {
        return parsedUserParamList;
    }

    public void addParsedFunctionParam(String paramName, String functionName) {
        if (parsedUserParamList.containsKey(paramName)){
            if (!parsedUserParamList.get(paramName).contains(functionName)){
                parsedUserParamList.get(paramName).add(functionName);
            }
        }
        else {
            ArrayList <String> functions = new ArrayList();
            functions.add(functionName);
            parsedUserParamList.put(paramName, functions);
        }
    }
    

    /**
     * Populate the current List of UserCustomParam with the cached params read in the xml.
     * 
     * @param parsedTST
     */
    private void fillUserParamListWithXMLData() {
        SortedMap<String, UserCustomParam> xmlUserParamList = readParamListFromXML();

        for (String xmlParamName : xmlUserParamList.keySet()) {
            if (parsedUserParamList.containsKey(xmlParamName)) {
                //  If the TST contains almost one time the paramName then add to the list  //
                UserCustomParam xmlCustomParam = xmlUserParamList.get(xmlParamName);
                UserCustomParam currentCustomParam = new UserCustomParam(
                        xmlCustomParam.getName(),
                        xmlCustomParam.getMin(),
                        xmlCustomParam.getMax(),
                        xmlCustomParam.getLockedValue(),
                        xmlCustomParam.isIsLockedValue());

                for (String xmlFunctionName : xmlCustomParam.getFunctionCustomParamList().keySet()) {
                    //  Now remove unused function  //
                    if (parsedUserParamList.get(xmlParamName).contains(xmlFunctionName)) {
                        currentCustomParam.getFunctionCustomParamList().put(xmlFunctionName,
                                xmlCustomParam.getFunctionCustomParamList().get(xmlFunctionName));
                    }
                }
                if (currentCustomParam.isParamSetByUser()
                        || currentCustomParam.getFunctionCustomParamList().size() > 0) {
                    //  if the param is set in the XML OR if there are custom function values   //
                    currentSetUserParamList.put(xmlParamName, currentCustomParam);
                }
            }
        }
    }

    
    
    /**
     * Populate the current List of UserCustomParam with the parameters parsed in the TST
     * 
     * @param parsedTST
     */
//    private void fillUserParamListWithTSTParams(ParsedTST parsedTST) {
//
//        for (TestCaseTST testcaseTST : parsedTST.getTestcaseTSTList()) {
//            for (Parameter parameter : testcaseTST.getParameterList()) {
//                if (!currentSetUserParamList.containsKey(parameter.getName())) {
//                    currentSetUserParamList.put(
//                            parameter.getName(),
//                            new UserCustomParam(parameter.getName(), 0, 0, 0, false));
//                }
//            }
//        }
//    }

    
    /**
     * This function saves the list of params to xml.
     * ATTENNTION: at the moment parameters will be never removed from the db
     * @param parsedTST
     */
    public void saveParamListToXML(ParsedTST parsedTST) {
        try {
            Document xmlDoc = new Document();
            xmlDoc.setRootElement(new Element("ParamElements",
                    Namespace.getNamespace("EsterMinMax List of Cached Parsed Params")));

            SortedMap<String, UserCustomParam> xmlUserParamList = readParamListFromXML();
            // paramElementsXML is the list used to save the new params. It will be:
            // CACHED PARAMS IN XML + NEW OR EDITED PARAMS  //

            // now add or overwrite the new values//
            for (String paramName : currentSetUserParamList.keySet()) {
                UserCustomParam userParam = currentSetUserParamList.get(paramName);
                
                if (xmlUserParamList.containsKey(paramName)) {
                    //  If the selected param name from the ParamList is in the xml //
                    UserCustomParam xmlUserParam = xmlUserParamList.get(paramName);
                    if (userParam.isParamSetByUser()) {
                        //OVERWRITE the Old XML PARAM only if the param has been set by the user OR if param is currently using by user//
                        xmlUserParam.setMax(userParam.getMax());
                        xmlUserParam.setMin(userParam.getMin());
                        xmlUserParam.setIsLockedValue(userParam.isIsLockedValue());
                        xmlUserParam.setLockedValue(userParam.getLockedValue());
                    }

                    //  Now ADD or APPEND function custom values   //
                    if (userParam.getFunctionCustomParamList().size() > 0){
                        for (String functionName : userParam.getFunctionCustomParamList().keySet()){
                            if (xmlUserParam.getFunctionCustomParamList().containsKey(functionName)){
                                //  if the xml param already contains custom function values for the specified function REPLACE IT  //
                                xmlUserParam.getFunctionCustomParamList().replace(functionName, userParam.getFunctionCustomParamList().get(functionName));
                            }
                            else {
                                //  if not ADD IT   //
                                xmlUserParam.getFunctionCustomParamList().put(functionName, userParam.getFunctionCustomParamList().get(functionName));
                            }
                        }
                    }
                    
                    //  Delete Custom Function Values from the XML if deleted by user   //
                    for (String functionName : xmlUserParam.getFunctionCustomParamList().keySet()){
                        if (!userParam.getFunctionCustomParamList().containsKey(functionName)
                                && parsedTST.isFunctionNameInTST(functionName)){
                            //  IF the functionName is not contained in the currentSetUserParamList AND function is in my current enviropnemnt //
                            xmlUserParam.getFunctionCustomParamList().remove(functionName);
                        }
                    }
                    
                    xmlUserParamList.replace(paramName, xmlUserParam);
                } else {
                    //  If the param is not in the XML is a new PARAM. So ADD IT    //
                    xmlUserParamList.put(paramName, userParam);
                }
            }

            //  Now PACK elements for XML saving    //
            for (String paramName : xmlUserParamList.keySet()) {
                UserCustomParam xmlUserParam = xmlUserParamList.get(paramName);
                
                Element paramElementXML = new Element("ParamElement");
                paramElementXML.addContent(new Element("Name").setText(xmlUserParam.getName()));
                paramElementXML.addContent(new Element("Min").setText(String.valueOf(xmlUserParam.getMin())));
                paramElementXML.addContent(new Element("Max").setText(String.valueOf(xmlUserParam.getMax())));
                paramElementXML.addContent(new Element("LockedValue").setText(String.valueOf(xmlUserParam.getLockedValue())));
                paramElementXML.addContent(new Element("IsLockedValue").setText(xmlUserParam.isIsLockedValue() ? "TRUE" : "FALSE"));

                //  add child elements if any //
                if (xmlUserParam.getFunctionCustomParamList().size() > 0) {
                    SortedMap<String, FunctionUserCustomParam> xmlFunctionUserParamList = xmlUserParam.getFunctionCustomParamList();
                    
                    Element childListXML = new Element("ChildList");
                    for (String functionName : xmlFunctionUserParamList.keySet()) {
                        FunctionUserCustomParam xmlFunctionUserParam = xmlFunctionUserParamList.get(functionName);
                        
                        Element childParamElementXML = new Element("ChildParamElement");
                        childParamElementXML.addContent(new Element("SubProgram").setText(functionName));
                        childParamElementXML.addContent(new Element("Max").setText(String.valueOf(xmlFunctionUserParam.getMax())));
                        childParamElementXML.addContent(new Element("Min").setText(String.valueOf(xmlFunctionUserParam.getMin())));
                        childParamElementXML.addContent(new Element("LockedValue").setText(String.valueOf(xmlFunctionUserParam.getLockedValue())));
                        childParamElementXML.addContent(new Element("IsLockedValue").setText(xmlFunctionUserParam.isIsLockedValue() ? "TRUE" : "FALSE"));

                        childListXML.addContent(childParamElementXML);
                    }

                    paramElementXML.addContent(childListXML);
                }

                xmlDoc.getRootElement().addContent(paramElementXML);
            }

            //JDOM document is ready now, lets write it to file now
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy__HH_mm_ss");
            String dateTimeFormatted = now.format(formatter);
            if (new File(Constants.PARAMLIST_XML_FILE_PATH).exists()) {
                FileUtils.copyFile(new File(Constants.PARAMLIST_XML_FILE_PATH),
                        new File(Constants.PARAMLIST_XML_FILE_PATH + "_bak_" + dateTimeFormatted));
                //  backup the prev file //
            }
            else (new File(Constants.PARAMLIST_XML_FILE_PATH)).createNewFile();
            xmlOutputter.output(xmlDoc, new FileOutputStream(Constants.PARAMLIST_XML_FILE_PATH));

        } catch (IOException e) {
            LoggerBoy.logError(this.getClass().getSimpleName(),
                    "Cannot Generate CACHED PARAM XML " + e.getLocalizedMessage());
        }

    }

    /**
     * This Function parses the content of paramlist contained in parse list xml
     * file.
     *
     * @return List of ParamElement cached in xml.
     */
    public SortedMap<String, UserCustomParam> readParamListFromXML() {
        SortedMap<String, UserCustomParam> xmlUserParamList = new TreeMap <>();
        try {
            //creating DOM Document
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            if (new File(Constants.PARAMLIST_XML_FILE_PATH).exists()) {
                org.w3c.dom.Document doc = dBuilder.parse(new File(Constants.PARAMLIST_XML_FILE_PATH));
                DOMBuilder domBuilder = new DOMBuilder();
                Document xmlDoc = domBuilder.build(doc);
                //we can create JDOM Document from DOM, SAX and STAX Parser Builder classes

                Element root = xmlDoc.getRootElement();
                List<Element> empListElements = root.getChildren("ParamElement");
                for (Element empElement : empListElements) {
                    UserCustomParam xmlUserParam = new UserCustomParam(
                            getStringValueFromXML(empElement, "Name"),
                            Long.parseLong(getStringValueFromXML(empElement, "Min")),
                            Long.parseLong(getStringValueFromXML(empElement, "Max")),
                            Long.parseLong(getStringValueFromXML(empElement, "LockedValue")),
                            getStringValueFromXML(empElement, "IsLockedValue").equals("TRUE")
                    );

                    if (empElement.getChild("ChildList") != null) {
                        List<Element> childElements = empElement.getChild("ChildList").getChildren("ChildParamElement");
                        SortedMap<String, FunctionUserCustomParam> xmlFunctionUserParamList = new TreeMap<>();

                        //  add child elements  //
                        if (!childElements.isEmpty()) {
                            for (Element childElement : childElements) {
                                FunctionUserCustomParam xmlFunctionUserParam = new FunctionUserCustomParam(
                                        xmlUserParam.getName(),
                                        Long.parseLong(getStringValueFromXML(childElement, "Min")),
                                        Long.parseLong(getStringValueFromXML(childElement, "Max")),
                                        Long.parseLong(getStringValueFromXML(childElement, "LockedValue")),
                                        getStringValueFromXML(childElement, "IsLockedValue").equals("TRUE"),
                                        getStringValueFromXML(childElement, "SubProgram")
                                );
                                xmlFunctionUserParamList.put(xmlFunctionUserParam.getFunctionName(), xmlFunctionUserParam);
                            }

                            xmlUserParam.setFunctionCustomParamList(xmlFunctionUserParamList);
                        }
                    }

                    xmlUserParamList.put(xmlUserParam.getName(), xmlUserParam);
                }
            }
        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            LoggerBoy.logError(this.getClass(), e);
        }
        
        return xmlUserParamList;
    }
    
    /**
     * This Function returns a string from a XMLNode.
     * @param xmlElement
     * @param tagName
     * @return the content of the tag node, a empty string if the tag is not found (null exception)
     */
    private String getStringValueFromXML (Element xmlElement, String tagName){
        return (xmlElement.getChildText(tagName) != null) ?
                            xmlElement.getChildText(tagName) : "";
    }

}
