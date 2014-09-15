/*
 * Created on Jan 27, 2006 by pladd
 *
 */
package com.bottinifuel.pladd.CheckFree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.bottinifuel.Energy.Info.AddressInfo;
import com.bottinifuel.Energy.Info.InfoFactory;
import com.bottinifuel.Energy.Info.CustInfo;

public class CorrectionDialog extends JDialog
{
    private JButton NoChangesBtn;
    private JButton CorrectBtn;
    private JLabel PaymentLabel;
    private JTextField EnergyAcctNumber;
    private JLabel BalanceText;
    private JTextPane EnergyText;
    private static final long serialVersionUID = 1L;
    protected static InfoFactory EnergyInfo = null;

    private boolean Corrected = false;
    private boolean Drop = true;
    private boolean Stop = false;

    private int CorrectedAcctNum;
    private final LineItem Item;
    
    /**
     * Create the dialog
     */
    public CorrectionDialog(CheckFreeImporter cf, LineItem item, boolean allowAccept) throws Exception
    {
        super();
        Item = item;
        setName("CorrectionDialog");
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Verify/Correct Account #");
        getContentPane().setLayout(new BorderLayout());
        setBounds(100, 100, 607, 468);

        if (EnergyInfo == null)
            EnergyInfo = new InfoFactory();

        final JPanel InfoPanel = new JPanel();
        InfoPanel.setLayout(new GridBagLayout());
        getContentPane().add(InfoPanel);

        final JLabel CheckFreeAcctNum = new JLabel("CheckFree: " + item.CustAcctNumText);
        CheckFreeAcctNum.setHorizontalAlignment(SwingConstants.CENTER);
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        InfoPanel.add(CheckFreeAcctNum, gridBagConstraints);

        final JPanel LookupPanel = new JPanel();
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.gridx = 1;
        gridBagConstraints_2.gridy = 0;
        gridBagConstraints_2.insets = new Insets(0, 0, 0, 1);
        InfoPanel.add(LookupPanel, gridBagConstraints_2);
        LookupPanel.setLayout(new BoxLayout(LookupPanel, BoxLayout.X_AXIS));

        final JLabel energyLabel = new JLabel();
        LookupPanel.add(energyLabel);
        energyLabel.setText("Energy: ");

        EnergyAcctNumber = new JTextField();
        EnergyAcctNumber.setMinimumSize(new Dimension(100, 0));
        EnergyAcctNumber.setColumns(20);
        String acctNum = item.CustAcctNumText;
        while (acctNum.charAt(0) == '0')
            acctNum = acctNum.substring(1);
        EnergyAcctNumber.setText(acctNum);
        EnergyAcctNumber.addActionListener(new RefreshAction());
        EnergyAcctNumber.getDocument().addDocumentListener(new DisableChangeListener());
        LookupPanel.add(EnergyAcctNumber);

        final JButton LookupBtn = new JButton();
        LookupBtn.addActionListener(new RefreshAction());
        LookupBtn.setText("Lookup");
        LookupPanel.add(LookupBtn);

        final JButton BottiniLookupButton = new JButton();
        BottiniLookupButton.setText("Bottini # Lookup");
        BottiniLookupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                String text = EnergyAcctNumber.getText();
                if (text.charAt(0) != 'B')
                    EnergyAcctNumber.setText("B" + text);
                ReloadEnergyInfo();
            }
        });
        LookupPanel.add(BottiniLookupButton);

        final JScrollPane CheckFreePane = new JScrollPane();
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.fill = GridBagConstraints.BOTH;
        gridBagConstraints_1.weighty = 1;
        gridBagConstraints_1.weightx = 1;
        gridBagConstraints_1.gridx = 0;
        gridBagConstraints_1.gridy = 1;
        InfoPanel.add(CheckFreePane, gridBagConstraints_1);

        final JTextPane CheckFreeText = new JTextPane();
        CheckFreeText.setEditable(false);
        CheckFreeText.setText("\n" + item.toString());

        CheckFreePane.setViewportView(CheckFreeText);

        final JScrollPane EnergyPane = new JScrollPane();
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.weighty = 1;
        gridBagConstraints_3.weightx = 1;
        gridBagConstraints_3.fill = GridBagConstraints.BOTH;
        gridBagConstraints_3.gridx = 1;
        gridBagConstraints_3.gridy = 1;
        gridBagConstraints_3.insets = new Insets(0, 0, 0, 1);
        InfoPanel.add(EnergyPane, gridBagConstraints_3);

        EnergyText = new JTextPane();
        EnergyText.setEditable(false);
        EnergyPane.setViewportView(EnergyText);

        final JPanel BalanceInfo = new JPanel();
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.gridy = 2;
        gridBagConstraints_4.gridx = 1;
        InfoPanel.add(BalanceInfo, gridBagConstraints_4);

        BalanceText = new JLabel();
        BalanceInfo.add(BalanceText);

        PaymentLabel = new JLabel();
        NumberFormat form = NumberFormat.getCurrencyInstance();
        PaymentLabel.setText("Payment: " + form.format(item.PaymentAmount));
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.gridy = 2;
        gridBagConstraints_5.gridx = 0;
        InfoPanel.add(PaymentLabel, gridBagConstraints_5);
        final JPanel panel_2 = new JPanel();
        getContentPane().add(panel_2, BorderLayout.SOUTH);

        final JButton StopBatchBtn = new JButton();
        StopBatchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Stop = true;
                dispose();
            }
        });
        panel_2.add(StopBatchBtn);
        StopBatchBtn.setText("Stop Batch");

        final JButton DropBtn = new JButton();
        DropBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Drop = true;
                dispose();
            }
        });
        panel_2.add(DropBtn);
        DropBtn.setText("Drop from batch");

        CorrectBtn = new JButton();
        CorrectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Corrected = true;
                Drop = false;
                dispose();
            }
        });
        panel_2.add(CorrectBtn);
        CorrectBtn.setText("Correct Account Number");

        NoChangesBtn = new JButton();
        NoChangesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Drop = false;
                dispose();
            }
        });
        NoChangesBtn.setText("No Changes");
        NoChangesBtn.setEnabled(allowAccept);
        panel_2.add(NoChangesBtn);
        
        ReloadEnergyInfo();
    }

    
    private class DisableChangeListener implements DocumentListener
    {

        public void insertUpdate(DocumentEvent arg0)
        {
            NoChangesBtn.setEnabled(false);
            CorrectBtn.setEnabled(false);
        }

        public void removeUpdate(DocumentEvent arg0)
        {
            NoChangesBtn.setEnabled(false);
            CorrectBtn.setEnabled(false);
        }

        public void changedUpdate(DocumentEvent arg0)
        {
            NoChangesBtn.setEnabled(false);
            CorrectBtn.setEnabled(false);
        }        
    }


    private void ReloadEnergyInfo()
    {
        try
        {
            String a = EnergyAcctNumber.getText();
            int shortAcctNum;
            int fullAccountNum;
            if (a.charAt(0) == InfoFactory.OACprefix)
            {
                shortAcctNum = EnergyInfo.OACLookup(a);
                fullAccountNum = EnergyInfo.InternalToFullAccount(shortAcctNum);
                EnergyAcctNumber.setText(Integer.toString(fullAccountNum));
            }
            else
            {
                fullAccountNum = Integer.parseInt(a);
                shortAcctNum = EnergyInfo.AccountNum(fullAccountNum);
            }
            CorrectedAcctNum = fullAccountNum;
            
            CustInfo ci = EnergyInfo.GetCustomer(shortAcctNum);

            String addrString = "";
            for (AddressInfo ai : ci.Addrs)
            {
                addrString += ai.toString() + "\n\n";
            }
            EnergyText.setText(addrString);
            
            NumberFormat form = NumberFormat.getCurrencyInstance();
            BalanceText.setText("Balance: " + form.format(ci.Balance)
                                + "   BPA: " + form.format(ci.BudgetPayment));

            int eacct, iacct = 0;
            try
            {
                iacct = Integer.parseInt(Item.CustAcctNumText);
            }
            catch (NumberFormatException e)
            {
                iacct = 0;
            }

            eacct = Integer.parseInt(EnergyAcctNumber.getText());
            if (eacct == iacct)
            {
                if (iacct != 0)
                    NoChangesBtn.setEnabled(true);
                else
                    NoChangesBtn.setEnabled(false);
                CorrectBtn.setEnabled(false);
            }
            else
            {
                NoChangesBtn.setEnabled(false);
                CorrectBtn.setEnabled(true);
            }
        }
        catch (NumberFormatException n)
        {
            EnergyText.setText("Account number is not numeric");
            BalanceText.setText("Account number is not numeric");
            NoChangesBtn.setEnabled(false);
            CorrectBtn.setEnabled(false);
        }
        catch (Exception e)
        {
            EnergyText.setText(e.getMessage());
            BalanceText.setText(e.getMessage());
            NoChangesBtn.setEnabled(false);
            CorrectBtn.setEnabled(false);
        }
    }
    
    private class RefreshAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            ReloadEnergyInfo();
        }
    }

    /**
     * @return Returns the corrected.
     */
    public boolean isCorrected()
    {
        return Corrected;
    }

    /**
     * @return Returns the correctedAcctNum.
     */
    public int getCorrectedAcctNum()
    {
        return CorrectedAcctNum;
    }

    /**
     * @return Returns the drop.
     */
    public boolean isDrop()
    {
        return Drop;
    }

    /**
     * @return Returns the stop.
     */
    public boolean isStop()
    {
        return Stop;
    }
}
