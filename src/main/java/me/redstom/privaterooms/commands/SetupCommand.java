package me.redstom.privaterooms.commands;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.Guild;
import me.redstom.privaterooms.db.services.GuildService;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RegisterCommand
@RequiredArgsConstructor
public class SetupCommand implements ICommand {

    private final GuildService guildService;
    private final I18n i18n;

    @Override
    public CommandData command() {
        return Commands
          .slash("setup", "Sets up the category and the channel to create the channels into.")
          .setGuildOnly(true)
          .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        ReplyCallbackAction reply = event.deferReply();

        Guild guild = guildService.of(event.getGuild().getIdLong());
        Translator translator = i18n.of(guild.locale());

        AtomicBoolean deleted = new AtomicBoolean(false);
        if (guild.categoryId() != 0 || guild.createChannelId() != 0) {
            Optional.ofNullable(event.getGuild().getCategoryById(guild.categoryId()))
              .ifPresent(cat -> {
                  cat.getChannels().forEach(chan -> chan.delete().queue());
                  cat.delete().queue((a) -> {}, (a) -> {});

                  deleted.set(true);
              });

            Optional.ofNullable(event.getGuild().getGuildChannelById(guild.createChannelId()))
              .ifPresent(chan -> {
                  chan.delete().queue((a) -> {}, (a) -> {});

                  deleted.set(true);
              });
        }

        event.getGuild().createCategory("Private Rooms").queue(cat -> cat
          .createVoiceChannel("🔐 Create a room")
          .setUserlimit(1)
          .queue(chan -> {
              guildService.update(guild, g -> g
                .categoryId(cat.getIdLong())
                .createChannelId(chan.getIdLong())
              );

              reply.setEphemeral(true)
                .setEmbeds(new EmbedBuilder()
                  .setTitle(translator.raw("commands.setup.success.title"))
                  .setDescription(translator.get("commands.setup.success.description")
                    .with("channel", chan.getAsMention())
                    .toString()
                  )
                  .setFooter(deleted.get() ? translator.raw("commands.setup.success.note") : "")
                  .setColor(0x00FF00)
                  .build()
                ).queue();
          }, err -> error(translator, reply, err)), err -> error(translator, reply, err));
    }

    private void error(Translator translator, ReplyCallbackAction reply, Throwable err) {

        reply.setEphemeral(true)
          .setEmbeds(new EmbedBuilder()
            .setTitle(translator.raw("commands.setup.error.title"))
            .setDescription(translator.get("commands.setup.error.description")
              .with("error", err.getMessage())
              .toString()
            )
            .setColor(0xFF0000)
            .build()
          ).queue();
    }
}
