package com.digitalid.presentation.console;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.registry.UseCaseRegistry;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.OrganisationType;
import com.digitalid.infrastructure.config.DependencyInjection;


public class ConsoleApplication {

    private final TerminalMenu terminal;
    private final DependencyInjection di;

    public ConsoleApplication() {
        this.terminal = new TerminalMenu();
        this.di = new DependencyInjection();
    }

    public void start() {
        OrganisationContext context = selectOrganisation();
        if (context == null) {
            terminal.close();
            return;
        }

        UseCaseRegistry registry = di.buildRegistry(context);
        ConsoleUI console = new BasicOrganisationConsole(terminal, registry, context, di.getWorkerRepository());
        runMenuLoop(console, context);
        terminal.close();
    }

    private OrganisationContext selectOrganisation() {
        OrganisationType[] types = OrganisationType.values();
        List<String> typeLabels = Arrays.stream(types)
                .map(OrganisationType::getDisplayName)
                .collect(Collectors.toList());

        int selected = terminal.select("Food Service Digital ID - Select Organisation", typeLabels);
        if (selected == -1) {
            return null;
        }

        OrganisationType selectedType = types[selected];

        String orgName;
        if (selectedType == OrganisationType.CENTRAL_AUTHORITY) {
            orgName = "Central Authority";
        } else {
            orgName = terminal.readLine("\nOrganisation name: ");
            if (orgName.isEmpty()) {
                orgName = "[UNNAMED] " + selectedType.getDisplayName() + " Org";
            }
        }

        String orgId = "ORG-" + System.currentTimeMillis();
        return di.createContext(orgId, selectedType, orgName);
    }

    private void runMenuLoop(ConsoleUI console, OrganisationContext context) {
        String title = context.getOrganisationName() + " (" + context.getType().getDisplayName() + ")";
        while (true) {
            int choice = console.showMenuAndSelect(title);
            if (choice == 0) {
                System.out.println("\nGoodbye!");
                break;
            }
            console.handleChoice(choice);
            console.pause();
        }
    }

}
