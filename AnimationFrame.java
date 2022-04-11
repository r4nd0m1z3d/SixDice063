 package orioni.sixdice;
 import java.awt.image.BufferedImage;
 public class AnimationFrame
 {
   protected BufferedImage m_image;
   protected int m_offset_x;
   protected int m_offset_y;
   protected byte[] m_optional_data;
   public AnimationFrame() {
     this(new BufferedImage(1, 1, 2), 0, 0);
   }
   public AnimationFrame(BufferedImage paramBufferedImage, int paramInt1, int paramInt2) {
     this.m_image = paramBufferedImage;
     this.m_offset_x = paramInt1;
     this.m_offset_y = paramInt2;
     this.m_optional_data = new byte[0];
   }
   public byte[] getOptionalData() {
     return this.m_optional_data;
   }
   public void setOptionalData(byte[] paramArrayOfbyte) {
     this.m_optional_data = paramArrayOfbyte;
   }
   public BufferedImage getImage() {
     return this.m_image;
   }
   public int getXOffset() {
     return this.m_offset_x;
   }
   public int getYOffset() {
     return this.m_offset_y;
   }
   public void setImage(BufferedImage paramBufferedImage) {
     this.m_image = paramBufferedImage;
   }
   public void setXOffset(int paramInt) {
     this.m_offset_x = paramInt;
   }
   public void setYOffset(int paramInt) {
     this.m_offset_y = paramInt;
   }
   public String toString() {
     return this.m_image.getWidth() + "x" + this.m_image.getHeight() + " @ (" + this.m_offset_x + ", " + this.m_offset_y + ")";
   }
 }