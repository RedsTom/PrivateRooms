package me.redstom.privaterooms;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.events.RegisterListener;
import net.dv8tion.jda.api.JDA;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PrivateRooms {
    private final ApplicationContext ctx;
    private final List<ICommand> commands;
    private final JDA client;


    @SneakyThrows
    public void run() {
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
