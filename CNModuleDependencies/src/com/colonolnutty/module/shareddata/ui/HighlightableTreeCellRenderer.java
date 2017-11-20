package com.colonolnutty.module.shareddata.ui;

import com.colonolnutty.module.shareddata.models.MessageBundle;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * User: Jack's Computer
 * Date: 11/19/2017
 * Time: 2:10 PM
 */
public class HighlightableTreeCellRenderer extends DefaultTreeCellRenderer {
    private static final Color HOVERCOLOR = new Color(102, 153, 204);

    public HighlightableTreeCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = ((DefaultMutableTreeNode) (value));
        Object userObj = node.getUserObject();
        this.setText(value.toString());
        if(userObj instanceof MessageBundle) {
            MessageBundle bundle = (MessageBundle) userObj;
            if (bundle.shouldHighlight()) {
                setForeground(Color.ORANGE);
                return this;
            }
        }
        setForeground(Color.BLACK);
        return this;
    }
}