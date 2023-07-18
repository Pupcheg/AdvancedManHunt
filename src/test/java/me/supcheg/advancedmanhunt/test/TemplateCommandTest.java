package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.supcheg.advancedmanhunt.command.TemplateCommand;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.json.JsonSerializer;
import me.supcheg.advancedmanhunt.structure.BukkitBrigadierCommandSourceMock;
import me.supcheg.advancedmanhunt.structure.template.DummyTemplate;
import me.supcheg.advancedmanhunt.structure.template.DummyTemplateRepository;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.task.impl.DummyTemplateTaskFactory;
import me.supcheg.advancedmanhunt.util.DeletingFileVisitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static me.supcheg.advancedmanhunt.assertion.MessageAssertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateCommandTest {
    private PlayerMock player;
    private BukkitBrigadierCommandSourceMock commandSource;
    private CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher;
    private TemplateRepository templateRepository;

    @BeforeEach
    void setup() {
        ServerMock mock = MockBukkit.mock();
        templateRepository = new DummyTemplateRepository();

        player = mock.addPlayer();
        commandSource = BukkitBrigadierCommandSourceMock.of(player);
        commandDispatcher = new CommandDispatcher<>();
        new TemplateCommand(templateRepository, new DummyTemplateTaskFactory(), JsonSerializer.createGson())
                .register(commandDispatcher);
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void generateWithoutChunkyTest() throws CommandSyntaxException {
        commandDispatcher.execute("template generate template_name 2 normal", commandSource);

        assertTranslatableMessage(player, "advancedmanhunt.no_plugin");
    }

    @Test
    void nonEmptyListTest() throws CommandSyntaxException {
        int templatesCount = 15;
        for (int i = 0; i < templatesCount; i++) {
            templateRepository.addTemplate(new DummyTemplate("name" + i));
        }

        commandDispatcher.execute("template list", commandSource);

        assertTranslatableMessage(player, "advancedmanhunt.template.list.title");
        assertTranslatableMessagesCount(player, "advancedmanhunt.template.list.info", templatesCount);
    }

    @Test
    void emptyListTest() throws CommandSyntaxException {
        commandDispatcher.execute("template list", commandSource);

        assertTranslatableMessages(player,
                "advancedmanhunt.template.list.title",
                "advancedmanhunt.template.list.empty"
        );
    }

    @Test
    void removeExistingTest() throws CommandSyntaxException {
        templateRepository.addTemplate(new DummyTemplate("my_template_1"));
        assertFalse(templateRepository.getTemplates().isEmpty());

        commandDispatcher.execute("template remove my_template_1", commandSource);

        assertTranslatableMessage(player, "advancedmanhunt.template.remove.success");
        assertTrue(templateRepository.getTemplates().isEmpty());
    }

    @Test
    void removeNotExistingTest() throws CommandSyntaxException {
        templateRepository.addTemplate(new DummyTemplate("my_template_1"));
        assertFalse(templateRepository.getTemplates().isEmpty());

        commandDispatcher.execute("template remove my_template_2", commandSource);

        assertTranslatableMessage(player, "advancedmanhunt.template.remove.not_found");
        assertFalse(templateRepository.getTemplates().isEmpty());
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    void exportAndLoadTest() throws IOException, CommandSyntaxException {
        Path tempDirectory = Files.createTempDirectory("template-export-test-");
        String templateName = "exported_template";

        Template template = new Template(templateName, Distance.ofBlocks(0), tempDirectory, Collections.emptyList());
        templateRepository.addTemplate(template);

        commandDispatcher.execute("template export " + templateName, commandSource);
        assertTranslatableMessage(player, "advancedmanhunt.template.export.success");

        templateRepository.removeTemplate(template);
        assertTrue(templateRepository.getTemplates().isEmpty());

        String normalizedPath = '"' + tempDirectory.toString().replace('\\', '/') + '"';
        commandDispatcher.execute("template load " + normalizedPath, commandSource);
        assertTranslatableMessage(player, "advancedmanhunt.template.load.success");

        assertFalse(templateRepository.getTemplates().isEmpty());

        Files.walkFileTree(tempDirectory, DeletingFileVisitor.INSTANCE);
    }
}
