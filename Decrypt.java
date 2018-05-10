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
		this.model  	 = secretImage.getColorModel();
		this.base        = coverImage.getWidth() * coverImage.getHeight() < 
			9 * secretImage.getWidth() * secretImage.getHeight() ? 5 : 7; 
                this.groupSize   = base == 5 ? 2 : 3;
		this.groupNumber = Integer.toString(255, base).length();
		this.factor 	 = base == 5 ? 8 : 9;
	}

	public BufferedImage decrypt()
	{	

		for(int row = 0; row < secretImage.getHeight(); row++)
		{
			for(int column = 0; column < secretImage.getWidth(); column++)
			{
				// Tek boyutta indeksle
				int index = (row * secretImage.getWidth() + column) * factor;
				
				// Her bir piksel degerini cikart
				int red   = extractPixel(index, 0);
				int green = extractPixel(index, 1);
				int blue  = extractPixel(index, 2);

				// Piksel degerlerini yerine koy
				// Gri resimler icin heyecan yapmaya gerek yok
				// gri resimlerde tum bu degerler aynidir
				secretImage.setSample(column, row, 0, red);
				secretImage.setSample(column, row, 1, green);
				secretImage.setSample(column, row, 2, blue);
			}
		}

		return new BufferedImage(model, secretImage, model.isAlphaPremultiplied(), null);
	}
	
	private int extractionFunction(int index, int band)
	{
		int weightedSum = 0;
		
		for(int i = 0; i < groupSize; i++)
		{

			weightedSum += (i + 1) * coverImage.getSample((index + i) % coverImage.getWidth(),
								      (index + i) / coverImage.getWidth(),
								      band);
		}

		return weightedSum % base;
	}

	private int extractPixel(int index, int band)
	{
		int pixel = 0;		

		// Belirtilen banddaki pikseli cikart
                for(int i = 0; i < groupNumber; i++)
                {
              		// Piksele ait her bir digiti cikart ve onluk tabana cevirerek piksel degerine ekle
                	pixel += Math.pow(base, (groupNumber - i - 1)) * extractionFunction(index + (groupSize * i), band);
                }
        	
		// Onluk tabanda piksel degerini dondur
		return pixel;
	}       
}			
			
