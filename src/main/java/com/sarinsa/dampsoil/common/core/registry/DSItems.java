package com.sarinsa.dampsoil.common.core.registry;

import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public class DSItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DampSoil.MODID);
    public static final Map<ResourceKey<CreativeModeTab>, List<RegistryObject<? extends Item>>> TAB_ITEMS = new HashMap<>();


    @SafeVarargs
    protected static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier, ResourceKey<CreativeModeTab>... creativeTabs) {
        RegistryObject<T> regObj = ITEMS.register(name, supplier);
        queueForCreativeTabs(regObj, creativeTabs);
        return regObj;
    }

    @SafeVarargs
    protected static void queueForCreativeTabs(RegistryObject<? extends Item> item, ResourceKey<CreativeModeTab>... creativeTabs) {
        for (ResourceKey<CreativeModeTab> tab : creativeTabs) {
            if (!TAB_ITEMS.containsKey(tab)) {
                List<RegistryObject<? extends Item>> list = new ArrayList<>();
                list.add(item);
                TAB_ITEMS.put(tab, list);
            } else {
                TAB_ITEMS.get(tab).add(item);
            }
        }
    }

    /**
     * Called when creative tabs gets populated with items.
     */
    public static void onCreativeTabPopulate(BuildCreativeModeTabContentsEvent event) {
        if (TAB_ITEMS.containsKey(event.getTabKey())) {
            List<RegistryObject<? extends Item>> items = TAB_ITEMS.get(event.getTabKey());
            items.forEach((regObj) -> event.accept(regObj.get()));
        }
    }
}
