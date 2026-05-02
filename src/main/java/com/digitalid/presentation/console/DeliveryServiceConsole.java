package com.digitalid.presentation.console;

import java.util.ArrayList;
import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.registry.UseCaseRegistry;
import com.digitalid.application.request.VerifyWorkerRequest;
import com.digitalid.application.request.ViewWorkerRequest;
import com.digitalid.domain.model.*;


public class DeliveryServiceConsole extends ConsoleUI {

    private final UseCaseRegistry registry;
    private final OrganisationContext context;
    private final List<MenuOption> menuOptions;

    public DeliveryServiceConsole(TerminalMenu terminal, UseCaseRegistry registry,
                                  OrganisationContext context, WorkerRepository workerRepository) {
        super(terminal, workerRepository);
        this.registry = registry;
        this.context = context;
        this.menuOptions = buildMenu();
    }

    private List<MenuOption> buildMenu() {
        List<MenuOption> options = new ArrayList<>();
        options.add(new MenuOption(1, "View Worker", ToolType.VIEW_WORKER));
        options.add(new MenuOption(2, "Verify Worker (Basic)", ToolType.VERIFY_BASIC));
        options.add(new MenuOption(3, "Verify Work Authorisation", ToolType.VERIFY_WORK_AUTHORISATION));
        options.add(new MenuOption(4, "Verify with Conditions", ToolType.VERIFY_WITH_CONDITIONS));
        return options;
    }

    @Override
    public List<MenuOption> getMenuOptions() {
        return menuOptions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    String workerId = promptWorkerId();
                    ViewWorkerRequest viewReq = new ViewWorkerRequest(workerId);
                    UseCase<ViewWorkerRequest, Worker> viewUC =
                            (UseCase<ViewWorkerRequest, Worker>) registry.getUseCase(ToolType.VIEW_WORKER, context);
                    Worker worker = viewUC.execute(viewReq);
                    printInfo("\n--- Worker Details ---");
                    printInfo("ID:     " + worker.getWorkerId());
                    printInfo("Name:   " + worker.getFullName());
                    printInfo("Email:  " + worker.getEmail());
                    printInfo("Region: " + worker.getRegion().getDisplayName());
                    printInfo("Status: " + worker.getStatus());
                    break;

                case 2: handleVerify(ToolType.VERIFY_BASIC); break;
                case 3: handleVerify(ToolType.VERIFY_WORK_AUTHORISATION); break;
                case 4: handleVerify(ToolType.VERIFY_WITH_CONDITIONS); break;
                default: printError("Invalid choice");
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void handleVerify(ToolType tool) {
        String workerId = promptWorkerId();
        VerifyWorkerRequest request = new VerifyWorkerRequest(workerId, tool);
        UseCase<VerifyWorkerRequest, VerificationResult> useCase =
                (UseCase<VerifyWorkerRequest, VerificationResult>) registry.getUseCase(tool, context);
        VerificationResult result = useCase.execute(request);
        printInfo("Valid: " + result.isValid());
        printInfo("Message: " + result.getMessage());
    }

}
