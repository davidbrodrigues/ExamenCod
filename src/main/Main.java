//imports de las librerias de discordj4 necesarias
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;


//codigo del bot de discord
public class Main {
    public static void main(final String[] args) {
        final String token = "e44ecd27857e48a50cf8b00d93fc83c12b879246b3bf037f33c329a93a0252d6";//mi key del bot discord
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if ("!ping".equals(message.getContent())) {
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("Pong!").block();
            }
        });

        gateway.onDisconnect().block();
    }
}
}