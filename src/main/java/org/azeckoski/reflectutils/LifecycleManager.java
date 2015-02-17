package org.azeckoski.reflectutils;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple LifecycleManager for the reflection utilities. This is provided to allow
 * cleanup under certain conditions (ie. the background thread that is run by the ReferenceMap)
 *
 * Initially, the LifecycleManager is not active, and the reflection utilities will attempt
 * to clean themselves up. To use an explicit lifecycle, first call setActive() to start the manager
 * (before anything uses the reflection utilities - ie. in the context initiliser of a web application!),
 * and then call 'shutdown()' during termination.
 */
public class LifecycleManager
{
    private static boolean isActive = false;
    private static List<Lifecycle> managedObjects = new ArrayList<Lifecycle>();
    private static Lock moLock = new ReentrantLock();

    /**
     * Test to see if the manager is active
     * @return true if active
     */
    public static boolean isActive() {
        return isActive;
    }

    /**
     * Activate the lifecycle manager
     * @param active
     */
    public static void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Register an object to receive lifecycle events
     * @param object
     */
    public static void register(Lifecycle object) {
        if (isActive) {
            moLock.lock();
            try {
                if (managedObjects == null) {
                    throw new RuntimeException("Unable to register - manager already shut down");
                }

                managedObjects.add(object);
            } finally {
                moLock.unlock();
            }
        }
    }

    /**
     * Request shutdown for any objects registered with the lifecycle manager
     */
    public synchronized static void shutdown() {
        moLock.lock();
        try {
            if (managedObjects != null) {
                while (managedObjects.size() > 0) {
                    Lifecycle obj = managedObjects.remove(managedObjects.size() - 1);
                    obj.shutdown();
                }

                managedObjects = null;
            }
        } finally {
            moLock.unlock();
        }
    }
}
