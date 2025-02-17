package com.mrdexstor.engineringcore;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// Значение здесь должно совпадать с записью в файле META-INF/mods.toml
@Mod(EngineringCore.MODID)
public class EngineringCore
{
    // Определяем ID мода в общем месте для всех ссылок
    public static final String MODID = "engineringcore";
    // Логгер для вывода сообщений
    private static final Logger LOGGER = LogUtils.getLogger();
    // Создаем Deferred Register для хранения блоков, которые будут зарегистрированы под пространством имен "MODID"
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Создаем Deferred Register для хранения предметов, которые будут зарегистрированы под пространством имен "MODID"
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Создаем Deferred Register для хранения вкладок креативного режима, которые будут зарегистрированы под пространством имен "MODID"
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Создаем новый блок с ID "examplemod:example_block"
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Создаем новый предмет блока с ID "examplemod:example_block"
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Создаем новый съедобный предмет с ID "examplemod:example_item", питательность 1 и насыщение 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // Создаем вкладку креативного режима с ID "examplemod:example_tab" для примера предмета, которая размещается после вкладки боя
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Добавляем пример предмета во вкладку
            }).build());

    public EngineringCore()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Регистрируем метод commonSetup для загрузки мода
        modEventBus.addListener(this::commonSetup);

        // Регистрируем Deferred Register в шине событий мода, чтобы блоки были зарегистрированы
        BLOCKS.register(modEventBus);
        // Регистрируем Deferred Register в шине событий мода, чтобы предметы были зарегистрированы
        ITEMS.register(modEventBus);
        // Регистрируем Deferred Register в шине событий мода, чтобы вкладки были зарегистрированы
        CREATIVE_MODE_TABS.register(modEventBus);

        // Регистрируем себя для серверных и других игровых событий, которые нас интересуют
        MinecraftForge.EVENT_BUS.register(this);

        // Регистрируем предмет в креативной вкладке
        modEventBus.addListener(this::addCreative);

        // Регистрируем конфигурацию мода, чтобы Forge мог создать и загрузить файл конфигурации
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Некоторый общий код настройки
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Добавляем пример блока в вкладку строительных блоков
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // Используем SubscribeEvent и позволяем шине событий обнаруживать методы для вызова
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Делаем что-то при старте сервера
        LOGGER.info("HELLO from server starting");
    }

    // Используем EventBusSubscriber для автоматической регистрации всех статических методов в классе, аннотированных @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Некоторый код настройки клиента
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
