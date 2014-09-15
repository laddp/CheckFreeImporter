/*
 * Created on Jan 31, 2006 by pladd
 *
 */
package com.bottinifuel.pladd.CheckFree;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * @author pladd
 *
 */
public class SSH_upload extends JDialog
{
    private static final long serialVersionUID = 1L;

    private JTextField HostKeyFile;
    private JTextField FileName;
    private JTextField DataBaseNum;
    private JTextField DealerName;
    private JPasswordField Password;
    private JTextField UserName;
    private JTextField HostName;
    
    private final String SourceFile;
    
    private boolean DoConnect = false;

    /** Upload line items to an ssh server via sftp
     * 
     * @param li
     * @throws Exception
     */
    public SSH_upload(CheckFreeImporter cf, Vector<LineItem> li)
    {
        super(cf, "Upload BANKPOST file");
        
        SourceFile = cf.OutFileName.getText();
        
        setSize(316, 228);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Upload to Energy");
        setName("SSH_upload");
        setModal(true);

        final JPanel ActionPanel = new JPanel();
        getContentPane().add(ActionPanel, BorderLayout.SOUTH);

        final JButton CancelButton = new JButton();
        CancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        ActionPanel.add(CancelButton);
        CancelButton.setText("Cancel");

        final JButton UploadButton = new JButton();
        UploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                DoConnect = true;
                setVisible(false);
            }
        });
        ActionPanel.add(UploadButton);
        UploadButton.setText("Upload");

        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getContentPane().add(panel);

        final JLabel HostLabel = new JLabel();
        HostLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        HostLabel.setHorizontalAlignment(SwingConstants.CENTER);
        HostLabel.setText("Host: ");
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints.gridx = 0;
        panel.add(HostLabel, gridBagConstraints);

        HostName = new JTextField();
        HostName.setText("energy.bottinifuel.com");
        HostName.setColumns(30);
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_1.weightx = 1;
        gridBagConstraints_1.gridx = 1;
        panel.add(HostName, gridBagConstraints_1);

        final JLabel hostKeyLabel = new JLabel();
        hostKeyLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        hostKeyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
        gridBagConstraints_12.ipadx = 10;
        gridBagConstraints_12.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints_12.gridx = 0;
        panel.add(hostKeyLabel, gridBagConstraints_12);
        hostKeyLabel.setText("Host Key File: ");

        HostKeyFile = new JTextField(cf.getDirectory() 
        		                     + System.getProperty("file.separator") + "data"
                                     + System.getProperty("file.separator") + "host_key.txt");
        HostKeyFile.setColumns(30);
        final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
        gridBagConstraints_13.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_13.gridx = 1;
        panel.add(HostKeyFile, gridBagConstraints_13);

        final JLabel UserLabel = new JLabel();
        UserLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        UserLabel.setHorizontalAlignment(SwingConstants.CENTER);
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.ipadx = 10;
        gridBagConstraints_2.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints_2.gridx = 0;
        panel.add(UserLabel, gridBagConstraints_2);
        UserLabel.setText("User: ");

        UserName = new JTextField();
        UserName.setColumns(30);
        UserName.setText("addsys");
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_3.weightx = 1;
        gridBagConstraints_3.gridx = 1;
        panel.add(UserName, gridBagConstraints_3);

        final JLabel PasswordLabel = new JLabel();
        PasswordLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        PasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        PasswordLabel.setText("Password: ");
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.ipadx = 10;
        gridBagConstraints_5.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints_5.gridx = 0;
        panel.add(PasswordLabel, gridBagConstraints_5);

        Password = new JPasswordField();
        Password.setColumns(30);
        Password.setText("addsys");
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_4.weightx = 1;
        gridBagConstraints_4.gridx = 1;
        panel.add(Password, gridBagConstraints_4);

        final JLabel dealerNameLabel = new JLabel();
        dealerNameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        dealerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dealerNameLabel.setText("Dealer Name:");
        final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
        gridBagConstraints_6.ipadx = 10;
        gridBagConstraints_6.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints_6.gridx = 0;
        panel.add(dealerNameLabel, gridBagConstraints_6);

        DealerName = new JTextField();
        DealerName.setColumns(30);
        DealerName.setText("BOT");
        final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
        gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_7.weightx = 1;
        gridBagConstraints_7.gridx = 1;
        panel.add(DealerName, gridBagConstraints_7);

        final JLabel databaseLabel = new JLabel();
        databaseLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        databaseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        databaseLabel.setText("Database #");
        final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
        gridBagConstraints_8.ipadx = 10;
        gridBagConstraints_8.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints_8.gridx = 0;
        panel.add(databaseLabel, gridBagConstraints_8);

        DataBaseNum = new JTextField();
        DataBaseNum.setText("1");
        DataBaseNum.setColumns(30);
        final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
        gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_9.weightx = 1;
        gridBagConstraints_9.gridx = 1;
        panel.add(DataBaseNum, gridBagConstraints_9);

        final JLabel filenameLabel = new JLabel();
        filenameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        filenameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        filenameLabel.setText("Filename: ");
        final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
        gridBagConstraints_11.ipadx = 10;
        gridBagConstraints_11.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints_11.gridx = 0;
        panel.add(filenameLabel, gridBagConstraints_11);

        FileName = new JTextField();
        FileName.setColumns(30);
        if (cf.getFileType().equals(FileTypes.METAVANTE))
        	FileName.setText("METAVANTE.ASC");
        else 	
           FileName.setText("CHECKFREE.ASC");
        final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
        gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_10.weightx = 1;
        gridBagConstraints_10.gridx = 1;
        panel.add(FileName, gridBagConstraints_10);
    }
    

    public void DoConnect() throws Exception
    {
        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts(HostKeyFile.getText());
            
            Session session = jsch.getSession(UserName.getText(), HostName.getText(), 22);
            session.setPassword(new String(Password.getPassword()));

            // invalid host keys handled via UserInfo interface.
            UserInfo ui = new MyUserInfo();
            session.setUserInfo(ui);
            
            session.connect();
            
            Channel channel=session.openChannel("sftp");
            channel.connect();
            ChannelSftp c=(ChannelSftp)channel;
            
            c.cd("/u_add/DEALERS/" + DealerName.getText() + "/AR/" + DataBaseNum.getText() + "/TMP");
            c.put(SourceFile, FileName.getText());
            c.quit();
        }
        catch (JSchException e)
        {
            throw new Exception("Error transferring file to Energy server: "
                                + e.getMessage());
        }
    }


    public class MyUserInfo implements UserInfo
    {
        public String getPassword(){ return new String(Password.getPassword()); }
        public boolean promptYesNo(String str)
        {
            Object[] options={ "yes", "no" };
            int foo =
                JOptionPane.showOptionDialog(null, 
                                             str,
                                             "Warning", 
                                             JOptionPane.DEFAULT_OPTION, 
                                             JOptionPane.WARNING_MESSAGE,
                                             null, options, options[0]);
            return foo==0;
        }
        
        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return false; }
        public boolean promptPassword(String message) { return false; }
        public void showMessage(String message)
        {
            JOptionPane.showMessageDialog(null, message);
        }
    }


    /**
     * @return connection button pressed?
     */
    public boolean getDoConnect()
    {
        return DoConnect;
    }
}
