package me.redstom.privaterooms.commands;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.services.GuildService;
import me.redstom.privaterooms.db.services.RoomService;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@RegisterCommand
@RequiredArgsConstructor
public class DeleteCommand implements ICommand {

    private final GuildService guildService;
    private final RoomService roomService;

    private final I18n i18n;

    @Override
    public CommandData command() {
        return Commands.slash("delete", "Delete the channel");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // event.getChannel().delete().queue(success -> event.replyEmbeds(successEmbed(event)).queue());
    }

    private MessageEmbed successEmbed(SlashCommandInteractionEvent event) {

        Translator translator = i18n.of(guildService.rawOf(event.getGuild()).locale());

        return new EmbedBuilder()
          .setAuthor(event.getUser().getName(),
            null,
            event.getUser().getAvatarUrl())
          .setTitle(translator.get("commands.delete.title")
            .with("room_name", event.getChannel().getName())
            .toString())
          .setDescription(translator.get("commands.delete.description")
            .with("create_channel_id", guildService.of(event.getGuild().getIdLong()).createChannelId())
            .toString())
          .setColor(0x00FF00)
          .setImage(null)
          .build();
    }
}
