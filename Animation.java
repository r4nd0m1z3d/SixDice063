 package orioni.sixdice;
 import java.awt.Color;
 import java.awt.Graphics;
 import java.awt.Image;
 import java.awt.image.BufferedImage;
 import java.io.File;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import javax.imageio.ImageIO;
 import orioni.jz.awt.AWTUtilities;
 import orioni.jz.awt.image.ImageUtilities;
 import orioni.jz.io.files.FileUtilities;
 import orioni.jz.util.Pair;
 import orioni.jz.util.strings.StringUtilities;
 public class Animation
 {
   protected List<AnimationFrame> m_frame_list;
   protected int m_directions;
   protected int m_frames;
   protected int m_transparent_index;
   protected List<String> m_warnings;
   protected byte[] m_optional_data;
   public Animation() {
     this(new AnimationFrame());
   }
   public Animation(Image paramImage) {
     this(Collections.singletonList(new AnimationFrame(ImageUtilities.bufferImage(paramImage), 0, 0)), 1, 1);
   }
   public Animation(AnimationFrame paramAnimationFrame) {
     this(Collections.singletonList(paramAnimationFrame), 1, 1);
   }
   public Animation(List<AnimationFrame> paramList, int paramInt1, int paramInt2) throws IllegalArgumentException {
     this(paramList, paramInt1, paramInt2, new ArrayList<String>());
   }
   public Animation(List<AnimationFrame> paramList, int paramInt1, int paramInt2, List<String> paramList1) throws IllegalArgumentException {
     if (paramList != null && paramList.size() != paramInt1 * paramInt2)
     {
       throw new IllegalArgumentException("Discrepancy between frame list size (" + paramList.size() + ") and the direction/frame counts (" + paramInt1 + " and " + paramInt2 + ").  List size must be the product of the other two.");
     }
     this.m_warnings = paramList1;
     initialize(paramList, paramInt1, paramInt2);
   }
   public Animation(Animation paramAnimation) {
     this.m_frame_list = new ArrayList<AnimationFrame>();
     for (AnimationFrame animationFrame : paramAnimation.m_frame_list)
     {
       this.m_frame_list.add(new AnimationFrame(ImageUtilities.copyImage(animationFrame.getImage()), animationFrame.getXOffset(), animationFrame.getYOffset()));
     }
     this.m_directions = paramAnimation.getDirectionCount();
     this.m_frames = paramAnimation.getFrameCount();
     if (paramAnimation.getOptionalData() == null) {
       this.m_optional_data = null;
     } else {
       this.m_optional_data = new byte[(paramAnimation.getOptionalData()).length];
       System.arraycopy(paramAnimation.getOptionalData(), 0, this.m_optional_data, 0, this.m_optional_data.length);
     } 
     this.m_transparent_index = paramAnimation.m_transparent_index;
     this.m_warnings = new ArrayList<String>();
     for (String str : paramAnimation.getWarnings()) this.m_warnings.add(str);
   }
   public Animation(File paramFile, String paramString) throws IOException {
     String str1, str2;
     this.m_warnings = new ArrayList<String>();
     int i = paramFile.getName().lastIndexOf('.');
     if (i == -1) {
       str1 = paramFile.getName();
       str2 = "";
     } else {
       str1 = paramFile.getName().substring(0, i);
       str2 = paramFile.getName().substring(i);
     } 
     if (!FileUtilities.FILESYSTEM_CASE_SENSITIVE) {
       str1 = str1.toLowerCase();
       str2 = str2.toLowerCase();
     } 
     HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
     int j = 0;
     int k = 0;
     for (File file : paramFile.getParentFile().listFiles()) {
       String str = file.getName();
       if (!FileUtilities.FILESYSTEM_CASE_SENSITIVE)
       {
         str = str.toLowerCase();
       }
       if (str.startsWith(str1) && str.endsWith(str2)) {
         str = str.substring(str1.length(), str.length() - str2.length());
         String[] arrayOfString = str.split(paramString);
         if (arrayOfString.length == 3 && "".equals(arrayOfString[0])) {
           int m;
           int n;
           try {
             m = Integer.parseInt(arrayOfString[1]);
             n = Integer.parseInt(arrayOfString[2]);
           } catch (NumberFormatException numberFormatException) {
             this.m_warnings.add("Possibly incorrectly named file (could not parse direction or frame): " + file);
           } 
           if (m < 0 || n < 0) {
             this.m_warnings.add("Possible incorrectly named file (direction<0 or frame<0): " + file);
           }
           else {
             BufferedImage bufferedImage = ImageIO.read(file);
             if (bufferedImage != null) {
               hashMap.put(new Pair(Integer.valueOf(m), Integer.valueOf(n)), bufferedImage);
               j = Math.max(j, m);
               k = Math.max(k, n);
             } else {
               this.m_warnings.add("Could not read file (no supported reader): " + file);
             } 
           } 
         } 
       } 
     } 
     ArrayList<AnimationFrame> arrayList = new ArrayList();
     for (byte b = 0; b <= j; b++) {
       for (byte b1 = 0; b1 <= k; b1++)
       {
         arrayList.add(new AnimationFrame((BufferedImage)hashMap.get(new Pair(Integer.valueOf(b), Integer.valueOf(b1))), 0, 0));
       }
     } 
     initialize(arrayList, j + 1, k + 1);
   }
   private void initialize(List<AnimationFrame> paramList, int paramInt1, int paramInt2) {
     if (this.m_warnings == null) this.m_warnings = new ArrayList<String>(); 
     if (paramList == null) {
       this.m_frame_list = new ArrayList<AnimationFrame>();
       for (byte b = 0; b < paramInt1 * paramInt2; b++)
       {
         this.m_frame_list.add(new AnimationFrame());
       }
     } else {
       this.m_frame_list = new ArrayList<AnimationFrame>(paramList);
       for (byte b = 0; b < this.m_frame_list.size(); b++) {
         if (this.m_frame_list.get(b) == null || ((AnimationFrame)this.m_frame_list.get(b)).getImage() == null)
         {
           this.m_frame_list.set(b, new AnimationFrame());
         }
       } 
     } 
     this.m_directions = paramInt1;
     this.m_frames = paramInt2;
     this.m_optional_data = new byte[0];
   }
   public byte[] getOptionalData() {
     return this.m_optional_data;
   }
   public void setOptionalData(byte[] paramArrayOfbyte) {
     this.m_optional_data = paramArrayOfbyte;
   }
   public void addWarnings(List<String> paramList) {
     this.m_warnings.addAll(paramList);
   }
   public String[] getWarnings() {
     return this.m_warnings.<String>toArray(StringUtilities.EMPTY_STRING_ARRAY);
   }
   protected void checkDirectionIndex(int paramInt, boolean paramBoolean) throws IndexOutOfBoundsException {
     if (paramInt < 0 || paramInt > getDirectionCount() || (paramInt == getDirectionCount() && !paramBoolean))
     {
       throw new IndexOutOfBoundsException(paramInt + " out of bounds [0," + getDirectionCount() + (paramBoolean ? "]" : ")"));
     }
   }
   protected void checkFrameIndex(int paramInt, boolean paramBoolean) throws IndexOutOfBoundsException {
     if (paramInt < 0 || paramInt > getFrameCount() || (paramInt == getFrameCount() && !paramBoolean))
     {
       throw new IndexOutOfBoundsException(paramInt + " out of bounds [0," + getFrameCount() + (paramBoolean ? "]" : ")"));
     }
   }
   public void addFrame(int paramInt) throws IndexOutOfBoundsException {
     checkFrameIndex(paramInt, true);
     if (this.m_directions == 0) {
       this.m_frame_list.add(new AnimationFrame());
       this.m_directions++;
     } else {
       for (int i = this.m_directions - 1; i >= 0; i--)
       {
         this.m_frame_list.add(i * this.m_frames + paramInt, new AnimationFrame());
       }
     } 
     this.m_frames++;
   }
   public void addDirection(int paramInt) throws IndexOutOfBoundsException {
     checkDirectionIndex(paramInt, true);
     if (this.m_frames == 0) {
       this.m_frame_list.add(new AnimationFrame());
       this.m_frames++;
     } else {
       for (byte b = 0; b < this.m_frames; b++)
       {
         this.m_frame_list.add(paramInt * this.m_frames, new AnimationFrame());
       }
     } 
     this.m_directions++;
   }
   public AnimationFrame getFrame(int paramInt1, int paramInt2) throws IndexOutOfBoundsException {
     checkDirectionIndex(paramInt1, false);
     checkFrameIndex(paramInt2, false);
     return this.m_frame_list.get(paramInt1 * this.m_frames + paramInt2);
   }
   public List<AnimationFrame> getFrames() {
     return new ArrayList<AnimationFrame>(this.m_frame_list);
   }
   public AnimationFrame setFrame(int paramInt1, int paramInt2, AnimationFrame paramAnimationFrame) throws IndexOutOfBoundsException {
     checkDirectionIndex(paramInt1, false);
     checkFrameIndex(paramInt2, false);
     return this.m_frame_list.set(paramInt1 * this.m_frames + paramInt2, paramAnimationFrame);
   }
   public void removeFrame(int paramInt) throws IndexOutOfBoundsException {
     checkFrameIndex(paramInt, false);
     for (int i = getDirectionCount() - 1; i >= 0; i--)
     {
       this.m_frame_list.remove(i * getFrameCount() + paramInt);
     }
     this.m_frames--;
   }
   public void removeDirection(int paramInt) throws IndexOutOfBoundsException {
     checkDirectionIndex(paramInt, false);
     for (byte b = 0; b < getFrameCount(); b++)
     {
       this.m_frame_list.remove(paramInt * getFrameCount());
     }
     this.m_directions--;
   }
   public int getFrameCount() {
     return this.m_frames;
   }
   public int getDirectionCount() {
     return this.m_directions;
   }
   public int getSmallestXOffset() {
     int i = Integer.MAX_VALUE;
     for (AnimationFrame animationFrame : this.m_frame_list)
     {
       i = Math.min(animationFrame.getXOffset(), i);
     }
     return i;
   }
   public int getLargestXOffset() {
     int i = Integer.MIN_VALUE;
     for (AnimationFrame animationFrame : this.m_frame_list)
     {
       i = Math.max(animationFrame.getXOffset(), i);
     }
     return i;
   }
   public int getSmallestYOffset() {
     int i = Integer.MAX_VALUE;
     for (AnimationFrame animationFrame : this.m_frame_list)
     {
       i = Math.min(animationFrame.getYOffset(), i);
     }
     return i;
   }
   public int getLargestYOffset() {
     int i = Integer.MIN_VALUE;
     for (AnimationFrame animationFrame : this.m_frame_list)
     {
       i = Math.max(animationFrame.getYOffset(), i);
     }
     return i;
   }
   public int getFirstXIndex() {
     return getSmallestXOffset();
   }
   public int getFirstYIndex() {
     return getSmallestYOffset();
   }
   public int getLastXIndex() {
     int i = Integer.MIN_VALUE;
     for (AnimationFrame animationFrame : this.m_frame_list)
     {
       i = Math.max(animationFrame.getXOffset() + animationFrame.getImage().getWidth() - 1, i);
     }
     return i;
   }
   public int getLastYIndex() {
     int i = Integer.MIN_VALUE;
     for (AnimationFrame animationFrame : this.m_frame_list)
     {
       i = Math.max(animationFrame.getYOffset() + animationFrame.getImage().getHeight() - 1, i);
     }
     return i;
   }
   public void scale(double paramDouble, boolean paramBoolean, int paramInt) {
     for (byte b = 0; b < getDirectionCount(); b++) {
       for (byte b1 = 0; b1 < getFrameCount(); b1++) {
         AnimationFrame animationFrame = getFrame(b, b1);
         animationFrame.setImage(ImageUtilities.bufferImage(animationFrame.getImage().getScaledInstance(Math.max(1, (int)(animationFrame.getImage().getWidth() * paramDouble)), Math.max(1, (int)(animationFrame.getImage().getHeight() * paramDouble)), paramInt)));
         if (paramBoolean) {
           animationFrame.setXOffset((int)(animationFrame.getXOffset() * paramDouble));
           animationFrame.setYOffset((int)(animationFrame.getYOffset() * paramDouble));
         } 
       } 
     } 
   }
   public void trimBorders() {
     for (AnimationFrame animationFrame : this.m_frame_list) {
       BufferedImage bufferedImage = animationFrame.getImage();
       int i = animationFrame.getXOffset();
       int j = animationFrame.getYOffset();
       boolean bool = true;
       int[] arrayOfInt = new int[bufferedImage.getWidth()];
       while (bool && bufferedImage.getHeight() > 1) {
         bool = false;
         bufferedImage.getRGB(0, bufferedImage.getHeight() - 1, arrayOfInt.length, 1, arrayOfInt, 0, arrayOfInt.length);
         boolean bool1 = true;
         for (int k : arrayOfInt) {
           if ((k & 0xFF000000) != 0) {
             bool1 = false;
             break;
           } 
         } 
         if (bool1) {
           bool = true;
           bufferedImage = bufferedImage.getSubimage(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight() - 1);
         } 
       } 
       bool = true;
       while (bool && bufferedImage.getHeight() > 1) {
         bool = false;
         bufferedImage.getRGB(0, 0, arrayOfInt.length, 1, arrayOfInt, 0, arrayOfInt.length);
         boolean bool1 = true;
         for (int k : arrayOfInt) {
           if ((k & 0xFF000000) != 0) {
             bool1 = false;
             break;
           } 
         } 
         if (bool1) {
           bool = true;
           bufferedImage = bufferedImage.getSubimage(0, 1, bufferedImage.getWidth(), bufferedImage.getHeight() - 1);
           j++;
         } 
       } 
       bool = true;
       while (bool && bufferedImage.getWidth() > 1) {
         bool = false;
         boolean bool1 = true;
         for (byte b = 0; b < bufferedImage.getHeight(); b++) {
           if ((bufferedImage.getRGB(bufferedImage.getWidth() - 1, b) & 0xFF000000) != 0) {
             bool1 = false;
             break;
           } 
         } 
         if (bool1) {
           bool = true;
           bufferedImage = bufferedImage.getSubimage(0, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight());
         } 
       } 
       bool = true;
       while (bool && bufferedImage.getWidth() > 1) {
         bool = false;
         boolean bool1 = true;
         for (byte b = 0; b < bufferedImage.getHeight(); b++) {
           if ((bufferedImage.getRGB(0, b) & 0xFF000000) != 0) {
             bool1 = false;
             break;
           } 
         } 
         if (bool1) {
           bool = true;
           bufferedImage = bufferedImage.getSubimage(1, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight());
           i++;
         } 
       } 
       animationFrame.setImage(bufferedImage);
       animationFrame.setXOffset(i);
       animationFrame.setYOffset(j);
     } 
   }
   public void adjustOffsets(int paramInt1, int paramInt2) {
     for (AnimationFrame animationFrame : this.m_frame_list) {
       animationFrame.setXOffset(animationFrame.getXOffset() - paramInt1);
       animationFrame.setYOffset(animationFrame.getYOffset() - paramInt2);
     } 
   }
   public BufferedImage getPaddedImage(int paramInt1, int paramInt2) {
     return getPaddedImage(paramInt1, paramInt2, AWTUtilities.COLOR_TRANSPARENT);
   }
   public BufferedImage getPaddedImage(int paramInt1, int paramInt2, Color paramColor) {
     int i = getFirstXIndex();
     int j = getFirstYIndex();
     BufferedImage bufferedImage = new BufferedImage(getLastXIndex() - i + 1, getLastYIndex() - j + 1, 2);
     Graphics graphics = bufferedImage.getGraphics();
     graphics.setColor(paramColor);
     graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
     AnimationFrame animationFrame = getFrame(paramInt1, paramInt2);
     graphics.drawImage(animationFrame.getImage(), animationFrame.getXOffset() - i, animationFrame.getYOffset() - j, null);
     return bufferedImage;
   }
 }