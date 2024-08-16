package de.legoshi.lccore.setup;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

public class ProjectInit {

    public static final String MC_SPIGOT_188_SERVER = "https://files.meowception.net/s/wRnAizc9ydgAFXA/download/spigot1.8.8.jar";
    public static final String PLUGIN_CONFIG_FOLDER = "server" + File.separator + "plugins" + File.separator + "LCCore";
    public static final String PLUGIN_FOLDER = "server" + File.separator + "plugins";
    public static final String SERVER_FOLDER = "server";
    public static final String PROJECT_CONFIG_FOLDER = "configs";
    // Essentials?
    public static final List<String> PLUGIN_DOWNLOAD_URLS = Arrays.asList(
            "https://files.meowception.net/s/PCwDRjHiyieCWpX/download/PlaceholderAPI-2.11.6.jar",
            "https://files.meowception.net/s/EdNYYsXHNzsQbQH/download/TAB%20v4.1.6.jar",
            "https://files.meowception.net/s/b4FQLmaaSnLDmXt/download/LuckPerms-Bukkit-5.4.137.jar",
            "https://files.meowception.net/s/Zos5coR2KEa5tmM/download/DiscordSRV-Build-1.28.0.jar",
            "https://files.meowception.net/s/8L2AarzDrHXEYsb/download/EssentialsX-2.20.1.jar",
            "https://files.meowception.net/s/5Tmyc87pWoMeBaN/download/EssentialsXChat-2.20.1.jar",
            "https://files.meowception.net/s/pHi74N8JMYdKfgt/download/EssentialsXSpawn-2.20.1.jar",
            "https://files.meowception.net/s/3r8BECJQCtywBdJ/download/EssentialsXProtect-2.20.1.jar",
            "https://files.meowception.net/s/KtoHmd7mctWGHfL/download/ServerSigns-4.9.0.jar",
            "https://files.meowception.net/s/42t5EnypZyKpcM7/download/SuperVanish-6.2.0.jar",
            "https://files.meowception.net/s/bsEYKsyP3CCdTwe/download/ProtocolLib.jar",
            "https://files.meowception.net/s/pEceWB6no8d6ngr/download/Vault.jar"
    );

    public static void main(String[] args) {
        Path configFolder = Paths.get(PLUGIN_CONFIG_FOLDER);
        Path pluginFolder = Paths.get(PLUGIN_FOLDER);
        Path serverFolder = Paths.get(SERVER_FOLDER);
        Path projectConfigFolder = Paths.get(PROJECT_CONFIG_FOLDER);

        try {
            Files.createDirectories(configFolder);

            for(String plugin : PLUGIN_DOWNLOAD_URLS) {
                downloadFile(plugin, pluginFolder);
            }

            downloadFile(MC_SPIGOT_188_SERVER, serverFolder);

            Files.walkFileTree(projectConfigFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = pluginFolder.resolve(projectConfigFolder.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetSubDir = pluginFolder.resolve(projectConfigFolder.relativize(dir));
                    if (Files.notExists(targetSubDir)) {
                        Files.createDirectories(targetSubDir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println("Error occurred while initializing server directory: " + e.getMessage());
        }
    }

    private static void downloadFile(String fileURL, Path targetDir) throws IOException {
        HttpURLConnection httpConn = getHttpURLConnection(fileURL);

        try (InputStream inputStream = new BufferedInputStream(httpConn.getInputStream())) {
            String fileName = getFileNameFromURL(fileURL);
            fileName = fileName.replace("%20", " ");
            Path targetFile = targetDir.resolve(fileName);

            try (FileOutputStream outputStream = new FileOutputStream(targetFile.toFile())) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } finally {
            httpConn.disconnect();
        }
    }

    private static HttpURLConnection getHttpURLConnection(String fileURL) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
            String newUrl = httpConn.getHeaderField("Location");
            httpConn = (HttpURLConnection) new URL(newUrl).openConnection();
        }
        return httpConn;
    }

    private static String getFileNameFromURL(String fileURL) {
        String[] parts = fileURL.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.split("\\?")[0];
    }
}
