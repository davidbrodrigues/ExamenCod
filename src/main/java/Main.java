public class Main {
        public static void main(final String[] args) {
            final String token = args[0];
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
