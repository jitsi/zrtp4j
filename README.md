zrtp4j (Jitsi Fork)
===================

This package provides a library that adds ZRTP support to JMF
and FMJ. Phil Zimmermann developed ZRTP to allow ad-hoc, easy to
use key negotiation to setup Secure RTP (SRTP) sessions. GNU ZRTP4J
together with Sun's JMF or the free alternative FMJ provides a ZRTP
implementation that can be directly embedded into client and server
applications.

The GNU zrtp4j implementation should be compliant to the required functions of
ZRTP as described in [RFC 6189](https://tools.ietf.org/html/rfc6189).
Currently, zrtp4j does not support the feature PBX SAS relay. The GNU zrtp4j
implementation already defines the necessary external interfaces and functions
for this feature, but they are not yet implemented (stubs only).

The first application that included this libarary was a SIP Communicator,
now Jitsi Desktop, release produced by Emanuel Onica during
Google Summer of Code (GSoC) 2008.

License
-------
This library is licensed under the GNU GPL version 3 or later
with Classpath Exception, and has been copyright assigned to
the Free Software Foundation.

For further information refer to the ZRTP FAQ and the GNU ZRTP
How-To. Both are part of the GNU Telephony wiki and are located in its
documentation category.

- [GNU ZRTP Howto](https://www.gnu.org/software/ccrtp/zrtp-howto.html)
- [ZRTP FAQ](https://www.gnu.org/software/ccrtp/zrtp-faq.html)
