package com.digitalid.presentation.console;

import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.domain.model.Worker;

public abstract class ConsoleUI {

    protected final TerminalMenu terminal;
    protected final WorkerRepository workerRepository;

    public ConsoleUI(TerminalMenu terminal, WorkerRepository workerRepository) {
        this.terminal = terminal;
        this.workerRepository = workerRepository;
    }

    public abstract List<MenuOption> getMenuOptions();
    public abstract void handleChoice(int choice);

    /**
     * Shows the menu using arrow key navigation and returns the selected option number.
     * Returns 0 if the user presses Q to quit.
     */
    public int showMenuAndSelect(String title) {
        List<MenuOption> options = getMenuOptions();
        List<String> labels = options.stream()
                .map(MenuOption::getLabel)
                .collect(Collectors.toList());
        labels.add("Exit");

        int selected = terminal.select(title, labels);

        if (selected == -1 || selected == labels.size() - 1) {
            return 0; // exit
        }
        return options.get(selected).getNumber();
    }

    protected String promptWorkerId() {
        List<Worker> workers = workerRepository.listAll();
        if (!workers.isEmpty()) {
            printInfo("\nExisting workers:");
            for (Worker w : workers) {
                printInfo("  " + w.getWorkerId() + " - " + w.getFullName() + " (" + w.getStatus() + ")");
            }
            printInfo("");
        }
        return readInput("Worker ID: ");
    }

    protected String readInput(String prompt) {
        return terminal.readLine(prompt);
    }

    protected void printSuccess(String message) {
        System.out.println("\033[32m[OK]\033[0m " + message);
    }

    protected void printError(String message) {
        System.out.println("\033[31m[ERROR]\033[0m " + message);
    }

    protected void printInfo(String message) {
        System.out.println(message);
    }

    protected void pause() {
        terminal.readLine("\nPress Enter to continue...");
    }

}
