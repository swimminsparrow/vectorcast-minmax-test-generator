package vectorcastesterminmaxgen.Configs;

/**
 * This Class contains common Constants
 * @author Roberto Caputo
 * @version 0.1 
 * @version 0.2.2a 09/04/2019
 */
public class Constants {    

    public final static String APPDIR_CONFIG_PATH = "VectorCastEsterMinMaxGenConfigFolder";
    public final static String APPDIR_CACHE_PATH = "VectorCastEsterMinMaxGenCacheFolder";
    public final static String CONFIG_XML_FILE_PATH = APPDIR_CONFIG_PATH + "\\vc_esterminmaxgen_config.xml";
    public final static String START_LICENSE_SERVER_SCRIPT_FILE_PATH = APPDIR_CONFIG_PATH + "\\customstartlicenseserverscript.bat";
    public final static String PARAMLIST_XML_FILE_PATH = APPDIR_CONFIG_PATH + "\\vc_esterminmaxgen_paramlist_cached.xml";
    
    
    public static String[] XML_EXTENSIONS = {"xml", "XML"};
    public static String[] TST_EXTENSIONS = {"tst", "TST"};
    
    /**
     * TST NAMES of Generated TESTCASES
     */
    public final static String REPARSED_TST_FILE = "_PARSED.tst";
    public final static String TESTCASE_NAME_INTORNO_SINISTRO = "_Intorno_Sinistro";
    public final static String TESTCASE_NAME_INTORNO_DESTRO = "_Intorno_Destro";
    public final static String TESTCASE_NAME_MIN = "_MIN";
    public final static String TESTCASE_NAME_MAX = "_MAX";

    
    //  TST PARAM VALUES    //
    public final static String MIN_VALUE = "<<MIN>>";
    public final static String MAX_VALUE = "<<MAX>>";
    
    
        //  VectorCast Command Line Arguments    //
        /**
         * VectorCast command to generate Script for a TestCase:
         *  clicast –e <env> [-u <unit> [-s <sub> [-t <testcase>]]] TESt Script Create <scriptfile>
         * 
         * VectorCast command to generate Full Report for a Function:
         *  clicast –e <env> [-u <unit> [-s <sub> ]] Reports Custom Full [<outputfile>]
         * 
         * CREA TEST PARTIZIONATO
         * C:\VCAST_2018_SP1\clicast -e SOMPRSTD TESt Partition
         * 
         * ESPORTA TST PER TUTTO L'ENVIRONMENT
         * C:\VCAST_2018_SP1\clicast -e SOMPRSTD TESt Script Create C:\test\partitiontest.tst
         */
    public final static String VC_CLI_EXE = "\\clicast";
    public final static String VC_CLI_ENVIRONMENT = " -e ";
    public final static String VC_CLI_UUT = " -u ";
    public final static String VC_CLI_SUBPROGRAM = " -s ";
    public final static String VC_CLI_TESTCASE = " -t ";
    public final static String VC_CLI_EXPORT_TST_COMMAND = " TESt Script Create ";
    public final static String VC_CLI_EXPORT_FULLREPORT_COMMAND = " Reports Custom Full ";
    public final static String VC_CLI_EXPORT_PROBEPOINTREPORT_COMMAND = " ENvironment Extract Probe_point_log ";


}
