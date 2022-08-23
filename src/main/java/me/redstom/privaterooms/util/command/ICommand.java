package me.redstom.privaterooms.util.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface ICommand {

    CommandData command();

    void execute(SlashCommandInteractionEvent event);
    default void complete(CommandAutoCompleteInteractionEvent event) {}

}
