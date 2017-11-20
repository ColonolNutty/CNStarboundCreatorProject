package com.colonolnutty.module.shareddata.ui;

import javax.swing.*;

/**
 * User: Jack's Computer
 * Date: 11/19/2017
 * Time: 4:36 PM
 */
public abstract class ConfirmationController {
    public static boolean getConfirmation(String message) {
        int result = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}
