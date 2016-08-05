package com.example.utility;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AmazonS3Utility.class, BufferedReader.class, InputStreamReader.class})
@PowerMockIgnore("javax.management.*")
public class AmazonS3UtilityTest {

    @InjectMocks
    private AmazonS3Utility amazonS3Utility;

    @Mock
    private AmazonS3Client amazonS3ClientMock;
    @Mock
    private ListObjectsV2Result objectListingMock;
    @Mock
    private S3Object s3Object;

    private String bucket;
    private String keyPath;
    private String fileName;
    private List<S3ObjectSummary> listS3ObjectSummary;
    private S3ObjectSummary s3ObjectSummary;


    @Before
    public void setup() {
        bucket = "nice-bucket";
        keyPath = "myvideo/secret";
        fileName = "heyzo-8823.txt";

        s3ObjectSummary = new S3ObjectSummary();
        listS3ObjectSummary = new ArrayList<>();

        s3ObjectSummary.setBucketName(bucket);
        s3ObjectSummary.setKey(keyPath);
        listS3ObjectSummary.add(s3ObjectSummary);

        when(objectListingMock.getObjectSummaries()).thenReturn(listS3ObjectSummary);
        when(objectListingMock.getBucketName()).thenReturn(bucket);

    }

    @Test
    public void test_listItems_success_returnSizeEqualOne() {

        when(amazonS3ClientMock.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(objectListingMock);

        List returnList = amazonS3Utility.listItems(bucket, keyPath);

        assertEquals(1, returnList.size());

    }

    @Test
    public void test_readFromS3_success_calledAmazonClientToGetObjectOnce() throws Exception {

        BufferedReader bufferReaderMock = mock(BufferedReader.class);
        InputStreamReader inputStreamReaderMock = mock(InputStreamReader.class);
        S3ObjectInputStream s3ObjectInputStreamMock = mock(S3ObjectInputStream.class);

        when(amazonS3ClientMock.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStreamMock);
        whenNew(InputStreamReader.class).withAnyArguments().thenReturn(inputStreamReaderMock);
        whenNew(BufferedReader.class).withAnyArguments().thenReturn(bufferReaderMock);
        when(bufferReaderMock.readLine()).thenReturn("line number one")
                .thenReturn("line number two")
                .thenReturn(null);


        amazonS3Utility.readTextFromS3(bucket, keyPath + fileName);

        verify(amazonS3ClientMock, times(1)).getObject(any(GetObjectRequest.class));

    }

    @Test
    public void test_readFromS3_success_returnListString() throws Exception {

        BufferedReader bufferReaderMock = mock(BufferedReader.class);
        InputStreamReader inputStreamReaderMock = mock(InputStreamReader.class);
        S3ObjectInputStream s3ObjectInputStreamMock = mock(S3ObjectInputStream.class);

        when(amazonS3ClientMock.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStreamMock);
        whenNew(InputStreamReader.class).withAnyArguments().thenReturn(inputStreamReaderMock);
        whenNew(BufferedReader.class).withAnyArguments().thenReturn(bufferReaderMock);
        when(bufferReaderMock.readLine()).thenReturn("line number one").thenReturn(null);

        List<String> returedListString = amazonS3Utility.readTextFromS3(bucket, keyPath + fileName);

        assertEquals(1, returedListString.size());

    }

    @Test
    public void test_copyFile_success_andReturnCopyObjectResult() throws Exception {
        // Given
        String sourcePathName = "source/in.txt", destinationPathName = "destination/out.txt";
        when(amazonS3ClientMock.copyObject(any(CopyObjectRequest.class))).thenReturn(new CopyObjectResult());

        // When
        CopyObjectResult result = amazonS3Utility.copyFile(bucket, sourcePathName, bucket, destinationPathName);

        // Then
        verify(amazonS3ClientMock, times(1)).copyObject(any(CopyObjectRequest.class));
        assertNotNull(result);
    }

    @Test(expected = AmazonServiceException.class)
    public void test_copyFile_fail_becauseSourceFileNotFound_mustBeThrowAmazonServiceException() throws Exception {
        // Given
        String sourcePathName = "source/in.txt", destinationPathName = "destination/out.txt";
        doThrow(new AmazonServiceException("Source file does not found")).when(amazonS3ClientMock).copyObject(any(CopyObjectRequest.class));

        // When
        CopyObjectResult result = amazonS3Utility.copyFile(bucket, sourcePathName, bucket, destinationPathName);

        // Then
        verify(amazonS3ClientMock, times(1)).copyObject(any(CopyObjectRequest.class));
        assertNull(result);
    }

    @Test
    public void test_deleteFile_success_andCallDeleteObjectOnce() throws Exception {
        // Given
        String sourcePathName = "source/in.txt";

        // When
        amazonS3Utility.deleteFile(bucket, sourcePathName);

        // Then
        verify(amazonS3ClientMock, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test(expected = AmazonServiceException.class)
    public void test_deleteFile_fail_becauseSourceFileNotFound_mustBeThrowAmazonServiceException() throws Exception {
        // Given
        String sourcePathName = "source/in.txt";
        doThrow(new AmazonServiceException("Source file does not found")).when(amazonS3ClientMock).deleteObject(any(DeleteObjectRequest.class));

        // When
        amazonS3Utility.deleteFile(bucket, sourcePathName);

        // Then
        verify(amazonS3ClientMock, times(1)).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    public void test_getFileNameFromKey_returnLastOfStringAfterSlash() {
        String key1 = "/folder/notepad.txt";
        String key2 = "/read_me.txt";
        String key3 = "/sada/dsada/dasdasd/asdasd/asdasd/asdasd/asd/asd/asd/asd/asd/xxx.mkv";
        String key4 = "//dd";


        String fileName1 = amazonS3Utility.getFileNameFromKey(key1);
        String fileName2 = amazonS3Utility.getFileNameFromKey(key2);
        String fileName3 = amazonS3Utility.getFileNameFromKey(key3);
        String fileName4 = amazonS3Utility.getFileNameFromKey(key4);

        assertEquals("notepad.txt", fileName1);
        assertEquals("read_me.txt", fileName2);
        assertEquals("xxx.mkv", fileName3);
        assertEquals("dd", fileName4);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void test_getFileNameFromKey_whenKeyIsEmpty() {
        String emptyKey = "/";

        amazonS3Utility.getFileNameFromKey(emptyKey);

    }


}
