package org.facil.practice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.AWSLambda;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:55 AM
 */
public class AbstractLambdaFileHandlerTest {

    @Test
    public void createFileIfNotExists() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        String functionName = "Lambda";
        String fileName = "aFile";
        File file = mock(File.class);
        AWSLambda awsLambda = mock(AWSLambda.class);
        AbstractLambdaFileHandler abstractLambdaFileHandler
            = spy(new AbstractLambdaFileHandler(functionName, fileName, file, awsLambda) {});

        when(file.exists()).thenReturn(true).thenReturn(false);
        when(file.createNewFile()).thenReturn(true).thenReturn(false).thenThrow(new IOException());

        assertTrue(abstractLambdaFileHandler.createFileIfNotExists());
        assertTrue(abstractLambdaFileHandler.createFileIfNotExists());
        assertFalse(abstractLambdaFileHandler.createFileIfNotExists());
        String errInfo = String.format("^error^ Could not create file [ %s ]^r^%n", fileName);
        assertEquals(errInfo, outputStream.toString());
        outputStream.reset();
        assertFalse(abstractLambdaFileHandler.createFileIfNotExists());
        assertEquals(errInfo, outputStream.toString());

        verify(abstractLambdaFileHandler, times(4)).createFileIfNotExists();
        verify(file, times(4)).exists();
        verify(file, times(3)).createNewFile();

        System.setOut(System.out);
    }

}
