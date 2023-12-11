/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package todolist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author Sujit T K
 */
public class ToDoList extends JFrame{
    
    private final JPanel paneBase = new JPanel();
    private final Map<String, Boolean> data = new HashMap<String, Boolean>();
    private final JPanel pnlTasks = new JPanel();
    private BorderLayout bl = new BorderLayout();
    private byte tabSelected = 0;
    
    private void setupFrame() {
        this.loadData();
        this.setTitle("Todo List App");
        this.setSize(400, 500);
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.paneBase.setPreferredSize(new Dimension(400,140));
        this.paneBase.setMinimumSize(new Dimension(400,140));
        this.paneBase.setMaximumSize(new Dimension(400,140));
        this.paneBase.setBackground(new Color(24, 25, 38));
        this.paneBase.setLayout(null);
        this.paneBase.setLocation(0, 0);
        this.setLayout(bl);
        this.add(paneBase, BorderLayout.NORTH);
        this.addUIComponents();
    }
    
    private JLabel getLabel(final String text, final int X, final int Y, final int width, final int height, final Color clrText, final Font f) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(X, Y, width, height);
        lbl.setFont(f);
        lbl.setForeground(clrText);
        return lbl;
    }
    
    private JTextField getTextBox(final int X, final int Y, final int width, final int height, final String promptText) {
        JTextField tField = new JTextField(promptText);
        tField.setBounds(X, Y, width, height);
        tField.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                if(tField.getText().isEmpty()) {
                    tField.setText(promptText);
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                if(tField.getText().equals(promptText)) {
                    tField.setText("");
                }
            }
            
        });
        return tField;
    }
    
    private JButton getButton(final String text, final int X, final int Y) {
        JButton btn = new JButton(text);
        btn.setBounds(X, Y, 70, 40);
        btn.setBackground(new Color(150, 180, 250));
        btn.setForeground(Color.BLACK);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(btn.isEnabled()) {
                    btn.setBackground(new Color(150, 180, 250));
                    btn.setForeground(Color.BLACK);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if(btn.isEnabled()) {
                    btn.setBackground(new Color(31, 30, 250));
                    btn.setForeground(Color.YELLOW);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if(btn.isEnabled()) {
                    btn.setBackground(new Color(150, 180, 250));
                    btn.setForeground(Color.BLACK);
                }
            }
        });
        return btn;
    }
    
    private void addUIComponents() {
        JLabel lblAppTitle = this.getLabel("My Todo List", 80, 20, 300, 60, new Color(250, 177, 5), new Font("Algerian", 22, 35));
        this.paneBase.add(lblAppTitle);
        
        JTextField txtAddNewTask = this.getTextBox(40, 80, 240, 40, "Add New Task here");        
        this.paneBase.add(txtAddNewTask);
        
        JButton btnAdd = this.getButton("Add", 295, 80);
        btnAdd.setEnabled(false);
        this.paneBase.add(btnAdd);
        
        btnAdd.addActionListener(e-> {
            if(this.data.containsKey(txtAddNewTask.getText())) {
                JOptionPane.showMessageDialog(this, "The task \"" + txtAddNewTask.getText() + "\"  has already been added" + (this.data.get(txtAddNewTask.getText()) ? 
                        " and has already been marked as completed." : " . You can find it under 'Pending' Tab."), "INFO : My To Do List", JOptionPane.INFORMATION_MESSAGE);
            } else {
                this.data.put(txtAddNewTask.getText(), false);
                this.saveData();
                this.addTask(txtAddNewTask.getText());
                this.paneBase.revalidate();
            }
            System.out.println(this.data);
            txtAddNewTask.setText("Add New Task here");
            btnAdd.setEnabled(false);
            this.tabSelected = 0;
            this.pnlTasks.removeAll();
            for(String s : this.data.keySet()) this.addTask(s);
            btnAdd.setBackground(new Color(150, 180, 250));
            btnAdd.setForeground(Color.BLACK);
            this.paneBase.revalidate();
            this.repaint();
        });
        
        txtAddNewTask.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                btnAdd.setEnabled(!txtAddNewTask.getText().isEmpty());// && !data.containsKey(txtAddNewTask.getText())
                repaint();
            }            
        });
        
        final JPanel pnlTabs = new JPanel(new FlowLayout());
        pnlTabs.setPreferredSize(new Dimension(400, 500-422));
        pnlTabs.setBackground(Color.DARK_GRAY);
        this.add(pnlTabs, BorderLayout.CENTER);
        
        final JButton btnAll = new JButton("Show All"), btnPending = new JButton("Pending"), btnCompleted = new JButton("Completed"), btnDelete = new JButton("Remove");
        final Color CLR_TAB_SELECTED = new Color(181,230,29), CLR_TAB_NORMAL = new Color(0, 162, 232);
        btnAll.setBackground(CLR_TAB_SELECTED);
        btnPending.setBackground(CLR_TAB_NORMAL);
        btnCompleted.setBackground(CLR_TAB_NORMAL);
        btnDelete.setBackground(CLR_TAB_NORMAL);
        btnAll.addActionListener(e-> {
            btnAll.setBackground(CLR_TAB_SELECTED);
            btnPending.setBackground(CLR_TAB_NORMAL);
            btnCompleted.setBackground(CLR_TAB_NORMAL);
            btnDelete.setBackground(CLR_TAB_NORMAL);
            this.pnlTasks.removeAll();
            this.tabSelected = 0;
            for(String s : this.data.keySet()) this.addTask(s);
            this.paneBase.revalidate();
            this.repaint();
        });
        pnlTabs.add(btnAll);
        btnPending.addActionListener(e-> {
            btnAll.setBackground(CLR_TAB_NORMAL);
            btnPending.setBackground(CLR_TAB_SELECTED);
            btnCompleted.setBackground(CLR_TAB_NORMAL);
            btnDelete.setBackground(CLR_TAB_NORMAL);
            this.pnlTasks.removeAll();
            this.tabSelected = 1;
            boolean added = false;
            int num = 1;
            for(String s : this.data.keySet()) {
                if (!this.data.get(s)) {
                    added = true;
                    this.addTask(num++ + ".) " + s);
                }
            }
            if(!added) this.addTask("YaY! No more pending tasks available...");
            this.paneBase.revalidate();
            this.repaint();
        });
        pnlTabs.add(btnPending);
        btnCompleted.addActionListener(e-> {
            btnAll.setBackground(CLR_TAB_NORMAL);
            btnPending.setBackground(CLR_TAB_NORMAL);
            btnCompleted.setBackground(CLR_TAB_SELECTED);
            btnDelete.setBackground(CLR_TAB_NORMAL);
            this.pnlTasks.removeAll();
            this.tabSelected = 2;
            boolean added = false;
            int num = 1;
            for(String s : this.data.keySet()) {
                if (this.data.get(s)) {
                    added = true;
                    this.addTask(num++ + ".) " + s);
                }
            }
            if(!added) this.addTask("No tasks has been marked completed!");
            this.paneBase.revalidate();
            this.repaint();
        });
        pnlTabs.add(btnCompleted);
        
        btnDelete.addActionListener(e-> {
            btnAll.setBackground(CLR_TAB_NORMAL);
            btnPending.setBackground(CLR_TAB_NORMAL);
            btnCompleted.setBackground(CLR_TAB_NORMAL);
            btnDelete.setBackground(CLR_TAB_SELECTED);
            this.pnlTasks.removeAll();
            this.tabSelected = 3;
            for(String s : this.data.keySet()) this.addTask(s);
            this.paneBase.revalidate();
            this.repaint();
        });
        pnlTabs.add(btnDelete);
        
        this.pnlTasks.setLayout(new BoxLayout(this.pnlTasks, BoxLayout.PAGE_AXIS));
        this.pnlTasks.setBackground(new Color(153, 217, 234));
        
        JScrollPane pnlScroll = new JScrollPane(this.pnlTasks);
        pnlScroll.setPreferredSize(new Dimension(400, 282));
        this.add(pnlScroll, BorderLayout.SOUTH);
        
        for(String s : this.data.keySet()) this.addTask(s);
        
        this.repaint();
        this.paneBase.revalidate();
    }
    
    private void addTask(final String strTask) {        
        switch (this.tabSelected) {
            case 0:
                JCheckBox completed = new JCheckBox(strTask);
                completed.setFont(new Font("Calibri", 30, 20));
                completed.setBackground(new Color(153, 217, 234));
                completed.setSelected(this.data.get(strTask));
                completed.addChangeListener(e->{
                    this.data.put(strTask, completed.isSelected());
                    this.saveData();
                });
                this.pnlTasks.add(completed);
                break;
            case 1: 
                JLabel lbl = new JLabel("  " + strTask);
                lbl.setFont(new Font("Calibri", 30, 20));
                lbl.setBackground(new Color(153, 217, 234));
                lbl.setSize(4000, 40);
                this.pnlTasks.add(lbl);
                break;
            case 2:
                JLabel LBL = new JLabel("  " + strTask);
                LBL.setFont(new Font("Calibri", 30, 20));
                LBL.setBackground(new Color(153, 217, 234));
                LBL.setSize(4000, 40);
                this.pnlTasks.add(LBL);
                break;
            case 3:
                JPanel panelRemove = new JPanel();
                panelRemove.setPreferredSize(new Dimension(400, 70));
                panelRemove.setBackground(new Color(153, 217, 234));
                panelRemove.setLayout(null);
                JLabel lblTask = new JLabel("  " + strTask);
                lblTask.setFont(new Font("Calibri", 30, 20));
                lblTask.setBackground(new Color(153, 217, 234));
                lblTask.setSize(4000, 40);    
                lblTask.setLocation(20, 10);
                JButton btnRemove = new JButton("Delete");
                btnRemove.setLocation(260, 8);
                btnRemove.setSize(90, 40);
                btnRemove.addActionListener(e-> {
                    this.data.remove(strTask);
                    this.saveData();
                    this.pnlTasks.remove(panelRemove);
                    this.paneBase.revalidate();
                    this.repaint();
                });
                panelRemove.add(btnRemove);
                panelRemove.add(lblTask);
                this.pnlTasks.add(panelRemove);
                panelRemove.repaint();
                panelRemove.revalidate();
                break;
            default:
                throw new AssertionError();
        }
    }
    
    public void saveData() {
        try {
            FileOutputStream f = new FileOutputStream("user_data.rar");
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(this.data);
            o.close();
            f.close();
        } catch (Exception e) {
            System.err.println("Error while saving data... Either FileNotFound or IO Exception.");
        }
    }

    public void loadData() {
        try {
            File f = new File("user_data.rar");
            if (f.exists()) {
                FileInputStream fi = new FileInputStream(f);
                ObjectInputStream oi = new ObjectInputStream(fi);
                this.data.putAll((HashMap<String, Boolean>) oi.readObject());
                oi.close();
                fi.close();
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("Error while retrieving data... The source file Not Found.");
        } catch (IOException ioe) {
            System.err.println("Error while retrieving data... Occurred while attempting to read contents of file.");
            ioe.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Unexpected ClassNotFoundException");
        }
    }
    
    public static void main(String[] args) {
        new ToDoList().setupFrame();
    }
    
}
