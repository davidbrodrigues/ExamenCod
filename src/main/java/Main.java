//imports de las librerias de discordj4 necesarias

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import java.io.*;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;


//codigo del bot de discord
public final class Main {

    /** Application name. */
    private static final String APPLICATION_NAME = "BotDavid";
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /** Directory to store authorization tokens for this application. */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Main.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("53067492883-lgmtusf8mvl00ko3p1caldtdnhtcdse0.apps.googleusercontent.com");
        //returns an authorized Credential object.
        return credential;
    }

    public static void main(final String[] args) {
        final String token = args[0];
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();


        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(Color.BLUE)
                .title("Embed bot de cod")
                .url("https://www.youtube.com/watch?v=z6-voch3JIk&ab_channel=DiscordBots")
                .author("David", "https://media.redadn.es/series/capi/capitulo_14058.jpg", "https://i.imgur.com/LshAsmQ.jpeg")
                .description("No hay descripcion")
                .thumbnail("https://i.imgur.com/EASQgbY.jpeg")
                .addField("EMBDE TO GUAPO", ":D", false)
                .addField("\u200B", "\u200B", false)
                .addField("prueba 1", "123", true)
                .addField("prueba2", "123", true)
                .addField("prueba3", "123", true)
                .image("https://i.imgur.com/tiibS57.jpeg")
                .timestamp(Instant.now())
                .footer("footer", "https://i.imgur.com/QOaCZiE.jpeg")
                .build();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            //si el mensaje recibido es !ping
            if ("!ping".equals(message.getContent())) {
                final MessageChannel channel = message.getChannel().block();
                //devolver un !Pong
                channel.createMessage("Pong!").block();
            }

            //si el mensaje recibido es hola nos pone el embed
            if ("hola".equalsIgnoreCase(message.getContent())){
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage(embed).block();
            }

            //si el mensaje es !files nos devuelve sus nombres
            if ("!files".equals(message.getContent())){
                File file = new File("C:\\Users\\david\\OneDrive\\Escritorio\\");
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    final MessageChannel channel = message.getChannel().block();
                    channel.createMessage(files[i].getName()).block();
                }
            }
            //si el mensaje es !descarga
            if("!descarga".equals(message.getContent())){
                //hace el build de un nuevo servicio autorizado de la API
                final NetHttpTransport HTTP_TRANSPORT;
                try {
                    HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                    Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();
                    //busca una capeta llamada BotImagenes dentro del drive
                    FileList result = service.files().list()
                            .setQ("name contains 'BotImagenes' and mimeType = 'application/vnd.google-apps.folder'")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .execute();
                    List<com.google.api.services.drive.model.File> files = result.getFiles();
                    //si no existe la carpeta nos avisa, saliendo por pantalla No files found
                    if (files == null || files.isEmpty()) {
                        System.out.println("No files found.");
                    } else { //si la carpeta si existe
                        String dirImagenes = null;
                        System.out.println("Files:");
                        for (com.google.api.services.drive.model.File file : files) {
                            System.out.printf("%s (%s)\n", file.getName(), file.getId());
                            dirImagenes = file.getId(); //guarda el nombre y el Id de la carpeta en el String dirImagenes
                        }
                        //busca la imagen, cuyo nombre contenga vegeta, dentro del directorio encontrado
                        FileList resultImagenes= service.files().list()
                                .setQ("name contains 'capibara' and parents in '" + dirImagenes + "'")
                                .setSpaces("drive")
                                .setFields("nextPageToken, files(id, name)")
                                .execute();
                        List<com.google.api.services.drive.model.File> filesImagenes = resultImagenes.getFiles();
                        //si no encuentra la imagen nos avisa, saliendo por pantalla No image found
                        if(filesImagenes == null || filesImagenes.isEmpty())
                            System.out.println("No image found.");
                        else{ //si encuentra la imagen
                            for (com.google.api.services.drive.model.File file : filesImagenes) {
                                System.out.printf("Imagen: %s\n", file.getName());
                                //guarda el 'stream' dentro del direcotrio aux.jpeg que tiene que existir
                                OutputStream outputStream = new FileOutputStream("C:\\Users\\david\\OneDrive\\Escritorio\\cd.jpg");
                                service.files().get(file.getId())
                                        .executeMediaAndDownloadTo(outputStream);
                                outputStream.flush();
                                outputStream.close();
                            }
                        }
                    }
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        gateway.onDisconnect().block();
    }
}