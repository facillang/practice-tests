package org.facil.practice;

import com.amazonaws.services.lambda.AWSLambda;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.facil.practice.ReflectionUtils.setNonAccesibleField;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:55 AM
 */
public class AbstractLambdaFileHandlerTest {

    private AbstractLambdaFileHandler abstractLambdaFileHandler;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private String fileName = "test.txt";
    private File file;
    private AWSLambda awsLambda;
    private String functionName = "getSomething";

    public AbstractLambdaFileHandlerTest() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        abstractLambdaFileHandler = mock(AbstractLambdaFileHandler.class, CALLS_REAL_METHODS);
        awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);

        setNonAccesibleField(abstractLambdaFileHandler.getClass(),abstractLambdaFileHandler, "fileName", fileName);
        setNonAccesibleField(abstractLambdaFileHandler.getClass(),abstractLambdaFileHandler, "awsLambda", awsLambda);
        setNonAccesibleField(abstractLambdaFileHandler.getClass(),abstractLambdaFileHandler, "functionName", functionName);

    }

    @Test
    public void createFileIfNotExists() throws IOException, NoSuchFieldException, IllegalAccessException {
        file = temporaryFolder.newFile(fileName);
        if(file.exists() && !file.delete()){
            fail();
        }
        setNonAccesibleField(abstractLambdaFileHandler.getClass(),abstractLambdaFileHandler, "file", file);
        assertTrue(abstractLambdaFileHandler.createFileIfNotExists());
    }

    @Test
    public void createFileIfNotExistsVerifyMethodCalls() throws IOException, NoSuchFieldException, IllegalAccessException {
        final File helperFile = temporaryFolder.newFile(fileName);
        file = mock(File.class, new AnswerForFile(helperFile));
        if(helperFile.exists() && !helperFile.delete()){
            fail();
        }
        setNonAccesibleField(abstractLambdaFileHandler.getClass(),abstractLambdaFileHandler, "file", file);
        assertTrue(abstractLambdaFileHandler.createFileIfNotExists());
        verify(file,times(1)).exists();
        verify(file,times(1)).createNewFile();
    }

    @Test
    public void dontCreateFileIfFileExists() throws IOException, NoSuchFieldException, IllegalAccessException {
        final File helperFile = temporaryFolder.newFile(fileName);
        file = mock(File.class, new AnswerForFile(helperFile));
        setNonAccesibleField(abstractLambdaFileHandler.getClass(),abstractLambdaFileHandler, "file", file);
        assertTrue(abstractLambdaFileHandler.createFileIfNotExists());
        verify(file,times(1)).exists();
        verify(file,never()).createNewFile();
    }


    @Test
    public void createFileIfNotExistsCatchAndReturnFalse() throws NoSuchFieldException, IllegalAccessException {
        Answer answerIOExceptionForFileOperations = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("exists")){
                    return Boolean.FALSE;
                }
                if(true) {
                    throw new IOException();
                }
                return null;
            }
        };
        file = mock(File.class, answerIOExceptionForFileOperations);
        setNonAccesibleField(abstractLambdaFileHandler.getClass(),abstractLambdaFileHandler, "file", file);
        assertFalse(abstractLambdaFileHandler.createFileIfNotExists());
    }

    @Test(expected = IOException.class)
    public void createFileIfNotExistsException() throws NoSuchFieldException, IllegalAccessException {
        Answer answerIOExceptionForAllFileOperations = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(true) {
                    throw new IOException();
                }
                return null;
            }
        };
        file = mock(File.class, answerIOExceptionForAllFileOperations);
        setNonAccesibleField(abstractLambdaFileHandler.getClass(),abstractLambdaFileHandler, "file", file);
        abstractLambdaFileHandler.createFileIfNotExists();
    }

    @Test
    public void dontCreateFileIfFileOperationsReturnFalse() throws NoSuchFieldException, IllegalAccessException {
        Answer answerIOExceptionForFileOperations = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("createNewFile")){
                    return Boolean.FALSE;
                }
                if(invocation.getMethod().getName().equals("exists")){
                    return Boolean.FALSE;
                }
                if(true) {
                    throw new IOException();
                }
                return null;
            }
        };
        file = mock(File.class, answerIOExceptionForFileOperations);
        setNonAccesibleField(abstractLambdaFileHandler.getClass(),abstractLambdaFileHandler, "file", file);
        assertFalse(abstractLambdaFileHandler.createFileIfNotExists());
    }


    private class AnswerForFile implements Answer {

        private File helperFile;
        public AnswerForFile(File helperFile){
            this.helperFile = helperFile;
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            if(invocation.getMethod().getName().equals("createNewFile")){
                return helperFile.createNewFile();
            }
            if(invocation.getMethod().getName().equals("exists")){
                return helperFile.exists();
            }
            return null;
        }
    }


}
