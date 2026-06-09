package com.sarinsa.dampsoil.api.impl;

import com.sarinsa.dampsoil.api.IDampSoilApi;
import com.sarinsa.dampsoil.api.IProduceCooldownManager;

public class DampSoilApi implements IDampSoilApi {
    
    public static final DampSoilApi INSTANCE = new DampSoilApi();
    
    private final ProduceCooldownManager produceCooldownManager = new ProduceCooldownManager();
    
    @Override
    public IProduceCooldownManager getProduceCooldownManager() {
        return produceCooldownManager;
    }
}
