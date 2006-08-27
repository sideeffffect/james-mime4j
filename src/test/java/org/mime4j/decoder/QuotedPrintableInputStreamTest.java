/*
 *  Copyright 2004 the mime4j project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mime4j.decoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

/**
 * 
 *
 * @author Niklas Therning
 * @version $Id: QuotedPrintableInputStreamTest.java,v 1.3 2004/10/04 15:36:44 ntherning Exp $
 */
public class QuotedPrintableInputStreamTest extends TestCase {

    public void setUp() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }
    
    public void testDecode() throws IOException, UnsupportedEncodingException {
        ByteArrayInputStream bis = null;
        QuotedPrintableInputStream decoder = null;
        byte[] bytes = null;

        bis = new ByteArrayInputStream("=e1=e2=E3=E4\r\n".getBytes("US-ASCII"));
        decoder = new QuotedPrintableInputStream(bis);
        assertEquals("����\r\n", new String(read(decoder), "ISO8859-1"));
        
        bis = new ByteArrayInputStream("=e1=g2=E3=E4\r\n".getBytes("US-ASCII"));
        decoder = new QuotedPrintableInputStream(bis);
        assertEquals("�=g2��\r\n", new String(read(decoder), "ISO8859-1"));
        
        bis = new ByteArrayInputStream("   =e1 =e2  =E3\t=E4  \t \t    \r\n".getBytes("US-ASCII"));
        decoder = new QuotedPrintableInputStream(bis);
        assertEquals("   � �  �\t�\r\n", new String(read(decoder), "ISO8859-1"));
        
        /*
         * Test soft line breaks.
         */
        bis = new ByteArrayInputStream("Soft line   = \t \r\nHard line   \r\n".getBytes("US-ASCII"));
        decoder = new QuotedPrintableInputStream(bis);
        assertEquals("Soft line   Hard line\r\n", new String(read(decoder), "ISO8859-1"));
        
        /*
         * This isn't valid qp (==) but it is known to occur in certain
         * messages, especially spam.
         */
        bis = new ByteArrayInputStream("width==\r\n340 height=3d200\r\n".getBytes("US-ASCII"));
        decoder = new QuotedPrintableInputStream(bis);
        assertEquals("width=340 height=200\r\n", new String(read(decoder), "ISO8859-1"));
    }

    
    private byte[] read(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {
            bos.write(b);
        }
        return bos.toByteArray();
    }
}
