package demo.legacy;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;

public class CustomJobExecutionListener implements JobExecutionListener {
    private static final Logger LOG = LoggerFactory.getLogger(CustomJobExecutionListener.class);

    LocalDate initialDate = new LocalDate();

    @Override
    public void beforeJob(final JobExecution jobExecution) {
        final JobParameters jobParameters = jobExecution.getJobParameters();
        final String filePath = jobParameters.getString("input.file.path");

        final String pPath = jobParameters.getString("processing.file.path");
        final String procDate = jobParameters.getDate("process.date") != null
                ? DateTimeUtil.formatDateMdyNoSlashes(jobParameters.getDate("process.date"))
                : Constants.BLANK;
        String logFileName = !StringUtils.isBlank(filePath) ? FileUtil.getFileNameFromPath(filePath)
                : jobExecution.getJobInstance().getJobName().concat(procDate);

        if (jobExecution.getJobInstance().getJobName().equals("agencyDebtExtract")) {
            final LocalDate processingExtractDate = DateTimeUtil
                    .convertToLocalDate(jobParameters.getDate("processing.date"));
            logFileName = jobExecution.getJobInstance().getJobName() + jobParameters.getString("agencyId")
                    + DateTimeUtil.formatDateYmdNoDashes(processingExtractDate);
            LOG.info("Log file name :" + logFileName);
        }

        if (jobExecution.getJobInstance().getJobName().equals("postMatchExtract")) {
            logFileName = jobParameters.getString("post.match.file.name");
            LOG.info("Log file name :" + logFileName);
        }

        if (jobExecution.getJobInstance().getJobName().equals("tpkExtract")) {
            logFileName = jobParameters.getString("tpk.offsets.extract.filename");
            LOG.info("Log file name :" + logFileName);
        }

        if (jobExecution.getJobInstance().getJobName().equals("dnpExtract")) {
            logFileName = jobParameters.getString("dnp.extract.filename");
            LOG.info("Log file name :" + logFileName);
        }

        if (jobExecution.getJobInstance().getJobName().equals("paymentExtract")) {
            logFileName = jobParameters.getString("payment.extract.filename");
            LOG.info("Log file name :" + logFileName);
        }

        if (jobExecution.getJobInstance().getJobName().equals("creditElectExtract")) {
            logFileName = jobParameters.getString("credit.elect.extract.filename");
            LOG.info("Log file name :" + logFileName);
        }

        MDC.remove("inputFileName");
        MDC.put("inputFileName", logFileName);

        if (StringUtils.isBlank(filePath)) {
            return;
        }

        FileUtil.move(filePath, pPath);

        final String icfp = jobParameters.getString("input.control.file.path");

        if (!StringUtils.isBlank(icfp)) {
            FileUtil.move(icfp, Constants.DEFAULT_PROCESSING_FILE_PATH + FileUtil.getFileNameFromPath(icfp));
        }

        final String mainCTXFile = getMainCTXFileIfCTXProcess(logFileName, "");
        if (!StringUtils.isBlank(mainCTXFile)) {
            FileUtil.move(Constants.DEFAULT_INPUT_FILE_PATH + mainCTXFile,
                    Constants.DEFAULT_PROCESSING_FILE_PATH + mainCTXFile);
        }
    }

    private String getMainCTXFileIfCTXProcess(final String inputFileName, String secondaryFile) {
        if (FileUtil.isCTXPaymentProcess(inputFileName) && inputFileName.contains("CTXSPLIT")) {
            return FileUtil.setupFile(inputFileName);
        }
        return null;
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        final JobParameters jobParameters = jobExecution.getJobParameters();
        final String processingFilePath = jobParameters.getString("processing.file.path");
        String inputControlFilePath = jobParameters.getString("input.control.file.path");

        if (StringUtils.isBlank(processingFilePath)) {
            return;
        }

        final String processedFilePath = Constants.DEFAULT_PROCESSED_FILE_PATH
                + FileUtil.getFileNameFromPath(processingFilePath);
        String processedControlFilePath = null;
        if (!StringUtils.isBlank(inputControlFilePath)) {
            processedControlFilePath = Constants.DEFAULT_PROCESSED_FILE_PATH
                    + FileUtil.getFileNameFromPath(inputControlFilePath);
        }

        if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
            FileUtil.move(processingFilePath, processedFilePath);
            if (!StringUtils.isBlank(processedControlFilePath)) {
                inputControlFilePath = Constants.DEFAULT_PROCESSING_FILE_PATH
                        + FileUtil.getFileNameFromPath(inputControlFilePath);
                FileUtil.move(inputControlFilePath, processedControlFilePath);
            }
        }

    }

}
