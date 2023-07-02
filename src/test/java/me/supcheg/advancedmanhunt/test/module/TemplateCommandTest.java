package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.test.structure.BukkitBrigadierCommandSourceMock;
import me.supcheg.advancedmanhunt.test.structure.TestPaperPlugin;
import me.supcheg.advancedmanhunt.test.structure.template.DummyTemplate;
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

import static me.supcheg.advancedmanhunt.test.MessageUtil.assertMessagesCount;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateCommandTest {

    PlayerMock player;
    BukkitBrigadierCommandSourceMock commandSource;
    TestPaperPlugin plugin;
    CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher;
    TemplateRepository templateRepository;

    @BeforeEach
    void setup() {
        ServerMock mock = MockBukkit.mock();
        player = mock.addPlayer();
        commandSource = BukkitBrigadierCommandSourceMock.of(player);
        plugin = TestPaperPlugin.load();
        commandDispatcher = plugin.getCommandDispatcher();
        templateRepository = plugin.getTemplateRepository();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void generate() throws CommandSyntaxException {
        commandDispatcher.execute("template generate template_name 2 normal", commandSource);
        assertMessagesCount(player, 1);
    }

    @Test
    void list() throws CommandSyntaxException {
        int templatesCount = 15;
        for (int i = 0; i < templatesCount; i++) {
            templateRepository.addTemplate(new DummyTemplate("name" + i));
        }

        commandDispatcher.execute("template list", commandSource);
        assertMessagesCount(player, templatesCount + 1);
    }

    @Test
    void addAndRemove() throws CommandSyntaxException {
        String templateName = "my_template_1";

        templateRepository.addTemplate(new DummyTemplate(templateName));
        assertFalse(templateRepository.getTemplates().isEmpty());

        commandDispatcher.execute("template remove " + templateName, commandSource);
        assertMessagesCount(player, 1);

        assertTrue(templateRepository.getTemplates().isEmpty());
    }

    @Test
    void removeNotExisting() throws CommandSyntaxException {
        templateRepository.addTemplate(new DummyTemplate());
        assertFalse(templateRepository.getTemplates().isEmpty());

        commandDispatcher.execute("template remove template_name", commandSource);
        assertMessagesCount(player, 1);

        assertFalse(templateRepository.getTemplates().isEmpty());
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    void exportAndLoad() throws IOException, CommandSyntaxException {
        Path tempDirectory = Files.createTempDirectory("template-export-test-");
        String templateName = "exported_template";

        Template template = new Template(templateName, Distance.ofBlocks(0), tempDirectory, Collections.emptyList());
        templateRepository.addTemplate(template);

        commandDispatcher.execute("template export " + templateName, commandSource);
        assertMessagesCount(player, 1);

        templateRepository.removeTemplate(template);
        assertTrue(templateRepository.getTemplates().isEmpty());

        String normalizedPath = '"' + tempDirectory.toString().replace('\\', '/') + '"';
        commandDispatcher.execute("template load " + normalizedPath, commandSource);
        assertMessagesCount(player, 1);

        assertFalse(templateRepository.getTemplates().isEmpty());

        Files.walkFileTree(tempDirectory, DeletingFileVisitor.INSTANCE);
    }
}
