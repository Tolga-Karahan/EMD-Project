import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ImagePanel extends JPanel{
    
    private static final long serialVersionUID = 1L;
    private JLabel imageLabel;
    private ImageIcon transformedImageIcon;
    
    public ImagePanel(){
        
        this.imageLabel = new JLabel();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(imageLabel, BorderLayout.CENTER);
    }
    
    public void updateImage(final Image image){     
	 SwingUtilities.invokeLater(new Runnable(){
         public void run(){		               
	 	imageLabel.setIcon(new ImageIcon(scaleImage(image)));
	}
	});
    }
    
    private Image scaleImage(Image image){
        return image.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
    }
    
    public void loadImage(File file) throws IOException{
        Image image = ImageIO.read(file);
        
        updateImage(image);
    }
}
