package me.supcheg.advancedmanhunt.config;

import me.supcheg.advancedmanhunt.reflect.ReflectCalled;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;

@SuppressWarnings("CanBeFinal")
@ConfigSerializable
public class AdvancedGuiConfig {
    private static AdvancedGuiConfig INSTANCE = new AdvancedGuiConfig();

    @ReflectCalled
    public static final String HEADER = """
            AdvancedManHunt guis configuration
            For more information you can visit
            github: https://github.com/Pupcheg/AdvancedManHunt
            """;

    @NotNull
    public static AdvancedGuiConfig guiConfig() {
        return INSTANCE;
    }

    @PostProcess
    void postProcess() {
        INSTANCE = this;
    }

    @Comment("Once at what time should the gui be ticking")
    public int tickDelay = 1;
}
