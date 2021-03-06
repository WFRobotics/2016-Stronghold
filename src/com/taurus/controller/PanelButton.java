package com.taurus.controller;

import edu.wpi.first.wpilibj.buttons.Button;
/**
 * Panel button for use with Command based robot
 * @author drlindne
 *
 */
public class PanelButton extends Button
{
    Panel hardware;
    Panel.ButtonType button;
    
    public PanelButton(Panel hardware, Panel.ButtonType button) {
        this.hardware = hardware;
        this.button = button;
    }

    public boolean get()
    {
        return hardware.getButton(button);
    }
}
