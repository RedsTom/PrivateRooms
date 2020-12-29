package org.reddev.privateroomsreborn.utils.general

import com.vdurmont.emoji.EmojiParser
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.Role

import java.util.function.Consumer

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class MatchRoleQuery implements Runnable {

    private final TextChannel textChannel
    private final List<Role> roleMatches
    private Role result
    private Consumer<Role> thenConsumer
    private Thread queryThread

    MatchRoleQuery(TextChannel textChannel, List<Role> roleMatches) {
        this.textChannel = textChannel
        this.roleMatches = roleMatches
        this.queryThread = new Thread(this::run)
    }

    private void setResult(Role result) {
        this.result = result
    }

    private List<Role> getRoleMatches() {
        return roleMatches
    }

    private TextChannel getTextChannel() {
        return textChannel
    }

    MatchRoleQuery then(Consumer<Role> thenConsumer) {
        this.thenConsumer = thenConsumer
        return this
    }

    void start() {
        this.queryThread.run()
    }

    @Override
    void run() {

        if (getRoleMatches().size() > 10) {
            getTextChannel().sendMessage(j(l("cmd.config.utils.match_role_query.too_much_results", getTextChannel().asServerChannel().get().getServer()), getTextChannel().asServerChannel().get().getServer()))
            return
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(j(l("cmd.config.utils.match_role_query.results_found", getTextChannel().asServerChannel().get().getServer()), getRoleMatches().size()))

        roleMatches.forEach { role ->
            eb.addField(NumberUtils.getByNumber(getRoleMatches().indexOf(role)) + " " + role.getName(),
                    /*"||" + getRoleMatches().indexOf(role) + " " + role.getName() + "||"*/ "** **")
            if (role.getColor().isPresent()) eb.setColor(role.getColor().get())
        }

        getTextChannel().sendMessage(eb).thenApply { message ->

            roleMatches.forEach { role ->
                message.addReaction(EmojiParser.parseToUnicode(NumberUtils.getByNumber(getRoleMatches().indexOf(role))))
            }

            message.addReactionAddListener((event) -> {
                if (event.getUser().get().isYourself()) return

                String s = EmojiParser.parseToAliases(event.getEmoji().asUnicodeEmoji().get())
                int i = NumberUtils.getByString(s)
                if (i == -1) {
                    message.getChannel().sendMessage(j(l("cmd.config.utils.match_role_query.error.reaction_not_correct", message.getServer().get()), message.getServer().get())).thenApplyAsync { message1 ->
                        try {
                            Thread.sleep(5000)
                        } catch (InterruptedException e) {
                            e.printStackTrace()
                        }
                        message1.delete("Expired")
                        return null
                    }
                    return
                }
                message.delete("Found !")
                thenConsumer.accept(roleMatches.get(i))
            })
            return null
        }

    }
}
