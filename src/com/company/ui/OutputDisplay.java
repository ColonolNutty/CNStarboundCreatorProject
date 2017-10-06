package com.company.ui;

import com.company.DebugWriter;
import com.company.models.MessageBundle;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
        _outputTree = new JTree(_topLevelOutputNode);
        _outputTree.setEditable(false);
        _outputTree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(_outputTree.isCollapsed(_outputTree.getRowForLocation(e.getX(),e.getY()))) {
                    _outputTree.expandRow(_outputTree.getRowForLocation(e.getX(),e.getY()));
                }
                else {
                    _outputTree.collapseRow(_outputTree.getRowForLocation(e.getX(),e.getY()));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) _outputTree.getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        CNUIExtensions.addInternalPadding(_outputTree, 10);

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

    private DefaultMutableTreeNode addNode(MessageBundle bundle) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(bundle.toString());
        bundle.orderBy(new Comparator<MessageBundle>() {
            @Override
            public int compare(MessageBundle o1, MessageBundle o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for(int i = 0; i < bundle.size(); i++) {
            top.add(addNode(bundle.get(i)));
        }
        return top;
    }
}
