import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
public class Encrypt
{ 
	ColorModel model;
	WritableRaster coverImage;      // Orten resim
	WritableRaster secretImage;     // Gizli resim
	int base;        	       	// (2n+1) sifreleme tabani
	int groupSize;       	       	// Sifrelemede kullanilacak n piksel sayisi 
	int factor;          	       	// Her bir pikselin sifrelenmesinde kullanilacak piksel sayisi
	double PSNR;                   	// Peak signal to noise ratio
	int debug = 0;
	public Encrypt(BufferedImage coverImage, BufferedImage secretImage) throws Exception
	{
		this.model = coverImage.getColorModel();
		this.coverImage = coverImage.getRaster();
		this.secretImage = secretImage.getRaster();

		if(!isSizeSufficient())
			throw new Exception("Size isn't sufficient");
		
		factor = base == 5 ? 8 : 9;		
		PSNR = 0;
	}

	public boolean isSizeSufficient()
	{
		// Orten resim boyutunun yeterli boyutta olup olmadığını kontrol et
		if(coverImage.getHeight() * coverImage.getWidth() < secretImage.getHeight() * secretImage.getWidth() * 8)
			return false;
 		
		// Mod işlemleri için tabanı (2n+1) olacak sekilde belirle
		// Her bir digitin gomulecegi grup buyuklugunu n olacak sekilde belirle
		if(coverImage.getHeight() * coverImage.getWidth() >= secretImage.getHeight() * secretImage.getWidth() * 9)
		{
			base = 7;
			groupSize = 3;
			return true;
		}
		else
		{
			base = 5;
			groupSize = 2;
			return true;
		}
				
	}

	public BufferedImage encrypt()
	{
		for(int row = 0; row < secretImage.getHeight(); row++)
		{
			for(int column = 0; column < secretImage.getWidth(); column++)
			{
				// Pikseli al ve sifreleme tabanina cevir
				int pixel = secretImage.getSample(column, row, 0);
				String cipherPixel = Integer.toString(pixel, base);
;	
				// Gerekiyorsa padding yap
				if(cipherPixel.length() < Integer.toString(255, base).length())
				{
					int diff = Integer.toString(255, base).length() - cipherPixel.length();

					for(int i = 0; i < diff; i++)
						cipherPixel = '0' + cipherPixel;
				}		
				
				// Her bir digiti taşıyıcı resme göm
				for(int i = 0; i < cipherPixel.length(); i++)
				{
					int embeddingDigit = Character.getNumericValue(cipherPixel.charAt(i));
					// Satir ve sutun sonu kontrollerinden kurtulmak icin tek boyutta indeksle
					int index = (row * secretImage.getWidth() + column) * factor;
					embed(embeddingDigit, index + i * groupSize);
					
				}
			
			}
		}
		return new BufferedImage(model, coverImage, model.isAlphaPremultiplied(), null); 
	}

	private void embed(int embeddingDigit, int index)
	{
		int weightedSum = 0;
		int difference;
		
		// Fonksiyonun sonucunu hesapla
		for(int i = 0; i < groupSize; i++)
			weightedSum += coverImage.getSample((index + i) % coverImage.getWidth(), (index + i) / coverImage.getWidth(), 0) * (i + 1) % base;
		
		// Farki bul, negatifse pozitife cevir
		difference = (embeddingDigit - weightedSum + base) % base;

		// Fark 0 ise islem yapmaya gerek yok
		if(difference == 0)
			return;
                
                // Fark n den buyukse (2n + 1) - farkı al		
		index = difference > groupSize ? index + (base - difference) - 1 : index + difference - 1;    
                int column = index % coverImage.getWidth();
                int row = index / coverImage.getWidth();

		// Fark n den kucukse ilgili pikseli arttir, n den buyukse ilgili pikseli azalt
		// Piksellerde en fazla fark 1 olacak sekilde degisiklik yaptigimizdan
		// PSNR basitce arttirma islemi ile hesaplanabilir ve sonuc en son normalize edilir
		if(difference <= groupSize)
		{
			coverImage.setSample(column, row, 0, coverImage.getSample(column, row, 0) + 1);
			PSNR++;
		}
		else
		{
      			coverImage.setSample(column, row, 0, coverImage.getSample(column, row, 0) - 1);
			PSNR++;
		} 
	}
	
	public double getPSNR()
	{
		return PSNR / (coverImage.getWidth() * coverImage.getHeight());
	}
}			  
