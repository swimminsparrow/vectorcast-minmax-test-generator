package vectorcastesterminmaxgen.CoreFunctions.Parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.xml.sax.SAXException;
import vectorcastesterminmaxgen.Configs.VectorCastConstants;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Model.VectorCastEnvironment;
import vectorcastesterminmaxgen.Utility.BrowseFileorFolderVCExtension;

/**
 * This Class browse the specified Environment Folder to find testcase_data.xml
 * This File contains all the testcase related to a function and UUT.
 *
 * @author Roberto Caputo
 * @version 1
 * @version 2 02/08/2018
 * @version 3 29/11/2018 Edited to be generic for future library implementation.
 * @version 4 09/04/2019 Adapted to new Models.
 */
public class KirkTestDataXMLParser {

    private VectorCastEnvironment vcEnv;

    /**
     * Main Constructor.
     *
     * @param vcEnv Pay Attention: The VectorCastEnvironment passed must be
     * already filled with the testcase data parsed by Spock.
     */
    public KirkTestDataXMLParser(VectorCastEnvironment vcEnv) {
        this.vcEnv = vcEnv;
    }

    /**
     * This Function scans the folder containing an environment.
     *
     * @return 
     */
    public VectorCastEnvironment scanEnvironmentForTestCaseDataXML() {
        File envSubDir = BrowseFileorFolderVCExtension.getEnvironmentSubFolder(vcEnv.getEnvDir());
        if (envSubDir.isDirectory()) {
            //  Hoping is not null  //
            File testCaseDataXML = new File(envSubDir.getPath() + "\\" + VectorCastConstants.TESTCASE_DATA_XML);
            if (testCaseDataXML.exists()) {
                // if testcase_data.xml exists  //                
                LoggerBoy.logEverywhere(this.getClass().getSimpleName(),
                        "testcase_data Found in " + testCaseDataXML.getPath());

                //  Now Parse testcasedata.xml  //
                parseXML(testCaseDataXML);
            }
        }
        return vcEnv;
    }

    /**
     * This Function parses VectorCast Internal Xml testcase_data.xml to get the
     * name of all TestCases for VectorCastEnvironment in specified folder by
     * envPath for each function of the UUT. The result is the vcEnv populated
     * with all the info in the xml.
     *
     * @param testcaseDataXML testcase_data.xml File
     */
    private void parseXML(File testcaseDataXML) {
        SortedMap<String, ArrayList<String>> subProgramMapList = new TreeMap<>();// KEY: UUT, VALUE: SUBPROGRAM
        String uutName = new String();
        try {
            //creating DOM Document
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(testcaseDataXML);
            DOMBuilder domBuilder = new DOMBuilder();
            Document xmlDoc = domBuilder.build(doc);
            //we can create JDOM Document from DOM, SAX and STAX Parser Builder classes

            Element root = xmlDoc.getRootElement();
            //  Unit Index (UUTs) List //            

            List<Element> UUTChildrenElements = root.getChildren("unit");

            for (Element uutElement : UUTChildrenElements) {
                //  For each UUT Element    //
                uutName = uutElement.getChildText("name");
                int uutID = Integer.parseInt(uutElement.getAttributeValue("index"));
                List<Element> subProgramChildrenElements = uutElement.getChildren("subprogram");
                ArrayList<String> subProgramList = new ArrayList();
                
                for (Element subProgramElement : subProgramChildrenElements) {
                    //  For each SubProgram Element    //
                    int subProgramID = Integer.parseInt(subProgramElement.getAttributeValue("index"));
                    String subProgramName = subProgramElement.getChildText("name");

                    subProgramList.add(subProgramName);
                    
                    List<Element> testCasesChildrenElements = subProgramElement.getChildren("testcase");

                    for (Element testCasesElement : testCasesChildrenElements) {
                        //  For each TestCase Element    //
                        String testCaseName = testCasesElement.getChildText("name");
                        int testCaseID = Integer.parseInt(testCasesElement.getChildText("unique_id"));
                        int indexOfTestCase = vcEnv.indexOfTestCase(uutID, subProgramID, testCaseID, testCaseName);
                        if (indexOfTestCase >= 0) {
                            //  If I found the testcase name in the list created by Spock   //
                            vcEnv.getTestcaseXMLList().get(indexOfTestCase).setUut(uutName);
                            vcEnv.getTestcaseXMLList().get(indexOfTestCase).setFunction(subProgramName);
                        }
                    }
                }
                
                subProgramMapList.put(uutName, subProgramList);
            }
            
            vcEnv.setSubProgramList(subProgramMapList);

        } catch (IOException | ParserConfigurationException | SAXException ex) {
            LoggerBoy.logError(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

}
