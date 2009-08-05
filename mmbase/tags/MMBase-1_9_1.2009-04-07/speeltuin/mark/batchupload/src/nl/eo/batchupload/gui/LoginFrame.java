package nl.eo.batchupload.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * LoginFrame<br>
 * <br>
 * Show a login frame in which the user can supply his credentials.
 *
 * @author Johannes Verelst
 */
public class LoginFrame extends JInternalFrame {
    JTextField tfUsername = new JTextField(15);
    JPasswordField tfPassword = new JPasswordField(15);
    JLabel lblMelding = new JLabel("Log in");
	
    public LoginFrame() {
        super("Login", false , false, false, true);
        setLocation(100,200);
        setSize(400,100);

        JPanel desktop = new JPanel(); 

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        desktop.setLayout(layout);

        // Algemene gridbag constraints
        c.fill = GridBagConstraints.HORIZONTAL; 
        c.anchor = GridBagConstraints.NORTH;

        // Specifieke gridbag constraints
        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        layout.setConstraints(lblMelding, c);
        desktop.add(lblMelding);

        JLabel lblUsername = new JLabel("Gebruikersnaam:");
        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        layout.setConstraints(lblUsername, c);
        desktop.add(lblUsername);

        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 1;
        layout.setConstraints(tfUsername, c);
        desktop.add(tfUsername);

        JLabel lblPassword = new JLabel("Wachtwoord:");
        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 2;
        layout.setConstraints(lblPassword, c);
        desktop.add(lblPassword);

        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 2;
        layout.setConstraints(tfPassword, c);
        desktop.add(tfPassword);

        AbstractAction loginAction = new AbstractAction("Login") {
            public void actionPerformed(java.awt.event.ActionEvent e) {
				String result = BatchUploadMain.login(tfUsername.getText(), new String(tfPassword.getPassword()));
                if (!result.equals("")) {
                    lblMelding.setText(result);
                    lblMelding.setForeground(Color.red);
                }
                tfPassword.setText("");
            };
        };

        JButton btnLogin = new JButton(loginAction);
        c.weightx = 0.1;
        c.gridx = 2;
        c.gridy = 1;
        layout.setConstraints(btnLogin, c);
        desktop.add(btnLogin);

        tfUsername.setNextFocusableComponent(tfPassword);
		tfPassword.setNextFocusableComponent(btnLogin);
		btnLogin.setNextFocusableComponent(tfUsername);

        getContentPane().add(desktop, BorderLayout.CENTER);
        super.pack();
    }
    
    public void focusUsername() {
        tfUsername.requestFocus();
        tfUsername.requestDefaultFocus();
    }
}
