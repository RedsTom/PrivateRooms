package org.reddev.pr.command.configsubs.utils;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.reddev.pr.EmbedUtils;
import org.reddev.pr.utils.NumberUtils;
import org.reddev.pr.utils.i18n.I18n;

import java.util.List;
import java.util.function.Consumer;

public class MatchRoleQuery implements Runnable {

    private final TextChannel textChannel;
    private final List<Role> roleMatches;
    private Role result;
    private Consumer<Role> thenConsumer;
    private Thread queryThread;

    public MatchRoleQuery(TextChannel textChannel, List<Role> roleMatches) {
        this.textChannel = textChannel;
        this.roleMatches = roleMatches;
        this.queryThread = new Thread(this::run);
    }

    private void setResult(Role result) {
        this.result = result;
    }

    private List<Role> getRoleMatches() {
        return roleMatches;
    }

    private TextChannel getTextChannel() {
        return textChannel;
    }

    public MatchRoleQuery then(Consumer<Role> thenConsumer) {
        this.thenConsumer = thenConsumer;
        return this;
    }

    public void start() {
        this.queryThread.run();
    }

    @Override
    public void run() {

        if (getRoleMatches().size() < 10) {
            getTextChannel().sendMessage(EmbedUtils.getErrorEmbed(I18n.format(getTextChannel().asServerChannel().get().getServer().getId(), "command.config.configsubs.utils.match_role_query.too_much_results"), getTextChannel().asServerChannel().get().getServer()));
            return;
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(String.format(I18n.format(getTextChannel().asServerTextChannel().get().getServer().getId(), "command.config.configsubs.utils.match_role_query.results_found"), getRoleMatches().size()));

        roleMatches.forEach((role) -> {
            eb.addField(
                    NumberUtils.getByNumber(getRoleMatches().indexOf(role)) + " " + role.getName(), "||" + getRoleMatches().indexOf(role) + " " + role.getName() + "||", true);
        });

        getTextChannel().sendMessage(eb).thenApply((message) -> {

            roleMatches.forEach((role) -> {
                message.addReaction(EmojiParser.parseToUnicode(NumberUtils.getByNumber(getRoleMatches().indexOf(role))));
            });

            message.addReactionAddListener((event) -> {
                if (event.getUser().isYourself()) return;

                String s = EmojiParser.parseToAliases(event.getEmoji().asUnicodeEmoji().get());
                int i = NumberUtils.getByString(s);
                if (i == -1) {
                    message.getChannel().sendMessage(EmbedUtils.getErrorEmbed(I18n.format(message.getServer().get().getId(), "command.config.configsubs.utils.match_role_query.error.reaction_not_correct"), message.getServer().get())).thenApplyAsync((message1) -> {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        message1.delete("Expired");
                        return null;
                    });
                    return;
                }
                message.delete("Found !");
                thenConsumer.accept(roleMatches.get(i));
            });
            return null;
        });

    }
}
