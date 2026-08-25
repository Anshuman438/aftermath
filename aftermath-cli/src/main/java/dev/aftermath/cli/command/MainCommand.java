package dev.aftermath.cli.command;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(
        name = "aftermath",
        mixinStandardHelpOptions = true,
        version = "Aftermath CLI v0.1.0",
        description = "Production failure capture, replay, and test generation tool",
        subcommands = {
                ListCommand.class,
                ViewCommand.class,
                ReplayCommand.class,
                TestGenCommand.class,
                StatusCommand.class
        }
)
public class MainCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("AFTERMATH CLI v0.1.0 — Production Failure & Incident Engine");
        System.out.println("Run 'aftermath --help' for available subcommands.");
    }
}
