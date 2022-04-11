 package orioni.sixdice;
 
 import java.awt.Color;
 import java.awt.image.BufferedImage;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileFilter;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import javax.imageio.ImageIO;
 import orioni.jz.awt.AWTUtilities;
 import orioni.jz.awt.image.ImageUtilities;
 import orioni.jz.awt.image.RestrictableIndexColorModel;
 import orioni.jz.common.exception.ParseException;
 import orioni.jz.io.files.FileUtilities;
 import orioni.jz.util.Pair;
 import orioni.jz.util.ProgressTracker;
 import orioni.jz.util.strings.StringUtilities;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class SixDiceCore
 {
   protected Animation m_animation = null;
   protected List<AnimationCodec> m_codecs = new ArrayList<AnimationCodec>(); public SixDiceCore(AnimationCodec... paramVarArgs) {
     for (AnimationCodec animationCodec : paramVarArgs) this.m_codecs.add(animationCodec); 
     this.m_virtual_transparent = null;
     this.m_transparent_to_virtal_on_save = false;
   }
 
 
   
   protected Color m_virtual_transparent;
 
   
   protected boolean m_transparent_to_virtal_on_save;
 
   
   public void addCodecs(AnimationCodec... paramVarArgs) {
     for (AnimationCodec animationCodec : paramVarArgs) this.m_codecs.add(animationCodec);
   
   }
 
 
 
 
 
 
   
   public void setVirtualTransparentColor(Color paramColor) {
     this.m_virtual_transparent = paramColor;
   }
 
 
 
 
 
 
 
   
   public void setTransparentToVirtualOnSave(boolean paramBoolean) {
     this.m_transparent_to_virtal_on_save = paramBoolean;
   }
 
 
 
 
 
 
   
   public Animation getAnimation() {
     return this.m_animation;
   }
 
 
 
 
   
   public void clearAnimation() {
     this.m_animation = null;
   }
 
 
 
 
   
   public void createNewAnimation() {
     this.m_animation = new Animation();
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public boolean loadAnimation(File paramFile, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) throws IOException {
     this.m_animation = null;
     for (AnimationCodec animationCodec : this.m_codecs) {
       
       if (animationCodec.getFileType().usesExtension(FileUtilities.getFileExtension(paramFile))) {
         
         try {
           
           this.m_animation = animationCodec.read(paramFile, animationCodec.formatContainsPalette() ? null : paramRestrictableIndexColorModel, paramProgressTracker);
           break;
         } catch (ParseException parseException) {}
       }
     } 
 
     
     return (this.m_animation != null);
   }
 
 
 
 
 
 
 
 
 
   
   public List<Pair<String, AnimationCodec.MessageType>> checkAnimation(File paramFile) {
     if (this.m_animation == null)
     {
       return Collections.singletonList(new Pair("No animation currently in memory.", AnimationCodec.MessageType.FATAL));
     }
 
 
     
     AnimationCodec animationCodec = this.m_codecs.get(0);
     for (AnimationCodec animationCodec1 : this.m_codecs) {
       
       if (animationCodec1.getFileType().usesExtension(FileUtilities.getFileExtension(paramFile))) {
         
         animationCodec = animationCodec1;
         
         break;
       } 
     } 
     return animationCodec.check(this.m_animation);
   }
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void saveAnimation(File paramFile, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) throws IOException {
     AnimationCodec animationCodec = this.m_codecs.get(0);
     for (AnimationCodec animationCodec1 : this.m_codecs) {
       
       if (animationCodec1.getFileType().usesExtension(FileUtilities.getFileExtension(paramFile))) {
         
         animationCodec = animationCodec1;
         
         break;
       } 
     } 
     animationCodec.write(paramFile, this.m_animation, animationCodec.formatContainsPalette() ? null : paramRestrictableIndexColorModel, paramProgressTracker);
   }
 
 
 
 
 
 
 
 
   
   public String importImage(File paramFile) {
     BufferedImage bufferedImage;
     try {
       bufferedImage = postProcessLoadedImage(ImageIO.read(paramFile));
       if (bufferedImage == null)
       {
         return "The provided file is not an image or this JRE has no image converter for that format.";
       }
     } catch (IOException iOException) {
       
       return "The following error occurred when trying to load " + paramFile + ":\n" + iOException.getMessage();
     } 
     this.m_animation = new Animation(bufferedImage);
     return null;
   }
 
 
 
 
 
 
 
 
 
 
 
 
   
   public String importImage(File paramFile, int paramInt1, int paramInt2, boolean paramBoolean) {
     BufferedImage bufferedImage;
     try {
       bufferedImage = postProcessLoadedImage(ImageIO.read(paramFile));
       if (bufferedImage == null)
       {
         return "The provided file is not an image or this JRE has no image converter for that format.";
       }
     } catch (IOException iOException) {
       
       return "The following error occurred when trying to load " + paramFile + ":\n" + iOException.getMessage();
     } 
     if (paramBoolean)
     {
       this.m_animation.addFrame(paramInt2);
     }
     this.m_animation.getFrame(paramInt1, paramInt2).setImage(bufferedImage);
     return null;
   }
 
 
 
 
 
 
 
 
   
   public String importSeries(File paramFile, String paramString) {
     this.m_animation = null;
     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
     PrintStream printStream = new PrintStream(byteArrayOutputStream);
     this.m_animation = AnimationIO.load(paramFile, paramString, printStream);
     printStream.close();
     if (byteArrayOutputStream.size() > 0)
     {
       return byteArrayOutputStream.toString();
     }
     
     for (AnimationFrame animationFrame : this.m_animation.getFrames())
     {
       animationFrame.setImage(postProcessLoadedImage(animationFrame.getImage()));
     }
     return null;
   }
 
 
 
 
 
 
 
 
 
 
 
 
   
   public String exportImage(File paramFile, String paramString, int paramInt1, int paramInt2) {
     try {
       BufferedImage bufferedImage = preProcessSavingImage(ImageUtilities.copyImage(this.m_animation.getFrame(paramInt1, paramInt2).getImage()));
       
       if (!ImageUtilities.writeImage(bufferedImage, paramString, paramFile))
       {
         return "The image format " + paramString + " is not supported.";
       }
     } catch (IOException iOException) {
       
       return "The following error occurred while trying to export the image:\n" + iOException.getMessage();
     } 
     return null;
   }
 
 
 
 
 
 
 
 
 
 
   
   public String exportSeries(File paramFile, String paramString1, String paramString2) {
     Animation animation = new Animation(null, this.m_animation.getDirectionCount(), this.m_animation.getFrameCount());
     for (byte b = 0; b < this.m_animation.getDirectionCount(); b++) {
       
       for (byte b1 = 0; b1 < this.m_animation.getFrameCount(); b1++)
       {
         animation.setFrame(b, b1, new AnimationFrame(preProcessSavingImage(this.m_animation.getPaddedImage(b, b1)), this.m_animation.getFirstXIndex(), this.m_animation.getFirstYIndex()));
       }
     } 
 
 
 
 
 
     
     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
     PrintStream printStream = new PrintStream(byteArrayOutputStream);
 
     
     AnimationIO.save(paramFile, paramString2, paramString1, animation, printStream);
     printStream.close();
     
     if (byteArrayOutputStream.size() > 0)
     {
       return byteArrayOutputStream.toString();
     }
     
     return null;
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void splitFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
     if (this.m_animation.getDirectionCount() <= paramInt1) this.m_animation.addDirection(paramInt1); 
     int i = paramInt2;
     if (this.m_animation.getFrameCount() <= i) this.m_animation.addFrame(i);
     
     BufferedImage bufferedImage = this.m_animation.getFrame(paramInt1, i).getImage();
     int j = (int)Math.ceil(bufferedImage.getHeight() / paramInt4);
     int k = (int)Math.ceil(bufferedImage.getWidth() / paramInt3);
     
     for (byte b = 0; b < j; b++) {
       
       for (byte b1 = 0; b1 < k; b1++) {
         
         BufferedImage bufferedImage1 = ImageUtilities.copyImage(bufferedImage.getSubimage(b1 * paramInt3, b * paramInt4, Math.min(paramInt3, bufferedImage.getWidth() - b1 * paramInt3), Math.min(paramInt4, bufferedImage.getHeight() - b * paramInt4)));
 
 
 
 
 
 
 
 
 
 
         
         if ((paramBoolean && b + b1 > 0) || this.m_animation.getFrameCount() <= i)
         {
           
           this.m_animation.addFrame(i);
         }
         this.m_animation.setFrame(paramInt1, i, new AnimationFrame(bufferedImage1, 0, 0));
         i++;
       } 
     } 
   }
 
 
 
 
 
 
 
   
   public void centerOffsets(int paramInt1, int paramInt2) {
     AnimationFrame animationFrame = this.m_animation.getFrame(paramInt1, paramInt2);
     this.m_animation.adjustOffsets(animationFrame.getXOffset() + animationFrame.getImage().getWidth() / 2, animationFrame.getYOffset() - animationFrame.getImage().getHeight() / 2);
   }
 
 
 
 
 
 
 
 
 
   
   public BufferedImage postProcessLoadedImage(BufferedImage paramBufferedImage) {
     if (this.m_virtual_transparent != null) {
       
       paramBufferedImage = ImageUtilities.copyImage(paramBufferedImage);
       ImageUtilities.replaceAll(paramBufferedImage, this.m_virtual_transparent, AWTUtilities.COLOR_TRANSPARENT);
     } 
     return paramBufferedImage;
   }
 
 
 
 
 
 
 
 
   
   public BufferedImage preProcessSavingImage(BufferedImage paramBufferedImage) {
     if (this.m_virtual_transparent != null && this.m_transparent_to_virtal_on_save)
     {
       ImageUtilities.replaceAll(paramBufferedImage, AWTUtilities.COLOR_TRANSPARENT, this.m_virtual_transparent);
     }
     return paramBufferedImage;
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void convertAnimationToImage(File paramFile, String paramString1, String paramString2, RestrictableIndexColorModel paramRestrictableIndexColorModel, PrintStream paramPrintStream1, PrintStream paramPrintStream2) {
     try {
       paramPrintStream1.print("Loading " + paramFile + "... ");
       if (loadAnimation(paramFile, paramRestrictableIndexColorModel, null)) {
         String str;
         paramPrintStream1.println("Complete.");
         paramFile = FileUtilities.replaceFileExtension(paramFile, '.' + paramString1);
         
         paramPrintStream1.print("Exporting " + paramFile + "... ");
         if (this.m_animation.getFrameCount() * this.m_animation.getDirectionCount() == 1) {
           
           str = exportImage(paramFile, paramString1, 0, 0);
         } else {
           
           str = exportSeries(paramFile, paramString1, paramString2);
         } 
         if (str != null) {
           
           paramPrintStream2.println("Error during export: " + str);
           paramPrintStream1.println(paramFile + " could not be exported.");
         } else {
           
           paramPrintStream1.println("Complete.");
         } 
       } 
     } catch (IOException iOException) {
       
       paramPrintStream2.println("I/O error occurred: " + iOException.getMessage());
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public Set<File> convertImageToAnimation(File paramFile, AnimationCodec paramAnimationCodec, String paramString, RestrictableIndexColorModel paramRestrictableIndexColorModel, PrintStream paramPrintStream1, PrintStream paramPrintStream2) {
     Set<File> set = new HashSet();
     try {
       String str;
       File file = AnimationIO.getSeriesGenerationFile(paramFile, paramString);
       
       if (file == null) {
         
         paramPrintStream1.print("Importing " + paramFile + "... ");
         str = importImage(paramFile);
         if (str == null) paramPrintStream1.println("Complete.");
       
       } else {
         set = AnimationIO.getImageSeriesParticipants(paramFile, paramString);
         paramPrintStream1.print("Importing " + file + "... ");
         str = importSeries(file, paramString);
         if (str == null) paramPrintStream1.println("Complete."); 
         paramFile = file;
       } 
       if (str == null) {
         
         paramFile = FileUtilities.replaceFileExtension(paramFile, '.' + paramAnimationCodec.getFileType().getExtensions()[0]);
         paramPrintStream1.print("Saving " + paramFile + "... ");
         
         ProgressTracker progressTracker = new ProgressTracker();
         progressTracker.addListener(new ConsoleProgressListener(paramPrintStream1));
         
         saveAnimation(paramFile, paramRestrictableIndexColorModel, progressTracker);
         paramPrintStream1.println("Complete.");
       } else {
         
         paramPrintStream2.println("Error while importing: " + str);
       } 
     } catch (IOException iOException) {
       
       paramPrintStream2.println("I/O error occurred: " + iOException.getMessage());
     } 
     return set;
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void batchConvert(File paramFile, boolean paramBoolean1, AnimationCodec paramAnimationCodec1, AnimationCodec paramAnimationCodec2, RestrictableIndexColorModel paramRestrictableIndexColorModel, OutputStream paramOutputStream1, OutputStream paramOutputStream2, boolean paramBoolean2) {
     PrintStream printStream1, printStream2;
     if (paramOutputStream1 instanceof PrintStream) {
       
       printStream1 = (PrintStream)paramOutputStream1;
     } else {
       
       printStream1 = new PrintStream(paramOutputStream1);
     } 
     if (paramOutputStream2 instanceof PrintStream) {
       
       printStream2 = (PrintStream)paramOutputStream2;
     } else {
       
       printStream2 = new PrintStream(paramOutputStream2);
     } 
     
     int i = Thread.currentThread().getPriority();
     if (paramBoolean2) {
       
       printStream1.println("Starting batch conversion...");
       Thread.currentThread().setPriority(1);
     } 
     
     for (File file : paramFile.listFiles()) {
       
       if (file.isDirectory()) {
         
         if (paramBoolean1)
         {
           batchConvert(file, paramBoolean1, paramAnimationCodec1, paramAnimationCodec2, paramRestrictableIndexColorModel, printStream1, printStream2, false);
         
         }
       }
       else if (paramAnimationCodec1.getFileType().usesExtension(FileUtilities.getFileExtension(file))) {
 
         
         try {
           printStream1.print("Loading " + file + "... ");
           if (loadAnimation(file, paramRestrictableIndexColorModel, null)) {
             
             printStream1.println("Complete.");
             file = FileUtilities.replaceFileExtension(file, paramAnimationCodec2.getFileType().getExtensions()[0]);
             
             printStream1.print("Saving " + file + "... ");
             
             ProgressTracker progressTracker = new ProgressTracker();
             progressTracker.addListener(new ConsoleProgressListener(printStream1));
             
             saveAnimation(file, paramRestrictableIndexColorModel, progressTracker);
             printStream1.println("Complete.");
           } 
         } catch (IOException iOException) {
           
           printStream2.println("I/O error occurred: " + iOException.getMessage());
         } 
       } 
     } 
 
     
     if (paramBoolean2) {
       
       Thread.currentThread().setPriority(i);
       printStream1.println("Batch conversion complete.");
       printStream1.close();
       printStream2.close();
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void batchConvert(File paramFile, boolean paramBoolean1, AnimationCodec paramAnimationCodec, String paramString1, String paramString2, RestrictableIndexColorModel paramRestrictableIndexColorModel, OutputStream paramOutputStream1, OutputStream paramOutputStream2, boolean paramBoolean2) {
     PrintStream printStream1, printStream2;
     if (paramOutputStream1 instanceof PrintStream) {
       
       printStream1 = (PrintStream)paramOutputStream1;
     } else {
       
       printStream1 = new PrintStream(paramOutputStream1);
     } 
     if (paramOutputStream2 instanceof PrintStream) {
       
       printStream2 = (PrintStream)paramOutputStream2;
     } else {
       
       printStream2 = new PrintStream(paramOutputStream2);
     } 
     
     int i = Thread.currentThread().getPriority();
     if (paramBoolean2) {
       
       printStream1.println("Starting batch conversion...");
       Thread.currentThread().setPriority(1);
     } 
     
     for (File file : paramFile.listFiles()) {
       
       if (file.isDirectory()) {
         
         if (paramBoolean1)
         {
           batchConvert(file, paramBoolean1, paramAnimationCodec, paramString1, paramString2, paramRestrictableIndexColorModel, printStream1, printStream2, false);
         
         }
       }
       else if (paramAnimationCodec.getFileType().usesExtension(FileUtilities.getFileExtension(file))) {
         
         convertAnimationToImage(file, paramString1, paramString2, paramRestrictableIndexColorModel, printStream1, printStream2);
       } 
     } 
 
     
     if (paramBoolean2) {
       
       Thread.currentThread().setPriority(i);
       printStream1.println("Batch conversion complete.");
       printStream1.close();
       printStream2.close();
     } 
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void batchConvert(File paramFile, boolean paramBoolean1, FileFilter paramFileFilter, AnimationCodec paramAnimationCodec, String paramString, RestrictableIndexColorModel paramRestrictableIndexColorModel, OutputStream paramOutputStream1, OutputStream paramOutputStream2, boolean paramBoolean2) {
     PrintStream printStream1, printStream2;
     if (paramOutputStream1 instanceof PrintStream) {
       
       printStream1 = (PrintStream)paramOutputStream1;
     } else {
       
       printStream1 = new PrintStream(paramOutputStream1);
     } 
     if (paramOutputStream2 instanceof PrintStream) {
       
       printStream2 = (PrintStream)paramOutputStream2;
     } else {
       
       printStream2 = new PrintStream(paramOutputStream2);
     } 
     
     int i = Thread.currentThread().getPriority();
     if (paramBoolean2) {
       
       printStream1.println("Starting batch conversion...");
       Thread.currentThread().setPriority(1);
     } 
     
     HashSet<File> hashSet = new HashSet();
     for (File file : paramFile.listFiles()) {
       
       if (file.isDirectory()) {
         
         if (paramBoolean1)
         {
           batchConvert(file, paramBoolean1, paramFileFilter, paramAnimationCodec, paramString, paramRestrictableIndexColorModel, printStream1, printStream2, false);
 
         
         }
       
       }
       else if (!hashSet.contains(file) && paramFileFilter.accept(file)) {
         
         hashSet.addAll(convertImageToAnimation(file, paramAnimationCodec, paramString, paramRestrictableIndexColorModel, printStream1, printStream2));
       } 
     } 
 
     
     if (paramBoolean2) {
       
       Thread.currentThread().setPriority(i);
       printStream1.println("Batch conversion complete.");
       printStream1.close();
       printStream2.close();
     } 
   }
 
 
 
 
 
 
   
   public SixDiceCore copy() {
     SixDiceCore sixDiceCore = new SixDiceCore(this.m_codecs.<AnimationCodec>toArray(new AnimationCodec[0]));
     sixDiceCore.setVirtualTransparentColor(this.m_virtual_transparent);
     sixDiceCore.setTransparentToVirtualOnSave(this.m_transparent_to_virtal_on_save);
     return sixDiceCore;
   }
 
 
 
 
 
 
 
 
   
   static class ConsoleProgressListener
     implements ProgressTracker.Listener
   {
     protected PrintStream m_print_stream;
 
 
 
 
 
 
 
     
     protected int m_characters_written_net;
 
 
 
 
 
 
     
     protected int m_last_percentage;
 
 
 
 
 
 
 
     
     public ConsoleProgressListener(PrintStream param1PrintStream) {
       this.m_print_stream = param1PrintStream;
       this.m_characters_written_net = 0;
       this.m_last_percentage = -1;
     }
 
 
 
 
 
 
 
 
     
     public void changeObserved(ProgressTracker param1ProgressTracker, double param1Double) {
       int i = (int)param1ProgressTracker.getPercentCompleted();
       if (i != this.m_last_percentage) {
         
         while (this.m_characters_written_net > 0) {
           
           this.m_print_stream.print('\b');
           this.m_characters_written_net--;
         } 
         if (param1ProgressTracker.getPercentCompleted() < 100.0D) {
           
           String str = StringUtilities.padLeft(Integer.toString(i), ' ', 3) + "%";
           this.m_characters_written_net += str.length();
           this.m_print_stream.print(str);
         } 
         this.m_last_percentage = i;
       } 
     }
   }
 }