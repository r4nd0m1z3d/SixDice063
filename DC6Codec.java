 package orioni.sixdice;
 
 import java.awt.image.BufferedImage;
 import java.awt.image.IndexColorModel;
 import java.awt.image.WritableRaster;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.List;
 import orioni.jz.awt.image.RestrictableIndexColorModel;
 import orioni.jz.common.exception.ParseException;
 import orioni.jz.io.FileType;
 import orioni.jz.io.PrimitiveInputStream;
 import orioni.jz.io.PrimitiveOutputStream;
 import orioni.jz.io.RandomAccessByteArrayInputStream;
 import orioni.jz.util.Pair;
 import orioni.jz.util.ProgressTracker;
 import orioni.jz.util.strings.StringUtilities;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class DC6Codec
   extends AnimationCodec
 {
   public boolean formatContainsPalette() {
     return false;
   }
 
 
 
 
 
 
   
   public String getName() {
     return "DC6 Codec";
   }
 
 
 
 
 
 
 
 
 
 
   
   public List<Pair<String, AnimationCodec.MessageType>> check(Animation paramAnimation) {
     ArrayList<Pair> arrayList = new ArrayList();
     for (byte b = 0; b < paramAnimation.getDirectionCount(); b++) {
       
       for (byte b1 = 0; b1 < paramAnimation.getFrameCount(); b1++) {
         
         AnimationFrame animationFrame = paramAnimation.getFrame(b, b1);
         if (animationFrame.getImage().getHeight() > 256 || animationFrame.getImage().getWidth() > 256)
         {
           arrayList.add(new Pair("Direction " + b + ", Frame " + b1 + ": " + "Diablo II does not accept DC6 files with frames containing images larger than 256x256.", AnimationCodec.MessageType.WARNING));
         }
 
 
 
         
         if ((animationFrame.getOptionalData()).length > 0)
         {
           arrayList.add(new Pair("Direction " + b + ", Frame " + b1 + ": " + "DC6 files cannot contain optional frame data.  This data will be overwritten.", AnimationCodec.MessageType.WARNING));
         }
       } 
     } 
 
 
 
     
     return (List)arrayList;
   }
 
 
 
 
 
 
   
   public FileType getFileType() {
     return new FileType("DC6 Files (*.dc6)", new String[] { "dc6" });
   }
 
 
 
 
 
 
 
 
 
 
 
   
   public Animation decode(byte[] paramArrayOfbyte, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) throws ParseException {
     paramRestrictableIndexColorModel = deriveCodecPalette(paramRestrictableIndexColorModel);
     
     ArrayList<String> arrayList = new ArrayList();
     ArrayList<AnimationFrame> arrayList1 = new ArrayList();
     
     RandomAccessByteArrayInputStream randomAccessByteArrayInputStream = new RandomAccessByteArrayInputStream(paramArrayOfbyte);
 
     
     try {
       byte b = (byte)paramRestrictableIndexColorModel.getMostTransparentIndex();
 
       
       PrimitiveInputStream primitiveInputStream = new PrimitiveInputStream((InputStream)randomAccessByteArrayInputStream, false);
       ParseException.performParseAssertion((primitiveInputStream.readInt() == 6), "The input file was not a DC6 file.");
       ParseException.performParseAssertion((primitiveInputStream.readInt() == 1), "The input file was not a DC6 file.");
       ParseException.performParseAssertion((primitiveInputStream.readInt() == 0), "The input file was not a DC6 file.");
       int i = primitiveInputStream.readInt();
       if (i != -286331154 && i != -842150451)
       {
         arrayList.add("DC6 header's \"magic number\" terminator was 0x" + StringUtilities.padLeft(Long.toString(i & 0xFFFFFFFFL, 16), '0', 8));
       }
 
       
       int j = primitiveInputStream.readInt();
       int k = primitiveInputStream.readInt();
 
       
       int[] arrayOfInt = new int[k * j]; byte b1;
       for (b1 = 0; b1 < arrayOfInt.length; b1++)
       {
         arrayOfInt[b1] = primitiveInputStream.readInt();
       }
       
       for (b1 = 0; b1 < k * j; b1++)
       {
         arrayList1.add(new AnimationFrame());
       }
       
       paramProgressTracker.setStartingValue(0.0D);
       paramProgressTracker.setEndingValue(arrayOfInt.length);
 
       
       for (b1 = 0; b1 < arrayOfInt.length; b1++) {
         
         randomAccessByteArrayInputStream.seek(arrayOfInt[b1]);
         int m = primitiveInputStream.readInt();
         int n = primitiveInputStream.readInt();
         int i1 = primitiveInputStream.readInt();
         int i2 = primitiveInputStream.readInt();
         int i3 = primitiveInputStream.readInt();
         primitiveInputStream.readInt();
         int i4 = primitiveInputStream.readInt();
         if (b1 < arrayOfInt.length - 1 && i4 != arrayOfInt[b1 + 1])
         {
           arrayList.add("Frame #" + b1 + ": \"next_block\" entry does not properly indicate the next block.  " + "Using offset table.");
         }
 
 
 
         
         int i5 = primitiveInputStream.readInt();
         
         byte[] arrayOfByte1 = new byte[i5];
         
         primitiveInputStream.read(arrayOfByte1);
 
         
         String str = null;
         byte[] arrayOfByte2 = new byte[n * i1];
         int i6;
         for (i6 = 0; i6 < arrayOfByte2.length; ) { arrayOfByte2[i6] = b; i6++; }
         
         i6 = 0;
         int i7 = (m == 0) ? (i1 - 1) : 0;
         int i8 = m * 2 - 1;
         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
         int i9 = byteArrayInputStream.read();
         while (i9 != -1) {
           
           if (i9 == 128) {
 
             
             i7 += i8;
             i6 = 0;
             
             if (i7 < 0)
             {
               str = "Frame #" + b1 + ": more newlines than rows.  Cursor reset to top line.";
             }
             else if (i7 >= i1)
             {
               str = "Frame #" + b1 + ": more newlines than rows.  Cursor reset to bottom line.";
             }
           
           } else {
             
             if (str != null) {
               
               arrayList.add(str);
               str = null;
             } 
             int i10 = i9 & 0x7F;
             byte[] arrayOfByte = new byte[i10];
             int i11;
             for (i11 = 0; i11 < arrayOfByte.length; ) { arrayOfByte[i11] = b; i11++; }
             
             if ((i9 & 0x80) == 0) {
 
               
               i11 = byteArrayInputStream.read(arrayOfByte);
               if (i11 < i10)
               {
                 arrayList.add("Frame #" + b1 + ": specified sequence length is greater than length of file.  Assuming " + "transparent for missing samples.");
               }
             } 
 
 
             
             if (i10 > n - i6) {
               
               arrayList.add("Frame #" + b1 + ": specified sequence would exceed width of image.  Truncating.");
 
               
               i10 = n - i6;
             } 
             System.arraycopy(arrayOfByte, 0, arrayOfByte2, i6 + i7 * n, i10);
             i6 += i10;
           } 
           
           i9 = byteArrayInputStream.read();
         } 
 
         
         BufferedImage bufferedImage = new BufferedImage(n, i1, 13, (IndexColorModel)paramRestrictableIndexColorModel);
         WritableRaster writableRaster = bufferedImage.getRaster();
         
         int[] arrayOfInt1 = new int[arrayOfByte2.length];
         byte b2 = 0;
         for (byte b3 : arrayOfByte2) arrayOfInt1[b2++] = b3; 
         writableRaster.setPixels(writableRaster.getMinX(), writableRaster.getMinY(), n, i1, arrayOfInt1);
 
         
         arrayList1.set(b1, new AnimationFrame(bufferedImage, i2, i3));
         
         paramProgressTracker.incrementProgress(1.0D);
       } 
       
       Animation animation = new Animation(arrayList1, j, k);
       animation.addWarnings(arrayList);
       return animation;
     } catch (EOFException eOFException) {
       
       throw new ParseException("Unexpected end of file.", eOFException);
     } catch (IOException iOException) {
       
       throw new ParseException(iOException.getMessage(), iOException);
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
   
   public byte[] encode(Animation paramAnimation, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) {
     paramRestrictableIndexColorModel = deriveCodecPalette(paramRestrictableIndexColorModel);
     
     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
 
 
     
     try {
       int i = 24 + 4 * paramAnimation.getDirectionCount() * paramAnimation.getFrameCount();
       
       int[] arrayOfInt = new int[paramAnimation.getDirectionCount() * paramAnimation.getFrameCount()];
       byte[][] arrayOfByte = new byte[paramAnimation.getDirectionCount() * paramAnimation.getFrameCount()][];
       paramProgressTracker.setStartingValue(0.0D);
       paramProgressTracker.setEndingValue(arrayOfInt.length);
       
       for (byte b = 0; b < paramAnimation.getDirectionCount(); b++) {
         
         for (byte b1 = 0; b1 < paramAnimation.getFrameCount(); b1++) {
           
           arrayOfInt[b * paramAnimation.getFrameCount() + b1] = i;
 
 
           
           BufferedImage bufferedImage = paramAnimation.getFrame(b, b1).getImage();
           
           byte[] arrayOfByte1 = encodeFrame(paramAnimation, b, b1, paramRestrictableIndexColorModel);
 
           
           ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
           PrimitiveOutputStream primitiveOutputStream1 = new PrimitiveOutputStream(byteArrayOutputStream1, false);
           primitiveOutputStream1.writeInt(0);
           primitiveOutputStream1.writeInt(bufferedImage.getWidth(null));
           primitiveOutputStream1.writeInt(bufferedImage.getHeight(null));
           primitiveOutputStream1.writeInt(paramAnimation.getFrame(b, b1).getXOffset());
           primitiveOutputStream1.writeInt(paramAnimation.getFrame(b, b1).getYOffset());
           primitiveOutputStream1.writeInt(0);
           i += 32 + arrayOfByte1.length + 3;
           primitiveOutputStream1.writeInt(i);
           primitiveOutputStream1.writeInt(arrayOfByte1.length);
           
           primitiveOutputStream1.write(arrayOfByte1);
           
           primitiveOutputStream1.close();
           byteArrayOutputStream1.close();
           arrayOfByte[b1 + b * paramAnimation.getFrameCount()] = byteArrayOutputStream1.toByteArray();
           paramProgressTracker.incrementProgress(1.0D);
         } 
       } 
 
       
       PrimitiveOutputStream primitiveOutputStream = new PrimitiveOutputStream(byteArrayOutputStream, false);
       
       primitiveOutputStream.writeInt(6);
       primitiveOutputStream.writeInt(1);
       primitiveOutputStream.writeInt(0);
       
       primitiveOutputStream.writeInt(-842150451);
       primitiveOutputStream.writeInt(paramAnimation.getDirectionCount());
       primitiveOutputStream.writeInt(paramAnimation.getFrameCount());
 
       
       for (int j : arrayOfInt) primitiveOutputStream.writeInt(j);
 
       
       for (byte[] arrayOfByte1 : arrayOfByte) {
         
         primitiveOutputStream.write(arrayOfByte1);
         
         primitiveOutputStream.write(205);
         primitiveOutputStream.write(205);
         primitiveOutputStream.write(205);
       } 
 
       
       byteArrayOutputStream.close();
     } catch (IOException iOException) {
 
       
       throw new IllegalStateException("ByteArrayOutputStream threw an IOException!", iOException);
     } 
     return byteArrayOutputStream.toByteArray();
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public static byte[] encodeFrame(Animation paramAnimation, int paramInt1, int paramInt2, RestrictableIndexColorModel paramRestrictableIndexColorModel) {
     try {
       BufferedImage bufferedImage = paramAnimation.getFrame(paramInt1, paramInt2).getImage();
       
       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
       int i = bufferedImage.getHeight() - 1;
       byte b = 0;
       
       int j = bufferedImage.getWidth();
       int k = bufferedImage.getHeight();
       int[] arrayOfInt1 = bufferedImage.getRGB(0, 0, j, k, null, 0, j);
       int[] arrayOfInt2 = new int[j * k]; int m;
       for (m = 0; m < arrayOfInt1.length; m++)
       {
         arrayOfInt2[m] = paramRestrictableIndexColorModel.find(arrayOfInt1[m]);
       }
       
       m = paramRestrictableIndexColorModel.getMostTransparentIndex();
       
       int n = bufferedImage.getWidth();
       while (i >= 0) {
         
         byte b1 = 0;
         int i1 = n * i;
         while (b < n) {
 
           
           if (b1) {
             
             while (b1 > 127) {
               
               byteArrayOutputStream.write(255);
               b1 -= 127;
             } 
             if (b1 > 0) {
               
               byteArrayOutputStream.write(0x80 | b1);
               b1 = 0;
             } 
           } 
           if (arrayOfInt2[i1 + b] == m) {
 
 
             
             while (b < n && arrayOfInt2[i1 + b] == m) {
 
               
               b++;
               b1++;
             } 
 
             
             continue;
           } 
 
           
           byte[] arrayOfByte = new byte[127];
           byte b2 = 0;
           
           while (b < n && arrayOfInt2[i1 + b] != m && b2 < 127) {
 
             
             arrayOfByte[b2] = (byte)arrayOfInt2[i1 + b];
             b++;
             b2++;
           } 
           
           assert b2 != '';
           byteArrayOutputStream.write(b2);
           byteArrayOutputStream.write(arrayOfByte, 0, b2);
         } 
         
         byteArrayOutputStream.write(128);
         i--;
         b = 0;
       } 
       byteArrayOutputStream.close();
       return byteArrayOutputStream.toByteArray();
     } catch (IOException iOException) {
 
       
       throw new IllegalStateException("ByteArrayOutputStream threw an IOException!", iOException);
     } 
   }
 }
