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
				"//home//apeiron//Documents//myRepos//EMD-Project//images//input-images//secret//rgbinput.jpg"));
			BufferedImage coverImage = ImageIO.read(new File(
				"//home//apeiron//Documents//myRepos//EMD-Project//images//input-images//cover//cover-image.jpg"));

			Encrypt encryption = new Encrypt(coverImage, input);
			BufferedImage output = encryption.encrypt();

			ImageIO.write(output, "BMP", new File(
				"//home//apeiron//Documents//myRepos//EMD-Project//images//output-images//modified-cover//output.bmp"));
			BufferedImage a = ImageIO.read(
				new File("//home//apeiron//Documents//myRepos//EMD-Project//images//output-images//modified-cover//output.bmp"));
			System.out.println(a.getType());
			Decrypt decryption = new Decrypt(a);
			BufferedImage secret = decryption.decrypt();

			ImageIO.write(secret, "BMP", new File(
				"//home//apeiron//Documents//myRepos//EMD-Project//images//output-images//output-secret//secret.bmp"));		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}	
