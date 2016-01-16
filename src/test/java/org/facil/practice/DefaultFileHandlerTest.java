package org.facil.practice;

import org.junit.Test;
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

    @Test
    public void SaveFileNullArgs(){
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        assertFalse(defaultFileHandler.saveFile(null, null));
    }

    @Test
    public void SaveFileNullArgForFile(){
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        assertFalse(defaultFileHandler.saveFile(null, "version 9.9"));
    }

    @Test
    public void SaveFileNullArgForVersion(){
        File file = mock(File.class);
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        assertFalse(defaultFileHandler.saveFile(file, null));
    }

    @Test
    public void saveFile() throws NoSuchFieldException, IllegalAccessException {
        File file = spy(new File("test"));
        String version = "9.9";
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        assertTrue(defaultFileHandler.saveFile(file,version));
        verify(file).getPath();
        if(file.exists()){
            file.delete();
        }
    }

    @Test
    public void saveFileVerifyFileExists() throws Exception {
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        File file = spy(new File("test"));
        defaultFileHandler.saveFile(file, "version 9.9");
        assertTrue(file.exists());
        verify(file).getPath();
        if(file.exists()){
            file.delete();
        }
    }

    @Test
    public void SaveFileIOException(){
        //using answer seems more appropriate here than using when
        File file = mock(File.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("getName")){
                    return "test";
                }else{
                    throw new IOException();
                }
            }
        });
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        assertFalse(defaultFileHandler.saveFile(file, "version 9.9"));
        verify(file).getName();
    }

    @Test
    public void readFileNullArgForFile(){
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        assertNull(defaultFileHandler.readFile(null));
    }

    @Test
    public void readFile() throws IOException {
        String version = "version 9.9";
        File file = spy(new File("test.txt"));
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(version);
        }
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        assertEquals(version, defaultFileHandler.readFile(file));
        if(file.exists()){
            file.delete();
        }
        verify(file, times(2)).getPath();
    }


    @Test
    public void readFileNotSame() throws IOException {
        String version = "version 9.9";
        File file = spy(new File("test.txt"));
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(version);
        }
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        assertNotSame("version 9.8", defaultFileHandler.readFile(file));
        if(file.exists()){
            file.delete();
        }
        verify(file, times(2)).getPath();
    }

    @Test
    public void readFileIOException(){
        File file = mock(File.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("getName")){
                    return "test";
                }else{
                    throw new IOException();
                }
            }
        });
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();
        assertNull(defaultFileHandler.readFile(file));
        verify(file).getName();
    }

}
