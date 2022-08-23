package me.redstom.privaterooms.commands;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.Guild;
import me.redstom.privaterooms.db.services.GuildService;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
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
              guildService.registerChannelsFor(guild, cat.getIdLong(), chan.getIdLong());

              reply.setEphemeral(true)
                .setEmbeds(new EmbedBuilder()
                  .setTitle("Setup complete")
                  .setDescription("""
                    ✅ The category and the channel have been created. You can now create rooms by joining %s.
                    
                    ➡ You can now edit the name of the channels and move them.
                    ➡ The rooms will be created in the category even if the "Create room" channel is moved.
                    """.stripIndent().formatted(chan.getAsMention())
                  )
                  .setFooter(deleted.get() ? "📝 The old channels have been deleted!" : "")
                  .setColor(0x00FF00)
                  .build()
                ).queue();
          }, err -> error(reply, err)), err -> error(reply, err));
    }

    private void error(ReplyCallbackAction reply, Throwable err) {
        reply.setEphemeral(true)
          .setEmbeds(new EmbedBuilder()
            .setTitle("Oops! An error occurred.")
            .setDescription("Detail : `%s`\n\n**Please try again later.**".formatted(err.getMessage()))
            .setColor(0xFF0000)
            .build()
          ).queue();
    }
}
