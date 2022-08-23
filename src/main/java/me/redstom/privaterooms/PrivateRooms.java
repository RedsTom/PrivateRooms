package me.redstom.privaterooms;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.redstom.privaterooms.util.Config;
import me.redstom.privaterooms.util.MigrationManager;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.events.RegisterListener;
import net.dv8tion.jda.api.JDA;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PrivateRooms {
    private final ApplicationContext ctx;
    private final List<ICommand> commands;
    private final Config config;
    private final MigrationManager migrationManager;
    private final JDA client;


    @SneakyThrows
    public void run() {
        if (Files.exists(Path.of("config.yml"))) {
            migrationManager.run();
        }

        this.client.addEventListener(
          ctx.getBeansWithAnnotation(RegisterListener.class).values().toArray(Object[]::new)
        );

        this.client
          .updateCommands()
          .addCommands(
            ctx.getBeansWithAnnotation(RegisterCommand.class).values().stream()
              .filter(a -> a instanceof ICommand)
              .map(a -> (ICommand) a)
              .peek(this.commands::add)
              .map(ICommand::command)
              .toList()
          ).queue();
    }

}
