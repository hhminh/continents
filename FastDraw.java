import java.awt.*;
import java.awt.image.*;

public class FastDraw
{
   public static void main(String [] args)
   {
      try
      {
         Frame frame = new Frame();
         frame.setSize(700,700);
         frame.setVisible(true);

         int [] pixels = new int[600 * 600];
         DirectColorModel colorModel = new DirectColorModel(24, 0x00FF0000, 0x0000FF00, 0x000000FF);
         MemoryImageSource source = new MemoryImageSource(600, 600, colorModel, pixels, 0, 600);
         source.setAnimated(true);
         Image image = frame.createImage(source);

         frame.createBufferStrategy(2);
         BufferStrategy strategy = frame.getBufferStrategy();

         for (int j=0;;)
         {
            long t = System.currentTimeMillis();
            for (int c=0;c<100;c++,j++)
            {
               for (int n=pixels.length; --n>=0;) pixels[n] = n+j;

               source.newPixels();

               Graphics g = strategy.getDrawGraphics();
               g.drawImage(image, 50, 50, null);
               strategy.show();
            }
            System.out.println((System.currentTimeMillis()-t)/100f);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
