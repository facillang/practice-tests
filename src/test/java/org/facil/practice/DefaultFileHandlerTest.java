package org.facil.practice;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.*;

import org.junit.Before;
import org.junit.Test;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:58 AM
 */
public class DefaultFileHandlerTest {

    String version;
    BufferedReader mockBufferedReader;
    BufferedWriter mockBufferedWritter;
    DefaultFileHandler defaultFileHandlerTest;

    @Before
    public void initialize() {
        version = "version";
        mockBufferedWritter = mock(BufferedWriter.class);
        mockBufferedReader  = mock(BufferedReader.class);
        defaultFileHandlerTest = new DefaultFileHandler(mockBufferedWritter, mockBufferedReader);

    }

    @Test
    public void saveFile() throws Exception {
        // TODO
        assertEquals(defaultFileHandlerTest.saveFile(null), false);

        doNothing().when(mockBufferedWritter).write(anyString());
        assertEquals(defaultFileHandlerTest.saveFile(version), true);

        doThrow(new IOException()).when(mockBufferedWritter).write(anyString());
        assertEquals(defaultFileHandlerTest.saveFile(version), false);

    }

    @Test
    public void readFile() throws Exception {
        // TODO
        doReturn(version).when(mockBufferedReader).readLine();
        assertEquals(defaultFileHandlerTest.readFile(), version);

        doThrow(new IOException()).when(mockBufferedReader).readLine();
        assertEquals(defaultFileHandlerTest.readFile(), null);
    }



}
