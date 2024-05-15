package com.sarinsa.dampsoil.api;

/**
 * This is the main interface for interacting with
 * the parts of Damp Soil that are intended for other
 * modders to access.
 */
public interface IDampSoilApi {

    /**
     * @return The {@link IProduceCooldownManager} implementation provided by Damp Soil.
     */
    IProduceCooldownManager getProduceCooldownManager();
}
