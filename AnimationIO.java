 package orioni.sixdice;
 
 import java.awt.image.BufferedImage;
 import java.io.File;
 import java.io.FileFilter;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Set;
 import javax.imageio.ImageIO;
 import orioni.jz.awt.image.ImageUtilities;
 import orioni.jz.io.files.FileRegularExpressionFilter;
 import orioni.jz.io.files.FileUtilities;
 import orioni.jz.math.MathUtilities;
 import orioni.jz.util.Pair;
 import orioni.jz.util.configuration.Configuration;
 import orioni.jz.util.configuration.ConfigurationElement;
 import orioni.jz.util.configuration.IntegerConfigurationElement;
 import orioni.jz.util.strings.StringUtilities;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class AnimationIO
 {
   public static final IntegerConfigurationElement META_ELEMENT_X_OFFSET = new IntegerConfigurationElement("offset.x", Integer.valueOf(0));
 
 
 
   
   public static final IntegerConfigurationElement META_ELEMENT_Y_OFFSET = new IntegerConfigurationElement("offset.y", Integer.valueOf(0));
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public static final String METAFILE_SUFFIX = ".meta";
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public static Set<File> getImageSeriesParticipants(File paramFile, String paramString) {
     String[] arrayOfString = paramFile.getName().split(paramString);
     if (arrayOfString.length != 3) return new HashSet<File>(); 
     return new HashSet<File>(Arrays.asList(paramFile.getParentFile().listFiles((FileFilter)new FileRegularExpressionFilter(arrayOfString[0] + paramString + "\\d+" + paramString + "\\d+\\..*"))));
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public static File getSeriesGenerationFile(File paramFile, String paramString) {
     String[] arrayOfString = FileUtilities.removeFileExtension(paramFile).getName().split(paramString);
     if (arrayOfString.length != 3) return null; 
     if (!MathUtilities.isNumber(arrayOfString[1], 10) || !MathUtilities.isNumber(arrayOfString[2], 10)) return null; 
     return new File(paramFile.getParent() + File.separatorChar + arrayOfString[0] + '.' + FileUtilities.getFileExtension(paramFile));
   }
 
 
 
 
 
 
 
 
 
 
   
   public static Animation load(File paramFile, String paramString, PrintStream paramPrintStream) {
     try {
       String str1, str2;
       ArrayList<String> arrayList = new ArrayList();
       
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
       for (File file1 : paramFile.getParentFile().listFiles()) {
         
         String str = file1.getName();
         if (!FileUtilities.FILESYSTEM_CASE_SENSITIVE)
         {
           str = str.toLowerCase();
         }
         if (str.startsWith(str1) && str.endsWith(str2)) {
           
           str = str.substring(str1.length(), str.length() - str2.length());
           String[] arrayOfString = str.split(paramString);
           if (arrayOfString.length == 3 && "".equals(arrayOfString[0])) {
             int i1;
 
             
             int i2;
             
             try {
               i1 = Integer.parseInt(arrayOfString[1]);
               i2 = Integer.parseInt(arrayOfString[2]);
             } catch (NumberFormatException numberFormatException) {
 
               
               arrayList.add("Possibly incorrectly named file (could not parse direction or frame): " + file1);
             } 
             
             if (i1 < 0 || i2 < 0) {
 
               
               arrayList.add("Possible incorrectly named file (direction<0 or frame<0): " + file1);
             
             }
             else {
               
               BufferedImage bufferedImage = ImageIO.read(file1);
               if (bufferedImage != null) {
                 
                 hashMap.put(new Pair(Integer.valueOf(i1), Integer.valueOf(i2)), bufferedImage);
                 j = Math.max(j, i1);
                 k = Math.max(k, i2);
               } else {
                 
                 arrayList.add("Could not read file (no supported reader): " + file1);
               } 
             } 
           } 
         } 
       } 
       
       int m = 0;
       int n = 0;
       File file = new File(paramFile.getParent() + File.separatorChar + str1 + ".meta");
       if (file.exists() && file.isFile()) {
         
         Configuration configuration = new Configuration(new ConfigurationElement[] { (ConfigurationElement)META_ELEMENT_X_OFFSET, (ConfigurationElement)META_ELEMENT_Y_OFFSET });
         String str = configuration.load(file);
         if (str == null) {
           
           m = ((Integer)configuration.getValue((ConfigurationElement)META_ELEMENT_X_OFFSET)).intValue();
           n = ((Integer)configuration.getValue((ConfigurationElement)META_ELEMENT_Y_OFFSET)).intValue();
         } else {
           
           paramPrintStream.println("Could not load metadata file " + file + ": " + str);
         } 
       } 
 
       
       ArrayList<AnimationFrame> arrayList1 = new ArrayList();
       for (byte b = 0; b <= j; b++) {
         
         for (byte b1 = 0; b1 <= k; b1++)
         {
           arrayList1.add(new AnimationFrame((BufferedImage)hashMap.get(new Pair(Integer.valueOf(b), Integer.valueOf(b1))), m, n));
         }
       } 
 
 
       
       return new Animation(arrayList1, j + 1, k + 1, arrayList);
     } catch (IOException iOException) {
       
       paramPrintStream.println("<html>An I/O error occurred during import:<br>    " + iOException + "<br>The import failed.");
       return null;
     } 
   }
 
 
 
 
 
 
 
 
 
   
   public static void save(File paramFile, String paramString1, String paramString2, Animation paramAnimation, PrintStream paramPrintStream) {
     String str2, str3;
     int i = (int)Math.ceil(Math.log10(Math.max(paramAnimation.getDirectionCount(), paramAnimation.getFrameCount())));
     
     String str1 = paramFile.getName();
 
     
     String str4 = paramFile.getParent() + File.separatorChar;
     if (str1.lastIndexOf('.') != -1) {
       
       str2 = str1.substring(0, str1.lastIndexOf('.'));
       str3 = str1.substring(str1.lastIndexOf('.'));
     } else {
       
       str2 = str1;
       str3 = "";
     } 
     
     for (byte b = 0; b < paramAnimation.getDirectionCount(); b++) {
       
       for (byte b1 = 0; b1 < paramAnimation.getFrameCount(); b1++) {
 
         
         try {
           if (!ImageUtilities.writeImage(paramAnimation.getPaddedImage(b, b1), paramString2, new File(str4 + str2 + paramString1 + StringUtilities.padLeft(String.valueOf(b), '0', i) + paramString1 + StringUtilities.padLeft(String.valueOf(b1), '0', i) + str3)))
           {
 
 
 
 
 
 
             
             paramPrintStream.println("Direction " + b + ", Frame " + b1 + ": Image writing failed: Image format not supported.");
           
           }
         }
         catch (IOException iOException) {
           
           paramPrintStream.println("Direction " + b + ", Frame " + b1 + ": Image writing failed: " + iOException.getMessage());
         } 
       } 
     } 
     Configuration configuration = new Configuration(new ConfigurationElement[] { (ConfigurationElement)META_ELEMENT_X_OFFSET, (ConfigurationElement)META_ELEMENT_Y_OFFSET });
     configuration.setValue((ConfigurationElement)META_ELEMENT_X_OFFSET, Integer.valueOf(paramAnimation.getFirstXIndex()));
     configuration.setValue((ConfigurationElement)META_ELEMENT_Y_OFFSET, Integer.valueOf(paramAnimation.getFirstYIndex()));
     File file = new File(str4 + str2 + ".meta");
     String str5 = configuration.save(file);
     if (str5 != null)
     {
       paramPrintStream.println("Error writing metadata file " + file + ": " + str5);
     }
   }
 }