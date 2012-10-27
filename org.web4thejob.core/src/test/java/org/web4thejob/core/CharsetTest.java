/*
 * Copyright (c) 2012 Veniamin Isaias.
 *
 * This file is part of web4thejob.
 *
 * Web4thejob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Web4thejob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with web4thejob.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.web4thejob.core;

import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class CharsetTest {

    @Test
    public void doTest() {
        File file;
        try {
            file = createTempFile();
            BufferedWriter writer = createUTF8FileStream(file);

            System.out.println(Charset.defaultCharset().toString());

            String s = "ΓΔΣτ";

            writer.write(s);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedWriter createUTF8FileStream(File file) throws IOException {
        final FileOutputStream out = new FileOutputStream(file);
        out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        return new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
    }

    private File createTempFile() throws IOException {
        File file = new File("C:\\temp.csv");
        return file;
    }

}
