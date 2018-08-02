package demo.legacy;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public interface Constants {

    BigDecimal ZERO = new BigDecimal("0.00");
    BigDecimal DEBT_AMOUNT_ALL_9 = new BigDecimal("9999999999.99");
    String AGENCY_UPDATE_RESET_IND = "!";
    String BLANK = "";
    byte[] ZERO_UUID = new byte[] { (byte) 0 };
    String TEST_HARDNESS_PROP_FILE_NAME = FileUtil.rootDirectory() + "/ARRIVED/TestHardnessParameters.properties";

    String DEFAULT_FEE = "NULL";
    String DEFAULT_US_CODE = "US";

    char YES_INDICATOR = 'Y';
    char NO_INDICATOR = 'N';

    String YES_INDICATOR_IND = "Y";
    String NO_INDICATOR_IND = "N";

    String DEFAULT_ENCODING = "UTF-8";

    String YES = "Yes";
    String NO = "No";

    LocalDate PENDING_EXTRACT_STATUS_DATE = DateTimeUtil.parseLocalDateFrom("8888-11-11");
    String DEFAULT_INPUT_FILE_PATH = FileUtil.rootDirectory() + "/ARRIVED/";
    String DEFAULT_PROCESSING_FILE_PATH = FileUtil.rootDirectory() + "/STARTED/";
    String DEFAULT_SUSPENSE_FILE_PATH = FileUtil.rootDirectory() + "/SUSPENSE/";
    String DEFAULT_OUTPUT_FILE_PATH = FileUtil.rootDirectory() + "/SENT/";
    String DEFAULT_PROCESSED_FILE_PATH = FileUtil.rootDirectory() + "/PROCESSED/";
    String EXTRACT_RUN_SCRIPT_FILE_PATH = FileUtil.rootDirectory() + "/top-batch/";

    String SALARY_ONI_FILE_SUFFIX = ".ONI";
    String SALARY_MID_CYCLE_FILE_INPUT_SUFFIX = ".SUSP";

    String FULL_FILE_REVERSAL_PAM_ACK_FILE_SUFFIX = ".FULLFILEREV.ACK";

    String ALL_ZERO_TIN = "000000000";
    String ALL = "ALL";
    char BLANK_INDICATOR = ' ';
    char DEFAULT_CHAR_VALUE = '\u0000';
    String SPACE = " ";
    String COMMA = ",";
    String ALL_AGENCIES_SITES = "ALL";
    String SPILT_REGEX = "\\.";
    String UNKNOWN = "UNKNOWN";

    int NUM_OF_RECS_PER_TXN = 100;
    String TRANSFER_FILE_STATUS_SUCCESS = "Success";
    String TRANSFER_FILE_STATUS_FAILED = "Failed";
    String REQ_HEADER_SM_USER = "SM_UNIVERSALID";

    String FILE_NAME_NODE_SEPERATOR = ".";
    String CNTL_NODE = ".CNTL.";
    String BATCH = "BATCH";
    String JOB_INPUT_FILE_NAME = "inputFileName";

    String PATTERN_ALPHA_NUMERIC = "[A-Za-z0-9]+";
    String PATTERN_ALPHA_NUMERIC_UNDERSCORE = "[a-zA-Z0-9-_',\\s/]+";
    String PATTERN_PHONE_NUMBER = "([0-9])*|^([0-9])\\1*$";
    String PATTERN_NUMERIC = "([0-9])*";
    String ZERO_UUID_STRING = "00";

    String FILE_SUFFIX = ".SPLIT";

    String CARAT_DELIMITER = "^";
    String ESCAPE_BACKSLASH = "\\";
    String QUOTE = "\"";

    int ZIP_CODE_LENGTH = 5;
    int CTX_CONNECT_DIRECT_RECORD_LENGTH = 909;

    String NO_FILE_TO_PROCESS = "No File to Process";
    String LATIN1 = "ISO-8859-1";
    String NONE = "None";
    char YES_LOWER_CASE_INDICATOR = 'y';
    String LEGACY_ACCTG_FILE_PATH = "/ROCDB12/ARRIVED/";

    String REJECT_REASON_INSUFFICIENT_FUNDS = "Insufficient Funds";
    String REJECT_REASON_OTHER = "Other";
    String RECEIVED = "Received";
    String SENT = "Sent";
}
