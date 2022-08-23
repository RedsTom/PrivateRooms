package me.redstom.privaterooms.events;

import lombok.extern.slf4j.Slf4j;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.events.RegisterListener;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RegisterListener
@Slf4j
public class AllEventListener extends ListenerAdapter {

    @Autowired
    private List<ICommand> commands;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        log.info("Ready !");
        log.info("Logged in as {}", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        this.commands.stream()
          .filter(cmd -> event.getInteraction().getName().equalsIgnoreCase(cmd.command().getName()))
          .forEach(cmd -> cmd.execute(event));
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        this.commands.stream()
          .filter(cmd -> event.getInteraction().getName().equalsIgnoreCase(cmd.command().getName()))
          .forEach(cmd -> cmd.complete(event));
    }
}
