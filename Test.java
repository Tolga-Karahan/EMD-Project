import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Test
{
	public static void main(String[] args) throws IOException
	{
		try
		{
			BufferedImage input = ImageIO.read(new File("input.jpg"));
			BufferedImage coverImage = ImageIO.read(new File("cover-image.jpg"));

			Encrypt encryption = new Encrypt(coverImage, input);
			BufferedImage output = encryption.encrypt();

			ImageIO.write(output, "JPEG", new File("output.jpg"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}	
