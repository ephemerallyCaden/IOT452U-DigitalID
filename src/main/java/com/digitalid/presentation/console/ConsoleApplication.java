package com.digitalid.presentation.console;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.registry.UseCaseRegistry;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.OrganisationType;
import com.digitalid.domain.model.Region;
import com.digitalid.infrastructure.config.DependencyInjection;


public class ConsoleApplication {

    private final TerminalMenu terminal;
    private final DependencyInjection di;

    public ConsoleApplication() {
        this.terminal = new TerminalMenu();
        this.di = new DependencyInjection();
    }

    public void start() {
        showWelcome();
        OrganisationContext context = selectOrganisation();
        if (context == null) {
            terminal.close();
            return;
        }

        UseCaseRegistry registry = di.buildRegistry(context);
        ConsoleUI console = createConsole(context, registry);
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
        Region operatingRegion = null;

        if (selectedType == OrganisationType.CENTRAL_AUTHORITY) {
            orgName = "Central Authority";
        } else {
            orgName = terminal.readLine("\nOrganisation name: ");
            if (orgName.isEmpty()) {
                orgName = "[UNNAMED] " + selectedType.getDisplayName() + " Org";
            }
            operatingRegion = selectRegion();
        }

        String orgId = "ORG-" + System.currentTimeMillis();
        return di.createContext(orgId, selectedType, orgName, operatingRegion);
    }

    private Region selectRegion() {
        Region[] regions = Region.values();
        List<String> regionLabels = Arrays.stream(regions)
                .map(Region::getDisplayName)
                .collect(Collectors.toList());

        int selected = terminal.select("Select operating region", regionLabels);
        if (selected == -1) {
            return Region.UNITED_KINGDOM; // default fallback
        }
        return regions[selected];
    }

    private ConsoleUI createConsole(OrganisationContext context, UseCaseRegistry registry) {
        switch (context.getType()) {
            case CENTRAL_AUTHORITY:
                return new CentralAuthorityConsole(terminal, registry, context,
                        di.getWorkerRepository(), di.getCertificationRepository());
            case FINE_DINING:
                return new FineDiningConsole(terminal, registry, context, di.getWorkerRepository());
            case DELIVERY_SERVICE:
                return new DeliveryServiceConsole(terminal, registry, context, di.getWorkerRepository());
            case STREET_VENDOR:
                return new StreetVendorConsole(terminal, registry, context, di.getWorkerRepository());
            default:
                return new BasicOrganisationConsole(terminal, registry, context, di.getWorkerRepository());
        }
    }

    private void showWelcome() {
        System.out.println("        ,----------------,              ,---------,");
        System.out.println("   ,-----------------------,          ,\"        ,\"|");
        System.out.println(" ,\"                      ,\"|        ,\"        ,\"  |");
        System.out.println("+-----------------------+  |      ,\"        ,\"    |");
        System.out.println("|  .-----------------.  |  |     +---------+      |");
        System.out.println("|  | Hello! Welcome  |  |  |     | -==----'|      |");
        System.out.println("|  |  to the         |  |  |     |         |      |");
        System.out.println("|  |     WORKER id   |  |  |/----|`---=    |      |");
        System.out.println("|  |  Management     |  |  |   ,/|==== ooo |      ;");
        System.out.println("|  |  System v1.0 >_ |  |  |  // |(((( [33]|    ,\"");
        System.out.println("|  `-----------------'  |,\" .;'| |((((     |  ,\"");
        System.out.println("+-----------------------+  ;;  | |         |,\"");
        System.out.println("   /_)______________(_/  //'   | +---------+");
        System.out.println("___________________________/___  `,");
        System.out.println("/  oooooooooooooooo  .o.  oooo /,   \\,\"-----------");
        System.out.println("/ ==ooooooooooooooo==.o.  ooo= //   ,`\\--{)B     ,\"");
        System.out.println("/_==__==========__==_ooo__ooo=_/'   /___________,\"");
        System.out.println("`-----------------------------'");
        System.out.println();
        terminal.readLine("Press Enter to continue...");
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
