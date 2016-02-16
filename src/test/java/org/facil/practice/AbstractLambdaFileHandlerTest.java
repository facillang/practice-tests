package org.facil.practice;

import com.amazonaws.services.lambda.AWSLambda;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Before;
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

    //Use an inheritance class to test the abstract class
    class AbstractClassImplement extends AbstractLambdaFileHandler{
        public AbstractClassImplement(String functionName, String fileName, File file, AWSLambda awsLambda){
            super(functionName, fileName, file, awsLambda);
        }
    }

    String functionName;
    String fileName;
    File file;
    AWSLambda awsLambda;
    AbstractClassImplement abstractLambdaFileHandler;

    @Before
    public void initialize(){
        functionName = "FunctionName";
        fileName = "FileName";
        file = mock(File.class);
        awsLambda = mock(AWSLambda.class);
        abstractLambdaFileHandler = new AbstractClassImplement(functionName, fileName, file, awsLambda);
    }

    @Test
    public void createFileIfNotExists() {
        // TODO
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String errorMessage = "^error^ Could not create file [ FileName ]^r^";


        when(file.exists()).thenReturn(true);
        assertTrue(abstractLambdaFileHandler.createFileIfNotExists());
        when(file.exists()).thenReturn(false);
        try {
            when(file.createNewFile()).thenReturn(true);
        }catch (IOException e){}
        assertTrue(abstractLambdaFileHandler.createFileIfNotExists());

        try {
            when(file.createNewFile()).thenReturn(false);
        }catch (IOException e){}
        assertTrue(!abstractLambdaFileHandler.createFileIfNotExists());
        assertTrue(outContent.toString().indexOf(errorMessage) >= 0);

        try {
            doThrow(new IOException("UnitTest")).when(file).createNewFile();
        }catch (IOException e){
            assertTrue(!abstractLambdaFileHandler.createFileIfNotExists());
            assertTrue(outContent.toString().indexOf(errorMessage) >= 0);
        }


    }

}
