import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.*;
import java.awt.Point;

public class Encrypt
{ 
	private BufferedImage unmodifiedImage;  // PSNR hesaplamak icin resmin ilk halini kaydet
	private BufferedImage modifiedImage;    // Orten resmin degismis hali
	private WritableRaster coverImage;      // Orten resim
	private WritableRaster secretImage;     // Gizli resim
	private int base;        	       	// (2n+1) sifreleme tabani
	private int groupSize;       	       	// Sifrelemede kullanilacak n piksel sayisi 
	private int factor;          	       	// Her bir pikselin sifrelenmesinde kullanilacak piksel sayisi
	
	public Encrypt(BufferedImage coverImage, BufferedImage secretImage) throws Exception
	{	
		this.unmodifiedImage = coverImage;
		this.modifiedImage   = new BufferedImage(coverImage.getWidth(), coverImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		this.modifiedImage.setData(coverImage.getRaster());  
		this.coverImage      = modifiedImage.getRaster();
		this.secretImage     = secretImage.getRaster();

		if(!isSizeSufficient())
			throw new Exception("Size isn't sufficient");
		
		factor    = base == 5 ? 8 : 9;	

		// Resim boyutlarini ilk piksellere gom
		embedSize();
			
	}

	private void embedSize()
	{
		String row = String.valueOf(secretImage.getHeight());
		String column = String.valueOf(secretImage.getWidth());

		if(row.length() < 4) 
			row = "0" + row;
		if(column.length() < 4)
			column = "0" + column;
		
		for(int i = 0; i < 4; i++)
		{
			coverImage.setSample(i, 0, 0, Character.getNumericValue(row.charAt(i)));
			coverImage.setSample(i + 4, 0, 0, Character.getNumericValue(column.charAt(i)));
		}		 	
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
		for(int row = 1; row < secretImage.getHeight(); row++)
		{
			for(int column = 1; column < secretImage.getWidth(); column++)
			{	
				// Her bir piksel karsilik dusen band(kanal) a gomulur
				embedPixel(column, row, 0);
				embedPixel(column, row, 1);
				embedPixel(column, row, 2);
	 		}
		}

		return modifiedImage; 
	}

	private void embedPixel(int column, int row, int band)
	{

		// Satir ve sutun sonu kontrollerinden kurtulmak icin tek boyutta indeksle
		int index = ((row * secretImage.getWidth() + column) + 1) * factor;

		// Pikseli al ve sifreleme tabanina cevir
                int pixel = secretImage.getSample(column, row, band);
                String cipherPixel = Integer.toString(pixel, base);

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

			// Sifreleme tabanindaki her bir digit orten resme gomulur 
                        embedDigit(embeddingDigit, index + (i * groupSize), band);
                }
	}
	
	private void embedDigit(int embeddingDigit, int index, int band)
	{
		int weightedSum = 0;
		int difference;
		WritableRaster unmodifiedImageRaster= unmodifiedImage.getRaster();
		
		// Fonksiyonun sonucunu hesapla
		for(int i = 0; i < groupSize; i++)
			weightedSum += ((coverImage.getSample((index + i) % coverImage.getWidth(), (index + i) / coverImage.getWidth(), band)) * (i + 1));
		// Sonucun tabana gore modunu al
		weightedSum %= base;
	
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
		if(difference <= groupSize)
		{
			coverImage.setSample(column, row, band, (unmodifiedImageRaster.getSample(column, row, band) + 1));
		}
		else
		{
      			coverImage.setSample(column, row, band, (unmodifiedImageRaster.getSample(column, row, band) - 1));
		} 
	}
	
	public double getPSNR()
	{
		WritableRaster unmodifiedImageRaster = unmodifiedImage.getRaster();
		double PSNR = 0;

		for(int row = 0; row < unmodifiedImageRaster.getHeight(); row++)
		{
			for(int column = 0; column < unmodifiedImageRaster.getHeight(); column++)
			{
				// Orten resmin ilk hali ile degistirilmis halindeki piksellerin farkini al
				// karesini PSNR ye ekle
				int difference = unmodifiedImageRaster.getSample(column, row, 0) -
					coverImage.getSample(column, row, 0);
				PSNR += Math.pow(difference, 2);
			}
		}
		
		// PSNR degerini normalize et ve dondur
		return PSNR / (unmodifiedImageRaster.getHeight() * unmodifiedImageRaster.getWidth());
	}	
}			  
