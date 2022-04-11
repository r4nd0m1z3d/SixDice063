 package orioni.sixdice;
 import java.awt.Color;
 import java.awt.image.BufferedImage;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Set;
 import java.util.Stack;
 import orioni.jz.awt.image.RestrictableIndexColorModel;
 import orioni.jz.common.exception.ParseException;
 import orioni.jz.io.FileType;
 import orioni.jz.io.PatternGeneratedInputStream;
 import orioni.jz.io.PrimitiveInputStream;
 import orioni.jz.io.PrimitiveOutputStream;
 import orioni.jz.io.RandomAccessByteArrayInputStream;
 import orioni.jz.io.bit.BitBufferOutputStream;
 import orioni.jz.io.bit.BitInputStream;
 import orioni.jz.io.bit.BitLimitedInputStream;
 import orioni.jz.io.bit.BitOrder;
 import orioni.jz.io.bit.BitOutputStream;
 import orioni.jz.io.bit.EndianFormat;
 import orioni.jz.math.MathUtilities;
 import orioni.jz.util.BitMap;
 import orioni.jz.util.DefaultValueHashMap;
 import orioni.jz.util.Pair;
 import orioni.jz.util.ProgressTracker;
 
 public class DCCCodec extends AnimationCodec {
   private static final int[] DCC_SIZE_COMPRESSION_FUNCTION = new int[] { 0, 1, 2, 4, 6, 8, 10, 12, 14, 16, 20, 24, 26, 28, 30, 32 };
 
 
 
 
   
   private static final int[] DCC_SIZE_COMPRESSION_FUNCTION_INVERTED = new int[] { 0, 1, 2, -1, 3, -1, 4, -1, 5, -1, 6, -1, 7, -1, 8, -1, 9, -1, -1, -1, 10, -1, -1, -1, 11, -1, 12, -1, 13, -1, 14, -1, 15 };
 
 
 
 
 
   
   public static final DCCCodec SINGLETON = new DCCCodec();
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public boolean formatContainsPalette() {
     return false;
   }
 
 
 
 
 
 
   
   public String getName() {
     return "DCC Codec";
   }
 
 
 
 
 
 
 
 
 
 
   
   public List<Pair<String, AnimationCodec.MessageType>> check(Animation paramAnimation) {
     ArrayList<Pair> arrayList = new ArrayList();
     if (paramAnimation.getDirectionCount() > 32)
     {
       arrayList.add(new Pair("Diablo II is reportedly incapable of reading DCCs with more than 32 directions.", AnimationCodec.MessageType.WARNING));
     }
 
 
     
     if (paramAnimation.getFrameCount() > 256)
     {
       arrayList.add(new Pair("Diablo II is reportedly incapable of reading DCCs with more than 256 frames per direction.", AnimationCodec.MessageType.WARNING));
     }
 
 
 
     
     if (paramAnimation.getDirectionCount() > 255)
     {
       arrayList.add(new Pair("DCC files cannot store more than 255 directions.", AnimationCodec.MessageType.FATAL));
     }
 
 
     
     return (List)arrayList;
   }
 
 
 
 
 
 
   
   public FileType getFileType() {
     return new FileType("DCC Files (*.dcc)", new String[] { "dcc" });
   }
 
 
 
 
 
 
 
 
 
 
 
 
   
   public Animation decode(byte[] paramArrayOfbyte, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) throws ParseException {
     paramRestrictableIndexColorModel = deriveCodecPalette(paramRestrictableIndexColorModel);
     
     RandomAccessByteArrayInputStream randomAccessByteArrayInputStream = new RandomAccessByteArrayInputStream(paramArrayOfbyte);
 
     
     try {
       PrimitiveInputStream primitiveInputStream = new PrimitiveInputStream((InputStream)randomAccessByteArrayInputStream, false);
       ArrayList<String> arrayList = new ArrayList();
       ParseException.performParseAssertion((primitiveInputStream.read() == 116), "This is not a DCC file.");
       int i = primitiveInputStream.read();
       if (i != 6)
       {
         arrayList.add("DCC file has a version which is not 6.  The reader may not properly read this file.");
       }
       int j = primitiveInputStream.read();
       if (j > 32)
       {
         arrayList.add("DCC file has more than 32 directions.  Diablo II should fail an assertion with this file.");
       }
       
       int k = primitiveInputStream.readInt();
       if (k > 256)
       {
         arrayList.add("DCC file has more than 256 frames per direction.  Diablo II should fail an assertion with this file.");
       }
 
       
       ParseException.performParseAssertion((k >= 0), "This DCC file has more than two billion frames per direction.  Probably not a DCC file.");
 
       
       if (primitiveInputStream.readInt() != 1) arrayList.add("DCC file's magic number is not 0x01.");
 
       
       primitiveInputStream.readInt();
 
       
       int[] arrayOfInt = new int[j];
       for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
       {
         arrayOfInt[b1] = primitiveInputStream.readInt();
       }
       
       paramProgressTracker.setStartingValue(0.0D);
       paramProgressTracker.setEndingValue(j);
 
       
       ArrayList<AnimationFrame> arrayList1 = new ArrayList();
       for (byte b2 = 0; b2 < arrayOfInt.length; b2++) {
         
         randomAccessByteArrayInputStream.seek(arrayOfInt[b2]);
         arrayList1.addAll(readDCCDirection((InputStream)randomAccessByteArrayInputStream, b2, k, paramRestrictableIndexColorModel, arrayList));
         paramProgressTracker.incrementProgress(1.0D);
       } 
       
       paramProgressTracker.setProgressCompleted();
       return new Animation(arrayList1, j, k);
     } catch (IOException iOException) {
 
       
       throw new IllegalStateException("ByteArrayInputStream threw an IOException!", iOException);
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   private static List<AnimationFrame> readDCCDirection(InputStream paramInputStream, int paramInt1, int paramInt2, RestrictableIndexColorModel paramRestrictableIndexColorModel, List<String> paramList) throws IOException, ParseException {
     try {
       BitLimitedInputStream bitLimitedInputStream1, bitLimitedInputStream2;
       BitInputStream bitInputStream2, bitInputStream3;
       byte b = (byte)paramRestrictableIndexColorModel.getMostTransparentIndex();
 
 
       
       AnimationFrame[] arrayOfAnimationFrame = new AnimationFrame[paramInt2];
       
       BitInputStream bitInputStream1 = new BitInputStream(paramInputStream, BitOrder.LOWEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
 
       
       PrimitiveInputStream primitiveInputStream = new PrimitiveInputStream((InputStream)bitInputStream1, false);
 
       
       primitiveInputStream.readInt();
       boolean bool1 = bitInputStream1.readBoolean();
       boolean bool2 = bitInputStream1.readBoolean();
       int i = DCC_SIZE_COMPRESSION_FUNCTION[bitInputStream1.readBits(4)];
       int j = DCC_SIZE_COMPRESSION_FUNCTION[bitInputStream1.readBits(4)];
       int k = DCC_SIZE_COMPRESSION_FUNCTION[bitInputStream1.readBits(4)];
       int m = DCC_SIZE_COMPRESSION_FUNCTION[bitInputStream1.readBits(4)];
       int n = DCC_SIZE_COMPRESSION_FUNCTION[bitInputStream1.readBits(4)];
       int i1 = DCC_SIZE_COMPRESSION_FUNCTION[bitInputStream1.readBits(4)];
       int i2 = DCC_SIZE_COMPRESSION_FUNCTION[bitInputStream1.readBits(4)];
       
       DCCFrameHeader[] arrayOfDCCFrameHeader = new DCCFrameHeader[paramInt2];
       boolean bool = false; int i3;
       for (i3 = 0; i3 < paramInt2; i3++) {
         
         if (bitInputStream1.readBits(i) != 0)
         {
           paramList.add("Direction " + paramInt1 + " Frame " + i3 + ": Var0 did not contain a zero.");
         }
         arrayOfDCCFrameHeader[i3] = new DCCFrameHeader(bitInputStream1.readBits(j), bitInputStream1.readBits(k), bitInputStream1.readBitsSigned(m), bitInputStream1.readBitsSigned(n), bitInputStream1.readBits(i1), bitInputStream1.readBits(i2), bitInputStream1.readBoolean());
 
 
 
 
 
 
 
         
         if (arrayOfDCCFrameHeader[i3].getOptionalDataSize() > 0) bool = true;
 
         
         if (!arrayOfDCCFrameHeader[i3].isBottomUp())
         {
           
           arrayOfDCCFrameHeader[i3].setYOffset(arrayOfDCCFrameHeader[i3].getYOffset() - arrayOfDCCFrameHeader[i3].getHeight() + 1);
         }
       } 
 
 
       
       if (bool) {
         
         bitInputStream1.findByteBoundary();
         for (DCCFrameHeader dCCFrameHeader : arrayOfDCCFrameHeader) {
           
           if (dCCFrameHeader.getOptionalDataSize() > 0) {
             
             byte[] arrayOfByte2 = new byte[dCCFrameHeader.getOptionalDataSize()];
             int i13 = arrayOfByte2.length;
             int i14 = 0;
             while (i13 > 0) {
               
               int i15 = bitInputStream1.read(arrayOfByte2, i14, i13);
               if (i15 == -1)
               {
                 throw new EOFException("Unexpected end of stream while reading optional frame data.");
               }
               
               i14 += i15;
               i13 -= i15;
             } 
             
             dCCFrameHeader.setOptionalData(arrayOfByte2);
           } 
         } 
       } 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       
       i3 = -1;
       
       int i5 = -1;
       int i6 = -1;
 
 
 
 
 
       
       if (bool2)
       {
         i3 = bitInputStream1.readBits(20);
       }
       int i4 = bitInputStream1.readBits(20);
       if (bool1) {
         
         i5 = bitInputStream1.readBits(20);
         i6 = bitInputStream1.readBits(20);
       } 
       
       byte[] arrayOfByte = new byte[256];
       byte b1 = 0; int i7;
       for (i7 = 0; i7 < 256; i7++) {
         
         if (bitInputStream1.readBoolean())
         {
           arrayOfByte[b1++] = (byte)i7;
         }
       } 
       
       if (i3 == -1) {
         
         BitInputStream bitInputStream5 = new BitInputStream((InputStream)new PatternGeneratedInputStream(new byte[1024]), BitOrder.LOWEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
 
 
         
         BitInputStream bitInputStream6 = bitInputStream5;
       } else {
         
         byte[] arrayOfByte2 = bitInputStream1.readBitsAsArray(i3);
         bitLimitedInputStream1 = new BitLimitedInputStream(new ByteArrayInputStream(arrayOfByte2), bitInputStream1.getBitOrder(), bitInputStream1.getEndianFormat(), i3);
 
         
         bitLimitedInputStream2 = new BitLimitedInputStream(new ByteArrayInputStream(arrayOfByte2), bitInputStream1.getBitOrder(), bitInputStream1.getEndianFormat(), i3);
       } 
 
 
 
 
 
       
       BitInputStream bitInputStream4 = bitInputStream1.getBufferedStream(i4);
       if (i5 == -1) {
         
         bitInputStream2 = new BitInputStream((InputStream)new PatternGeneratedInputStream(new byte[1024]), BitOrder.HIGHEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
       
       }
       else {
         
         bitInputStream2 = bitInputStream1.getBufferedStream(i5);
       } 
       
       if (i6 == -1) {
         
         bitInputStream3 = new BitInputStream((InputStream)new PatternGeneratedInputStream(new byte[1024]), BitOrder.HIGHEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
       
       }
       else {
         
         bitInputStream3 = bitInputStream1.getBufferedStream(i6);
       } 
 
 
       
       i7 = Integer.MAX_VALUE;
       int i8 = Integer.MIN_VALUE;
       int i9 = Integer.MAX_VALUE;
       int i10 = Integer.MIN_VALUE; int i11;
       for (i11 = 0; i11 < paramInt2; i11++) {
         
         i7 = Math.min(i7, arrayOfDCCFrameHeader[i11].getXOffset());
         i8 = Math.max(i8, arrayOfDCCFrameHeader[i11].getXOffset() + arrayOfDCCFrameHeader[i11].getWidth());
 
 
         
         i9 = Math.min(i9, arrayOfDCCFrameHeader[i11].getYOffset());
         i10 = Math.max(i10, arrayOfDCCFrameHeader[i11].getYOffset() + arrayOfDCCFrameHeader[i11].getHeight());
       } 
 
 
 
 
       
       i11 = i8 - i7;
       int i12 = i10 - i9;
       DCCFrameBufferPalette[][][] arrayOfDCCFrameBufferPalette = new DCCFrameBufferPalette[paramInt2][(i12 + 3) / 4][(i11 + 3) / 4];
 
       
       for (byte b2 = 0; b2 < paramInt2; b2++) {
         
         DCCFrameCellContext dCCFrameCellContext = new DCCFrameCellContext(i7, i9, arrayOfDCCFrameHeader[b2]);
 
         
         for (int i13 = dCCFrameCellContext.getFrameCellTopIndex(); i13 <= dCCFrameCellContext.getFrameCellBottomIndex(); i13++) {
           
           int i14 = dCCFrameCellContext.getFrameCellLeftIndex();
           for (; i14 <= dCCFrameCellContext.getFrameCellRightIndex(); i14++) {
             DCCFrameBufferPalette dCCFrameBufferPalette;
 
 
 
             
             int i15 = b2 - 1;
             while (i15 >= 0 && arrayOfDCCFrameBufferPalette[i15][i13][i14] == null)
             {
               
               i15--;
             }
             boolean bool3 = (i15 >= 0) ? true : false;
             if (i15 < 0) {
               
               dCCFrameBufferPalette = new DCCFrameBufferPalette();
             } else {
               
               dCCFrameBufferPalette = arrayOfDCCFrameBufferPalette[i15][i13][i14].copy();
             } 
 
             
             if (!bool3 || !bitLimitedInputStream1.readBoolean()) {
               int i16;
 
               
               if (bool3) {
                 
                 i16 = bitInputStream4.readBits(4);
               } else {
                 
                 i16 = 15;
               } 
               
               if (i16 != 0) {
                 
                 int i17 = 0;
                 boolean bool4 = bitInputStream2.readBoolean();
                 Stack<Byte> stack = new Stack();
                 byte b4 = 0;
                 while (b4 < MathUtilities.countSetBits(i16)) {
                   
                   int i18 = i17;
                   if (bool4) {
                     
                     i17 = bitInputStream3.readBits(8);
                   } else {
                     int i19;
 
                     
                     do {
                       i19 = bitInputStream1.readBits(4);
                       i17 += i19;
                       i17 %= 256;
                     } while (i19 == 15);
                   } 
                   if (i17 == i18) {
                     break;
                   }
 
                   
                   b4++;
                   stack.push(Byte.valueOf(arrayOfByte[i17]));
                 } 
 
                 
                 byte b5 = 0;
                 while (i16 != 0) {
                   
                   if ((i16 & 0x1) != 0) {
                     
                     boolean bool5 = (stack.size() > 0) ? ((Byte)stack.pop()).byteValue() : false;
                     
                     dCCFrameBufferPalette.setColor(b5, bool5);
                   } 
                   i16 >>= 1;
                   b5++;
                 } 
               } 
             } 
 
             
             arrayOfDCCFrameBufferPalette[b2][i13][i14] = dCCFrameBufferPalette;
           } 
         } 
       } 
 
       
       byte[][] arrayOfByte1 = new byte[i12][i11];
       DCCFrameBufferCell[][] arrayOfDCCFrameBufferCell = new DCCFrameBufferCell[(i12 + 3) / 4][(i11 + 3) / 4];
       
       boolean[][] arrayOfBoolean = new boolean[(i12 + 3) / 4][(i11 + 3) / 4];
 
       
       for (DCCFrameBufferCell[] arrayOfDCCFrameBufferCell1 : arrayOfDCCFrameBufferCell) {
         
         for (byte b4 = 0; b4 < arrayOfDCCFrameBufferCell1.length; b4++)
         {
           arrayOfDCCFrameBufferCell1[b4] = new DCCFrameBufferCell();
         }
       } 
       
       for (byte b3 = 0; b3 < paramInt2; b3++) {
         
         DCCFrameCellContext dCCFrameCellContext = new DCCFrameCellContext(i7, i9, arrayOfDCCFrameHeader[b3]);
 
         
         for (int i13 = dCCFrameCellContext.getFrameCellTopIndex(); i13 <= dCCFrameCellContext.getFrameCellBottomIndex(); i13++) {
 
           
           int i14 = dCCFrameCellContext.getFrameCellHeight(i13);
           int i15 = dCCFrameCellContext.getFrameCellYOffset(i13);
           
           int i16 = dCCFrameCellContext.getFrameCellLeftIndex();
           for (; i16 <= dCCFrameCellContext.getFrameCellRightIndex(); i16++) {
 
             
             int i17 = dCCFrameCellContext.getFrameCellWidth(i16);
             int i18 = dCCFrameCellContext.getFrameCellXOffset(i16);
 
             
             if (arrayOfBoolean[i13][i16] && bitLimitedInputStream2.readBoolean()) {
 
 
 
               
               if (i14 != arrayOfDCCFrameBufferCell[i13][i16].getHeight() || i17 != arrayOfDCCFrameBufferCell[i13][i16].getWidth() || i18 != arrayOfDCCFrameBufferCell[i13][i16].getXOffset() || i15 != arrayOfDCCFrameBufferCell[i13][i16].getYOffset())
               {
 
 
 
 
 
 
                 
                 for (byte b4 = 0; b4 < 4; b4++) {
                   
                   for (byte b5 = 0; b5 < 4; b5++)
                   {
                     int i19 = b4 + 4 * i13;
                     int i20 = b5 + 4 * i16;
                     if (i19 < arrayOfByte1.length && i20 < (arrayOfByte1[i19]).length)
                     {
                       
                       arrayOfByte1[i19][i20] = b;
                     }
                   }
                 
                 } 
               }
             } else {
               
               DCCFrameBufferPalette dCCFrameBufferPalette = arrayOfDCCFrameBufferPalette[b3][i13][i16];
               int i19 = 4 * i13 + i15;
               for (; i19 < 4 * i13 + i15 + i14; i19++) {
                 
                 int i20 = 4 * i16 + i18;
                 for (; i20 < 4 * i16 + i18 + i17; i20++)
                 {
                   arrayOfByte1[i19][i20] = dCCFrameBufferPalette.getColor(bitInputStream1.readBits(dCCFrameBufferPalette.getColorBits()));
                 }
               } 
               arrayOfDCCFrameBufferCell[i13][i16].setWidth(i17);
               arrayOfDCCFrameBufferCell[i13][i16].setHeight(i14);
               arrayOfDCCFrameBufferCell[i13][i16].setXOffset(i18);
               arrayOfDCCFrameBufferCell[i13][i16].setYOffset(i15);
             } 
             arrayOfBoolean[i13][i16] = true;
           } 
         } 
 
         
         BufferedImage bufferedImage = new BufferedImage(Math.max(1, arrayOfDCCFrameHeader[b3].getWidth()), Math.max(1, arrayOfDCCFrameHeader[b3].getHeight()), 2);
 
 
         
         if (arrayOfDCCFrameHeader[b3].isBottomUp()) {
           
           for (byte b4 = 0; b4 < arrayOfDCCFrameHeader[b3].getHeight(); b4++)
           {
             for (byte b5 = 0; b5 < arrayOfDCCFrameHeader[b3].getWidth(); b5++)
             {
               int i14 = arrayOfDCCFrameHeader[b3].getXOffset() - i7;
               int i15 = i12 - arrayOfDCCFrameHeader[b3].getYOffset() - i9 - 1;
               
               bufferedImage.setRGB(b5, b4, paramRestrictableIndexColorModel.getRGB(arrayOfByte1[i15 + b4][i14 + b5] & 0xFF));
             }
           
           }
         
         } else {
           
           for (byte b4 = 0; b4 < arrayOfDCCFrameHeader[b3].getHeight(); b4++) {
             
             for (byte b5 = 0; b5 < arrayOfDCCFrameHeader[b3].getWidth(); b5++) {
               
               int i14 = arrayOfDCCFrameHeader[b3].getXOffset() - i7;
               int i15 = arrayOfDCCFrameHeader[b3].getYOffset() - i9;
               bufferedImage.setRGB(b5, b4, paramRestrictableIndexColorModel.getRGB(arrayOfByte1[i15 + b4][i14 + b5] & 0xFF));
             } 
           } 
         } 
 
 
 
 
         
         arrayOfAnimationFrame[b3] = new AnimationFrame(bufferedImage, arrayOfDCCFrameHeader[b3].getXOffset(), arrayOfDCCFrameHeader[b3].getYOffset());
         
         if (arrayOfDCCFrameHeader[b3].getOptionalData() != null)
         {
           arrayOfAnimationFrame[b3].setOptionalData(arrayOfDCCFrameHeader[b3].getOptionalData());
         }
       } 
       
       return Arrays.asList(arrayOfAnimationFrame);
     } catch (EOFException eOFException) {
       
       throw new ParseException("Direction decode failed: Unexpected end of bitstream.", eOFException);
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
   
   public byte[] encode(Animation paramAnimation, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) {
     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
     
     paramRestrictableIndexColorModel = deriveCodecPalette(paramRestrictableIndexColorModel);
 
     
     int[] arrayOfInt = new int[paramAnimation.getDirectionCount()];
     int[][] arrayOfInt1 = new int[paramAnimation.getDirectionCount()][paramAnimation.getFrameCount()];
     int i = 24 + 4 * paramAnimation.getDirectionCount() * paramAnimation.getFrameCount();
     for (byte b1 = 0; b1 < arrayOfInt1.length; b1++) {
       
       int j = 0;
       for (byte b = 0; b < (arrayOfInt1[b1]).length; b++) {
 
         
         arrayOfInt1[b1][b] = (DC6Codec.encodeFrame(paramAnimation, b1, b, paramRestrictableIndexColorModel)).length;
         j += arrayOfInt1[b1][b];
       } 
       j += 35 * paramAnimation.getFrameCount();
       arrayOfInt[b1] = j;
       i += j;
     } 
 
 
 
     
     byte[][] arrayOfByte = new byte[paramAnimation.getDirectionCount()][];
     for (byte b2 = 0; b2 < paramAnimation.getDirectionCount(); b2++) {
       
       arrayOfByte[b2] = encodeDirection(paramAnimation, b2, true, true, arrayOfInt[b2], arrayOfInt1[b2], paramRestrictableIndexColorModel, paramProgressTracker.getSubtrackerByPercentage(25.0D));
 
 
       
       byte[] arrayOfByte1 = encodeDirection(paramAnimation, b2, true, false, arrayOfInt[b2], arrayOfInt1[b2], paramRestrictableIndexColorModel, paramProgressTracker.getSubtrackerByPercentage(25.0D));
 
       
       if (arrayOfByte1.length < (arrayOfByte[b2]).length)
       {
         arrayOfByte[b2] = arrayOfByte1;
       }
       
       arrayOfByte1 = encodeDirection(paramAnimation, b2, false, true, arrayOfInt[b2], arrayOfInt1[b2], paramRestrictableIndexColorModel, paramProgressTracker.getSubtrackerByPercentage(25.0D));
 
       
       if (arrayOfByte1.length < (arrayOfByte[b2]).length)
       {
         arrayOfByte[b2] = arrayOfByte1;
       }
       
       arrayOfByte1 = encodeDirection(paramAnimation, b2, false, false, arrayOfInt[b2], arrayOfInt1[b2], paramRestrictableIndexColorModel, paramProgressTracker.getSubtrackerByPercentage(25.0D));
 
       
       if (arrayOfByte1.length < (arrayOfByte[b2]).length)
       {
         arrayOfByte[b2] = arrayOfByte1;
       }
     } 
 
     
     try {
       PrimitiveOutputStream primitiveOutputStream = new PrimitiveOutputStream(byteArrayOutputStream, false);
       
       primitiveOutputStream.writeUnsignedByte(116);
       primitiveOutputStream.writeUnsignedByte(6);
       primitiveOutputStream.writeUnsignedByte((byte)paramAnimation.getDirectionCount());
       primitiveOutputStream.writeInt(paramAnimation.getFrameCount());
       primitiveOutputStream.writeInt(1);
       primitiveOutputStream.writeInt(i);
 
       
       int j = 15 + 4 * paramAnimation.getDirectionCount();
       for (byte b = 0; b < paramAnimation.getDirectionCount(); b++) {
         
         primitiveOutputStream.writeInt(j);
         j += (arrayOfByte[b]).length;
       } 
       primitiveOutputStream.close();
       
       for (byte[] arrayOfByte1 : arrayOfByte)
       {
         byteArrayOutputStream.write(arrayOfByte1);
       
       }
     
     }
     catch (IOException iOException) {
 
       
       throw new IllegalStateException("ByteArrayOutputStream threw an IOException!", iOException);
     } 
     paramProgressTracker.setProgressCompleted();
     return byteArrayOutputStream.toByteArray();
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   private byte[] encodeDirection(Animation paramAnimation, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int[] paramArrayOfint, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) {
     ProgressTracker progressTracker1 = paramProgressTracker.getSubtrackerByPercentage(0.0D, paramAnimation.getFrameCount(), 50.0D);
     ProgressTracker progressTracker2 = paramProgressTracker.getSubtrackerByPercentage(0.0D, paramAnimation.getFrameCount(), 50.0D);
 
 
 
     
     int i = paramRestrictableIndexColorModel.getMostTransparentIndex();
     Color color = new Color(paramRestrictableIndexColorModel.getRGB(i), true);
 
     
     try {
       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
       PrimitiveOutputStream primitiveOutputStream = new PrimitiveOutputStream(byteArrayOutputStream, false);
       primitiveOutputStream.writeInt(paramInt2);
       
       BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream, BitOrder.LOWEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
       bitOutputStream.writeBit(paramBoolean2);
       bitOutputStream.writeBit(paramBoolean1);
 
       
       boolean bool = false;
       int j = 0;
       int k = 0;
       int m = 0;
       int n = 0;
       int i1 = 0;
       int i2 = 0;
       int i3;
       for (i3 = 0; i3 < paramAnimation.getFrameCount(); i3++) {
         
         AnimationFrame animationFrame = paramAnimation.getFrame(paramInt1, i3);
         j = Math.max(j, getCompressionBitCode(animationFrame.getImage().getWidth(), false));
         k = Math.max(k, getCompressionBitCode(animationFrame.getImage().getHeight(), false));
         
         m = Math.max(m, getCompressionBitCode(animationFrame.getXOffset(), true));
         
         n = Math.max(n, getCompressionBitCode(animationFrame.getYOffset() + animationFrame.getImage().getHeight() - 1, true));
 
 
         
         i1 = Math.max(i1, getCompressionBitCode((animationFrame.getOptionalData()).length, false));
         
         i2 = Math.max(i2, getCompressionBitCode(paramArrayOfint[i3], false));
       } 
 
       
       bitOutputStream.writeBits(bool, 4);
       bitOutputStream.writeBits(j, 4);
       bitOutputStream.writeBits(k, 4);
       bitOutputStream.writeBits(m, 4);
       bitOutputStream.writeBits(n, 4);
       bitOutputStream.writeBits(i1, 4);
       bitOutputStream.writeBits(i2, 4);
       
       i3 = Integer.MAX_VALUE;
       int i4 = Integer.MAX_VALUE;
       int i5 = Integer.MIN_VALUE;
       int i6 = Integer.MIN_VALUE;
       int i7;
       for (i7 = 0; i7 < paramAnimation.getFrameCount(); i7++) {
         
         AnimationFrame animationFrame = paramAnimation.getFrame(paramInt1, i7);
         
         bitOutputStream.writeBits(0, DCC_SIZE_COMPRESSION_FUNCTION[bool]);
         bitOutputStream.writeBits(animationFrame.getImage().getWidth(), DCC_SIZE_COMPRESSION_FUNCTION[j]);
         bitOutputStream.writeBits(animationFrame.getImage().getHeight(), DCC_SIZE_COMPRESSION_FUNCTION[k]);
         bitOutputStream.writeBitsSigned(animationFrame.getXOffset(), DCC_SIZE_COMPRESSION_FUNCTION[m]);
         
         bitOutputStream.writeBitsSigned(animationFrame.getYOffset() + animationFrame.getImage().getHeight() - 1, DCC_SIZE_COMPRESSION_FUNCTION[n]);
 
         
         bitOutputStream.writeBits((animationFrame.getOptionalData()).length, DCC_SIZE_COMPRESSION_FUNCTION[i1]);
         bitOutputStream.writeBits(paramArrayOfint[i7], DCC_SIZE_COMPRESSION_FUNCTION[i2]);
         bitOutputStream.writeBit(false);
 
         
         i3 = Math.min(i3, animationFrame.getXOffset());
         i4 = Math.min(i4, animationFrame.getYOffset());
         i5 = Math.max(i5, animationFrame.getXOffset() + animationFrame.getImage().getWidth());
         i6 = Math.max(i6, animationFrame.getYOffset() + animationFrame.getImage().getHeight());
       } 
       
       i7 = i5 - i3;
       int i8 = i6 - i4;
       
       if (i1 > 0) {
         
         bitOutputStream.flush();
         for (byte b = 0; b < paramAnimation.getFrameCount(); b++)
         {
           bitOutputStream.write(paramAnimation.getFrame(paramInt1, b).getOptionalData());
         }
       } 
 
 
 
 
       
       BitBufferOutputStream bitBufferOutputStream1 = null;
       
       BitBufferOutputStream bitBufferOutputStream3 = null;
       BitBufferOutputStream bitBufferOutputStream4 = null;
 
       
       if (paramBoolean1)
       {
         bitBufferOutputStream1 = new BitBufferOutputStream(BitOrder.LOWEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
       }
       
       BitBufferOutputStream bitBufferOutputStream2 = new BitBufferOutputStream(BitOrder.LOWEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
       if (paramBoolean2) {
         
         bitBufferOutputStream4 = new BitBufferOutputStream(BitOrder.LOWEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
         
         bitBufferOutputStream3 = new BitBufferOutputStream(BitOrder.LOWEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
       } 
       
       BitBufferOutputStream bitBufferOutputStream5 = new BitBufferOutputStream(BitOrder.LOWEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
       BitBufferOutputStream bitBufferOutputStream6 = new BitBufferOutputStream(BitOrder.LOWEST_BIT_FIRST, EndianFormat.LITTLE_ENDIAN);
 
       
       byte[][] arrayOfByte = new byte[i8][i7];
       DCCFrameBufferPalette[][] arrayOfDCCFrameBufferPalette = new DCCFrameBufferPalette[(i8 + 3) / 4][(i7 + 3) / 4];
       
       DCCFrameBufferCell[][] arrayOfDCCFrameBufferCell = new DCCFrameBufferCell[(i8 + 3) / 4][(i7 + 3) / 4];
       
       boolean[][] arrayOfBoolean = new boolean[(i8 + 3) / 4][(i7 + 3) / 4];
       
       DCCFrameBufferPalette[][][] arrayOfDCCFrameBufferPalette1 = new DCCFrameBufferPalette[paramAnimation.getFrameCount()][(i8 + 3) / 4][(i7 + 3) / 4];
 
       
       BufferedImage[][][] arrayOfBufferedImage = new BufferedImage[paramAnimation.getFrameCount()][(i8 + 3) / 4][(i7 + 3) / 4];
 
 
       
       for (DCCFrameBufferCell[] arrayOfDCCFrameBufferCell1 : arrayOfDCCFrameBufferCell) {
         
         for (byte b = 0; b < arrayOfDCCFrameBufferCell1.length; b++)
         {
           arrayOfDCCFrameBufferCell1[b] = new DCCFrameBufferCell();
         }
       } 
 
       
       DefaultValueHashMap<Color, Byte> defaultValueHashMap = new DefaultValueHashMap(Byte.valueOf((byte)i));
       HashMap<Object, Object> hashMap1 = new HashMap<Object, Object>();
       for (int i9 = paramRestrictableIndexColorModel.getMapSize() - 1; i9 >= 0; i9--) {
         
         if (paramRestrictableIndexColorModel.isValid(i9)) {
           
           int i10 = paramRestrictableIndexColorModel.getRGB(i9);
           defaultValueHashMap.put(new Color(i10, true), Byte.valueOf((byte)i9));
           hashMap1.put(Integer.valueOf(i10), Byte.valueOf((byte)i9));
         } 
       } 
 
 
 
       
       BitMap bitMap = new BitMap(256);
       for (byte b1 = 0; b1 < paramAnimation.getFrameCount(); b1++) {
         
         AnimationFrame animationFrame = paramAnimation.getFrame(paramInt1, b1);
         DCCFrameCellContext dCCFrameCellContext = new DCCFrameCellContext(i3, i4, animationFrame);
 
         
         int i10 = 0;
         BufferedImage bufferedImage = paramRestrictableIndexColorModel.redraw(animationFrame.getImage());
         
         for (int i11 = dCCFrameCellContext.getFrameCellTopIndex(); i11 <= dCCFrameCellContext.getFrameCellBottomIndex(); i11++) {
 
           
           int i12 = dCCFrameCellContext.getFrameCellHeight(i11);
           
           int i13 = 0;
           
           int i14 = dCCFrameCellContext.getFrameCellLeftIndex();
           for (; i14 <= dCCFrameCellContext.getFrameCellRightIndex(); i14++) {
 
             
             int i15 = dCCFrameCellContext.getFrameCellWidth(i14);
 
             
             BufferedImage bufferedImage1 = bufferedImage.getSubimage(i13, i10, i15, i12);
             
             arrayOfBufferedImage[b1][i11][i14] = bufferedImage1;
 
             
             Set set = ImageUtilities.ditherImage(bufferedImage1, 4, true, (ColorComparator)TransparencyCriticizingSampleDifferenceComparator.SINGLETON, new Color[] { color });
 
             
             set.remove(color);
             byte b4 = 0;
             DCCFrameBufferPalette dCCFrameBufferPalette = new DCCFrameBufferPalette();
             for (Color color1 : set)
             {
               dCCFrameBufferPalette.setColor(b4++, ((Byte)defaultValueHashMap.get(color1)).byteValue());
             }
             while (b4 < 4)
             {
               dCCFrameBufferPalette.setColor(b4++, (byte)i);
             }
             dCCFrameBufferPalette.sortSamples((byte)i);
             
             for (byte b5 = 0; b5 < 4; b5++)
             {
               bitMap.setBit(dCCFrameBufferPalette.getColor(b5) & 0xFF, true);
             }
             
             arrayOfDCCFrameBufferPalette1[b1][i11][i14] = dCCFrameBufferPalette;
             
             i13 += i15;
           } 
           i10 += i12;
         } 
         
         progressTracker1.incrementProgress(1.0D);
       } 
 
       
       HashMap<Object, Object> hashMap2 = new HashMap<Object, Object>();
       byte b2 = 0; byte b3;
       for (b3 = 0; b3 < 'Ā'; b3++) {
         
         if (bitMap.getBit(b3))
         {
           hashMap2.put(Byte.valueOf((byte)b3), Integer.valueOf(b2++));
         }
       } 
 
       
       for (b3 = 0; b3 < paramAnimation.getFrameCount(); b3++) {
         
         AnimationFrame animationFrame = paramAnimation.getFrame(paramInt1, b3);
         DCCFrameCellContext dCCFrameCellContext = new DCCFrameCellContext(i3, i4, animationFrame);
 
         
         for (int i10 = dCCFrameCellContext.getFrameCellTopIndex(); i10 <= dCCFrameCellContext.getFrameCellBottomIndex(); i10++) {
 
           
           int i11 = dCCFrameCellContext.getFrameCellHeight(i10);
           int i12 = dCCFrameCellContext.getFrameCellYOffset(i10);
           
           int i13 = dCCFrameCellContext.getFrameCellLeftIndex();
           for (; i13 <= dCCFrameCellContext.getFrameCellRightIndex(); i13++) {
 
             
             int i14 = dCCFrameCellContext.getFrameCellWidth(i13);
             int i15 = dCCFrameCellContext.getFrameCellXOffset(i13);
 
             
             BufferedImage bufferedImage = arrayOfBufferedImage[b3][i10][i13];
             DCCFrameBufferPalette dCCFrameBufferPalette = arrayOfDCCFrameBufferPalette1[b3][i10][i13];
 
             
             boolean bool1 = false;
             if (paramBoolean1 && arrayOfBoolean[i10][i13]) {
 
               
               DCCFrameBufferCell dCCFrameBufferCell = arrayOfDCCFrameBufferCell[i10][i13];
               if (dCCFrameBufferCell.getWidth() == i14 && dCCFrameBufferCell.getHeight() == i11 && dCCFrameBufferCell.getXOffset() == i15 && dCCFrameBufferCell.getYOffset() == i12) {
 
 
 
 
                 
                 bool1 = true;
                 for (byte b = 0; b < i11; b++)
                 {
                   for (byte b4 = 0; b4 < i14; b4++) {
                     
                     if (((Byte)hashMap1.get(Integer.valueOf(bufferedImage.getRGB(b4, b)))).byteValue() != arrayOfByte[i10 * 4 + b][i13 * 4 + b4]) {
 
                       
                       bool1 = false;
                       break;
                     } 
                   } 
                   if (!bool1)
                   {
                     break;
 
                   
                   }
 
                 
                 }
 
               
               }
               else if (i14 < 5 && i11 < 5) {
                 
                 bool1 = true; byte b;
                 for (b = 0; b < i11; b++) {
                   
                   for (byte b4 = 0; b4 < i14; b4++) {
                     
                     if (((Byte)hashMap1.get(Integer.valueOf(bufferedImage.getRGB(b4, b)))).byteValue() != i) {
 
                       
                       bool1 = false;
                       break;
                     } 
                   } 
                 } 
                 if (bool1)
                 {
                   for (b = 0; b < i11; b++) {
                     
                     for (byte b4 = 0; b4 < i14; b4++) {
                       
                       int i16 = b + 4 * i10;
                       int i17 = b4 + 4 * i13;
                       if (i16 < arrayOfByte.length && i17 < (arrayOfByte[i16]).length)
                       {
                         
                         arrayOfByte[i16][i17] = (byte)i;
                       }
                     } 
                   } 
                 }
               } 
             } 
 
             
             if (bool1) {
               
               bitBufferOutputStream1.writeBit(true);
             } else {
               byte b4;
               if (arrayOfBoolean[i10][i13] && bitBufferOutputStream1 != null)
               {
                 bitBufferOutputStream1.writeBit(false);
               }
 
               
               boolean bool2 = false;
               
               byte[] arrayOfByte1 = dCCFrameBufferPalette.getInvertedPaletteValuesArray(i);
               if (arrayOfBoolean[i10][i13]) {
                 
                 b4 = dCCFrameBufferPalette.getPixelMask(arrayOfDCCFrameBufferPalette[i10][i13]);
               } else {
                 
                 b4 = 15;
               } 
               
               if (paramBoolean2 && b4 > 0) {
                 DCCFrameBufferPalette dCCFrameBufferPalette1; byte b8;
                 int i16 = 0;
 
                 
                 byte b6 = 0;
                 for (byte b7 = 0; b7 < arrayOfByte1.length; b7++) {
                   
                   if ((b4 >>> arrayOfByte1.length - 1 - b7 & 0x1) != 0) {
                     
                     b8 = ((Integer)hashMap2.get(Byte.valueOf(arrayOfByte1[b7]))).intValue() - b6;
                     i16 += 4 * (1 + b8 / 15);
                   } 
                 } 
                 if (b4 >>> arrayOfByte1.length != 0)
                 {
                   i16 += 4;
                 }
 
 
                 
                 if (arrayOfBoolean[i10][i13]) {
                   
                   dCCFrameBufferPalette1 = dCCFrameBufferPalette.copy();
                   dCCFrameBufferPalette1.rearrangeToResemble(arrayOfDCCFrameBufferPalette[i10][i13], i);
                   
                   b8 = dCCFrameBufferPalette1.getPixelMask(arrayOfDCCFrameBufferPalette[i10][i13]);
                 } else {
                   
                   dCCFrameBufferPalette1 = dCCFrameBufferPalette;
                   b8 = b4;
                 } 
                 int i17 = (((b4 & 0x8) != 0) ? 8 : 0) + (((b4 & 0x4) != 0) ? 8 : 0) + (((b4 & 0x2) != 0) ? 8 : 0) + (((b4 & 0x1) != 0) ? 8 : 0);
 
 
 
                 
                 if (i16 > i17) {
                   
                   bool2 = true;
                   dCCFrameBufferPalette = dCCFrameBufferPalette1;
                   b4 = b8;
                   arrayOfByte1 = dCCFrameBufferPalette1.getInvertedPaletteValuesArray(i);
                 } 
               } 
 
               
               if (arrayOfBoolean[i10][i13]) bitBufferOutputStream2.writeBits(b4, 4); 
               if (b4 > 0) {
                 
                 if (paramBoolean2) bitBufferOutputStream3.writeBit(bool2);
                 
                 int i16 = 0;
                 for (byte b = 0; b < arrayOfByte1.length; b++) {
                   
                   if ((b4 >>> arrayOfByte1.length - 1 - b & 0x1) != 0) {
                     
                     int i17 = ((Integer)hashMap2.get(Byte.valueOf(arrayOfByte1[b]))).intValue();
                     if (bool2) {
                       
                       bitBufferOutputStream4.writeBits(i17, 8);
                     } else {
                       
                       int i18 = i17 - i16;
                       while (i18 > 14) {
                         
                         bitBufferOutputStream5.writeBits(15, 4);
                         i18 -= 15;
                       } 
                       bitBufferOutputStream5.writeBits(i18, 4);
                     } 
                     i16 = i17;
                   } 
                 } 
                 if (b4 >>> arrayOfByte1.length != 0)
                 {
                   if (bool2) {
                     
                     bitBufferOutputStream4.writeBits(i16, 8);
                   } else {
                     
                     bitBufferOutputStream5.writeBits(0, 4);
                   } 
                 }
               } 
               arrayOfDCCFrameBufferPalette[i10][i13] = dCCFrameBufferPalette;
 
               
               for (byte b5 = 0; b5 < bufferedImage.getHeight(); b5++) {
                 
                 for (byte b = 0; b < bufferedImage.getWidth(); b++) {
                   
                   byte b6 = ((Byte)hashMap1.get(Integer.valueOf(bufferedImage.getRGB(b, b5)))).byteValue();
                   bitBufferOutputStream6.writeBits(dCCFrameBufferPalette.findSampleValue(b6), dCCFrameBufferPalette.getColorBits());
                   
                   arrayOfByte[i10 * 4 + i12 + b5][i13 * 4 + i15 + b] = b6;
                 } 
               } 
 
               
               DCCFrameBufferCell dCCFrameBufferCell = arrayOfDCCFrameBufferCell[i10][i13];
               dCCFrameBufferCell.setWidth(i14);
               dCCFrameBufferCell.setHeight(i11);
               dCCFrameBufferCell.setXOffset(i15);
               dCCFrameBufferCell.setYOffset(i12);
             } 
 
             
             arrayOfBoolean[i10][i13] = true;
           } 
         } 
         
         progressTracker2.incrementProgress(1.0D);
       } 
       
       if (paramBoolean1) bitOutputStream.writeBits(bitBufferOutputStream1.bitsWritten(), 20); 
       bitOutputStream.writeBits(bitBufferOutputStream2.bitsWritten(), 20);
       if (paramBoolean2) {
         
         bitOutputStream.writeBits(bitBufferOutputStream3.bitsWritten(), 20);
         bitOutputStream.writeBits(bitBufferOutputStream4.bitsWritten(), 20);
       } 
       
       bitOutputStream.write(bitMap.getBitmap());
       
       if (paramBoolean1)
       {
         bitOutputStream.writeBitsFromArray(bitBufferOutputStream1.toByteArray(), bitBufferOutputStream1.bitsWritten());
       }
 
       
       bitOutputStream.writeBitsFromArray(bitBufferOutputStream2.toByteArray(), bitBufferOutputStream2.bitsWritten());
 
       
       if (paramBoolean2) {
         
         bitOutputStream.writeBitsFromArray(bitBufferOutputStream3.toByteArray(), bitBufferOutputStream3.bitsWritten());
 
         
         bitOutputStream.writeBitsFromArray(bitBufferOutputStream4.toByteArray(), bitBufferOutputStream4.bitsWritten());
       } 
 
       
       bitOutputStream.writeBitsFromArray(bitBufferOutputStream5.toByteArray(), bitBufferOutputStream5.bitsWritten());
 
       
       bitOutputStream.writeBitsFromArray(bitBufferOutputStream6.toByteArray(), bitBufferOutputStream6.bitsWritten());
 
 
       
       bitOutputStream.close();
       
       return byteArrayOutputStream.toByteArray();
     } catch (IOException iOException) {
 
       
       throw new IllegalStateException("ByteArrayOutputStream threw an IOException!", iOException);
     } 
   }
 
 
 
 
 
 
 
 
 
   
   private int getCompressionBitCode(int paramInt, boolean paramBoolean) {
     int i = MathUtilities.countSignificantBits(Math.abs(paramInt));
     if (paramBoolean)
     {
       if (paramInt >= 0 || MathUtilities.countSetBits(Math.abs(paramInt)) != 1)
       {
 
 
 
         
         i++;
       }
     }
     while (DCC_SIZE_COMPRESSION_FUNCTION_INVERTED[i] == -1)
     {
       i++;
     }
     return DCC_SIZE_COMPRESSION_FUNCTION_INVERTED[i];
   }
 
 
 
 
 
   
   static class DCCFrameCellContext
   {
     protected final int m_frame_cell_top_index;
 
 
 
     
     protected final int m_frame_cell_bottom_index;
 
 
 
     
     protected final int m_frame_cell_left_index;
 
 
 
     
     protected final int m_frame_cell_right_index;
 
 
 
     
     protected final int m_top_frame_cell_height;
 
 
 
     
     protected final int m_bottom_frame_cell_height;
 
 
 
     
     protected final int m_left_frame_cell_width;
 
 
 
     
     protected final int m_right_frame_cell_width;
 
 
 
     
     protected final int m_top_frame_cell_offset;
 
 
 
     
     protected final int m_left_frame_cell_offset;
 
 
 
 
     
     public DCCFrameCellContext(int param1Int1, int param1Int2, AnimationFrame param1AnimationFrame) {
       this(param1Int1, param1Int2, param1AnimationFrame.getImage().getWidth(), param1AnimationFrame.getImage().getHeight(), param1AnimationFrame.getXOffset(), param1AnimationFrame.getYOffset());
     }
 
 
 
 
 
 
 
 
 
 
     
     public DCCFrameCellContext(int param1Int1, int param1Int2, DCCCodec.DCCFrameHeader param1DCCFrameHeader) {
       this(param1Int1, param1Int2, param1DCCFrameHeader.getWidth(), param1DCCFrameHeader.getHeight(), param1DCCFrameHeader.getXOffset(), param1DCCFrameHeader.getYOffset());
     }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     
     public DCCFrameCellContext(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
       this.m_frame_cell_left_index = (param1Int5 - param1Int1) / 4;
       this.m_frame_cell_top_index = (param1Int6 - param1Int2) / 4;
       this.m_frame_cell_right_index = (param1Int5 - param1Int1 + param1Int3 - 2) / 4;
       this.m_frame_cell_bottom_index = (param1Int6 - param1Int2 + param1Int4 - 2) / 4;
 
 
 
       
       this.m_top_frame_cell_height = Math.min(4 - (param1Int6 - param1Int2) % 4, param1Int4);
       this.m_left_frame_cell_width = Math.min(4 - (param1Int5 - param1Int1) % 4, param1Int3);
       this.m_top_frame_cell_offset = (param1Int6 - param1Int2) % 4;
       this.m_left_frame_cell_offset = (param1Int5 - param1Int1) % 4;
       
       int i = (6 - this.m_top_frame_cell_height + param1Int4 % 4) % 4 + 2;
       if (i == 5 && param1Int4 < 5)
       {
         i = 1;
       }
       this.m_bottom_frame_cell_height = Math.min(i, param1Int4);
       
       int j = (6 - this.m_left_frame_cell_width + param1Int3 % 4) % 4 + 2;
       if (j == 5 && param1Int3 < 5)
       {
         j = 1;
       }
       this.m_right_frame_cell_width = Math.min(j, param1Int3);
     }
 
     
     public int getBottomFrameCellHeight() {
       return this.m_bottom_frame_cell_height;
     }
 
     
     public int getFrameCellBottomIndex() {
       return this.m_frame_cell_bottom_index;
     }
 
     
     public int getFrameCellLeftIndex() {
       return this.m_frame_cell_left_index;
     }
 
     
     public int getFrameCellRightIndex() {
       return this.m_frame_cell_right_index;
     }
 
     
     public int getFrameCellTopIndex() {
       return this.m_frame_cell_top_index;
     }
 
     
     public int getLeftFrameCellOffset() {
       return this.m_left_frame_cell_offset;
     }
 
     
     public int getLeftFrameCellWidth() {
       return this.m_left_frame_cell_width;
     }
 
     
     public int getRightFrameCellWidth() {
       return this.m_right_frame_cell_width;
     }
 
     
     public int getTopFrameCellHeight() {
       return this.m_top_frame_cell_height;
     }
 
     
     public int getTopFrameCellOffset() {
       return this.m_top_frame_cell_offset;
     }
 
 
 
 
 
 
 
 
 
     
     public int getFrameCellWidth(int param1Int) {
       if (param1Int == getFrameCellRightIndex())
       {
         return getRightFrameCellWidth(); } 
       if (param1Int == getFrameCellLeftIndex())
       {
         return getLeftFrameCellWidth();
       }
       
       return 4;
     }
 
 
 
 
 
 
 
 
 
     
     public int getFrameCellHeight(int param1Int) {
       if (param1Int == getFrameCellBottomIndex())
       {
         return getBottomFrameCellHeight(); } 
       if (param1Int == getFrameCellTopIndex())
       {
         return getTopFrameCellHeight();
       }
       
       return 4;
     }
 
 
 
 
 
 
 
 
 
 
 
 
     
     public int getFrameCellXOffset(int param1Int) {
       if (param1Int == getFrameCellLeftIndex())
       {
         return getLeftFrameCellOffset();
       }
       
       return 0;
     }
 
 
 
 
 
 
 
 
 
 
 
     
     public int getFrameCellYOffset(int param1Int) {
       if (param1Int == getFrameCellTopIndex())
       {
         return getTopFrameCellOffset();
       }
       
       return 0;
     }
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   static class DCCFrameBufferPalette
   {
     protected byte[] m_palette_colors = new byte[4];
 
 
 
 
 
 
 
 
 
     
     public byte getColor(int param1Int) {
       return this.m_palette_colors[param1Int];
     }
 
 
 
 
 
 
 
 
     
     public void setColor(int param1Int, byte param1Byte) {
       this.m_palette_colors[param1Int] = param1Byte;
     }
 
 
 
 
 
 
 
 
     
     public int getDistinctColorCount() {
       if (this.m_palette_colors[1] == this.m_palette_colors[0]) return 1; 
       if (this.m_palette_colors[2] == this.m_palette_colors[1]) return 2; 
       if (this.m_palette_colors[3] == this.m_palette_colors[2]) return 3; 
       return 4;
     }
 
 
 
 
 
 
 
     
     public int getColorBits() {
       switch (getDistinctColorCount()) {
         
         case 1:
           return 0;
         case 2:
           return 1;
         case 3:
         case 4:
           return 2;
       } 
       throw new IllegalStateException("Illegal result from getDistinctColorCount()");
     }
 
 
 
 
 
 
 
     
     public DCCFrameBufferPalette copy() {
       DCCFrameBufferPalette dCCFrameBufferPalette = new DCCFrameBufferPalette();
       dCCFrameBufferPalette.setColor(0, getColor(0));
       dCCFrameBufferPalette.setColor(1, getColor(1));
       dCCFrameBufferPalette.setColor(2, getColor(2));
       dCCFrameBufferPalette.setColor(3, getColor(3));
       return dCCFrameBufferPalette;
     }
 
 
 
 
 
 
 
 
 
     
     public int findSampleValue(byte param1Byte) {
       if (this.m_palette_colors[0] == param1Byte) return 0; 
       if (this.m_palette_colors[1] == param1Byte) return 1; 
       if (this.m_palette_colors[2] == param1Byte) return 2; 
       if (this.m_palette_colors[3] == param1Byte) return 3; 
       throw new IllegalArgumentException("Sample value " + (param1Byte & 0xFF) + " does not appear in the palette [" + (this.m_palette_colors[0] & 0xFF) + ", " + (this.m_palette_colors[1] & 0xFF) + ", " + (this.m_palette_colors[2] & 0xFF) + ", " + (this.m_palette_colors[3] & 0xFF) + "]");
     }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     
     public void sortSamples(byte param1Byte) {
       int i = this.m_palette_colors[0] & 0xFF;
       int j = this.m_palette_colors[1] & 0xFF;
       int k = this.m_palette_colors[2] & 0xFF;
       int m = this.m_palette_colors[3] & 0xFF;
       if (i == param1Byte || (j != param1Byte && i < j)) {
         
         int n = i;
         i = j;
         j = n;
       } 
       if (j == param1Byte || (k != param1Byte && j < k)) {
         
         int n = j;
         j = k;
         k = n;
       } 
       if (k == param1Byte || (m != param1Byte && k < m)) {
         
         int n = k;
         k = m;
         m = n;
       } 
       if (i == param1Byte || (j != param1Byte && i < j)) {
         
         int n = i;
         i = j;
         j = n;
       } 
       if (j == param1Byte || (k != param1Byte && j < k)) {
         
         int n = j;
         j = k;
         k = n;
       } 
       if (i == param1Byte || (j != param1Byte && i < j)) {
         
         int n = i;
         i = j;
         j = n;
       } 
       this.m_palette_colors[0] = (byte)i;
       this.m_palette_colors[1] = (byte)j;
       this.m_palette_colors[2] = (byte)k;
       this.m_palette_colors[3] = (byte)m;
     }
 
 
 
 
 
 
 
 
 
 
     
     public byte[] getInvertedPaletteValuesArray(int param1Int) {
       byte b = (byte)param1Int;
       if (this.m_palette_colors[0] == b) return Utilities.EMPTY_BYTE_ARRAY; 
       if (this.m_palette_colors[1] == b) return new byte[] { this.m_palette_colors[0] }; 
       if (this.m_palette_colors[2] == b) return new byte[] { this.m_palette_colors[1], this.m_palette_colors[0] }; 
       if (this.m_palette_colors[3] == b)
       {
         return new byte[] { this.m_palette_colors[2], this.m_palette_colors[1], this.m_palette_colors[0] };
       }
       return new byte[] { this.m_palette_colors[3], this.m_palette_colors[2], this.m_palette_colors[1], this.m_palette_colors[0] };
     }
 
 
 
 
 
 
 
 
 
 
 
     
     public void rearrangeToResemble(DCCFrameBufferPalette param1DCCFrameBufferPalette, int param1Int) {
       byte b = (byte)param1Int;
       byte b1 = 3;
       while (b1 > 0 && getColor(b1) == b)
       {
         b1--;
       }
       while (b1 > 0 && param1DCCFrameBufferPalette.getColor(b1) == b)
       {
         b1--;
       }
       b1++;
       
       for (byte b2 = 0; b2 < b1; b2++) {
         
         for (byte b3 = 0; b3 < b1; b3++) {
           
           if (param1DCCFrameBufferPalette.getColor(b3) == this.m_palette_colors[b2] && b2 != b3) {
             
             byte b4 = this.m_palette_colors[b2];
             this.m_palette_colors[b2] = this.m_palette_colors[b3];
             this.m_palette_colors[b3] = b4;
           } 
         } 
       } 
     }
 
 
 
 
 
 
 
     
     public int getPixelMask(DCCFrameBufferPalette param1DCCFrameBufferPalette) {
       if (param1DCCFrameBufferPalette.getColor(3) == getColor(3)) {
         
         if (param1DCCFrameBufferPalette.getColor(2) == getColor(2)) {
           
           if (param1DCCFrameBufferPalette.getColor(1) == getColor(1)) {
             
             if (param1DCCFrameBufferPalette.getColor(0) == getColor(0))
             {
               return 0;
             }
             
             return 1;
           } 
 
           
           if (param1DCCFrameBufferPalette.getColor(0) == getColor(0))
           {
             return 2;
           }
           
           return 3;
         } 
 
 
         
         if (param1DCCFrameBufferPalette.getColor(1) == getColor(1)) {
           
           if (param1DCCFrameBufferPalette.getColor(0) == getColor(0))
           {
             return 4;
           }
           
           return 5;
         } 
 
         
         if (param1DCCFrameBufferPalette.getColor(0) == getColor(0))
         {
           return 6;
         }
         
         return 7;
       } 
 
 
 
       
       if (param1DCCFrameBufferPalette.getColor(2) == getColor(2)) {
         
         if (param1DCCFrameBufferPalette.getColor(1) == getColor(1)) {
           
           if (param1DCCFrameBufferPalette.getColor(0) == getColor(0))
           {
             return 8;
           }
           
           return 9;
         } 
 
         
         if (param1DCCFrameBufferPalette.getColor(0) == getColor(0))
         {
           return 10;
         }
         
         return 11;
       } 
 
 
       
       if (param1DCCFrameBufferPalette.getColor(1) == getColor(1)) {
         
         if (param1DCCFrameBufferPalette.getColor(0) == getColor(0))
         {
           return 12;
         }
         
         return 13;
       } 
 
       
       if (param1DCCFrameBufferPalette.getColor(0) == getColor(0))
       {
         return 14;
       }
       
       return 15;
     }
 
 
 
 
 
 
 
 
 
 
     
     public String toString() {
       return "[" + (this.m_palette_colors[0] & 0xFF) + ", " + (this.m_palette_colors[1] & 0xFF) + ", " + (this.m_palette_colors[2] & 0xFF) + ", " + (this.m_palette_colors[3] & 0xFF) + "]";
     }
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   static class DCCFrameBufferCell
   {
     protected int m_width = 4;
     protected int m_height = 4;
     protected int m_x_offset = 0;
     protected int m_y_offset = 0;
 
 
     
     public int getHeight() {
       return this.m_height;
     }
 
     
     public void setHeight(int param1Int) {
       this.m_height = param1Int;
     }
 
     
     public int getWidth() {
       return this.m_width;
     }
 
     
     public void setWidth(int param1Int) {
       this.m_width = param1Int;
     }
 
     
     public int getXOffset() {
       return this.m_x_offset;
     }
 
     
     public void setXOffset(int param1Int) {
       this.m_x_offset = param1Int;
     }
 
     
     public int getYOffset() {
       return this.m_y_offset;
     }
 
     
     public void setYOffset(int param1Int) {
       this.m_y_offset = param1Int;
     }
   }
 
 
 
 
   
   static class DCCFrameHeader
   {
     protected int m_width;
 
 
 
     
     protected int m_height;
 
 
 
     
     protected int m_x_offset;
 
 
 
     
     protected int m_y_offset;
 
 
     
     protected int m_optional_data_size;
 
 
     
     protected int m_coded_bytes;
 
 
     
     protected boolean m_bottom_up;
 
 
     
     protected byte[] m_optional_data;
 
 
 
     
     public DCCFrameHeader(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, boolean param1Boolean) {
       this.m_bottom_up = param1Boolean;
       this.m_coded_bytes = param1Int6;
       this.m_height = param1Int2;
       this.m_optional_data_size = param1Int5;
       this.m_width = param1Int1;
       this.m_x_offset = param1Int3;
       this.m_y_offset = param1Int4;
     }
 
     
     public boolean isBottomUp() {
       return this.m_bottom_up;
     }
 
     
     public int getCodedBytes() {
       return this.m_coded_bytes;
     }
 
     
     public int getHeight() {
       return this.m_height;
     }
 
     
     public byte[] getOptionalData() {
       return this.m_optional_data;
     }
 
     
     public int getOptionalDataSize() {
       return this.m_optional_data_size;
     }
 
     
     public int getWidth() {
       return this.m_width;
     }
 
     
     public int getXOffset() {
       return this.m_x_offset;
     }
 
     
     public int getYOffset() {
       return this.m_y_offset;
     }
 
     
     public void setOptionalData(byte[] param1ArrayOfbyte) {
       this.m_optional_data = param1ArrayOfbyte;
     }
 
     
     public void setYOffset(int param1Int) {
       this.m_y_offset = param1Int;
     }
 
 
 
 
     
     public String toString() {
       return "DCC Frame Header: " + this.m_width + "x" + this.m_height + " @ (" + this.m_x_offset + "," + this.m_y_offset + "): " + (this.m_bottom_up ? "Bottom-Up" : "Top-Down");
     }
   }
 }