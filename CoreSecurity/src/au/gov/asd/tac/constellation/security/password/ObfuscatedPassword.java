/*
 * Copyright 2010-2024 Australian Signals Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.security.password;

import java.security.InvalidParameterException;

/**
 * This class represents an obfuscated password. It is really only needed to
 * allow obfuscated passwords to be distinguished from non-obfuscated ones.
 * Passwords are obfuscated using the <code>PasswordObfuscator</code> class.
 * <p>
 * Note that storing obfuscated passwords in source code or configuration files
 * is strongly discouraged. This is NOT a good security practice and might make
 * sense if working locally and as a temporary measure. Once again, strongly
 * discourage using password obfuscation in a production environment. USE AT
 * YOUR OWN RISK!
 *
 * @author ruby_crucis
 */
public class ObfuscatedPassword {

    protected String password;

    /**
     * Store an obfuscated password. This is a string containing a even number
     * of hex digits.
     *
     * @param password Obfuscated password, as generated by the main method of
     * <code>PasswordObfuscator</code>.
     *
     */
    public ObfuscatedPassword(final String password) {
        if (password.length() % 2 != 0) {
            throw new InvalidParameterException("Obfuscated password is the wrong length");
        }
        this.password = password;
    }

    /**
     * Return the obfuscated password as a byte array.
     *
     * @return
     */
    public byte[] getBytes() {
        final byte[] bytes = new byte[password.length() / 2];
        for (int i = 0; i < password.length() / 2; i++) {
            bytes[i] = (byte) Integer.parseInt(password.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    @Override
    public String toString() {
        return password;
    }
}
