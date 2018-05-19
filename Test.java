import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Test
{
	public static void main(String[] args) throws IOException
	{
		try
		{
			BufferedImage input = ImageIO.read(new File(
				"//home//apeiron//Documents//myRepos//EMD-Project//images//input-images//secret//rgbinput2.jpg"));
			BufferedImage coverImage = ImageIO.read(new File(
				"//home//apeiron//Documents//myRepos//EMD-Project//images//input-images//cover//cover-image.jpg"));

			Encrypt encryption = new Encrypt(coverImage, input);
			BufferedImage output = encryption.encrypt();

			ImageIO.write(output, "JPEG", new File(
				"//home//apeiron//Documents//myRepos//EMD-Project//images//output-images//modified-cover//output.jpg"));

			Decrypt decryption = new Decrypt(coverImage);
			BufferedImage secret = decryption.decrypt();

			ImageIO.write(secret, "JPEG", new File(
				"//home//apeiron//Documents//myRepos//EMD-Project//images//output-images//output-secret//secret.jpg"));		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}	
