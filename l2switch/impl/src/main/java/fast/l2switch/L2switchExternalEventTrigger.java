/*
 * Copyright © 2016 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package fast.l2switch;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketReceived;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.NotificationListener;

public class L2switchExternalEventTrigger implements BindingAwareProvider,
                                                            PacketProcessingListener, AutoCloseable {

    private ListenerRegistration<NotificationListener> registration = null;

    private FastMainBase main = null;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        NotificationProviderService nps = session.getSALService(NotificationProviderService.class);
        registration = nps.registerNotificationListener(this);
    }

    public void bind(FastMainBase main) {
        this.main = main;
    }

    protected void trigger(FastMainBase main, PacketReceived notification) {
        if (main == null) {
            return;
        }
        main.onPacketIn(notification);
    }

    @Override
    public void onPacketReceived(PacketReceived notification) {
        trigger(main, notification);
    }

    @Override
    public void close() {
        if (registration == null) {
            return;
        }
        registration.close();
    }
}
