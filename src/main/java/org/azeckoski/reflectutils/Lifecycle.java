package org.azeckoski.reflectutils;

/**
 * Interface for objects that wish to participate in lifecycle events.
 */
public interface Lifecycle
{
    void shutdown();
}
