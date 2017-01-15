/*
 * Copyright (C) 2017 Ingo Bauersachs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gnu.java.zrtp.utils;

import java.security.InvalidParameterException;
import java.security.SecureRandom;

/**
 * Utility class that provides a singleton for secure random numbers
 * inside the entire ZRTP library.
 */
public class ZrtpSecureRandom extends SecureRandom {
    private static SecureRandom instance;

    public static synchronized SecureRandom getInstance() {
        if (instance == null) {
            instance = new FortunaSecureRandom();
        }

        return instance;
    }

    public static synchronized void setInstance(SecureRandom secureRandom) {
        if (instance == null) {
            instance = secureRandom;
        } else {
            throw new InvalidParameterException("The singleton has already been initialized");
        }
    }
}
