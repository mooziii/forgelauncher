package me.obsilabor.forgelauncher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForgeLauncher {
    private static Process forgeProcess;

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("forgelauncher");
        Runtime.getRuntime().addShutdownHook(new Thread("shutdown") {
            @Override
            public void run() {
                if (forgeProcess != null) {
                    forgeProcess.destroy();
                    logger.info("Forge process destroyed");
                }
            }
        });
        logger.info("Gathering system information..");
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        File forgeFile = new File("forgelauncher.txt");
        String mcVersion = "";
        String javaBinary = "";
        String jvmArgs = "";
        try {
            if (!forgeFile.exists()) {
                logger.warn(forgeFile.getName() + " doesn't exist, creating a new one with the defaults");
                forgeFile.createNewFile();
                Files.write(forgeFile.toPath(), Arrays.asList("1.17.1-37.1.1", "java", "-Xmx1G"));
            }
            List<String> allLines = Files.readAllLines(forgeFile.toPath());
            mcVersion = allLines.get(0);
            javaBinary = allLines.get(1);
            jvmArgs = allLines.get(2);
        } catch (IOException | NullPointerException exception) {
            logger.error("Couldn't read " + forgeFile.getName(), exception);
            System.exit(0);
        }
        boolean is117orNewer = false;
        String majorVersion = mcVersion.split("\\.")[1];
        try {
            if (Integer.parseInt(majorVersion) >= 17) {
                is117orNewer = true;
            }
        } catch (NumberFormatException exception) {
            logger.error("\"" + majorVersion + "\" is not a number");
            System.exit(0);
        }
        if (is117orNewer) {
            logger.info("Preparing start for 1.17 based forge-installations (modern ones)");
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(javaBinary);
            arguments.add(jvmArgs);
            try {
                logger.info("Reading forge parameters");
                List<String> forgeArguments = Files.readAllLines(Paths.get("libraries/net/minecraftforge/forge/" + mcVersion + "/" + (isWindows ? "win" : "unix") + "_args.txt"));
                for (String forgeArgument : forgeArguments) {
                    arguments.addAll(Arrays.asList(forgeArgument.split(" ")));
                }
            } catch (IOException exception) {
                logger.error("Reading forge parameters failed", exception);
            }
            launchForgeServer(logger, mcVersion, args, arguments);
        } else {
            logger.info("Preparing start for older forge-installations");
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(javaBinary);
            arguments.add(jvmArgs);
            arguments.add("-jar");
            arguments.add("forge-" + mcVersion + ".jar");
            launchForgeServer(logger, mcVersion, args, arguments);
        }
    }

    private static void launchForgeServer(Logger logger, String mcVersion, String[] args, ArrayList<String> arguments) {
        arguments.addAll(Arrays.asList(args)); // inherit process args
        try {
            logger.info("Starting..");
            logger.debug(arguments.toString());
            forgeProcess = new ProcessBuilder(arguments)
                    .directory(new File(System.getProperty("user.dir")))
                    .redirectErrorStream(true)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .redirectInput(ProcessBuilder.Redirect.INHERIT)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .start();
            forgeProcess.waitFor();
        } catch (IOException | InterruptedException exception) {
            logger.error("Failed to start forge " + mcVersion, exception);
        }
    }
}
