package com.mrdexstor.engineringcore;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Пример класса конфигурации. Это не обязательно, но хорошая идея иметь его для организации конфигурации.
// Демонстрирует использование API конфигурации Forge
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // Логическое значение, определяющее, нужно ли логировать блок земли при общей настройке
    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Логировать ли блок земли при общей настройке")
            .define("logDirtBlock", true);

    // Целочисленное значение, представляющее "магическое число"
    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("Магическое число")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    // Сообщение-введение для "магического числа"
    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("Сообщение-введение для магического числа")
            .define("magicNumberIntroduction", "Магическое число...");

    // Список строк, которые рассматриваются как ресурсные местоположения для предметов
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("Список предметов для логирования при общей настройке.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;

    // Проверка корректности имени предмета
    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemName));
    }

    // Обработчик события загрузки конфигурации
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        // Преобразование списка строк в набор предметов
        items = ITEM_STRINGS.get().stream()
                .map(itemName -> BuiltInRegistries.ITEM.get(new ResourceLocation(itemName)))
                .collect(Collectors.toSet());
    }
}
