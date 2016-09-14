/*
 * Copyright © 2016 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package fast.l2switch;

import javax.annotation.Nonnull;

import fast.api.FastSystem;
import fast.api.UserHints;

import org.maple.core.increment.packet.ARP;
import org.maple.core.increment.packet.Ethernet;
import org.maple.core.increment.packet.IPv4;
import org.maple.core.increment.util.HexString;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.l2switch.packethandler.decoders.ArpDecoder;
import org.opendaylight.l2switch.packethandler.decoders.EthernetDecoder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.arp.rev140528.ArpPacketReceived;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.arp.rev140528.arp.packet.received.packet.chain.packet.ArpPacket;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.basepacket.rev140528.packet.chain.grp.packet.chain.packet.RawPacket;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ethernet.rev140528.EthernetPacketReceived;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketReceived;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L2switchMain extends FastMainBase {

    private static final Logger LOG = LoggerFactory.getLogger(L2switchMain.class);

    /* the ID for the first function instance */
    private String fid;

    /* Packet Decoders */
    //private EthernetDecoder ed = new EthernetDecoder(null);
    //private ArpDecoder ad = new ArpDecoder(null);

    public void onCreate() {
        LOG.info("L2switchMain Session Initiated");

        /*
         * This is usually where you launch the first FAST function instance.
         *
         * But you can just get the FastSystem instance and submit function instances later.
         * */
        //fid = this.fast.submit(new L2switchFunction());

        LOG.info("L2switchMain initialized successfully");
    }

    @Override
    public void onCreate(@Nonnull FastSystem fast, @Nonnull L2switchExternalEventTrigger trigger) {
        LOG.info("L2switchMainBase Session Initiated");
        this.fast = fast;
        trigger.bind(this);
        LOG.info("L2switchMainBase initialized successfully");
    }

    @Override
    public void close() throws Exception {
        this.fast.delete(fid);

        LOG.info("L2switchLauncher Closed");
    }

    @Override
    public void onPacketIn(PacketReceived packetIn) {
        LOG.info("Received one packet");

        /*
         * This is the handler of the triggered event.
         *
         * You can launch function instances here.
         * */

        byte[] payload = packetIn.getPayload();
        Ethernet frame = new Ethernet();
        frame.deserialize(payload, 0, payload.length);
        if(frame.getEtherType() == Ethernet.TYPE_ARP) {
            L2switchFunction f = new L2switchFunction(packetIn);
            this.fast.submit(f);
        }
        /* You can set the precedence to make sure the first function instance is always executed first */
        //String[] precedences = { fid };
    }
}
