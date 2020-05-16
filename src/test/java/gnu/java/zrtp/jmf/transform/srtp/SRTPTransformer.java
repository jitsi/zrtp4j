/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * 
 * Some of the code in this class is derived from ccRtp's SRTP implementation,
 * which has the following copyright notice: 
 *
  Copyright (C) 2004-2006 the Minisip Team

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
*/
package gnu.java.zrtp.jmf.transform.srtp;

import gnu.java.zrtp.jmf.transform.PacketTransformer;
import gnu.java.zrtp.jmf.transform.RawPacket;

import java.util.*;


/**
 * SRTPTransformer implements PacketTransformer and provides implementations
 * for RTP packet to SRTP packet transformation and SRTP packet to RTP packet
 * transformation logic.
 * 
 * It will first find the corresponding SRTPCryptoContext for each packet based
 * on their SSRC and then invoke the context object to perform the
 * transformation and reverse transformation operation. 
 * 
 * @author Bing SU (nova.su@gmail.com)
 */
public class SRTPTransformer
    implements PacketTransformer
{
    /**
     * The SRTPTransformEngine we are using
     */
    private SRTPTransformEngine engine;
    
    /**
     * All the known SSRC's corresponding SRTPCryptoContexts
     */
    private Hashtable<Long,SRTPCryptoContext> contexts;

    /**
     * Construct a SRTPTransformer
     *
     * @param engine the SRTPTransformEngine we are using
     */
    public SRTPTransformer(SRTPTransformEngine engine)
    {
        this.engine = engine;
        contexts = new Hashtable<Long,SRTPCryptoContext>();
    }

    /* (non-Javadoc)
     * @see net.java.sip.communicator.impl.media.transform.PacketTransformer#
     * transform(net.java.sip.communicator.impl.media.transform.RawPacket)
     */
    public RawPacket transform(RawPacket pkt) {
        long ssrc = PacketManipulator.GetRTPSSRC(pkt);

        SRTPCryptoContext context = contexts.get(ssrc);

        if (context == null) {
            context = engine.getDefaultContext().deriveContext(ssrc, 0, 0);
            if (context != null) {
                context.deriveSrtpKeys(0);
                contexts.put(ssrc, context);
            }
            else
                return pkt;
        }
        context.transformPacket(pkt);

        return pkt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.java.sip.communicator.impl.media.transform.PacketTransformer#
     *      reverseTransform(net.java.sip.communicator.impl.media.transform.RawPacket)
     */
    public RawPacket reverseTransform(RawPacket pkt) {
        long ssrc = PacketManipulator.GetRTPSSRC(pkt);
        int seqNum = PacketManipulator.GetRTPSequenceNumber(pkt);
        SRTPCryptoContext context = contexts.get(ssrc);

        if (context == null) {
            context = engine.getDefaultContext().deriveContext(ssrc, 0, 0);
            if (context != null) {
                context.deriveSrtpKeys(seqNum);
                contexts.put(ssrc, context);
            }
            else
                return pkt;
        }

        boolean validPacket = context.reverseTransformPacket(pkt);
        if (!validPacket) {
            return null;
        }
        return pkt;
    }

    /**
     * Close the transformer and underlying transform engine.
     * 
     * The close functions closes all stored crypto contexts. This deletes key data 
     * and forces a cleanup of the crypto contexts.
     */
    public void close() {
        engine.close();
        for(Long ssrc : contexts.keySet()) {
            SRTPCryptoContext context = contexts.get(ssrc);
            if (context != null) {
                context.close();
                contexts.remove(ssrc);
            }
        }
    }
}
