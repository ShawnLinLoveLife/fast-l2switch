
package org.opendaylight.yang.gen.v1.urn.fast.app.l2switch.impl.rev160902;

import fast.api.FastSystem;

import fast.l2switch.L2switchExternalEventTrigger;
import fast.l2switch.L2switchMain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L2switchModule extends org.opendaylight.yang.gen.v1.urn.fast.app.l2switch.impl.rev160902.AbstractL2switchModule {
    private static final Logger LOG = LoggerFactory.getLogger(L2switchMain.class);

    public L2switchModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public L2switchModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.fast.app.l2switch.impl.rev160902.L2switchModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        FastSystem fast = getFastSystemDependency();
        if (fast == null) {
            LOG.error("Error loading L2switchMain: No FAST system found.");
            return null;
        }

        final L2switchExternalEventTrigger trigger = new L2switchExternalEventTrigger();
        try {
            getBrokerDependency().registerProvider(trigger);
        } catch (Exception e) {
            LOG.error("Error loading L2switchMain: Cannot register external event trigger");
            return null;
        }

        final L2switchMain main = new L2switchMain();
        main.onCreate(fast, trigger);
        //main.onCreate();

        return new AutoCloseable() {
            @Override
            public void close() {
                try {
                    trigger.close();
                } catch (Exception e) {
                }
                try {
                    main.close();
                } catch (Exception e) {
                }
            }
        };
    }

}
