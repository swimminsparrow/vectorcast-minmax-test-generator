package vectorcastesterminmaxgen.Configs;

/**
 * This Class contains common Constants for VectorCast.
 * @author Roberto Caputo
 * @version 1 29/11/2018 
 */
public class VectorCastConstants {    

    //TST VALUES    //
    public static String MinValue = "<<MIN>>";
    public static String MaxValue = "<<MAX>>";
    
    /**
     * TESTCASE_DATA_XML is the name of the xml file used by VectorCast to store
     * info avout all the testcases of the environment.
     */
    public static String TESTCASE_DATA_XML = "testcase_data.xml";
    
    
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
    public static String VC_CLI_EXE="\\clicast";
    public static String VC_CLI_ENVIRONMENT=" -e ";
    public static String VC_CLI_UUT=" -u ";
    public static String VC_CLI_SUBPROGRAM=" -s ";
    public static String VC_CLI_TESTCASE=" -t ";
    public static String VC_CLI_EXPORT_TST_COMMAND=" TESt Script Create ";
    public static String VC_CLI_EXPORT_FULLREPORT_COMMAND=" Reports Custom Full ";
    public static String VC_CLI_EXPORT_PROBEPOINTREPORT_COMMAND=" ENvironment Extract Probe_point_log ";
}
