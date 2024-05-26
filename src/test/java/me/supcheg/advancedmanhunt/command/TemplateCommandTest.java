package me.supcheg.advancedmanhunt.command;

import be.seeseemelk.mockbukkit.MockBukkitExtension;
import be.seeseemelk.mockbukkit.MockBukkitInject;
import be.seeseemelk.mockbukkit.ServerMock;
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.supcheg.advancedmanhunt.bridge.RegionPositionWriter;
import me.supcheg.advancedmanhunt.bridge.command.KeyArgument;
import me.supcheg.advancedmanhunt.command.argument.RealEnvironmentArgument;
import me.supcheg.advancedmanhunt.command.argument.TemplateArgument;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.io.DeletingFileVisitor;
import me.supcheg.advancedmanhunt.structure.BukkitBrigadierCommandSourceMock;
import me.supcheg.advancedmanhunt.structure.template.TemplateMock;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.TemplateService;
import me.supcheg.advancedmanhunt.template.impl.BukkitWorldGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.supcheg.advancedmanhunt.assertion.MessageAssertions.assertNextTranslatableMessage;
import static me.supcheg.advancedmanhunt.assertion.MessageAssertions.assertNextTranslatableMessages;
import static me.supcheg.advancedmanhunt.assertion.MessageAssertions.assertNextTranslatableMessagesCount;
import static me.supcheg.advancedmanhunt.util.Keys.advancedmanhuntKey;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled // todo
@ExtendWith(MockBukkitExtension.class)
class TemplateCommandTest {
    BukkitBrigadierCommandSourceMock commandSource;
    CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher;
    TemplateRepository templateRepository;

    @BeforeEach
    void setup(@MockBukkitInject ServerMock mock) {
        templateRepository = Mockito.mock(TemplateRepository.class);

        commandSource = BukkitBrigadierCommandSourceMock.of(mock.addPlayer());
        commandDispatcher = new CommandDispatcher<>();

        TemplateService service = new TemplateService(
                templateRepository,
                Mockito.mock(TemplateLoader.class),
                new BukkitWorldGenerator(),
                Mockito.mock(ContainerAdapter.class),
                Mockito.mock(RegionPositionWriter.class)
        );

        TemplateCommand template = new TemplateCommand(
                service,
                Mockito.mock(TemplateArgument.class),
                Mockito.mock(KeyArgument.class),
                Mockito.mock(RealEnvironmentArgument.class)
        );
        template.register(commandDispatcher);
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
            templateRepository.storeEntity(new TemplateMock(advancedmanhuntKey("name" + i)));
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
        templateRepository.storeEntity(new TemplateMock(advancedmanhuntKey("my_template_1")));
        assertFalse(templateRepository.getEntities().isEmpty());

        commandDispatcher.execute("template remove my_template_1", commandSource);

        assertNextTranslatableMessage(commandSource, "advancedmanhunt.template.remove.success");
        assertTrue(templateRepository.getEntities().isEmpty());
    }

    @Test
    void removeNotExistingTest() throws CommandSyntaxException {
        templateRepository.storeEntity(new TemplateMock(advancedmanhuntKey("my_template_1")));
        assertFalse(templateRepository.getEntities().isEmpty());

        commandDispatcher.execute("template remove my_template_2", commandSource);

        assertFalse(templateRepository.getEntities().isEmpty());
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    void exportAndImportTest() throws IOException, CommandSyntaxException {
        Path tempDirectory = Files.createTempDirectory("template-export-test-");
        String templateName = "exported_template";

        Template template = new TemplateMock(advancedmanhuntKey(templateName), tempDirectory);
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
