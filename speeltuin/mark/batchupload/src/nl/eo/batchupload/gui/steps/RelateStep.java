package nl.eo.batchupload.gui.steps;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.BatchUpdateException;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import nl.eo.batchupload.gui.BatchUploadMain;
import nl.eo.batchupload.gui.StepsFrame;

/**
 *
 */
public class RelateStep extends StepFrame {
//	StepsFrame parent;
    JTabbedPane tabbedPane = new JTabbedPane();
    SearchPanel searchPanel = new SearchPanel();
    NewPanel newPanel = new NewPanel();
	JButton nextButton;

	public RelateStep(StepsFrame parent) {
        super(parent);
//		this.parent = parent;


        tabbedPane.addTab("Niet relateren", null, new JPanel(), "Niet relateren aan een object");
        tabbedPane.setSelectedIndex(0);
        tabbedPane.setEnabledAt(0, true);

        tabbedPane.addTab("Nieuw", null, newPanel, "Nieuw object om aan te koppelen");
        tabbedPane.setEnabledAt(1, true);
        
        tabbedPane.addTab("Zoek", null, searchPanel, "Zoek naar object om aan te koppelen");
        tabbedPane.setEnabledAt(2, true);
        
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        
        add(new NavigationPane(this), BorderLayout.SOUTH);
	}

    public void actionPerformed(ActionEvent arg0) {
        if ("Cancel".equals(arg0.getActionCommand())) {
            parent.dispose();
            return;
        }
        if (tabbedPane.getSelectedIndex() == 0) {
            parent.putClientProperty("relateaction", "none");
        } else if (tabbedPane.getSelectedIndex() == 1) {
    		parent.putClientProperty("relateaction", "new");

			parent.putClientProperty("relate-title", newPanel.newTitle.getText());
			parent.putClientProperty("relate-subtitle", newPanel.newSubtitle.getText());
			parent.putClientProperty("relate-intro", newPanel.newIntro.getText());
			parent.putClientProperty("relate-body", newPanel.newBody.getText());
			//parent.putClientProperty("relate-type", new Integer(newPanel.newType.getSelectedIndex()));
    	} else if (tabbedPane.getSelectedIndex() == 2) {
            if (searchPanel.resultTable.getSelectedRow() >= 0) {
                parent.putClientProperty("relateaction", "existing");
    	    	Integer objectnumber = new Integer(Integer.parseInt((String)searchPanel.resultModel.getValueAt(
    	    		searchPanel.resultTable.getSelectedRow(), 0
    	    	)));
    	    	String objectTitle = (String)searchPanel.resultModel.getValueAt(
    	    		searchPanel.resultTable.getSelectedRow(), 2
    	    	);
            	parent.putClientProperty("relate-objectid", objectnumber);
    			parent.putClientProperty("relate-title", objectTitle);
            } else {
                parent.putClientProperty("relateaction", "none");
            }
        }
		parent.actionPerformed(arg0);    		
    }



	class SearchPanel extends JPanel implements ActionListener {
		JTextField searchTitle = new JTextField();
		JComboBox searchAge =  new JComboBox(new String[] {"7 Dagen", "1 Maand", "1 Jaar", "1001 Nachten"});
		JTextField searchOwner = new JTextField();
        
		//JComboBox searchType = new JComboBox(new String[] {
		//	"Niet gespecificeerd", "Onbekend",
		//	"Nieuws",	"Interview",	"Verslag", "Column","Recensie", "Fotoverslag", "Mededeling",
		//	"Voorbeschouwing", "Special", "Flevokrant", "Preek", "CD-Recensie", "Boekrecensie",
	    //	"Filmrecensie","Concertrecensie"});
		JButton searchButton = new JButton("Zoek");
		JTable resultTable;

		DefaultTableModel resultModel = new DefaultTableModel() {
			public boolean isCellEditable(int arg0,int arg1) {
				return false;
			}	
		};

		public SearchPanel() {
			JPanel jp = new JPanel();
			
			jp.setLayout(new GridLayout(4, 2));
	        jp.setBorder(BorderFactory.createTitledBorder("Zoek"));
	        jp.add(new JLabel("Titel: "));
	    	jp.add(searchTitle);
            /*
		    jp.add(new JLabel("Leeftijd:"));
	      	jp.add(searchAge);
            */

		    jp.add(new JLabel("Eigenaar:"));
            searchOwner.setText(BatchUploadMain.getCurrentUser());
			jp.add(searchOwner);
			//jp.add(new JLabel("Type:"));
			//searchType.setSelectedIndex(7);
			//jp.add(searchType);
			jp.add(new JLabel(""));
			jp.add(searchButton);
		
			searchButton.addActionListener(this);
			searchButton.setActionCommand("Search");
		
			this.setLayout(new BorderLayout());
			this.add(jp, BorderLayout.NORTH);
			
			JPanel rp = new JPanel();
			rp.setLayout(new BorderLayout());
			
			resultModel.addColumn("Nummer");
			resultModel.addColumn("Eigenaar");
			resultModel.addColumn("Titel");
			//resultModel.addColumn("Type");
			
			resultTable = new JTable(resultModel);
			resultTable.getTableHeader().setReorderingAllowed(false);
			resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JScrollPane p = new JScrollPane(resultTable);
			resultTable.setPreferredScrollableViewportSize(new Dimension(600, 50));
			
			rp.add(p, BorderLayout.CENTER);
			
			this.add(rp, BorderLayout.CENTER);
		}	
		
		public void actionPerformed(ActionEvent e) {
			if ("Search".equals(e.getActionCommand())) {
				String query = "(1 = 1) ";
				if (!searchTitle.getText().equals("")) {
					query += " AND (title LIKE '%" + searchTitle.getText() + "%') ";
				}
				if (!searchOwner.getText().equals("")) {
					query += " AND (owner LIKE '%" + searchOwner.getText() + "%') ";	
				}
				//if (searchType.getSelectedIndex() != 0) {
					//TODO: as soon as you run this against MMBase 1.5, change it to 'type'
					//query += " AND (typ = " + (searchType.getSelectedIndex() - 1) + ") ";
				//}
				
				Vector al = BatchUploadMain.search(query);
				while (resultModel.getRowCount() > 0) {
					resultModel.removeRow(0);
				}

				for (int i=0; i<al.size(); i++) {
					Hashtable h = (Hashtable) al.get(i);
					resultModel.addRow(new Object[] {h.get("_number"), h.get("_owner"), h.get("title")});
				}
			}
		}
		
	}
	
	class NewPanel extends JPanel {
		JTextField newTitle = new JTextField();
		JTextField newSubtitle = new JTextField();
		JTextArea newIntro = new JTextArea();
		JTextArea newBody = new JTextArea();
//		JComboBox newType = new JComboBox(new String[] {
//            "Onbekend",
//            "Nieuws",   "Interview",   "Verslag", "Column","Recensie", "Fotoverslag", "Mededeling",
//            "Voorbeschouwing", "Special", "Flevokrant", "Preek", "CD-Recensie", "Boekrecensie",
//            "Filmrecensie","Concertrecensie"});
		
		public NewPanel() {
	        this.setBorder(BorderFactory.createTitledBorder("Nieuw"));
	        GridBagLayout layout = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();

	        this. setLayout(layout);
	        c.fill = GridBagConstraints.BOTH; 
    	    c.anchor = GridBagConstraints.NORTH;

	        c.weightx = 0.1;
	        c.weighty = 0.1;
	        c.gridx = 0;
	        c.gridwidth = 1;
	        
	        JLabel lblTitel = new JLabel("Titel:");
	        JLabel lblSubtitel= new JLabel("Subtitel:");
	        JLabel lblIntro = new JLabel("Intro:");
	        JLabel lblBody = new JLabel("Body:");
	        //JLabel lblType = new JLabel("Type:");
	        
	        c.gridy = 0;
	        layout.setConstraints(lblTitel, c);
	        this.add(lblTitel);

			c.gridy = 1;
	        layout.setConstraints(lblSubtitel, c);
	        this.add(lblSubtitel);

			c.gridy = 2;
	        layout.setConstraints(lblIntro, c);
	        this.add(lblIntro);

			c.gridy = 3;
	        layout.setConstraints(lblBody, c);
	        this.add(lblBody);

			//c.gridy = 4;
	        //layout.setConstraints(lblType, c);
	        //this.add(lblType);

	        c.weightx = 1;
	        c.gridx = 1;
	        c.gridy = 0;
	        layout.setConstraints(newTitle, c);
			this.add(newTitle);

	        c.gridy = 1;
	        layout.setConstraints(newSubtitle, c);
			this.add(newSubtitle);

	        c.weighty = 1;

	        c.gridy = 2;
			JScrollPane introPane = new JScrollPane(newIntro);
	        layout.setConstraints(introPane, c);
			this.add(introPane);

	        c.gridy = 3;
	        JScrollPane bodyPane = new JScrollPane(newBody);
	        layout.setConstraints(bodyPane, c);
			this.add(bodyPane);

//	        c.weighty = 0.1;
//	        c.gridy = 4;
//			layout.setConstraints(newType, c);
//			newType.setSelectedIndex(6);
//			this.add(newType);
		}
	}
}
