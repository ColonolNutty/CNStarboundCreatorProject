package com.colonolnutty.module.shareddata.ui;

import com.colonolnutty.module.shareddata.DebugWriter;
import com.colonolnutty.module.shareddata.models.MessageBundle;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/27/2017
 * Time: 10:25 AM
 */
public class OutputDisplay extends DebugWriter {
    private JPanel _displayPanel;
    private JTextArea _outputDisplayTextArea;
    private JScrollPane _outputDisplayScroll;
    private DefaultMutableTreeNode _topLevelOutputNode;
    private JTree _outputTree;

    public JPanel get() {
        setup();
        return _displayPanel;
    }

    public void clear() {
        _outputDisplayTextArea.setText("");
    }

    @Override
    public void writeln(String text) {
        if(_outputDisplayTextArea.getText().isEmpty()) {
            _outputDisplayTextArea.append(text);
            return;
        }
        _outputDisplayTextArea.append("\n" + text);
        _outputDisplayScroll.getHorizontalScrollBar().setValue(0);
    }

    @Override
    public void write(Exception e) {
        writeln(e.getMessage());
    }

    public void updateTreeDisplay(Hashtable<String, MessageBundle> bundles) {
        _topLevelOutputNode.removeAllChildren();
        DefaultTreeModel model = (DefaultTreeModel)_outputTree.getModel();
        model.reload();
        Enumeration<String> keys = bundles.keys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            MessageBundle bundle = bundles.get(key);
            _topLevelOutputNode.add(addNode(bundle));
        }
        if(bundles.isEmpty()) {
            return;
        }
        _outputTree.expandRow(0);
    }

    private void setup() {
        if(_displayPanel != null) {
            return;
        }
        _displayPanel = new JPanel();
        GroupLayout layout = new GroupLayout(_displayPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        _displayPanel.setLayout(layout);

        JLabel label = new JLabel("Console Output: ");

        _outputDisplayTextArea = new JTextArea();
        DefaultCaret caret = (DefaultCaret) _outputDisplayTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        _outputDisplayTextArea.setEditable(false);
        _outputDisplayTextArea.setRows(20);
        _outputDisplayTextArea.setAutoscrolls(true);
        CNUIExtensions.addInternalPadding(_outputDisplayTextArea, 10);
        _outputDisplayScroll = new JScrollPane(_outputDisplayTextArea);
        _outputDisplayScroll.setAutoscrolls(true);
        _outputDisplayScroll.getHorizontalScrollBar().setValue(0);

        _topLevelOutputNode = new DefaultMutableTreeNode("File Events");
        _outputTree = setupOutputTree(_topLevelOutputNode);

        JScrollPane outputTree = new JScrollPane(_outputTree);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(outputTree, 500, 500, 500)
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(label)
                                        .addComponent(_outputDisplayScroll, 500, 500, 10000)
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addComponent(outputTree)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(label)
                                        .addComponent(_outputDisplayScroll)
                        )
        );
        _displayPanel.setVisible(true);
    }

    private static JTree setupOutputTree(DefaultMutableTreeNode _topLevelOutputNode) {
        final JTree tree = new JTree(_topLevelOutputNode);
        tree.setEditable(false);
        tree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e){
                JTree sourceTree = (JTree) e.getSource();
                int selectedRow = sourceTree.getRowForLocation(e.getX(), e.getY());
                if(sourceTree.isCollapsed(selectedRow)) {
                    sourceTree.expandRow(selectedRow);
                }
                else {
                    sourceTree.collapseRow(selectedRow);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        CNUIExtensions.addInternalPadding(tree, 10);

        tree.setCellRenderer(new HighlightableTreeCellRenderer());
        return tree;
    }

    private DefaultMutableTreeNode addNode(MessageBundle bundle) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(bundle);
        top.setUserObject(bundle);
        //bundle.orderBy(new Comparator<MessageBundle>() {
        //    @Override
        //    public int compare(MessageBundle o1, MessageBundle o2) {
        //        return o1.toString().compareTo(o2.toString());
        //    }
        //});
        for(int i = 0; i < bundle.size(); i++) {
            top.add(addNode(bundle.get(i)));
        }
        return top;
    }
}
