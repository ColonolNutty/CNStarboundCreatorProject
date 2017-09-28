package com.company.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * User: Jack's Computer
 * Date: 09/28/2017
 * Time: 3:26 PM
 */
public abstract class CNUIExtensions {

    public static void addInternalPadding(JComponent component, int padding) {
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        component.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
    }
}
