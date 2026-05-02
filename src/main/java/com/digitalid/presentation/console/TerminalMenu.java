package com.digitalid.presentation.console;

import java.io.IOException;
import java.util.List;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;


public class TerminalMenu {

    private static final int KEY_ESCAPE = 27;
    private static final int FIRST_TYPEABLE_CHAR = 32;
    private static final List<Integer> ENTER_KEYS = List.of(13, 10);
    private static final List<Integer> BACKSPACE_KEYS = List.of(127, 8);

    private final Terminal terminal;

    public TerminalMenu() {
        try {
            this.terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();
            terminal.enterRawMode();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start terminal", e);
        }
    }

    // Displays a list of options
    public int select(String title, List<String> options) {
        int selected = 0;
        NonBlockingReader reader = terminal.reader();

        while (true) {
            render(title, options, selected);

            try {
                int key = reader.read();

                // Arrow keys + wrapping logic:
                // "ESC [ A" for up,
                // "ESC [ B" for down
                if (key == KEY_ESCAPE) {
                    reader.read();
                    int arrow = reader.read();
                    if (arrow == 'A') {
                        selected = (selected - 1 + options.size()) % options.size();
                    }
                    if (arrow == 'B') {
                        selected = (selected + 1) % options.size();
                    }
                }
                else if (ENTER_KEYS.contains(key)) { // enter is pressed
                    clearScreen();
                    return selected;
                } else if (Character.toLowerCase(key) == 'q') { //quit is pressed
                    clearScreen();
                    return -1;
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading input", e);
            }
        }
    }

    // Reads input from a user
    public String readLine(String prompt) {
        System.out.print(prompt);
        StringBuilder sb = new StringBuilder();
        NonBlockingReader reader = terminal.reader();

        try {
            while (true) {
                int ch = reader.read();
                if (ENTER_KEYS.contains(ch)) {
                    System.out.println();
                    return sb.toString();
                } else if (BACKSPACE_KEYS.contains(ch)) {
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                        System.out.print("\b \b");
                    }
                } else if (ch >= FIRST_TYPEABLE_CHAR) {
                    sb.append((char) ch);
                    System.out.print((char) ch);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading input", e);
        }
    }

    private void render(String title, List<String> options, int selected) {
        clearScreen();

        // Title in a box
        int boxWidth = title.length() + 4;
        System.out.println("┌" + "─".repeat(boxWidth) + "┐");
        System.out.println("│  " + title + "  │");
        System.out.println("└" + "─".repeat(boxWidth) + "┘");
        System.out.println();

        // Options
        for (int i = 0; i < options.size(); i++) {
            if (i == selected) {
                System.out.println("  \033[34m▶ " + options.get(i) + "\033[0m");
            } else {
                System.out.println("    " + options.get(i));
            }
        }

        System.out.println();
        System.out.println("  ↑/↓ navigate · Enter select · Q quit");
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void close() {
        try {
            terminal.close();
        } catch (IOException e) {
            // ignore
        }
    }

}
