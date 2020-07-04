package org.reddev.pr.command;

import com.google.common.collect.Lists;
import fr.il_totore.ucp.CommandContext;
import fr.il_totore.ucp.GeneralResult;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.Main;
import org.reddev.pr.event.ServerJoinEventListener;
import org.reddev.pr.event.ServerLeaveEventListener;
import org.reddev.pr.utils.i18n.I18n;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.function.BiFunction;

import static org.reddev.pr.Main.langs;

public class LangCommand implements BiFunction<MessageCreateEvent, CommandContext<MessageCreateEvent>, GeneralResult> {


    @Override
    public GeneralResult apply(MessageCreateEvent event, CommandContext<MessageCreateEvent> commandContext) {

        List<String> reks82dzo291 = Lists.newArrayList(event.getMessageContent().split(" "));
        reks82dzo291.remove(0);
        String[] args = reks82dzo291.toArray(new String[0]);

        assert event.getServer().isPresent();
        Server server = event.getServer().get();
        TextChannel textChannel = event.getChannel();

        if (args.length != 1) {
            textChannel.sendMessage(Main.getErrorEmbed(I18n.format(server.getId(), "command.lang.error.syntax"), server));
            return null;
        }
        if (!(langs.contains(args[0]))) {
            textChannel.sendMessage(Main.getErrorEmbed(I18n.format(server.getId(), "command.lang.error.not_correct_lang"), server));
            return null;
        }
        try {
            PreparedStatement stmt = Main.getDatabaseManager().getConnection().prepareStatement("UPDATE servers SET lang=? WHERE id=?");
            stmt.setString(1, args[0]);
            stmt.setLong(2, server.getId());
            stmt.execute();
            stmt.close();
            textChannel.sendMessage(Main.getSuccessEmbed(I18n.format(server.getId(), "command.lang.successful.title"), I18n.format(server.getId(), "command.lang.successful.description")));
            ServerLeaveEventListener.deleteChannels(server);
            ServerJoinEventListener.createChannels(server, true);
        } catch (Exception e) {
            textChannel.sendMessage(Main.getErrorEmbed(I18n.format(server.getId(), "error.sql_error"), server));
        }
        return null;
    }

}
