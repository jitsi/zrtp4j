/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package gnu.java.zrtp.jmf.transform;

import java.net.*;
import java.util.*;

import javax.media.rtp.*;

/**
 * TransformOutputStream implements OutputDataStream. It is use by RTPManager
 * to send RTP/RTCP packet data out.
 * 
 * In this implementation, UDP socket is used to send the data out. When a 
 * normal RTP/RTCP packet is passed down from RTPManager, we first transform 
 * the packet using user define PacketTransformer and then send it out through
 * network to all the stream targets. 
 * 
 * @author Bing SU (nova.su@gmail.com)
 */
public class TransformOutputStream
    implements OutputDataStream
{
    /**
     * UDP socket used to send packet data
     */
    private DatagramSocket socket;
    
    /**
     * PacketTransformer used to transform RTP/RTCP packets
     */
    private PacketTransformer transformer;

    /**
     * Stream targets' ip addresses
     */
    private Vector<InetAddress> remoteAddrs;
    
    /**
     * Stream targets' ports, corresponding to their ip addresses.
     */
    private Vector<Integer> remotePorts;

    /**
     * Construct a TransformOutputStream based on the given UDP socket and
     * PacketTransformer
     * 
     * @param socket UDP socket used to send packet data out
     * @param transformer PacketTransformer used to transform RTP/RTCP packets
     */
    public TransformOutputStream(DatagramSocket socket, PacketTransformer transformer) { this.socket = socket;
        this.transformer = transformer;
        this.remoteAddrs = new Vector<>();
        this.remotePorts = new Vector<>();
    }

    /**
     * Add a target to stream targets list
     * 
     * @param remoteAddr target ip address
     * @param remotePort target port
     */
    public void addTarget(InetAddress remoteAddr, int remotePort) {
        remoteAddrs.add(remoteAddr);
        remotePorts.add(remotePort);
    }

    /**
     * Remove a target from stream targets list
     * 
     * @param remoteAddr target ip address
     * @param remotePort target port
     * @return true if the target is in stream target list and can be removed
     *         false if not
     */
    public boolean removeTarget(InetAddress remoteAddr, int remotePort) {
        boolean ok;
        ok = remoteAddrs.remove(remoteAddr);
        ok = ok && remotePorts.remove(new Integer(remotePort));

        return ok;
    }

    /**
     * Remove all stream targets from this session.
     */
    public void removeTargets() {
        remoteAddrs.removeAllElements();
        remotePorts.removeAllElements();
    }

    /* (non-Javadoc)
     * @see javax.media.rtp.OutputDataStream#write(byte[], int, int)
     */
    public int write(byte[] buffer, int offset, int length) {
        // Transformation could be non-inplace, we shall not modify the the old buffer
        RawPacket pkt = transformer.transform(new RawPacket(buffer, offset, length));
        for (int i = 0; i < remoteAddrs.size(); ++i) {
            InetAddress remoteAddr = remoteAddrs.elementAt(i);
            int remotePort = remotePorts.elementAt(i);

            try {
                this.socket.send(new DatagramPacket(pkt.getBuffer(), pkt
                        .getOffset(), pkt.getLength(), remoteAddr, remotePort));
            } catch (Exception e) {
                // TODO error handling
                return -1;
            }
        }
        // yes, we should return the pre-transformed packet length
        return length;
    }
}
