/*
 * Copyright © 2016 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package fast.l2switch;

import fast.api.FastDataStore;
import fast.api.FastFunction;

import fast.l2switch.utils.AddressWriter;
import org.maple.core.increment.packet.ARP;
import org.maple.core.increment.packet.Ethernet;
import org.maple.core.increment.packet.IPv4;
import org.maple.core.increment.util.HexString;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L2switchFunction implements FastFunction {

    private static final Logger LOG = LoggerFactory.getLogger(L2switchFunction.class);

    private FastDataStore datastore = null;

    private AddressWriter aw = null;

    public L2switchFunction(PacketReceived packetIn) {
        this.aw = new AddressWriter(datastore);
        byte[] payload = packetIn.getPayload();
        Ethernet frame = new Ethernet();
        frame.deserialize(payload, 0, payload.length);
        if(frame.getEtherType() == Ethernet.TYPE_ARP) {
            LOG.info("Received a ARP packet");
            ARP arp = (ARP) frame.getPayload();
            LOG.info("Maple src IP address is : " + IPv4.fromIPv4Address(IPv4.toIPv4Address(arp.getSenderProtocolAddress())));
            LOG.info("Maple src MAC address is: " + HexString.toHexString(frame.getSourceMACAddress()));
            LOG.info("NodeConnectorRef" + packetIn.getIngress().toString());
            MacAddress macAddress = new MacAddress(HexString.toHexString(frame.getSourceMACAddress()));
            Ipv4Address ipv4Address = new Ipv4Address(IPv4.fromIPv4Address(IPv4.toIPv4Address(arp.getSenderProtocolAddress())));
            IpAddress ipAddress = new IpAddress(ipv4Address);
            aw.addAddress(macAddress, ipAddress, packetIn.getIngress());
        }

    }

    @Override
    public void init(FastDataStore datastore) {
        LOG.info("I got a datastore");
        this.datastore = datastore;
    }

    @Override
    public void run() {
        /*
         * Implement your control logic here.
         * */
        LOG.info("L2switchFunction said, \"Hello world!\"");
    }
}
