package org.facil.practice;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:58 AM
 */
public class DefaultFileHandlerTest {

    private DefaultFileHandler defaultFileHandler;
    private Answer answerIOExceptionForFileOperations;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    public DefaultFileHandlerTest(){
        this.defaultFileHandler = new DefaultFileHandler();
        answerIOExceptionForFileOperations = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("getName")){
                    return "test.txt";
                }
                if(true) {
                    throw new IOException();
                }
                return null;
            }
        };
    }

    @Test
    public void SaveFileNullArgs(){
        assertFalse(defaultFileHandler.saveFile(null, null));
    }

    @Test
    public void SaveFileNullArgForFile(){
        assertFalse(defaultFileHandler.saveFile(null, "Hello"));
    }

    @Test
    public void SaveFileNullArgForVersion(){
        assertFalse(defaultFileHandler.saveFile(new File("test.txt"), null));
    }

    @Test
    public void saveFile() throws IOException {
        File file = temporaryFolder.newFile("test.txt");
        assertTrue(defaultFileHandler.saveFile(file, "Hello"));
    }

    @Test
    public void saveFileExists() throws IOException {
        File file = temporaryFolder.newFile("test.txt");
        defaultFileHandler.saveFile(file, "Hello");
        assertTrue(file.exists());
    }

    @Test
    public void SaveFileIOException(){
        File file = mock(File.class, answerIOExceptionForFileOperations);
        assertFalse(defaultFileHandler.saveFile(file, "Hello"));
        verify(file).getName();
    }

    @Test
    public void readFileNullArgForFile(){
        assertNull(defaultFileHandler.readFile(null));
    }

    @Test
    public void readFile() throws IOException {
        File file = temporaryFolder.newFile("test.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write("Hello");
        bufferedWriter.close();
        assertEquals("Hello", defaultFileHandler.readFile(file));
    }


    @Test
    public void readFileNotSame() throws IOException {
        File file = temporaryFolder.newFile("test.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write("Bye");
        bufferedWriter.close();
        assertNotSame("Hello", defaultFileHandler.readFile(file));
    }

    @Test
    public void readFileIOException(){
        File file = mock(File.class, answerIOExceptionForFileOperations);
        assertNull(defaultFileHandler.readFile(file));
        verify(file).getName();
    }

}
