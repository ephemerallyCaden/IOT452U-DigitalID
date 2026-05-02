package com.digitalid.presentation.console;

import com.digitalid.domain.model.ToolType;

public class MenuOption {

    private final int number;
    private final String label;
    private final ToolType toolType;

    public MenuOption(int number, String label, ToolType toolType) {
        this.number = number;
        this.label = label;
        this.toolType = toolType;
    }

    public int getNumber() {
        return number;
    }

    public String getLabel() {
        return label;
    }

    public ToolType getToolType() {
        return toolType;
    }

    public String display() {
        return "  [" + number + "] " + label;
    }

}
