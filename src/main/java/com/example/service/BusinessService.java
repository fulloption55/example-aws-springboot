package com.example.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.utility.AmazonS3Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BusinessService {

    @Autowired
    AmazonS3Utility amazonS3Utility;

    private static String BUCKET = "aws-ttmn-th-alp-ersftpdownloader-sch";

    private static String FOLDER_GRAPE_RECCONCILIATION_FILE = "grape/reconciliation/raw";

    public void doCronJob() {

        System.out.println("Puck you");

    }

    public List<S3ObjectSummary> getReconciliationFiles() {

        return amazonS3Utility.list(BUCKET, FOLDER_GRAPE_RECCONCILIATION_FILE);
    }

    public void readReconciliationFiles(List<S3ObjectSummary> listFile) throws IOException {

        for (S3ObjectSummary s3ObjectSummary : listFile) {
            amazonS3Utility.readFromS3(s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey());
        }

    }
}
