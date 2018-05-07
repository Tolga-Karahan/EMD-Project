import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Test
{
	public static void main(String[] args) throws IOException
	{
		try
		{
			BufferedImage input = ImageIO.read(new File("input.png"));
			BufferedImage coverImage = ImageIO.read(new File("cover-image.jpg"));

			Encrypt encryption = new Encrypt(coverImage, input);
			BufferedImage output = encryption.encrypt();

			ImageIO.write(output, "JPEG", new File("output.jpg"));

			Decrypt decryption = new Decrypt(coverImage, input);
			BufferedImage secret = decryption.decrypt();

			ImageIO.write(secret, "JPEG", new File("secret.jpg"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}	
