 package orioni.sixdice;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.util.List;
 import orioni.jz.awt.image.RestrictableIndexColorModel;
 import orioni.jz.common.exception.ParseException;
 import orioni.jz.io.FileType;
 import orioni.jz.io.files.FileUtilities;
 import orioni.jz.util.Pair;
 import orioni.jz.util.ProgressTracker;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class AnimationCodec
 {
   protected int m_transparent_index;
   
   public enum MessageType
   {
     WARNING,
 
 
     
     FATAL;
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public AnimationCodec() {
     this.m_transparent_index = 0;
   }
 
 
 
 
 
 
 
 
   
   public int getTransparentIndex() {
     return this.m_transparent_index;
   }
 
 
 
 
 
 
   
   public void setTransparentIndex(int paramInt) {
     this.m_transparent_index = paramInt;
   }
 
 
 
 
 
 
 
 
 
 
   
   public RestrictableIndexColorModel deriveCodecPalette(RestrictableIndexColorModel paramRestrictableIndexColorModel) {
     if (getTransparentIndex() != -1) {
       
       paramRestrictableIndexColorModel = paramRestrictableIndexColorModel.deriveWithTransparentInices(new int[] { getTransparentIndex() });
       paramRestrictableIndexColorModel.addRestrictedIndex(getTransparentIndex());
     } 
     return paramRestrictableIndexColorModel;
   }
 
 
 
 
 
 
   
   public String toString() {
     return getName();
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public Animation read(File paramFile, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) throws ParseException, IOException {
     if (paramProgressTracker == null) paramProgressTracker = new ProgressTracker(0.0D, 1.0D); 
     return decode(FileUtilities.getFileContents(paramFile), paramRestrictableIndexColorModel, paramProgressTracker);
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void write(File paramFile, Animation paramAnimation, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) throws IOException {
     if (paramProgressTracker == null) paramProgressTracker = new ProgressTracker(0.0D, 1.0D); 
     FileOutputStream fileOutputStream = null;
     boolean bool = false;
 
     
     try {
       try {
         fileOutputStream = new FileOutputStream(paramFile);
         bool = true;
         fileOutputStream.write(encode(paramAnimation, paramRestrictableIndexColorModel, paramProgressTracker));
       } finally {
         
         if (fileOutputStream != null) {
           
           try {
             
             fileOutputStream.close();
           } catch (IOException iOException) {}
         }
       }
     
     }
     catch (IOException iOException) {
       
       if (bool) paramFile.delete(); 
       throw iOException;
     } 
   }
   
   public abstract Animation decode(byte[] paramArrayOfbyte, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker) throws ParseException;
   
   public abstract byte[] encode(Animation paramAnimation, RestrictableIndexColorModel paramRestrictableIndexColorModel, ProgressTracker paramProgressTracker);
   
   public abstract List<Pair<String, MessageType>> check(Animation paramAnimation);
   
   public abstract FileType getFileType();
   
   public abstract boolean formatContainsPalette();
   
   public abstract String getName();
 }