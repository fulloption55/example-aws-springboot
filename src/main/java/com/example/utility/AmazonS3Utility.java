package com.example.utility;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class AmazonS3Utility {
    @Autowired
    private AmazonS3Client amazonS3Client;

    private static Logger logger = LoggerFactory.getLogger(AmazonS3Utility.class);


    public List<S3ObjectSummary> listItems(String bucket, String pathFolder) {
        ListObjectsV2Result objectListing = amazonS3Client.listObjectsV2(new ListObjectsV2Request().withBucketName(bucket).withPrefix(pathFolder));

        List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();

        logger.info("Found {} file(s) bucket {} in folder : {}", s3ObjectSummaries.size() - 1, bucket, pathFolder);
        return s3ObjectSummaries;
    }


    public List<String> readTextFromS3(String bucket, String pathFolder) throws IOException {
        logger.info("Start loading file : {} , in bucket : {}", pathFolder, bucket);
        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucket, pathFolder));

        BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));

        String line;
        List<String> stringFileContent = new ArrayList();
        int countLine = 0;
        while ((line = reader.readLine()) != null) {
            countLine++;
            stringFileContent.add(line);
            logger.debug("File {} line {} : {}", pathFolder, countLine, line);
        }

        reader.close();
        s3Object.close();
        logger.info("End loading file : {} , in bucket : {}", pathFolder, bucket);
        return stringFileContent;

    }

    public CopyObjectResult copyFile(String sourceBucketName, String sourcePathName, String destinationBucketName, String destinationPathName) throws AmazonClientException, AmazonServiceException {
        logger.info("Copying file from {}/{} to {}/{} on S3", sourceBucketName, sourcePathName, destinationBucketName, destinationPathName);
        CopyObjectRequest request = new CopyObjectRequest(sourceBucketName, sourcePathName, destinationBucketName, destinationPathName);
        CopyObjectResult result = amazonS3Client.copyObject(request);
        return result;
    }

    public void deleteFile(String bucketName, String pathName) throws AmazonClientException, AmazonServiceException {
        logger.info("Delete file {}/{} on S3", bucketName, pathName);
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, pathName);
        amazonS3Client.deleteObject(request);
    }

    public String getFileNameFromKey(String keyPath) {
        String[] bits = keyPath.split("/");
        String fileName = bits[bits.length - 1];
        return fileName;
    }


}
