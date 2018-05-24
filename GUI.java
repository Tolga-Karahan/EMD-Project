import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class GUI extends JFrame {

    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JMenu jMenu1;
    private JMenu jMenu2;
    private JMenu helpMenu;	
    private JMenu aboutMenu;	
    private JMenuBar jMenuBar1;
    private JMenuItem jMenuItem1;
    private JMenuItem jMenuItem2;
    private ImagePanel jPanel1;
    private ImagePanel jPanel2;
    private JFileChooser fileChooser;
    private String helpString;	   	

    public GUI() {
	setTitle("EMD Application");
	setResizable(false);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1     = new ImagePanel();
        jPanel2     = new ImagePanel();
        jLabel1     = new JLabel();
        jLabel2     = new JLabel();
        jLabel3     = new JLabel();
        jMenuBar1   = new JMenuBar();
        jMenu1      = new JMenu();
        jMenuItem1  = new JMenuItem();
        jMenu2      = new JMenu();
        jMenuItem2  = new JMenuItem();
	helpMenu    = new JMenu();
	aboutMenu   = new JMenu();
	fileChooser = new JFileChooser();
	helpString  = String.format("1.%s%n2.%s%n3.%s%n4.%s",
		"You can use Encryption menu to hide images",
		"You can use Decryption menu to extract images",
		"You can use both  gray and  color images",
		"Sample images are provided in images directory"); 	

	jLabel1.setVisible(false);
	jLabel2.setVisible(false);
	jPanel1.setVisible(false);
	jPanel2.setVisible(false);
  
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1000, 600));
	setMinimumSize(new java.awt.Dimension(1000, 600));
        getContentPane().setLayout(null);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(50, 100, 400, 400);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(550, 100, 400, 400);

        jLabel1.setIcon(new ImageIcon(this.getClass().getResource(".//icons//input-icon.jpg"))); 
        getContentPane().add(jLabel1);
        jLabel1.setBounds(164, 64, 130, 30);

        jLabel2.setIcon(new ImageIcon(this.getClass().getResource(".//icons//output-icon.jpg"))); 
        getContentPane().add(jLabel2);
        jLabel2.setBounds(674, 64, 140, 30);

        jLabel3.setIcon(new ImageIcon(this.getClass().getResource(".//icons//background.jpg"))); 
        getContentPane().add(jLabel3);
        jLabel3.setBounds(0, 10, 1310, 570);

        jMenu1.setText("Encryption");

        jMenuItem1.setText("Load Image");
	jMenuItem1.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e){			
                        	
				JOptionPane.showMessageDialog(GUI.this, "Please select the cover image");
			        int result1 = fileChooser.showOpenDialog(GUI.this);
				if(result1 == JFileChooser.APPROVE_OPTION){
				File file1 = fileChooser.getSelectedFile();
				JOptionPane.showMessageDialog(GUI.this, "Please select the secret image");
				int result2 = fileChooser.showOpenDialog(GUI.this);
				if(result2 == JFileChooser.APPROVE_OPTION){
					File file2 = fileChooser.getSelectedFile();
					try{
						Encrypt encryption = new Encrypt(ImageIO.read(file1), ImageIO.read(file2));
						BufferedImage modifiedCover = encryption.encrypt();
					        JOptionPane.showMessageDialog(GUI.this, 
							"Please save the file. You can provide any extension but we'll save this as bmp anyway!");
						int saveResult = GUI.this.fileChooser.showSaveDialog(GUI.this);
						if(saveResult == JFileChooser.APPROVE_OPTION)
							ImageIO.write(modifiedCover, "BMP", fileChooser.getSelectedFile());
						GUI.this.jPanel1.loadImage(file1);
						GUI.this.jPanel2.loadImage(fileChooser.getSelectedFile());
						String psnr = String.format("Encryption is successful. PSNR is: %.4f", encryption.getPSNR());	
						JOptionPane.showMessageDialog(GUI.this,psnr);
						jLabel1.setVisible(true);
						jLabel2.setVisible(true);
						jPanel1.setVisible(true);
						jPanel2.setVisible(true);
							
				}catch(Exception ex){
					String errorMessage = ex.getMessage();
					JOptionPane.showMessageDialog(GUI.this, errorMessage, null, JOptionPane.WARNING_MESSAGE);
				} 
				
				}else{
					JOptionPane.showMessageDialog(GUI.this, "Selection failed, try again", "Warning", JOptionPane.WARNING_MESSAGE);
				}
			}else{
				JOptionPane.showMessageDialog(GUI.this, "Selection failed, try again", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}	
	});
			
        jMenu1.add(jMenuItem1);
	

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Decryption");

        jMenuItem2.setText("Load Image");
	jMenuItem2.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e){
			JOptionPane.showMessageDialog(GUI.this, "Please select the cipher image");
			int result = fileChooser.showOpenDialog(GUI.this);	
			if(result == JFileChooser.APPROVE_OPTION){
				try{
					File file1 = fileChooser.getSelectedFile();
					Decrypt decryption = new Decrypt(ImageIO.read(file1));
					BufferedImage secretImage = decryption.decrypt();
					JOptionPane.showMessageDialog(GUI.this, 
						"Please save the file!");
					int saveResult = fileChooser.showSaveDialog(GUI.this);
					if(saveResult == JFileChooser.APPROVE_OPTION){
						ImageIO.write(decryption.decrypt(),"BMP", fileChooser.getSelectedFile());
					}
					jPanel1.loadImage(file1);
					jPanel2.loadImage(fileChooser.getSelectedFile());
					jLabel1.setVisible(true);
					jLabel2.setVisible(true);
					jPanel1.setVisible(true);
					jPanel2.setVisible(true);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}else{
				JOptionPane.showMessageDialog(GUI.this, "Selection failed, try again", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	});		
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);
	
	helpMenu.setText("Help");
	helpMenu.addMenuListener(new MenuListener(){
		@Override
		public void menuSelected(MenuEvent e){
			JOptionPane.showMessageDialog(GUI.this, helpString, "Help", JOptionPane.INFORMATION_MESSAGE);
		}
		
		@Override
		public void menuDeselected(MenuEvent e){}
		
		@Override
		public void menuCanceled(MenuEvent e){}
		
	});
	jMenuBar1.add(helpMenu);

	aboutMenu.setText("About");
	aboutMenu.addMenuListener(new MenuListener(){
                @Override
                public void menuSelected(MenuEvent e){
                        JOptionPane.showMessageDialog(GUI.this, 
				"It's a steganography project developed by Tolga Karahan and Mahsum Yatki",
				"About",	
			        JOptionPane.INFORMATION_MESSAGE);
                }

                @Override
                public void menuDeselected(MenuEvent e){}

                @Override
                public void menuCanceled(MenuEvent e){}

        });
	jMenuBar1.add(aboutMenu);	
	
        setJMenuBar(jMenuBar1);
        pack();
    }

}

