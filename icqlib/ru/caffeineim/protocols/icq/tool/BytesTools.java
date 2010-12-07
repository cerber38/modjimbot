/**
 * Copyright 2008 Caffeine-Soft Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.caffeineim.protocols.icq.tool;


/**
 * @author Pavel Samolisov
 * @author Manuel Linsmayer
 * @author Andreas Rossbacher
 * @author Sergey Chernov
 * @author Andrey B. Ivlev
 * @since  07.02.2009
 */
public class BytesTools {

    /**
     * Compare to byte arrays
     *
     * @param buf1
     * @param off1
     * @param buf2
     * @param off2
     * @param len
     * @return true if equals, false otherwise
     */
    public static boolean byteArrayEquals(byte[] buf1, int off1, byte[] buf2,
            int off2, int len) {

        // Length check
        if ((off1 + len > buf1.length) || (off2 + len > buf2.length)) {
            return false;
        }

        // Compare bytes, stop at first mismatch
        for (int i = 0; i < len; i++) {
            if (buf1[off1 + i] != buf2[off2 + i]) {
                return false;
            }
        }

        // Return true if this point is reached
        return true;
    }
}
