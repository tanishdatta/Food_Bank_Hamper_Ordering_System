/** GUIViewController.java
 *  Java Class file for ENSF409 Final Project - Winter 2022 - Group 5
 *  Represents controllers for the GUI Interface and manages page changes
 *  Copyright © 2022 I.B., T.D., M.M.
 *  @author Ideen, Tanish, Mary 
 *  @version 1.9
 *  @since 1.4
 */

package edu.ucalgary.ensf409;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.regex.Matcher;

import java.awt.Color;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;


public class GUIViewController extends JFrame implements ActionListener, MouseListener{

    private JPanel upperPanel = new JPanel();
    private JPanel midPanel = new JPanel();
    private JPanel footerPanel = new JPanel();
    private JDialog loadingPane;

    /**
    * Constructor for GUIViewCOntroller 
    * Creates new GUI Object and sets default values
    */
    public GUIViewController(){
        super("Hamper Application");

        setSize(1000,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       
        
    }
    
    /**
     * reloadGUI()
     * Reloads the JPanel Boxes be removing them from the JFrame, readding them and finally re-rendering them.
     * @return void
     */
    public void reloadGUI(){
        this.remove(upperPanel);
        this.remove(midPanel);
        this.remove(footerPanel);
        this.add(upperPanel, BorderLayout.NORTH);
        this.add(midPanel, BorderLayout.CENTER);
        this.add(footerPanel, BorderLayout.PAGE_END);

        upperPanel.revalidate();
        upperPanel.repaint();
        midPanel.revalidate();
        midPanel.repaint();
        footerPanel.revalidate();
        footerPanel.repaint();
        
    }

    /**
     * resetsLayouts()
     * Resets JPanel contents by removing all children and resetting layout.
     * Prepares panels for future additions of objects
     * @return void
     */
    public void resetLayouts(){
        upperPanel.removeAll();
        midPanel.removeAll();
        footerPanel.removeAll();
        
        upperPanel.setLayout(new FlowLayout());
        midPanel.setLayout(new FlowLayout());
        footerPanel.setLayout(new FlowLayout());
    }

    /**
    * GUILoadHome()
    * Loads the Home View on the main JFrame Object
    * and adds all the required display / user input elements
    * to the view.
    * @return void
    */
    public void GUILoadHome(){
        resetLayouts();
        JLabel title = new JLabel("Please select what you would like to do:");
        JLabel inventorySize = new JLabel("There are " + HamperApp.inventory.getFoodlist().size() + " items in the inventory");

        JButton createOrder = new JButton("Create Order");
        createOrder.setVerticalAlignment(SwingConstants.CENTER);
        createOrder.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Create new Request Order
                HamperApp.currentRequest = new Request("Order", LocalDate.now());
                GUIViewController.this.GUILoadOrder();
            }
        });
        
        upperPanel.add(title);
        midPanel.add(createOrder);
        footerPanel.add(inventorySize);
        

        reloadGUI();
    }

    /**
    * GUILoadOrder()
    * Loads the Order View on the main JFrame Object
    * and adds all the required display / user input elements
    * to the view.
    * @return void
    */
    public void GUILoadOrder(){
        resetLayouts();
        JLabel title = new JLabel("List of all current Hampers");

        for (Hamper hamper : HamperApp.currentRequest.getHampers()){
            JPanel pane = new JPanel();
            JPanel paneDetails = new JPanel();
            pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
            paneDetails.setLayout(new FlowLayout());
            pane.setBorder(BorderFactory.createLineBorder(Color.black));

            JLabel clientName = new JLabel(hamper.getClientName() + ":");
            JLabel numAdultFemale = new JLabel("Adult Females: " + String.valueOf(hamper.getNumAdultFemales()));
            JLabel numAdultMale = new JLabel("Adult Males: " + String.valueOf(hamper.getNumAdultMales()));
            JLabel numChildUnder8 = new JLabel("Children Under 8: " + String.valueOf(hamper.getNumChildUnder8()));
            JLabel numChildOver8 = new JLabel("Children Over 8: " + String.valueOf(hamper.getNumChildOver8()));

            pane.add(clientName);
            paneDetails.add(numAdultMale);
            paneDetails.add(numAdultFemale);
            paneDetails.add(numChildUnder8);
            paneDetails.add(numChildOver8);
            pane.add(paneDetails);
            midPanel.add(pane);
            
        }
        JButton processOrder = new JButton("Process Order");
        processOrder.addActionListener(this);
        processOrder.setHorizontalAlignment(SwingConstants.RIGHT);
        processOrder.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                boolean valid = true;
                if(HamperApp.currentRequest.getHampers().size() == 0){
                    displayError("You must have at least 1 Hamper to process an order!");
                    valid = false;
                }
                if (valid){
                    if (InventoryService.inventoryCheckAlgorithm()){
                        // success
                        GUIViewController.this.GUIDisplayFormConfirmation();

                    } else{
                        // InventoryNotAvailableException
                        GUIViewController.this.GUIDisplayPostError();
                    }
                    
                }

            }
        });

        JButton addHamper = new JButton("Add Hamper");
        addHamper.setHorizontalAlignment(SwingConstants.LEFT);
        addHamper.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GUIViewController.this.GUIAddHamperBox();

            }
        });
        
        upperPanel.add(title);
        footerPanel.add(addHamper);
        footerPanel.add(processOrder);

        reloadGUI();
    }

    /**
    * GUIAddHamperBox()
    * Loads the Add Hamper View on the main JFrame Object
    * and adds all the required display / user input elements
    * to the view.
    * @return void
    */
    public void GUIAddHamperBox(){
        resetLayouts();
        JLabel title = new JLabel("Add New Hamper:");

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBorder(BorderFactory.createLineBorder(Color.black));


        JLabel hamperID = new JLabel("(#" + String.valueOf(HamperApp.currentRequest.getHampers().size() + 1) + ")");
        JTextFieldPlaceHolder clientName = new JTextFieldPlaceHolder("Client Name");
        JTextFieldPlaceHolder numAdultMale = new JTextFieldPlaceHolder("Adult Males");
        JTextFieldPlaceHolder numAdultFemale = new JTextFieldPlaceHolder("Adult Females");
        JTextFieldPlaceHolder numChildUnder8 = new JTextFieldPlaceHolder("Child < 8");
        JTextFieldPlaceHolder numChildOver8 = new JTextFieldPlaceHolder("Child > 8"); 

        clientName.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                hamperID.setText(clientName.getText() + "(#" + String.valueOf(HamperApp.currentRequest.getHampers().size() + 1) + ")");

            }
            public void removeUpdate(DocumentEvent e) {
                hamperID.setText(clientName.getText() + "(#" + String.valueOf(HamperApp.currentRequest.getHampers().size() + 1 + ")" ));

            }
            public void insertUpdate(DocumentEvent e) {
                hamperID.setText(clientName.getText() + "(#" + String.valueOf(HamperApp.currentRequest.getHampers().size() + 1 + ")" ));

            }
          
          });

        JButton back = new JButton("Back");
        back.setHorizontalAlignment(SwingConstants.LEFT);
        back.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GUIViewController.this.GUILoadOrder();

            }
        });

        JButton save = new JButton("Save");
        save.setHorizontalAlignment(SwingConstants.RIGHT);
        save.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Validate inputs add hamper and return if ok
                boolean valid = true;
                int male = 0;
                int female = 0;
                int childunder = 0;
                int childover = 0;
                Pattern stringPattern = Pattern.compile("[a-zA-Z\\s]*$");
                Matcher matcher = stringPattern.matcher(clientName.getText());
                // BEGIN ERROR CHECKING
                if (!matcher.matches() || clientName.getText().equals("Client Name")){
                    valid = false;
                    displayError("Client name can only contain letters or spaces!");
                }

                try {
                    male = Integer.parseInt(numAdultMale.getText());
                    if (male < 0){
                        valid = false;
                        displayError(PersonType.ADULTMALE);
                    }
                } catch (Exception eevent ){
                    valid = false;
                    displayError(PersonType.ADULTMALE);
                }

                try {
                     female = Integer.parseInt(numAdultFemale.getText());
                     if (female < 0){
                        valid = false;
                        displayError(PersonType.ADULTFEMALE);
                    }
                } catch (Exception eevent ){
                    valid = false;
                    displayError(PersonType.ADULTFEMALE);
                }

                try {
                     childunder = Integer.parseInt(numChildUnder8.getText());
                     if (childunder < 0){
                        valid = false;
                        displayError(PersonType.CHILDUNDER8);
                    }
                } catch (Exception eevent ){
                    valid = false;
                    displayError(PersonType.CHILDUNDER8);
                }

                try {
                    childover = Integer.parseInt(numChildOver8.getText());
                    if (childover < 0){
                        valid = false;
                        displayError(PersonType.CHILDOVER8);
                    }
                } catch (Exception eevent ){
                    valid = false;
                    displayError(PersonType.CHILDOVER8);
                }

                if (valid){
                    GUIViewController.this.genericLoader("Creating Hamper");
                    if (HamperApp.currentRequest.addHamper(clientName.getText(), male, female, childunder, childover)){
                        GUIViewController.this.GUILoadOrder();
                        GUIViewController.this.genericLoaderHide();
                    }
                    
                }
                // END ERROR CHECKING

            }

            
        });
        
        pane.add(hamperID, BorderLayout.WEST);
        pane.add(clientName);
        pane.add(numAdultMale);
        pane.add(numAdultFemale);
        pane.add(numChildUnder8);
        pane.add(numChildOver8);

        upperPanel.add(title);
        midPanel.add(pane, BorderLayout.AFTER_LAST_LINE);
        footerPanel.add(back);
        footerPanel.add(save);
        reloadGUI();
    }

    /**
    * GUIDisplayFormConfirmation()
    * Loads the Confirmation View on the main JFrame Object
    * and adds all the required display / user input elements
    * to the view.
    * @return void
    * 
    */
    public void GUIDisplayFormConfirmation(){
        resetLayouts();
        JLabel title = new JLabel("Order Form:");
        JLabel amount = new JLabel(HamperApp.currentRequest.getHampers().size() + " Total Hampers");
        JLabel body = new JLabel("The order form has been succesfully created and placed in the working directory!");
        
        JButton home = new JButton("Home");
        home.addActionListener(this);
        home.setHorizontalAlignment(SwingConstants.CENTER);
        home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GUIViewController.this.GUILoadHome();

            }
        });
        
        upperPanel.add(title);
        upperPanel.add(amount);
        midPanel.add(body);
        footerPanel.add(home);

        reloadGUI();
    }

    /**
    * GUILoadOrder()
    * Loads the Error View on the main JFrame Object
    * and adds all the required display / user input elements
    * to the view after an unsuccesful process order sequence.
    * @return void
    * 
    */
    public void GUIDisplayPostError(){
        resetLayouts();
        JLabel title = new JLabel("Could not fulfill order:");
        JLabel amount = new JLabel(HamperApp.currentRequest.getHampers().size() + " Total Hampers");
        JPanel errorBox = new JPanel();
        errorBox.setLayout(new BoxLayout(errorBox, BoxLayout.Y_AXIS));
        JLabel message1 = new JLabel("There were some shortages when attempting to fulfill the entire request. They have been highlighted below:");
        errorBox.add(message1);
        for (Map.Entry item:  InventoryService.getMissingCategory().entrySet()){
            JLabel message = new JLabel("There was a shortage in the " + item.getKey() + " category");
            errorBox.add(message);
        }
        // if true for category print

        
        JButton home = new JButton("Home");
        home.addActionListener(this);
        home.setHorizontalAlignment(SwingConstants.CENTER);
        home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GUIViewController.this.GUILoadHome();

            }
        });
        
        upperPanel.add(title);
        upperPanel.add(amount);
        midPanel.add(errorBox);
        footerPanel.add(home);

        reloadGUI();
    }

    /**
    * displayError(PersonType error)
    * Displays an error to the GUI for a specific PersonType
    * @return void
    * @param type A PersonType that specifies which field the error is gonna be for
    */
    public void displayError(PersonType type){
        JOptionPane.showMessageDialog(midPanel, type.toString() + " must be a number!", "Invalid input", JOptionPane.ERROR_MESSAGE);
    }

    /**
    * displayError(String error)
    * Displays an error to the GUI with the specified text
    * @return void
    * @param error  astring that contains the error message
    */
    public void displayError(String error){
        JOptionPane.showMessageDialog(midPanel, error, "Invalid input", JOptionPane.ERROR_MESSAGE);
    }

    /**
    * genericError(String error)
    * Displays a generic warning message to user with the string provided
    * @return void
    * @param error A string that contains the warning message
    */
    public static void genericError(String error){
        JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.WARNING_MESSAGE);
    }

    /**
    * genericLoader(String loadingMessage)
    * Displays a generic loading screen with a message from provided string
    * @return void
    * @param loadingMessage A string that contain the loading message
    */
    public void genericLoader(String loadingMessage){
        if (loadingPane !=null){
            this.genericLoaderHide();
        }
        
        loadingPane = new JDialog();
        JLabel label = new JLabel(loadingMessage);  
        JPanel panel = new JPanel();
        panel.add(label, BorderLayout.NORTH);
        
        loadingPane.setLocationRelativeTo(this);
        loadingPane.setTitle("Loading...");
        loadingPane.setLayout(new FlowLayout());
        loadingPane.setSize(250,175);
        loadingPane.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        loadingPane.add(panel, BorderLayout.NORTH);

        loadingPane.setVisible(true);
        
    }

    /**
    * genericLoaderHide()
    * Hides a previous loading screen if it was present
    * @return void
    *   
    */
    public void genericLoaderHide(){
        loadingPane.dispose();
        loadingPane.setModal(false);
        loadingPane.removeAll();
        
        loadingPane.setVisible(false);
        loadingPane.repaint();;
        
    }

    /**
    * actionPerformed(ActionEvent event)
    * Handles the specified actions 
    * (We do not handle actions here, we handle them 
    * directly under each Object using ActionListeners 
    * and nested classes)
    * @return void
    * @param event an event that occured due to the user interacting with the app
    */
    public void actionPerformed(ActionEvent event){


    }
    
    /**
    * mouseClicked(ActionEvent event)
    * Handles the specified actions 
    * (We do not handle actions here, we handle them 
    * directly under each Object using MouseListeners  
    * and nested classes)
    * @return void
    * @param event a mouse event that occured due to the user interacting with the app
    */
    public void mouseClicked(MouseEvent event){
 
                
    }

    /**
    * mouseEntered(ActionEvent event)
    * Handles the specified actions 
    * (We do not handle actions here, we handle them 
    * directly under each Object using MouseListeners  
    * and nested classes)
    * @return void
    * @param event a mouse event that occured due to the user interacting with the app
    */
    public void mouseEntered(MouseEvent event){
        
    }

    /**
    * mouseExited(ActionEvent event)
    * Handles the specified actions 
    * (We do not handle actions here, we handle them 
    * directly under each Object using MouseListeners 
    * and nested classes)
    * @return void
    * @param event a mouse event that occured due to the user interacting with the app
    */
    public void mouseExited(MouseEvent event){
        
    }

    /**
    * mousePressed(ActionEvent event)
    * Handles the specified actions 
    * (We do not handle actions here, we handle them 
    * directly under each Object using MouseListeners 
    * and nested classes)
    * @return void
    * @param event a mouse event that occured due to the user interacting with the app
    */
    public void mousePressed(MouseEvent event){
        
    }

    /**
    * mouseReleased(ActionEvent event)
    * Handles the specified actions 
    * (We do not handle actions here, we handle them 
    * directly under each Object using MouseListeners 
    * and nested classes)
    * @return void
    * @param event a mouse event that occured due to the user interacting with the app
    */
    public void mouseReleased(MouseEvent event){
        
    }
    
}

/**
    * JTextFieldPlaceHolder
    * Special Local Class that extends a standard JTextField
    * but adds the functionality of a grey placeholder when the field is empty
    *
    */
class JTextFieldPlaceHolder extends JTextField{
    public JTextFieldPlaceHolder(String placeholder){
        super(placeholder, 15);
        this.placeholder = placeholder;

        this.setForeground(Color.GRAY);
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (JTextFieldPlaceHolder.this.getText().equals(placeholder)) {
                    JTextFieldPlaceHolder.this.setText("");
                    JTextFieldPlaceHolder.this.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (JTextFieldPlaceHolder.this.getText().isEmpty()) {
                    JTextFieldPlaceHolder.this.setForeground(Color.GRAY);
                    JTextFieldPlaceHolder.this.setText(placeholder);
                }
            }
            });
        
    }
    private String placeholder;

}