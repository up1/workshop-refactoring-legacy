package demo.legacy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * This resource facade supports out-bound file operations.
 * 
 * @author admin
 */
public final class FileUtil {

    private static final String SMATCH = "SMATCH";
    private static final String FILE_NAME_WITH_AG = ".AG";
    private static final String FILE_NAME_BEGIN_INDICATOR = "FDM";
    private static final String SUSP = "SUSP";
    private static final String SALUPD = "SALUPD";
    private static final String SALUPI = "SALUPI";
    private static final String NTXUPD = "NTXUPD";
    private static final String NTXUPI = "NTXUPI";
    private static final String NTVUPD = "NTVUPD";
    private static final String NTVUPI = "NTVUPI";
    private static final String NTSUPD = "NTSUPD";
    private static final String NTSUPI = "NTSUPI";
    private static final String SALEXT = "SALEXT";
    private static final String CTXSPLIT = ".CTXSPLIT";
    public static final String DATE_PATTERN = "yyMMdd";
    public static final String TIME_PATTERN = "HHmm";
    public static final String DATE_TIME_PATTERN = "yyMMdd:HHmm";

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    public static final String PAM_PROCESS = "PAMINP";
    public static final String RRB_PROCESS = "RRBINP";
    public static final String OPM_PROCESS = "OPMINP";

    /**
     * Constructor
     */
    private FileUtil() {
    }

    /**
     * sets the logger
     * 
     * @param logger
     */
    public static void setLogger(final Logger logger) {
        FileUtil.logger = logger;
    }

    public static String getFileNameFromPath(final String name) {
        if (name == null) {
            return null;
        }
        final int index = name.lastIndexOf('/');

        if (index > 0) {
            return name.substring(index + 1);
        }

        return name;
    }

    /**
     * gets the CTX main file name from split file name
     * 
     * @param name file name
     * @return file name
     */
    public static String setupFile(String name) {
        if (name == null) {
            return null;
        }
        int index = name.lastIndexOf('/');

        if (index > 0) {
            name = name.substring(index + 1);
        }

        index = name.lastIndexOf(CTXSPLIT);
        if (index > 0) {
            return name.substring(0, index);
        }

        return name;
    }

    public static String getFilePathFromPaymentScheduleFileName(final String paymentScheduleFileName) {
        return Constants.DEFAULT_PROCESSING_FILE_PATH + paymentScheduleFileName;
    }

    public static String getSecondNodeOfFileName(final String fileName) {
        if (fileName == null) {
            return null;
        }

        final String[] fileNameParts = fileName.split(Constants.SPILT_REGEX);
        if (fileNameParts.length > 1) {
            return fileNameParts[1];
        }

        return null;
    }

    public static String readFirstLine(final String filePath) {
        try (final BufferedReader br = FileUtil.utf8Reader(filePath)) {
            final String line = br.readLine();
            if (line == null) {
                throw new RuntimeException(String.format("File [%s] was empty", filePath));
            }
            return line;
        } catch (final IOException e) {
            throw new RuntimeException(String.format("Could not read file [%s]", filePath), e);
        }
    }

    public static String readLastLine(final String filePath) {

        final File file = lookupPath(filePath, false);
        try (RandomAccessFile fileHandler = new RandomAccessFile(file, "r")) {

            final long fileLength = file.length() - 1;
            final StringBuilder sb = new StringBuilder();

            // set counter to zero
            int i = 0;

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                final int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer == fileLength) {
                        continue;
                    }
                    break;
                } else if (readByte == 0xD) {
                    if (filePointer == fileLength - 1) {
                        continue;
                    }
                    break;
                }

                sb.append((char) readByte);
                i++;
            }

            return sb.reverse().toString();
        } catch (final Exception e) {
            throw new IllegalArgumentException("exception getting the last line of the file", e);
        }
    }

    public static BufferedReader classpathUtf8Reader(final String fileName) {

        try {
            final Resource resource = new ClassPathResource(fileName);
            return new BufferedReader(new InputStreamReader(resource.getInputStream(), Constants.DEFAULT_ENCODING));
        } catch (final IOException e) {
            throw new IllegalArgumentException("Could not read file [" + fileName + "]", e);
        }
    }

    public static BufferedReader utf8Reader(final String filePath) {
        return utf8Reader(lookupPath(filePath));
    }

    public static BufferedReader utf8Reader(final String filePath, final int size) {
        return utf8Reader(lookupPath(filePath), size);
    }

    public static BufferedReader utf8Reader(final File file) {
        return utf8Reader(file, 0);
    }

    private static BufferedReader utf8Reader(final File file, final int size) {

        try {
            final InputStreamReader in = new InputStreamReader(new FileInputStream(file), Constants.DEFAULT_ENCODING);
            if (size == 0) {
                return new BufferedReader(in);
            }

            return new BufferedReader(in, size);

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new IllegalArgumentException("Could not create reader for [" + file.getAbsoluteFile() + "]", e);
        }
    }

    public static BufferedWriter utf8Writer(final String filePath, final boolean append) {
        return utf8Writer(lookupPath(filePath), append);
    }

    public static BufferedWriter utf8Writer(final File file, final boolean append) {
        try {
            return new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file, append), Constants.DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new IllegalArgumentException("Could not create writer for [" + file.getAbsoluteFile() + "]", e);
        }
    }

    public static BufferedWriter latin1Writer(final String filePath, final boolean append) {
        return utf8Writer(lookupPath(filePath), append);
    }

    public static BufferedWriter latin1Writer(final File file, final boolean append) {
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), Constants.LATIN1));
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new IllegalArgumentException("Could not create writer for [" + file.getAbsoluteFile() + "]", e);
        }
    }

    public static boolean isPAMPaymentProcess(final String fileName) {
        return getProcessName(fileName).equals(FileUtil.PAM_PROCESS);
    }

    public static boolean isRRBPaymentProcess(final String fileName) {
        return getProcessName(fileName).equals(FileUtil.RRB_PROCESS);
    }

    public static boolean isOPMPaymentProcess(final String fileName) {
        return getProcessName(fileName).equals(FileUtil.OPM_PROCESS);
    }

    private static String getProcessName(final String fileName) {

        final String[] fileNameArray = fileName.split(Constants.SPILT_REGEX);
        if (fileNameArray.length > 2) {
            return fileNameArray[1];
        }
        return Constants.UNKNOWN;
    }

    public static String dropLastTSNodeFromFilename(final String inputFilePath) {

        int tsNodeIndex = inputFilePath.lastIndexOf('.');
        if (inputFilePath.contains(CTXSPLIT)) {
            tsNodeIndex = inputFilePath.indexOf(".T");
        }

        if (tsNodeIndex > 0) {
            final String tsNodeVal = inputFilePath.substring(tsNodeIndex + 1);
            if (tsNodeVal.startsWith("T")) {
                if (!inputFilePath.contains(CTXSPLIT)) {
                    return inputFilePath.substring(0, tsNodeIndex);
                }
                final int ctxIndex = inputFilePath.indexOf(CTXSPLIT);
                return inputFilePath.substring(0, tsNodeIndex) + inputFilePath.substring(ctxIndex);

            }
        }

        return inputFilePath;

    }

    public static String getPaymentOutputFileName(final String fileNameFromPath, final String fileNameSecondNode) {
        final String[] splitNodes = fileNameFromPath.split(Constants.SPILT_REGEX);
        if (splitNodes.length > 2) {
            String secondNode = splitNodes[1];
            secondNode = secondNode.replaceAll("INP", "O");
            secondNode = fileNameSecondNode.concat(secondNode);
            return fileNameFromPath.replaceAll(splitNodes[1], secondNode);
        }

        return fileNameFromPath;
    }

    private static String getNSVReversalOutputFileName(final String fileNameFromPath, final String fileNameSecondNode) {
        return fileNameFromPath.replaceAll(".NTSREV.", "." + fileNameSecondNode + "NTSRAK.");
    }

    private static String getNSXReversalOutputFileName(final String fileNameFromPath, final String fileNameSecondNode) {
        return fileNameFromPath.replaceAll(".NTXREV.", "." + fileNameSecondNode + "NTXRAK.");
    }

    private static String getNTDOReversalOutputFileName(final String fileNameFromPath,
            final String fileNameSecondNode) {
        return fileNameFromPath.replaceAll(".NTVREV.", "." + fileNameSecondNode + "NTVRAK.");
    }

    public static String getFileNameForAccountingExtract(final LocalDate processingDate, final LocalDate runDate) {
        return getFileNameForAccountingExtract(processingDate, runDate, "E");
    }

    public static String getFileNameForAccountingExtract(final LocalDate processingDate, final LocalDate runDate,
            final String environmentName) {
        final String fileNameTemplate = "FTO" + environmentName + "H.FACDR.PyyMMdd.RyyMMdd.HRC.DAT405.I";
        final String fileName = fileNameTemplate.replaceAll("PyyMMdd",
                "P" + DateTimeUtil.formatDateWithPattern(processingDate, DATE_PATTERN));
        return fileName.replaceAll("RyyMMdd", "R" + DateTimeUtil.formatDateWithPattern(runDate, DATE_PATTERN));
    }

    public static String getFileNameForPotentialMatchExtract(final LocalDate processingDate,
            final String fileNameSecondNode) {
        return getFileNameForPotentialMatchExtract(processingDate, "E", fileNameSecondNode);
    }

    public static String getFileNameForPotentialMatchExtract(final LocalDate processingDate,
            final String environmentName, final String fileNameSecondNode) {
        final String fileNameTemplate = FILE_NAME_BEGIN_INDICATOR + environmentName + "." + fileNameSecondNode
                + "PMATCH.MERGED.DyyMMdd";
        final String fileName = fileNameTemplate.replaceAll("PyyMMdd",
                "P" + DateTimeUtil.formatDateWithPattern(processingDate, DATE_PATTERN));
        return fileName.replaceAll(DATE_PATTERN, DateTimeUtil.formatDateWithPattern(processingDate, DATE_PATTERN));
    }

    public static String getFileNameForAgencyDebtExtract(final LocalDate processingDate, final String environmentName,
            final String creditorAgencyId, final String fileNameSecondNode) {
        final String fileName = FILE_NAME_BEGIN_INDICATOR + environmentName + "." + fileNameSecondNode + "DEBTAX.AG"
                + creditorAgencyId + "S.DyyMMdd";
        return fileName.replaceAll(DATE_PATTERN, DateTimeUtil.formatDateWithPattern(processingDate, DATE_PATTERN));
    }

    public static String getSalaryOutputFileName(final String fileNameFromPath, final String fileNameSecondNode) {

        if (!StringUtils.isBlank(fileNameFromPath)) {
            final String[] fileNameParts = fileNameFromPath.split(Constants.SPILT_REGEX);

            if (fileNameParts.length >= 2) {
                switch (fileNameParts[1]) {
                case SALEXT:
                    return fileNameFromPath.replaceAll(SALEXT, fileNameSecondNode.concat(SMATCH));
                case SALUPD:
                    return fileNameFromPath.replaceAll(SALUPD, fileNameSecondNode.concat("SALAKN"));
                case SALUPI:
                    return fileNameFromPath.replaceAll(SALUPI, fileNameSecondNode.concat("SALAKN"));

                case "NTSEXT":
                    return fileNameFromPath.replaceAll("NTSEXT", fileNameSecondNode.concat("TMATCH"));
                case NTSUPD:
                    return fileNameFromPath.replaceAll(NTSUPD, fileNameSecondNode.concat("NTSAKN"));
                case NTSUPI:
                    return fileNameFromPath.replaceAll(NTSUPI, fileNameSecondNode.concat("NTSAKN"));

                case "NTXEXT":
                    return fileNameFromPath.replaceAll("NTXEXT", fileNameSecondNode.concat("XMATCH"));
                case NTXUPD:
                    return fileNameFromPath.replaceAll(NTXUPD, fileNameSecondNode.concat("NTXAKN"));
                case NTXUPI:
                    return fileNameFromPath.replaceAll(NTXUPI, fileNameSecondNode.concat("NTXAKN"));

                case "NTVEXT":
                    return fileNameFromPath.replaceAll("NTVEXT", fileNameSecondNode.concat("VMATCH"));
                case NTVUPD:
                    return fileNameFromPath.replaceAll(NTVUPD, fileNameSecondNode.concat("NTVAKN"));
                case NTVUPI:
                    return fileNameFromPath.replaceAll(NTVUPI, fileNameSecondNode.concat("NTVAKN"));

                }
            }
        }

        return null;
    }

    public static String getCCRExtractOutputFileName(final String fileNameFromPath, final String fileNameSecondNode) {

        if (!StringUtils.isBlank(fileNameFromPath)) {
            final String[] fileNameParts = fileNameFromPath.split(Constants.SPILT_REGEX);

            if (fileNameParts.length >= 2) {
                if (fileNameParts[1].equals("CCRFLG")) {
                    return fileNameFromPath.replaceAll("CCRFLG", fileNameSecondNode + "CCRUPD");
                }

            }
        }

        return null;
    }

    public static String getNodeDataFromFilePath(final String filePath, final int nodePosition) {
        final String fileNameFromPath = FileUtil.getFileNameFromPath(filePath);
        final String[] fileParts = fileNameFromPath.split(Constants.SPILT_REGEX);
        if (fileParts.length >= nodePosition) {
            return fileParts[nodePosition - 1];
        }

        return null;
    }

    public static String getPaymentTypeFromFilename(final String filePath) {
        final String[] fileNameParts = FileUtil.getFileNameFromPath(filePath).split(Constants.SPILT_REGEX);
        if (fileNameParts.length > 3) {
            return fileNameParts[3];
        }
        return null;
    }

    public static String getSalaryPaymentAgencyIdFromFileName(final String fileName) {
        final String[] fileNameParts = fileName.split("\\.");
        if (fileNameParts.length > 3) {
            if (fileNameParts[3].length() >= 4) {
                return fileNameParts[3].substring(2, 4);
            }
        }
        return null;
    }

    public static String getSalaryONIFileName(final String inputFilePath, final String fileNameSecondNode) {

        String fileNameFromPath = FileUtil.getFileNameFromPath(inputFilePath);
        if (!StringUtils.isBlank(fileNameFromPath)) {
            final String[] fileNameParts = fileNameFromPath.split(Constants.SPILT_REGEX);
            final String lastValueOfFileName = fileNameParts[fileNameParts.length - 1];

            fileNameFromPath = fileNameFromPath.replaceAll(Constants.SPILT_REGEX + lastValueOfFileName,
                    lastValueOfFileName);

            if (fileNameParts.length >= 2) {
                if (fileNameParts[1].equals(SALUPD)) {
                    return fileNameFromPath.replaceAll(SALUPD, fileNameSecondNode.concat("ONI370.HRC.SAL"));
                }

                if (fileNameParts[1].equals(SALUPI)) {
                    return fileNameFromPath.replaceAll(SALUPI, fileNameSecondNode.concat("ONI370.HRC.SAL"));
                }

                if (fileNameParts[1].equals(NTSUPD)) {
                    return fileNameFromPath.replaceAll(NTSUPD, fileNameSecondNode.concat("ONI370.HRC.NTS"));
                }

                if (fileNameParts[1].equals(NTSUPI)) {
                    return fileNameFromPath.replaceAll(NTSUPI, fileNameSecondNode.concat("ONI370.HRC.NTS"));
                }

                if (fileNameParts[1].equals(NTXUPD)) {
                    return fileNameFromPath.replaceAll(NTXUPD, fileNameSecondNode.concat("ONI370.HRC.NTX"));
                }

                if (fileNameParts[1].equals(NTXUPI)) {
                    return fileNameFromPath.replaceAll(NTXUPI, fileNameSecondNode.concat("ONI370.HRC.NTX"));
                }

                if (fileNameParts[1].equals(NTVUPD)) {
                    return fileNameFromPath.replaceAll(NTVUPD, fileNameSecondNode.concat("ONI370.HRC.NTV"));
                }

                if (fileNameParts[1].equals(NTVUPI)) {
                    return fileNameFromPath.replaceAll(NTVUPI, fileNameSecondNode.concat("ONI370.HRC.NTV"));
                }

            }
        }

        return null;
    }

    public static String getMatchedNameControlFileName(final String inputFilePath) {
        final String dataFileName = FileUtil.getFileNameFromPath(inputFilePath);

        final String[] fileNameArray = dataFileName.split(Constants.SPILT_REGEX);

        return fileNameArray[0] + Constants.FILE_NAME_NODE_SEPERATOR + fileNameArray[1]
                + Constants.FILE_NAME_NODE_SEPERATOR + fileNameArray[2] + Constants.CNTL_NODE + fileNameArray[3];
    }

    public static boolean isCTXPaymentProcess(final String name) {
        return getProcessName(name).contains("CTX");
    }

    public static boolean isSalaryMatchProcess(final String name) {
        return getProcessName(name).contains("EXT");
    }

    public static boolean isSalaryUpdateProcess(final String name) {
        return getProcessName(name).contains("UPD");
    }

    public static boolean isDebtLoadProcess(final String name) {
        return getProcessName(name).contains("DL");
    }

    public static String getPaymentAgencyIDFromFileName(final String fileName) {
        if (fileName == null) {
            return null;
        }
        if (fileName.contains("TCSREV")) {
            return "TCS";
        }
        if (fileName.contains(FILE_NAME_WITH_AG)) {
            final int index = fileName.lastIndexOf(FILE_NAME_WITH_AG);

            if (index > 0 && fileName.length() > index + 4) {
                return fileName.substring(index + 3, index + 5);
            }

        }

        return null;
    }

    public static String getCreditorAgencyIDFromDebtExtractFileName(final String fileName) {
        if (fileName == null) {
            return null;
        }
        if (fileName.contains(FILE_NAME_WITH_AG)) {
            final int index = fileName.lastIndexOf(FILE_NAME_WITH_AG);

            if (index > 0 && fileName.length() > index + 4) {
                return fileName.substring(index + 3, index + 5);
            }

        }

        return null;
    }

    public static List<File> getFilesBasedOnPattern(final String fileNamePattern, final String fileDirectory) {
        final File directory = lookupPath(fileDirectory);
        final List<File> selectedFiles = Arrays.asList(directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.getName().contains(fileNamePattern);
            }

        }));

        return selectedFiles;
    }

    public static void move(final String originalPath, final String targetPath) {
        try {
            FileUtils.moveFile(lookupPath(originalPath), lookupPath(targetPath));
        } catch (final IOException e) {
            logger.error(String.format("Could not move file from [%s] to [%s] - %s", originalPath, targetPath,
                    e.getMessage()));
        }
    }

    public static void delete(final String fileName) {
        if (fileName == null) {
            return;
        }

        final File file = lookupPath(fileName);
        if (file.exists()) {
            file.delete();
        }

    }

    public static String rootDirectory() {
        return System.getProperty("top.root", "/theroot");
    }

    private static String fileNameFrom(final String outputFilePath, final int i) {
        return outputFilePath + Constants.FILE_SUFFIX + i;
    }

    public static void deleteFilesWithExtension(final String directoryName, final String extension) {

        final File dir = lookupPath(directoryName);
        final String[] allFiles = dir.list();
        for (final String file : allFiles) {
            if (file.endsWith(extension)) {
                new File(directoryName + "/" + file).delete();
            }
        }
    }

    public static boolean isfileForPaymentAgencyAndOfficeId(final String fileName, final String paymentAgency,
            final String spaOffice) {
        logger.info("isfileForPaymentAgencyAndOfficeId - File Name : " + fileName);
        final String[] fileNameParts = fileName.split("\\.");
        if (fileNameParts.length > 3) {
            final String agencyIdFromFileNameNode = fileNameParts[3].substring(2, 4);
            logger.info("agencyIdFromFileNameNode : " + agencyIdFromFileNameNode);
            String spaOfficeIdFromFileNameNode = null;

            // File Name : FDMD.IPSMATCH.C201349.AG12.PCIV.SUSP
            if (fileNameParts.length > 4) {
                spaOfficeIdFromFileNameNode = fileNameParts[4].substring(1, 4);
            }

            logger.info("spaOfficeIdFromFileNameNode : " + spaOfficeIdFromFileNameNode);

            if (paymentAgency.equals(agencyIdFromFileNameNode) && !fileName.contains("SALSPLIT")) {
                if (spaOffice.equals(Constants.ALL)) {
                    logger.info("File Name matches ALL criteria" + fileName);
                    return true;
                }
                if (spaOffice.equals(spaOfficeIdFromFileNameNode)) {
                    logger.info("File Name matches specific sub code criteria" + fileName);
                    return true;
                }
            }
        }

        logger.info("File Name is a NO MATCH: " + fileName);
        return false;
    }

    public static String getFileNameForWarningLetterExtract(final String paymentSource, final LocalDate paymentDate,
            final String environmentName, final String fileNameSecondNode) {
        return generateFileName(paymentDate, environmentName, fileNameSecondNode,
                "WLT" + paymentSource + ".HRC." + paymentSource);
    }

    public static String getFileNameForSalaryWarningLetterExtract(final String paymentSource,
            final LocalDate paymentDate, final String environmentName, final String fileNameSecondNode,
            final String paymentAgencyId) {
        return generateFileName(paymentDate, environmentName, fileNameSecondNode,
                "WLT" + paymentSource + ".HRC." + paymentSource + ".AG" + paymentAgencyId);
    }

    private static String generateFileName(final LocalDate processingDate, final String environmentName,
            final String fileNameSecondNode, final String suffix) {
        final String fileName = FILE_NAME_BEGIN_INDICATOR + environmentName + "." + fileNameSecondNode + suffix
                + ".DyyMMdd";
        return fileName.replaceAll(DATE_PATTERN, DateTimeUtil.formatDateWithPattern(processingDate, DATE_PATTERN));
    }

    private static String generateFileName(final DateTime processingDate, final String environmentName,
            final String fileNameSecondNode, final String suffix) {
        String fileName = FILE_NAME_BEGIN_INDICATOR + environmentName + "." + fileNameSecondNode + suffix + ".DyyMMdd"
                + ".THHmm";
        final String dateWithPattern = DateTimeUtil.formatDateWithPattern(processingDate, DATE_TIME_PATTERN);
        fileName = fileName.replaceAll(DATE_PATTERN, dateWithPattern.substring(0, 6));
        fileName = fileName.replaceAll(TIME_PATTERN, dateWithPattern.substring(7));
        return fileName;
    }

    public static String getPartitionFromCreditElectExtractFileName(final String fileName) {

        if (StringUtils.isBlank(fileName)) {
            return null;
        }

        final String[] tokens = fileName.split("\\.");

        return tokens.length >= 3 ? tokens[2].substring("TO-IRS".length()) : null;
    }

    public static File lookupPath(final String filePath) {
        return lookupPath(filePath, false);
    }

    public static File lookupPath(final String filePath, final boolean fileMustExist) {
        final File file = new File(filePath);

        if (fileMustExist && !file.exists()) {
            throw new IllegalArgumentException("Non existent file path: " + filePath);
        }

        return file;
    }

    public static List<File> retrieveFiles(final String filePath, final String fileFiltername,
            final boolean fileMustContain) {
        final File directory = lookupPath(filePath, true);

        final List<File> listFiles = Arrays.asList(directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                if (fileMustContain && file.getName().contains(fileFiltername)
                        || !fileMustContain && !file.getName().contains(fileFiltername)) {
                    return true;
                }
                return false;
            }

        }));
        return listFiles;
    }

}
