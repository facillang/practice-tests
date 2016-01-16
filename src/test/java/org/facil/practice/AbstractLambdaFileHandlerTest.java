package org.facil.practice;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.facil.practice.ReflectionUtils.setNonAccessibleField;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:55 AM
 */
public class AbstractLambdaFileHandlerTest {

    @Test
    public void createFileIfNotExists() throws IOException, NoSuchFieldException, IllegalAccessException {
        File file = mock(File.class);
        when(file.exists()).thenReturn(Boolean.FALSE);
        when(file.createNewFile()).thenReturn(Boolean.TRUE);
        AbstractLambdaFileHandler abstractLambdaFileHandler = mock(AbstractLambdaFileHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaFileHandler, "file", file);
        assertTrue(abstractLambdaFileHandler.createFileIfNotExists());
        verify(file,times(1)).exists();
        verify(file,times(1)).createNewFile();
    }


    @Test
    public void donotCreateNewFileIfExists() throws IOException, NoSuchFieldException, IllegalAccessException {
        File file = mock(File.class);
        when(file.exists()).thenReturn(Boolean.TRUE);
        when(file.createNewFile()).thenReturn(Boolean.FALSE);
        AbstractLambdaFileHandler abstractLambdaFileHandler = mock(AbstractLambdaFileHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaFileHandler, "file", file);
        assertTrue(abstractLambdaFileHandler.createFileIfNotExists());
        verify(file,times(1)).exists();
        verify(file,never()).createNewFile();
    }

    @Test
    public void donotCreateFileWhenFileOperationsReturnFalse() throws NoSuchFieldException, IllegalAccessException, IOException {
        String fileName = "test.txt";
        File file = mock(File.class);
        when(file.exists()).thenReturn(Boolean.FALSE);
        when(file.createNewFile()).thenReturn(Boolean.FALSE);
        AbstractLambdaFileHandler abstractLambdaFileHandler = mock(AbstractLambdaFileHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaFileHandler, "file", file);
        setNonAccessibleField(abstractLambdaFileHandler, "fileName", fileName);
        assertFalse(abstractLambdaFileHandler.createFileIfNotExists());
        verify(file,times(1)).exists();
        verify(file,times(1)).createNewFile();
    }

    @Test(expected = IOException.class)
    public void createFileIfNotExistsWhenExceptionForFileOperations() throws NoSuchFieldException, IllegalAccessException, IOException {
        File file = mock(File.class);
        when(file.exists()).thenThrow(IOException.class);
        AbstractLambdaFileHandler abstractLambdaFileHandler = mock(AbstractLambdaFileHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaFileHandler, "file", file);
        abstractLambdaFileHandler.createFileIfNotExists();
        verify(file,times(1)).exists();
        verify(file,never()).createNewFile();
    }

    @Test
    public void createFileIfNotExistsFalseWhenIOException() throws NoSuchFieldException, IllegalAccessException, IOException {
        String fileName = "test.txt";
        File file = mock(File.class);
        when(file.exists()).thenReturn(Boolean.FALSE);
        when(file.createNewFile()).thenThrow(IOException.class);
        AbstractLambdaFileHandler abstractLambdaFileHandler = mock(AbstractLambdaFileHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaFileHandler, "file", file);
        setNonAccessibleField(abstractLambdaFileHandler, "fileName", fileName);
        assertFalse(abstractLambdaFileHandler.createFileIfNotExists());
        verify(file,times(1)).exists();
        verify(file,times(1)).createNewFile();
    }

}
