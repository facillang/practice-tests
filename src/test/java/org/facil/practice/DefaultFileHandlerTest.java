package org.facil.practice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:58 AM
 */
public class DefaultFileHandlerTest {

    private final OutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream printStream = new PrintStream(outputStream);

    @Before
    public void setup() {
        System.setOut(printStream);
    }

    @After
    public void tearDown() {
        System.setOut(System.out);
    }

    @Test
    public void saveFile() throws IOException {
        String version = "0.1";
        String fileName = "aFile";
        File file = spy(new File(fileName));
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();

        when(file.getName()).thenReturn(fileName);
        assertFalse(defaultFileHandler.saveFile(null, version));
        assertFalse(defaultFileHandler.saveFile(file, null));
        assertTrue(defaultFileHandler.saveFile(file, version));

        when(file.getPath()).thenAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IOException();
            }
        });
        assertFalse(defaultFileHandler.saveFile(file, version));
        String errInfo = String.format("^error^ Could not write version [ %s ] into file [ %s]^r^%n", version, file.getName());
        assertEquals(errInfo, outputStream.toString());
        verify(file, times(2)).getPath();
        verify(file, times(2)).getName();

        file.deleteOnExit();
    }

    @Test
    public void readFile() throws IOException {
        String fileName = "aFile";
        String content = "first line";
        File file = spy(new File(fileName));
        DefaultFileHandler defaultFileHandler = new DefaultFileHandler();

        when(file.getName()).thenReturn(fileName);
        assertNull(defaultFileHandler.readFile(null));
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.close();
        String readContent = defaultFileHandler.readFile(file);
        assertEquals(content, readContent);

        when(file.getPath()).thenAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IOException();
            }
        });
        assertNull(defaultFileHandler.readFile(file));
        String errInfo = String.format("^error^ Cannot read file [ %s ]^r^%n", file.getName());
        assertEquals(errInfo, outputStream.toString());

        verify(file, times(2)).getName();
        file.deleteOnExit();
    }

}
