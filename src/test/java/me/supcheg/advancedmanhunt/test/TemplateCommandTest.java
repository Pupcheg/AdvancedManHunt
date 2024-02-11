package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.supcheg.advancedmanhunt.command.TemplateCommand;
import me.supcheg.advancedmanhunt.command.service.TemplateService;
import me.supcheg.advancedmanhunt.structure.BukkitBrigadierCommandSourceMock;
import me.supcheg.advancedmanhunt.structure.template.TemplateMock;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.impl.BukkitWorldGenerator;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.DeletingFileVisitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.supcheg.advancedmanhunt.assertion.MessageAssertions.assertNextTranslatableMessage;
import static me.supcheg.advancedmanhunt.assertion.MessageAssertions.assertNextTranslatableMessages;
import static me.supcheg.advancedmanhunt.assertion.MessageAssertions.assertNextTranslatableMessagesCount;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled // todo
class TemplateCommandTest {
    private BukkitBrigadierCommandSourceMock commandSource;
    private CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher;
    private TemplateRepository templateRepository;

    @BeforeEach
    void setup() {
        ServerMock mock = MockBukkit.mock();
        templateRepository = Mockito.mock(TemplateRepository.class);

        commandSource = BukkitBrigadierCommandSourceMock.of(mock.addPlayer());
        commandDispatcher = new CommandDispatcher<>();

        TemplateService service = new TemplateService(
                templateRepository,
                new BukkitWorldGenerator(),
                Runnable::run,
                Mockito.mock(ContainerAdapter.class)
        );

        new TemplateCommand(service).register(commandDispatcher);
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void generateWithoutChunkyTest() throws CommandSyntaxException {
        commandDispatcher.execute("template generate template_name 2 normal", commandSource);

        assertNextTranslatableMessage(commandSource, "advancedmanhunt.no_plugin");
    }

    @Test
    void nonEmptyListTest() throws CommandSyntaxException {
        int templatesCount = 15;
        for (int i = 0; i < templatesCount; i++) {
            templateRepository.storeEntity(new TemplateMock("name" + i));
        }

        commandDispatcher.execute("template list", commandSource);

        assertNextTranslatableMessage(commandSource, "advancedmanhunt.template.list.title");
        assertNextTranslatableMessagesCount(commandSource, "advancedmanhunt.template.list.info", templatesCount);
    }

    @Test
    void emptyListTest() throws CommandSyntaxException {
        commandDispatcher.execute("template list", commandSource);

        assertNextTranslatableMessages(commandSource,
                "advancedmanhunt.template.list.title",
                "advancedmanhunt.template.list.empty"
        );
    }

    @Test
    void removeExistingTest() throws CommandSyntaxException {
        templateRepository.storeEntity(new TemplateMock("my_template_1"));
        assertFalse(templateRepository.getEntities().isEmpty());

        commandDispatcher.execute("template remove my_template_1", commandSource);

        assertNextTranslatableMessage(commandSource, "advancedmanhunt.template.remove.success");
        assertTrue(templateRepository.getEntities().isEmpty());
    }

    @Test
    void removeNotExistingTest() throws CommandSyntaxException {
        templateRepository.storeEntity(new TemplateMock("my_template_1"));
        assertFalse(templateRepository.getEntities().isEmpty());

        commandDispatcher.execute("template remove my_template_2", commandSource);

        assertFalse(templateRepository.getEntities().isEmpty());
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    void exportAndImportTest() throws IOException, CommandSyntaxException {
        Path tempDirectory = Files.createTempDirectory("template-export-test-");
        String templateName = "exported_template";

        Template template = new TemplateMock(templateName, tempDirectory);
        templateRepository.storeEntity(template);

        commandDispatcher.execute("template export " + templateName, commandSource);
        assertNextTranslatableMessage(commandSource, "advancedmanhunt.template.export.success");

        templateRepository.invalidateEntity(template);
        assertTrue(templateRepository.getEntities().isEmpty());

        String normalizedPath = '"' + tempDirectory.toString().replace('\\', '/') + '"';
        commandDispatcher.execute("template import " + normalizedPath, commandSource);
        assertNextTranslatableMessage(commandSource, "advancedmanhunt.template.import.success");

        assertFalse(templateRepository.getEntities().isEmpty());

        Files.walkFileTree(tempDirectory, DeletingFileVisitor.INSTANCE);
    }
}
