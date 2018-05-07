import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;

public class Decrypt	
{
	private WritableRaster coverImage; 	// Orten resim
	private WritableRaster secretImage;     // Gizli resim
	private ColorModel model;		// Gizli resmin renk modeli
	private int base;                       // Sifrelemede kullanilan taban
	private int groupSize;                  // Grup buyuklugu
	private int groupNumber;                // Grup sayisi
	private int factor;                     // Sifrelemede her pikselin saklandigi piksel sayisi 

	public Decrypt(BufferedImage coverImage, BufferedImage secretImage)
	{
		this.coverImage  = coverImage.getRaster();
		this.secretImage = secretImage.getRaster();
		this.model  = secretImage.getColorModel();
		this.base        = coverImage.getWidth() * coverImage.getHeight() < 
			9 * secretImage.getWidth() * secretImage.getHeight() ? 5 : 7; 
		this.groupNumber = Integer.toString(255, base).length();
		this.factor = base == 5 ? 8 : 9;
	}

	public BufferedImage decrypt()
	{
		// Birlestirilmis digitleri tutacak olan degisken
		String pixel = "";		

		for(int row = 0; row < secretImage.getHeight(); row++)
		{
			for(int column = 0; column < secretImage.getWidth(); column++)
			{
				// Her bir digiti cikart
				for(int i = 0; i < groupNumber; i++)
				{
					// Tek boyutta indeksle
					int index = (row * secretImage.getWidth() + column) * factor;
					String digit  = String.valueOf(extract(index + (groupSize * i)));
					pixel = pixel + digit;	
				}
				secretImage.setSample(column, row, 0, Integer.parseInt(pixel, 10));
			}
		}

		return new BufferedImage(model, secretImage, model.isAlphaPremultiplied(), null);
	}
	
	private int extract(int index)
	{
		int digit = 0;		
		int column = index % coverImage.getWidth();
                int row    = index / coverImage.getWidth();
		
		for(int i = 0; i < groupSize; i++)
		{

			digit += (i + 1) * coverImage.getSample(column, row, 0) % base;
		}

		return digit;
	}
}			
			
