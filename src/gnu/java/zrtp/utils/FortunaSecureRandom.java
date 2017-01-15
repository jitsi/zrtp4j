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

import java.security.SecureRandom;
import java.security.SecureRandomSpi;

/**
 * A SecureRandom that generates the data based on the Fortuna PRNG.
 */
public class FortunaSecureRandom extends SecureRandom {
    public FortunaSecureRandom() {
        super(new FortunaSpi(), null);
    }

    private static class FortunaSpi extends SecureRandomSpi {
        private FortunaGenerator fortuna = new FortunaGenerator(engineGenerateSeed(256));

        @Override
        protected byte[] engineGenerateSeed(int numBytes) {
            byte[] someData = new byte[numBytes];
            new SecureRandom().nextBytes(someData);
            return someData;
        }

        @Override
        protected void engineNextBytes(byte[] bytes) {
            fortuna.nextBytes(bytes, 0, bytes.length);
        }

        @Override
        protected void engineSetSeed(byte[] seed) {
            fortuna.addSeedMaterial(seed);
        }
    }
}
