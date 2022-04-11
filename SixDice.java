 package orioni.sixdice;
 
 import java.awt.Color;
 import java.awt.HeadlessException;
 import java.io.File;
 import java.io.FileFilter;
 import javax.imageio.ImageIO;
 import orioni.jz.awt.image.RestrictableIndexColorModel;
 import orioni.jz.common.exception.ParseException;
 import orioni.jz.io.files.FileExtensionFilter;
 import orioni.jz.io.files.FileUtilities;
 import orioni.jz.util.Pair;
 import orioni.jz.util.programparameters.ProgramParameter;
 import orioni.jz.util.programparameters.ProgramParameterInstance;
 import orioni.jz.util.programparameters.ProgramParameterManager;
 import orioni.jz.util.strings.BoundedIntegerInterpreter;
 import orioni.jz.util.strings.ColorInterpreter;
 import orioni.jz.util.strings.StringInterpreter;
 import orioni.jz.util.strings.StringUtilities;
 import orioni.jz.util.strings.ValueInterpreter;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class SixDice
 {
   public static final String VERSION_STRING = "0.63";
   
   public static void showHelpAndBail(String... paramVarArgs) {
     for (String str : paramVarArgs)
     {
       System.err.println(str);
     }
     if (paramVarArgs.length > 0) System.err.println();
 
 
     
     System.err.println("SixDice v0.63");
     System.err.println("Usage: java -jar SixDice.jar [options] <file> [file] [file] ...");
     System.err.println();
     System.err.println("(Options marked with a '*' must be set.)");
     System.err.println("Options:");
     System.err.println("    -h, --help                Displays this help screen.");
     System.err.println("    -i, --import              Specifies that the program should convert image");
     System.err.println("                              files to animation files.  Setting the format");
     System.err.println("                              acts as a file extension filter.  Either -i or -e");
     System.err.println("                              must be used.");
     System.err.println("    -e, --export              Specifies that the program should convert");
     System.err.println("                              animation files to image files.  Requires that");
     System.err.println("                              the format be set.  Either -i or -e must be used.");
     System.err.println("*   -p, --palette             Specifies the palette to be used by the");
     System.err.println("                              animation.  For a list of palettes, try \"-p ?\".");
     System.err.println("    -f, --format              Specifies the format to be used by the converter.");
     System.err.println("                              For a list of formats supported by your JRE, try");
     System.err.println("                              \"-f ?\".");
     System.err.println("    -r, --recursive           Specifies that any directories should be");
     System.err.println("                              processed recursively.  Default is not to process");
     System.err.println("                              recursively.");
     System.err.println("    -s, --separator-string    Specifies the separator string used to separate");
     System.err.println("                              direction and frame number from the filename in");
     System.err.println("                              multi-frame export image sets.  By default, this");
     System.err.println("                              is \"__\".");
     System.err.println("*   -c, --codec               Specifies the codec to be used.  For a list of ");
     System.err.println("                              animation codecs, try \"-c ?\".");
     System.err.println("    -v, --virtual-clear-color Specifies the color in image files to be set as ");
     System.err.println("                              transparent.  For exports, this is the color that");
     System.err.println("                              transparent pixels will become.");
     System.err.println("    -x, --transparent-index   The index in the codec which should be treated as");
     System.err.println("                              transparent, or -1 for no transparent index.  By");
     System.err.println("                              default, this value is zero.");
     System.exit(1);
   }
 
 
 
 
 
 
 
 
   
   public static void main(String[] paramArrayOfString) {
     if (paramArrayOfString.length == 0) {
 
       
       try {
         SixDiceFrame sixDiceFrame = new SixDiceFrame();
         sixDiceFrame.setLocationRelativeTo(null);
         sixDiceFrame.setVisible(true);
       } catch (HeadlessException headlessException) {
         
         showHelpAndBail(new String[0]);
       } 
     } else {
       DCCCodec dCCCodec;
       
       ProgramParameterManager programParameterManager = new ProgramParameterManager();
       
       programParameterManager.addParameter(new ProgramParameter(new String[] { "h", "?", "help" }, true));
       
       programParameterManager.addParameter(new ProgramParameter("e", "export", false));
       
       programParameterManager.addParameter(new ProgramParameter("i", "import", false));
       
       programParameterManager.addParameter(new ProgramParameter("r", "recursive", false));
       
       programParameterManager.addParameter(new ProgramParameter("v", "virtual-clear-color", false, (ValueInterpreter)ColorInterpreter.SINGLETON));
       
       programParameterManager.addParameter(new ProgramParameter("p", "palette", false, (ValueInterpreter)StringInterpreter.SINGLETON));
       
       programParameterManager.addParameter(new ProgramParameter("f", "format", false, (ValueInterpreter)StringInterpreter.SINGLETON));
       
       programParameterManager.addParameter(new ProgramParameter("s", "separator-string", false, (ValueInterpreter)StringInterpreter.SINGLETON));
       
       programParameterManager.addParameter(new ProgramParameter("c", "codec", false, (ValueInterpreter)StringInterpreter.SINGLETON));
       
       programParameterManager.addParameter(new ProgramParameter("x", "transparent-index", false, (ValueInterpreter)new BoundedIntegerInterpreter(-1, 255)));
 
 
       
       Pair pair = null;
       
       try {
         pair = programParameterManager.parse(paramArrayOfString);
       } catch (ParseException parseException) {
         
         showHelpAndBail(new String[0]);
       } 
 
 
 
       
       byte b = 2;
       
       Color color = null;
       boolean bool1 = false;
       String str1 = null;
       String str2 = null;
       String str3 = "__";
       DC6Codec dC6Codec = null;
       int i = 0;
       
       for (ProgramParameterInstance programParameterInstance : (ProgramParameterInstance[])pair.getFirst()) {
         
         if ("e".equals(programParameterInstance.getString()) || "i".equals(programParameterInstance.getString())) {
           
           if (b != 2) {
             
             showHelpAndBail(new String[] { "Only one operational mode (import or export) can be chosen." });
           
           }
           else if ("i".equals(programParameterInstance.getString())) {
             
             b = 1;
           } else {
             
             b = 0;
           }
         
         } else if ("r".equals(programParameterInstance.getString())) {
           
           bool1 = true;
         } else if ("v".equals(programParameterInstance.getString())) {
           
           color = (Color)programParameterInstance.getSubparameters()[0];
         } else if ("p".equals(programParameterInstance.getString())) {
           
           str1 = (String)programParameterInstance.getSubparameters()[0];
         } else if ("f".equals(programParameterInstance.getString())) {
           
           str2 = (String)programParameterInstance.getSubparameters()[0];
         } else if ("s".equals(programParameterInstance.getString())) {
           
           str3 = (String)programParameterInstance.getSubparameters()[0];
         } else if ("c".equals(programParameterInstance.getString())) {
           
           String str = (String)programParameterInstance.getSubparameters()[0];
           if ("dc6".equalsIgnoreCase(str)) {
             
             dC6Codec = new DC6Codec();
           } else if ("dcc".equalsIgnoreCase(str)) {
             
             dCCCodec = new DCCCodec();
           } else {
             
             showHelpAndBail(new String[] { "Invalid codec (\"" + str + "\").\nCodec must be one of {dc6, dcc}." });
           } 
         } else if ("x".equals(programParameterInstance.getString())) {
           
           i = ((Integer)programParameterInstance.getSubparameters()[0]).intValue();
         } 
       } 
       
       if (str1 == null)
       {
         showHelpAndBail(new String[] { "A palette must be specified.  Use -p." });
       }
       RestrictableIndexColorModel restrictableIndexColorModel = Diablo2DefaultPalettes.PALETTE_MAP.get(str1);
       if (restrictableIndexColorModel == null) {
         
         String[] arrayOfString1 = (String[])Diablo2DefaultPalettes.PALETTE_MAP.keySet().toArray((Object[])StringUtilities.EMPTY_STRING_ARRAY);
         
         String[] arrayOfString2 = new String[arrayOfString1.length + 1];
         arrayOfString2[0] = "The palette \"" + restrictableIndexColorModel + "\" does not exist.  It must be one of the following:";
         for (byte b1 = 1; b1 < arrayOfString2.length; b1++)
         {
           arrayOfString2[b1] = "        \"" + arrayOfString1[b1 - 1] + "\"";
         }
         showHelpAndBail(arrayOfString2);
       } 
       
       if (b == 0 && str2 == null)
       {
         showHelpAndBail(new String[] { "When converting animations to images, an image format must be specified." });
       }
       if (dCCCodec == null) {
         
         showHelpAndBail(new String[] { "A codec must be specified." });
       } else {
         
         dCCCodec.setTransparentIndex(i);
       } 
       if (!ImageIO.getImageReadersByFormatName(str2).hasNext())
       {
         showHelpAndBail(new String[] { "The format \"" + str2 + "\" is not valid.  Format must be one of the following:" + "\n        " + StringUtilities.createDelimitedList("\n        ", ImageIO.getReaderFormatNames()) });
       }
 
 
 
 
       
       if (((String[])pair.getSecond()).length == 0)
       {
         showHelpAndBail(new String[] { "No files specified." });
       }
       
       boolean bool2 = false;
       
       SixDiceCore sixDiceCore = new SixDiceCore(new AnimationCodec[] { dCCCodec });
       sixDiceCore.setVirtualTransparentColor(color);
       sixDiceCore.setTransparentToVirtualOnSave((color != null));
       
       for (String str : (String[])pair.getSecond()) {
         
         File file = new File(str);
         if (file.exists()) {
           
           if (file.isDirectory())
           {
             bool2 = true;
             if (b == 0) {
               
               sixDiceCore.batchConvert(file, bool1, dCCCodec, str2, str3, restrictableIndexColorModel, System.out, System.err, true);
             } else {
               
               sixDiceCore.batchConvert(file, bool1, (FileFilter)new FileExtensionFilter(new String[] { '.' + str2 }, ), dCCCodec, str3, restrictableIndexColorModel, System.out, System.err, true);
             
             }
 
           
           }
           else if (b == 0)
           {
             sixDiceCore.convertAnimationToImage(FileUtilities.replaceFileExtension(file, '.' + str2), str2, str3, restrictableIndexColorModel, System.out, System.err);
           
           }
           else
           {
             sixDiceCore.convertImageToAnimation(file, dCCCodec, str3, restrictableIndexColorModel, System.out, System.err);
           }
         
         } else {
           
           System.err.println("File " + file + " does not exist.");
         } 
       } 
       
       if (!bool2 && bool1)
       {
         System.err.println("Warning: recursive was specified but none of the files provided were directories.");
       }
     } 
   }
 }