 package orioni.sixdice;
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Container;
 import java.awt.Dialog;
 import java.awt.Dimension;
 import java.awt.Font;
 import java.awt.Frame;
 import java.awt.Graphics;
 import java.awt.Image;
 import java.awt.LayoutManager;
 import java.awt.datatransfer.DataFlavor;
 import java.awt.dnd.DropTargetDropEvent;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.ComponentAdapter;
 import java.awt.event.ComponentEvent;
 import java.awt.event.MouseListener;
 import java.awt.image.BufferedImage;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.util.List;
 import javax.swing.Action;
 import javax.swing.ImageIcon;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JComponent;
 import javax.swing.JDialog;
 import javax.swing.JFileChooser;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JScrollPane;
 import javax.swing.JSlider;
 import javax.swing.JTextArea;
 import javax.swing.JTextField;
 import javax.swing.KeyStroke;
 import javax.swing.border.EtchedBorder;
 import javax.swing.border.TitledBorder;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 import javax.swing.event.DocumentEvent;
 import javax.swing.event.DocumentListener;
 import javax.swing.filechooser.FileFilter;
 import orioni.jz.awt.InformalGridLayout;
 import orioni.jz.awt.SpongyLayout;
 import orioni.jz.awt.image.ImageUtilities;
 import orioni.jz.awt.image.RestrictableIndexColorModel;
 import orioni.jz.awt.swing.ApprovalButtonPanel;
 import orioni.jz.awt.swing.ColorSelectorButton;
 import orioni.jz.awt.swing.DirectoryPathField;
 import orioni.jz.awt.swing.InterpretedTextField;
 import orioni.jz.awt.swing.JZSwingUtilities;
 import orioni.jz.awt.swing.ProgressBarTracker;
 import orioni.jz.awt.swing.SwingFileFilterWrapper;
 import orioni.jz.awt.swing.action.ExecuteMethodAction;
 import orioni.jz.awt.swing.convenience.ComponentConstructorPanel;
 import orioni.jz.awt.swing.convenience.ContentConstructorTabbedPane;
 import orioni.jz.awt.swing.convenience.SelfReturningJLabel;
 import orioni.jz.awt.swing.dialog.ScrollableTextDialog;
 import orioni.jz.awt.swing.dialog.SwingErrorStreamDialog;
 import orioni.jz.awt.swing.dialog.WaitingDialog;
 import orioni.jz.awt.swing.icon.ColoredBlockIcon;
 import orioni.jz.awt.swing.list.IntegerRangeListModel;
 import orioni.jz.awt.swing.list.SortedListModel;
 import orioni.jz.awt.swing.listener.WhatIsThisMouseListener;
 import orioni.jz.awt.swing.popup.PopupOption;
 import orioni.jz.io.FileType;
 import orioni.jz.io.NullOutputStream;
 import orioni.jz.io.StreamPipe;
 import orioni.jz.io.files.FileExtensionFilter;
 import orioni.jz.io.files.FileUtilities;
 import orioni.jz.math.MathUtilities;
 import orioni.jz.util.Pair;
 import orioni.jz.util.configuration.BooleanConfigurationElement;
 import orioni.jz.util.configuration.BoundedIntegerConfigurationElement;
 import orioni.jz.util.configuration.Configuration;
 import orioni.jz.util.configuration.ConfigurationElement;
 import orioni.jz.util.configuration.StringConfigurationElement;
 import orioni.jz.util.strings.ValueInterpreter;
 public class SixDiceFrame extends AbstractEditorFrame {
   protected static final File CONFIGURATION_FILE = new File(System.getProperty("user.home") + File.separatorChar + ".sixdice.cfg");
   public static final String HELP_DEFAULT_PALETTE = "These animation files do not carry information about specific colors in them, but a general description \"color 1 goes here, color 2 goes there\"; this is called an \"indexed color model.\"  Therefore, when an animation is loaded, it must be associated with a palette before it can be displayed.  The provided palettes are from Diablo II: Lord of Destruction v1.10 and animation files used by that program will be displayed using one of these palettes.  To determine which palette your program should use, consult the program's documentation or other informational resources.  For Diablo II: LoD, the default \"Act 0\" palette is usually advisable.";
   protected static final BooleanConfigurationElement CONFIG_ELEMENT_UI_CLEAR_ON_DND_IMPORTS = new BooleanConfigurationElement("ui.dnd.load.clear", Boolean.valueOf(true));
   protected static final ConfigurationElement<Color> CONFIG_ELEMENT_UI_IMAGE_BACKGROUND = new ConfigurationElement("ui.image.background", (ValueInterpreter)ColorInterpreter.SINGLETON, new Color(96, 96, 96));
   protected static final ConfigurationElement<Color> CONFIG_ELEMENT_UI_IMAGE_UNDERLAY = new ConfigurationElement("ui.image.underlay", (ValueInterpreter)ColorInterpreter.SINGLETON, Color.BLACK);
   protected static final StringConfigurationElement CONFIG_ELEMENT_UI_LOOK_AND_FEEL_CLASSNAME = new StringConfigurationElement("ui.lnf.classname", UIManager.getLookAndFeel().getClass().getName());
   protected static final BoundedIntegerConfigurationElement CONFIG_ELEMENT_UI_LAYOUT = new BoundedIntegerConfigurationElement("ui.layout", 0, 0, 3);
   protected static final BooleanConfigurationElement CONFIG_ELEMENT_UI_IMAGE_RENDERED_IN_PALETTE = new BooleanConfigurationElement("ui.image.paletterendered", Boolean.valueOf(true));
   protected static final BooleanConfigurationElement CONFIG_ELEMENT_UI_SAVED_FILES_RELOADED = new BooleanConfigurationElement("ui.loadonsave", Boolean.valueOf(false));
   protected static final StringConfigurationElement CONFIG_ELEMENT_SELECTED_PALETTE = new StringConfigurationElement("palette.selected", "Act 0");
   protected static final StringConfigurationElement CONFIG_ELEMENT_PORT_FILTER = new StringConfigurationElement("port.filter", null);
   protected static final StringConfigurationElement CONFIG_ELEMENT_PORT_SEPARATOR = new StringConfigurationElement("port.separator", "__");
   protected static final BooleanConfigurationElement CONFIG_ELEMENT_DISPLAY_PALETTE_WARNING = new BooleanConfigurationElement("ui.image.palette.warning", Boolean.valueOf(true));
   protected static final StringConfigurationElement CONFIG_ELEMENT_BATCH_CONVERSION_PATH = new StringConfigurationElement("batch.path", System.getProperty("user.home"));
   protected static final BooleanConfigurationElement CONFIG_ELEMENT_BATCH_CONVERSION_RECURSIVE = new BooleanConfigurationElement("batch.recursive", Boolean.valueOf(false));
   protected static final StringConfigurationElement CONFIG_ELEMENT_BATCH_CONVERSION_PALETTE = new StringConfigurationElement("batch.palette", "Act 0");
   protected static final IntegerConfigurationElement CONFIG_ELEMENT_BATCH_CONVERSION_SOURCE_INDEX = new IntegerConfigurationElement("batch.sourceindex", Integer.valueOf(0));
   protected static final IntegerConfigurationElement CONFIG_ELEMENT_BATCH_CONVERSION_TARGET_INDEX = new IntegerConfigurationElement("batch.targetindex", Integer.valueOf(0));
   protected static final ConfigurationElement<Color> CONFIG_ELEMENT_IMPORT_VIRTUAL_CLEAR_COLOR = new ConfigurationElement("import.color.virtualclear", (ValueInterpreter)ColorInterpreter.SINGLETON, Color.BLACK);
   protected static final BooleanConfigurationElement CONFIG_ELEMENT_EXPORT_CLEAR_TO_VIRTUAL = new BooleanConfigurationElement("export.color.virtualclear.translate", Boolean.valueOf(true));
   protected static final BooleanConfigurationElement CONFIG_ELEMENT_IMPORT_INSERTS = new BooleanConfigurationElement("import.insert", Boolean.valueOf(false));
   protected static final BooleanConfigurationElement CONFIG_ELEMENT_FRAME_SPLIT_INSERTS = new BooleanConfigurationElement("frame.split.insert", Boolean.valueOf(false));
   protected static final BoundedIntegerConfigurationElement CONFIG_ELEMENT_CODEC_DC6_CLEAR_INDEX = new BoundedIntegerConfigurationElement("codec.dc6.index.clear", 0, 0, 255);
   protected static final BoundedIntegerConfigurationElement CONFIG_ELEMENT_CODEC_DCC_CLEAR_INDEX = new BoundedIntegerConfigurationElement("codec.dc6.index.clear", 0, 0, 255);
   public static final String SPLASH_FILE_NAME = "SixDice-Splash-v" + ("0.63".endsWith("b") ? "0.63".substring(0, "0.63".length() - 1) : "0.63") + ".png";
   public static final int ZOOM_SLIDER_MINIMUM_PERCENT = 1;
   public static final int ZOOM_SLIDER_MAXIMUM_PERCENT = 800;
   protected LookAndFeelDialog m_lafd;
   protected CenteredImageComponent m_image_component;
   protected JScrollPane m_image_scroll_pane;
   protected JList m_frame_list;
   protected JList m_direction_list;
   protected JButton m_direction_add_button;
   protected JButton m_direction_insert_button;
   protected JButton m_direction_remove_button;
   protected JButton m_frame_add_button;
   protected JButton m_frame_insert_button;
   protected JButton m_frame_remove_button;
   protected JLabel m_label_width;
   protected JLabel m_label_height;
   protected JTextField m_offset_x;
   protected JTextField m_offset_y;
   protected JComboBox m_palette_box;
   protected JCheckBox m_palette_rendering;
   protected JSlider m_zoom_slider;
   protected JTextField m_zoom_field;
   protected BufferedImage m_splash_image;
   protected SixDiceCore m_core;
   protected OptionallyPaintedPanel m_warnings_panel;
   protected DC6Codec m_dc6_codec;
   protected AnimationCodec[] m_codecs;
   protected DelayedDialogDisplayer m_waiting_dialog_displayer;
   protected JProgressBar m_waiting_dialog_progress_bar;
   public SixDiceFrame() {
     super(CONFIGURATION_FILE, "SixDice v0.63");
     final SixDiceFrame scoped_this = this;
     PCXReaderSpi.register();
     PCXWriterSpi.register();
     this.m_configuration.addElements(new ConfigurationElement[] { (ConfigurationElement)CONFIG_ELEMENT_UI_CLEAR_ON_DND_IMPORTS, CONFIG_ELEMENT_UI_IMAGE_BACKGROUND, CONFIG_ELEMENT_UI_IMAGE_UNDERLAY, (ConfigurationElement)CONFIG_ELEMENT_UI_LOOK_AND_FEEL_CLASSNAME, (ConfigurationElement)CONFIG_ELEMENT_UI_LAYOUT, (ConfigurationElement)CONFIG_ELEMENT_UI_IMAGE_RENDERED_IN_PALETTE, (ConfigurationElement)CONFIG_ELEMENT_UI_SAVED_FILES_RELOADED, (ConfigurationElement)CONFIG_ELEMENT_SELECTED_PALETTE, (ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER, (ConfigurationElement)CONFIG_ELEMENT_PORT_SEPARATOR, (ConfigurationElement)CONFIG_ELEMENT_DISPLAY_PALETTE_WARNING, (ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_PATH, (ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_RECURSIVE, (ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_PALETTE, (ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_SOURCE_INDEX, (ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_TARGET_INDEX, CONFIG_ELEMENT_IMPORT_VIRTUAL_CLEAR_COLOR, (ConfigurationElement)CONFIG_ELEMENT_EXPORT_CLEAR_TO_VIRTUAL, (ConfigurationElement)CONFIG_ELEMENT_IMPORT_INSERTS, (ConfigurationElement)CONFIG_ELEMENT_FRAME_SPLIT_INSERTS, (ConfigurationElement)CONFIG_ELEMENT_CODEC_DC6_CLEAR_INDEX, (ConfigurationElement)CONFIG_ELEMENT_CODEC_DCC_CLEAR_INDEX });
     final SwingErrorStreamDialog sesd = new SwingErrorStreamDialog(null, "SixDice Error Stream Report");
     swingErrorStreamDialog.setLocation(0, 0);
     final WaitingDialog wd = new WaitingDialog((Frame)this);
     this.m_waiting_dialog_progress_bar = waitingDialog.getProgressBar();
     this.m_waiting_dialog_displayer = new DelayedDialogDisplayer((Dialog)waitingDialog)
       {
         public synchronized void setVisible(boolean param1Boolean)
         {
           this.m_dialog.setLocationRelativeTo((Component)scoped_this);
           super.setVisible(param1Boolean);
         }
       };
     addWindowListener(new WindowAdapter()
         {
           public void windowClosed(WindowEvent param1WindowEvent)
           {
             sesd.dispose();
             wd.dispose();
           }
         });
     this.m_dc6_codec = new DC6Codec();
     this.m_codecs = new AnimationCodec[] { this.m_dc6_codec, DCCCodec.SINGLETON };
     this.m_core = new SixDiceCore(this.m_codecs);
     this.m_lafd = new LookAndFeelDialog((Frame)this, new Component[] { (Component)this });
     this.m_lafd.setSelection((String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_UI_LOOK_AND_FEEL_CLASSNAME));
     this.m_lafd.apply();
     this.m_lafd.apply();
     int i = this.m_menu_bar.getExtendedMenu("File").getIndexOfSeperator(0);
     this.m_menu_bar.insertSeparator("File", i);
     i++;
     this.m_menu_bar.add("File", "Import", i, (Action)new ExecuteMethodAction(this, "performMenuImport"), KeyStroke.getKeyStroke(73, 128));
     i++;
     this.m_menu_bar.add("File", "Import Series", i, (Action)new ExecuteMethodAction(this, "performMenuImportSeries"), KeyStroke.getKeyStroke(77, 128));
     i++;
     this.m_menu_bar.add("File", "Export", i, (Action)new ExecuteMethodAction(this, "performMenuExport"), KeyStroke.getKeyStroke(69, 128));
     i++;
     this.m_menu_bar.add("File", "Export Series", i, (Action)new ExecuteMethodAction(this, "performMenuExportSeries"), KeyStroke.getKeyStroke(88, 128));
     this.m_menu_bar.add("Image", "Batch Convert", (Action)new ExecuteMethodAction(this, "performMenuBatchConvert", true), KeyStroke.getKeyStroke(66, 128));
     this.m_menu_bar.addSeparator("Image");
     this.m_menu_bar.add("Image", "Trim Borders", (Action)new ExecuteMethodAction(this, "performMenuTrimBorders"), KeyStroke.getKeyStroke(84, 128));
     this.m_menu_bar.add("Image", "Adjust Offsets", (Action)new ExecuteMethodAction(this, "performMenuAdjustOffset"), KeyStroke.getKeyStroke(74, 128));
     this.m_menu_bar.add("Image", "Center Offset", (Action)new ExecuteMethodAction(this, "performMenuCenterOffset"), KeyStroke.getKeyStroke(67, 128));
     this.m_menu_bar.addSeparator("Image");
     this.m_menu_bar.add("Image", "Split Frame", (Action)new ExecuteMethodAction(this, "performMenuSplitFrame"), KeyStroke.getKeyStroke(80, 128));
     this.m_menu_bar.add("Image", "Join Frames", (Action)new ExecuteMethodAction(this, "performMenuJoinFrames"), KeyStroke.getKeyStroke(74, 128));
     this.m_menu_bar.addSeparator("Image");
     this.m_menu_bar.add("Image", "Color Change", (Action)new ExecuteMethodAction(this, "performMenuColorChange"), KeyStroke.getKeyStroke(76, 128));
     this.m_menu_bar.add("Image", "Resize Images", (Action)new ExecuteMethodAction(this, "performMenuResize"), KeyStroke.getKeyStroke(82, 128));
     this.m_menu_bar.add("Options", "Look & Feel", (Action)new ExecuteMethodAction(this, "performMenuLookAndFeel"));
     this.m_menu_bar.add("Options", "Preferences", (Action)new ExecuteMethodAction(this, "performMenuPreferences"));
     this.m_menu_bar.add("Help", "Quick Tips").addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             (new Thread("Quick Tips Display")
               {
                 public void run()
                 {
                   JOptionPane.showMessageDialog((Component)scoped_this, "<html>The following pieces of information may be useful when learning how to use SixDice:<ul><li>Right-clicking on most interface components (buttons, lists, checkboxes, etc.) will provide a \"What Is This?\"<br>button that you can use to learn more about that interface component's function.</li><li>SixDice can be run as a command-line application.  Try running SixDice with the parameter \"-h\" for <br>more information (on most systems, \"java -jar SixDice.jar -h\").</li></ul>Thanks for trying SixDice.&nbsp;  :)  Happy modding!", "Quick Tips", 1);
                 }
               }).start();
           }
         });
     this.m_menu_bar.add("Help", "About").addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             (new Thread("Vanity Display")
               {
                 public void run()
                 {
                   JOptionPane.showMessageDialog((Component)scoped_this, "<html><center><font face=\"Sans Serif\"><b>SixDice v0.63<br>by Zachary Palmer<br>zep01@bahj.com<br><br>(c)2005, All Rights Reserved", "About SixDice", 1);
                 }
               }).start();
           }
         });
     this.m_menu_bar.autoAssignAllMnemonics();
     this.m_splash_image = null;
     try {
       URL uRL = getClass().getResource("/media/" + SPLASH_FILE_NAME);
       if (uRL != null) this.m_splash_image = ImageIO.read(uRL); 
     } catch (IOException iOException) {}
     if (this.m_splash_image == null) {
       try {
         this.m_splash_image = ImageIO.read(new File("." + File.separatorChar + "media" + File.separatorChar + SPLASH_FILE_NAME));
       }
       catch (IOException iOException) {}
     }
     if ("0.63".endsWith("b")) {
       if (this.m_splash_image == null)
       {
         this.m_splash_image = new BufferedImage(256, 256, 1);
       }
       this.m_splash_image = ImageUtilities.adjustHSB(this.m_splash_image, 0.0D, -1.0D, -0.5D);
       Graphics graphics = this.m_splash_image.getGraphics();
       graphics.setColor(Color.WHITE);
       graphics.translate(this.m_splash_image.getWidth() / 2, this.m_splash_image.getHeight() / 2);
       if (graphics instanceof Graphics2D)
       {
         ((Graphics2D)graphics).rotate(0.5235987755982988D);
       }
       JLabel jLabel = new JLabel("BETA");
       jLabel.setForeground(Color.WHITE);
       jLabel.setFont(new Font("Serif", 3, 80));
       jLabel.setSize(jLabel.getPreferredSize());
       graphics.translate(-jLabel.getWidth() / 2, -jLabel.getHeight() / 2);
       jLabel.paint(graphics);
     } 
     this.m_image_component = new CenteredImageComponent(this.m_splash_image, (Color)this.m_configuration.getValue(CONFIG_ELEMENT_UI_IMAGE_BACKGROUND))
       {
         public void setImage(Image param1Image)
         {
           if (SixDiceFrame.this.m_zoom_slider == null || SixDiceFrame.this.m_zoom_slider.getValue() == 100) {
             super.setImage(param1Image);
           } else {
             double d = SixDiceFrame.this.m_zoom_slider.getValue() / 100.0D;
             super.setImage(param1Image.getScaledInstance(Math.max(1, (int)(param1Image.getWidth(null) * d)), Math.max(1, (int)(param1Image.getHeight(null) * d)), 8));
             doLayout();
           } 
         }
       };
     this.m_image_component.setPreferredSize(new Dimension(0, 0));
     DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
     defaultListCellRenderer.setAlignmentX(1.0F);
     this.m_direction_list = new JList((ListModel<?>)new IntegerRangeListModel(0, 0));
     this.m_direction_list.setCellRenderer(defaultListCellRenderer);
     this.m_direction_list.setSelectionMode(0);
     this.m_frame_list = new JList((ListModel<?>)new IntegerRangeListModel(0, 0));
     this.m_frame_list.setCellRenderer(defaultListCellRenderer);
     this.m_frame_list.setSelectionMode(1);
     this.m_label_width = new JLabel("0");
     this.m_label_height = new JLabel("0");
     this.m_label_width.setAlignmentX(1.0F);
     this.m_label_height.setAlignmentX(1.0F);
     this.m_offset_x = new JTextField("0", 3);
     this.m_offset_y = new JTextField("0", 3);
     this.m_offset_x.setHorizontalAlignment(4);
     this.m_offset_y.setHorizontalAlignment(4);
     SortedListModel sortedListModel = new SortedListModel(Diablo2DefaultPalettes.PALETTE_MAP.keySet().toArray(), true);
     this.m_palette_box = new JComboBox((ComboBoxModel<?>)sortedListModel);
     this.m_palette_rendering = new JCheckBox(" Use Palette Rendering?", ((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_UI_IMAGE_RENDERED_IN_PALETTE)).booleanValue());
     this.m_palette_rendering.setHorizontalAlignment(11);
     this.m_zoom_slider = new JSlider(0, 1, 800, 100);
     this.m_zoom_field = new JTextField("100", 4);
     this.m_direction_add_button = new JButton("Add");
     this.m_direction_insert_button = new JButton("Ins");
     this.m_direction_remove_button = new JButton("Rem");
     this.m_frame_add_button = new JButton("Add");
     this.m_frame_insert_button = new JButton("Ins");
     this.m_frame_remove_button = new JButton("Rem");
     final JButton show_warnings_button = new JButton("Show Warnings");
     this.m_warnings_panel = new OptionallyPaintedPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL, 2, 2, false, true))
       {
         public void setPainted(boolean param1Boolean)
         {
           show_warnings_button.setEnabled(param1Boolean);
           super.setPainted(param1Boolean);
         }
       };
     jButton.setEnabled(false);
     this.m_warnings_panel.add((Component)(new SelfReturningJLabel("There are warnings.")).setForegroundAndReturn(new Color(192, 96, 0)).setFontAndReturn(new Font("Dialog", 1, 12)));
     this.m_warnings_panel.add((Component)new SpacingComponent(10, 10));
     this.m_warnings_panel.add(jButton);
     this.m_warnings_panel.setPainted(false);
     this.m_image_component.addMouseListener((MouseListener)new WhatIsThisMouseListener("Image Preview", "This portion of the SixDice screen allows you to see one frame of the animation you are currently using.  To determine which frame, find the \"Direction\" and \"Frame\" lists."));
     this.m_direction_list.addMouseListener((MouseListener)new WhatIsThisMouseListener("Direction List", "An animation file is composed of a number of directions (presumably in which some object moves) and a number of frames per direction.  The highlighted item in this list determines which direction you are currently viewing.  In combination with the frame list, this allows SixDice to pick one image out of the animation file to show you, along with its offset information."));
     this.m_frame_list.addMouseListener((MouseListener)new PopupMenuMouseListener()
         {
           public JPopupMenu getPopupMenu(MouseEvent param1MouseEvent)
           {
             return (JPopupMenu)new PopupOptionMenu(Integer.valueOf(SixDiceFrame.this.m_frame_list.getSelectedIndex()), new PopupOption[][] { { new PopupOption("What Is This?")
                     {
                       public void execute(Object param2Object)
                       {
                         ScrollableTextDialog.displayMessage((Frame)scoped_this, "What Is This?", "Frame List", "An animation file is composed of a number of directions (presumably in which some object moves) and a number of frames per direction.  The highlighted item in this list determines which frame is showing in the current direction.  To determine which direction is being used, find the direction list.");
                       }
                     }, new PopupOption("Join Frames")
                     {
                       public void execute(Object param2Object)
                       {
                         SixDiceFrame.this.performMenuJoinFrames();
                       }
                     } } });
           }
         });
     this.m_label_width.addMouseListener((MouseListener)new WhatIsThisMouseListener("Image Width", "This number represents the width of the image currently being shown."));
     this.m_label_height.addMouseListener((MouseListener)new WhatIsThisMouseListener("Image Height", "This number represents the height of the image currently being shown."));
     this.m_offset_x.addMouseListener((MouseListener)new WhatIsThisMouseListener("Image X Offset", "Images in a single direction in an animation are usually meant to be animated together.  This number describes where this particular frame's image should be drawn on the screen relative to some other location.  For example, if the X offset of frame A is 5 and the X offset of frame B is 10, frame B will be displayed to the right of frame A when the animation is displayed in an animation program."));
     this.m_offset_y.addMouseListener((MouseListener)new WhatIsThisMouseListener("Image Y Offset", "Images in a single direction in an animation file are usually meant to be animated together.  This number describes where this particular frame's image should be drawn on the screen relative to some other location.  Animation files invert the Y offsets, so a lower Y offset number means that the image will be displayed higher on the screen.  For example, if the Y offset of frame A is 5 and the Y offset of frame B is 10, frame B will be displayed below frame A in an animation program."));
     this.m_palette_box.addMouseListener((MouseListener)new WhatIsThisMouseListener("Default Palette", "These animation files do not carry information about specific colors in them, but a general description \"color 1 goes here, color 2 goes there\"; this is called an \"indexed color model.\"  Therefore, when an animation is loaded, it must be associated with a palette before it can be displayed.  The provided palettes are from Diablo II: Lord of Destruction v1.10 and animation files used by that program will be displayed using one of these palettes.  To determine which palette your program should use, consult the program's documentation or other informational resources.  For Diablo II: LoD, the default \"Act 0\" palette is usually advisable."));
     int j = sortedListModel.getIndexOf(this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_SELECTED_PALETTE));
     if (j == -1) j = 0; 
     this.m_palette_box.setSelectedIndex(j);
     applyConfiguration(true);
     this.m_direction_list.addListSelectionListener(new ListSelectionListener()
         {
           public void valueChanged(ListSelectionEvent param1ListSelectionEvent)
           {
             SixDiceFrame.this.updateDisplay();
           }
         });
     this.m_frame_list.addListSelectionListener(new ListSelectionListener()
         {
           public void valueChanged(ListSelectionEvent param1ListSelectionEvent)
           {
             SixDiceFrame.this.updateDisplay();
           }
         });
     this.m_direction_add_button.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             if (SixDiceFrame.this.m_core.getAnimation() == null) {
               JOptionPane.showMessageDialog((Component)scoped_this, "No animation currently loaded.", "No Animation Loaded", 0);
             }
             else {
               SixDiceFrame.this.m_core.getAnimation().addDirection(SixDiceFrame.this.m_core.getAnimation().getDirectionCount());
               ((IntegerRangeListModel)SixDiceFrame.this.m_direction_list.getModel()).setMaximum(SixDiceFrame.this.m_core.getAnimation().getDirectionCount());
               SixDiceFrame.this.m_direction_list.setSelectedIndex(SixDiceFrame.this.m_core.getAnimation().getDirectionCount() - 1);
               SixDiceFrame.this.contentsUpdated();
               SixDiceFrame.this.updateDisplay();
             } 
           }
         });
     this.m_direction_insert_button.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             if (SixDiceFrame.this.m_core.getAnimation() == null) {
               JOptionPane.showMessageDialog((Component)scoped_this, "No animation currently loaded.", "No Animation Loaded", 0);
             }
             else if (SixDiceFrame.this.m_direction_list.getSelectedIndex() == -1) {
               JOptionPane.showMessageDialog((Component)scoped_this, "You must select a direction.  The new direction will be inserted above the selected one.", "No Direction Selected", 0);
             }
             else {
               SixDiceFrame.this.m_core.getAnimation().addDirection(SixDiceFrame.this.m_direction_list.getSelectedIndex());
               ((IntegerRangeListModel)SixDiceFrame.this.m_direction_list.getModel()).setMaximum(SixDiceFrame.this.m_core.getAnimation().getDirectionCount());
               SixDiceFrame.this.contentsUpdated();
               SixDiceFrame.this.updateDisplay();
             } 
           }
         });
     this.m_direction_remove_button.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             SixDiceFrame.this.performDirectionRemove(SixDiceFrame.this.m_direction_list.getSelectedIndices());
           }
         });
     this.m_frame_add_button.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             if (SixDiceFrame.this.m_core.getAnimation() == null) {
               JOptionPane.showMessageDialog((Component)scoped_this, "No animation currently loaded.", "No Animation Loaded", 0);
             }
             else {
               SixDiceFrame.this.m_core.getAnimation().addFrame(SixDiceFrame.this.m_core.getAnimation().getFrameCount());
               ((IntegerRangeListModel)SixDiceFrame.this.m_frame_list.getModel()).setMaximum(SixDiceFrame.this.m_core.getAnimation().getFrameCount());
               SixDiceFrame.this.m_frame_list.setSelectedIndex(SixDiceFrame.this.m_core.getAnimation().getFrameCount() - 1);
               SixDiceFrame.this.contentsUpdated();
               SixDiceFrame.this.updateDisplay();
             } 
           }
         });
     this.m_frame_insert_button.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             if (SixDiceFrame.this.m_core.getAnimation() == null) {
               JOptionPane.showMessageDialog((Component)scoped_this, "No animation currently loaded.", "No Animation Loaded", 0);
             }
             else if (SixDiceFrame.this.m_frame_list.getSelectedIndex() == -1) {
               JOptionPane.showMessageDialog((Component)scoped_this, "You must select a frame.  The new frame will be inserted above the selected one.", "No Frame Selected", 0);
             }
             else {
               SixDiceFrame.this.m_core.getAnimation().addFrame(SixDiceFrame.this.m_frame_list.getSelectedIndex());
               ((IntegerRangeListModel)SixDiceFrame.this.m_frame_list.getModel()).setMaximum(SixDiceFrame.this.m_core.getAnimation().getFrameCount());
               SixDiceFrame.this.contentsUpdated();
               SixDiceFrame.this.updateDisplay();
             } 
           }
         });
     this.m_frame_remove_button.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             if (SixDiceFrame.this.m_core.getAnimation() == null) {
               JOptionPane.showMessageDialog((Component)scoped_this, "No animation currently loaded.", "No Animation Loaded", 0);
             }
             else if (SixDiceFrame.this.m_frame_list.getSelectedIndex() == -1) {
               JOptionPane.showMessageDialog((Component)scoped_this, "You must select a frame to remove.", "No Frame Selected", 0);
             }
             else {
               boolean bool = false;
               for (byte b = 0; b < SixDiceFrame.this.m_core.getAnimation().getDirectionCount(); b++) {
                 BufferedImage bufferedImage = SixDiceFrame.this.m_core.getAnimation().getFrame(b, SixDiceFrame.this.m_frame_list.getSelectedIndex()).getImage();
                 if (bufferedImage.getWidth() > 1 || bufferedImage.getHeight() > 1) {
                   bool = true;
                   break;
                 } 
               } 
               if (!bool || JOptionPane.showConfirmDialog((Component)scoped_this, "<html>Are you sure you want to delete all of the images in that frame for each direction?<br>This operation cannot be undone.", "Are You Sure?", 0, 3) == 0) {
                 int[] arrayOfInt = SixDiceFrame.this.m_frame_list.getSelectedIndices();
                 for (int i = arrayOfInt.length - 1; i >= 0; i--)
                 {
                   SixDiceFrame.this.m_core.getAnimation().removeFrame(arrayOfInt[i]);
                 }
                 ((IntegerRangeListModel)SixDiceFrame.this.m_frame_list.getModel()).setMaximum(SixDiceFrame.this.m_core.getAnimation().getFrameCount());
                 SixDiceFrame.this.contentsUpdated();
                 SixDiceFrame.this.m_frame_list.setSelectedIndex(Math.min(SixDiceFrame.this.m_core.getAnimation().getFrameCount() - 1, SixDiceFrame.this.m_frame_list.getSelectedIndex()));
                 SixDiceFrame.this.updateDisplay();
               } 
             } 
           }
         });
     this.m_palette_box.addItemListener(new ItemListener()
         {
           public void itemStateChanged(ItemEvent param1ItemEvent)
           {
             if (param1ItemEvent.getStateChange() == 1 && SixDiceFrame.this.m_core.getAnimation() != null) {
               if (((Boolean)SixDiceFrame.this.m_configuration.getValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_DISPLAY_PALETTE_WARNING)).booleanValue())
               {
                 (new Thread("Palette Change Notice Display")
                   {
                     public void run()
                     {
                       if (ScrollableTextAndCheckboxDialog.displayMessage((Frame)scoped_this, "Palette Change Notice", "Notice about changing file palettes:", "Unlike many applications, SixDice does not simply change the palette in which the image data is handled; it actually filters each image using the specified palette.  This means that if, for example, a certain animation were saved in one palette and then loaded and saved in another, the quality of the image would be reduced (similar to that which happens when an image is resized several times).  It is important to recognize this, as the quality loss may be minimal at first.\n\nWhen an image is imported, however, it is stored in verbatim form until it is saved (and, depending on your settings, afterward as well).  This means that the palette can be changed after an image is imported without any image distortion or loss of quality.", "Do not display this message again.", false))
                       {
                         SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_DISPLAY_PALETTE_WARNING, Boolean.valueOf(false));
                       }
                     }
                   }).start();
               }
               SixDiceFrame.this.updateDisplay();
             } 
           }
         });
     this.m_palette_rendering.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_UI_IMAGE_RENDERED_IN_PALETTE, Boolean.valueOf(SixDiceFrame.this.m_palette_rendering.isSelected()));
             SixDiceFrame.this.updateDisplay();
           }
         });
     this.m_zoom_field.getDocument().addDocumentListener((DocumentListener)new DocumentAnyChangeListener()
         {
           public void documentChanged(DocumentEvent param1DocumentEvent)
           {
             String str = "";
             try {
               str = param1DocumentEvent.getDocument().getText(0, param1DocumentEvent.getDocument().getLength());
             } catch (BadLocationException badLocationException) {}
             if (str.length() == 0)
               return; 
             try {
               SixDiceFrame.this.m_zoom_slider.setValue(Integer.parseInt(str));
             } catch (NumberFormatException numberFormatException) {
               EventQueue.invokeLater(new Runnable()
                   {
                     public void run()
                     {
                       JOptionPane.showMessageDialog((Component)scoped_this, "Zoom value must be a positive integer.", "Invalid Zoom", 0);
                     }
                   });
             } 
           }
         });
     this.m_zoom_slider.addChangeListener(new ChangeListener()
         {
           public void stateChanged(ChangeEvent param1ChangeEvent)
           {
             EventQueue.invokeLater(new Runnable()
                 {
                   public void run()
                   {
                     SixDiceFrame.this.m_zoom_field.setText(Integer.toString(SixDiceFrame.this.m_zoom_slider.getValue()));
                   }
                 });
           }
         });
     this.m_zoom_slider.addChangeListener(new ChangeListener()
         {
           public void stateChanged(ChangeEvent param1ChangeEvent)
           {
             SixDiceFrame.this.updateDisplay();
           }
         });
     this.m_offset_x.addFocusListener(new FocusAdapter()
         {
           public void focusLost(FocusEvent param1FocusEvent)
           {
             if (SixDiceFrame.this.m_core.getAnimation() != null && SixDiceFrame.this.m_direction_list.getSelectedIndex() != -1 && SixDiceFrame.this.m_frame_list.getSelectedIndex() != -1) {
               AnimationFrame animationFrame = SixDiceFrame.this.m_core.getAnimation().getFrame(SixDiceFrame.this.m_direction_list.getSelectedIndex(), SixDiceFrame.this.m_frame_list.getSelectedIndex());
               try {
                 animationFrame.setXOffset(Integer.parseInt(SixDiceFrame.this.m_offset_x.getText()));
               } catch (NumberFormatException numberFormatException) {
                 JOptionPane.showMessageDialog((Component)scoped_this, "The contents of the X-Offset field must be an integer.", "Not An Integer", 0);
                 SixDiceFrame.this.m_offset_x.setText(Integer.toString(animationFrame.getXOffset()));
                 SixDiceFrame.this.m_offset_x.requestFocus();
               } 
             } 
           }
         });
     this.m_offset_x.getDocument().addDocumentListener(new DocumentListener()
         {
           public void removeUpdate(DocumentEvent param1DocumentEvent)
           {
             performUpdate();
           }
           public void insertUpdate(DocumentEvent param1DocumentEvent) {
             performUpdate();
           }
           public void changedUpdate(DocumentEvent param1DocumentEvent) {
             performUpdate();
           }
           private void performUpdate() {
             if (SixDiceFrame.this.m_core.getAnimation() != null && SixDiceFrame.this.m_direction_list.getSelectedIndex() != -1 && SixDiceFrame.this.m_frame_list.getSelectedIndex() != -1) {
               AnimationFrame animationFrame = SixDiceFrame.this.m_core.getAnimation().getFrame(SixDiceFrame.this.m_direction_list.getSelectedIndex(), SixDiceFrame.this.m_frame_list.getSelectedIndex());
               try {
                 animationFrame.setXOffset(Integer.parseInt(SixDiceFrame.this.m_offset_x.getText()));
               } catch (NumberFormatException numberFormatException) {}
             } 
           }
         });
     this.m_offset_y.addFocusListener(new FocusAdapter()
         {
           public void focusLost(FocusEvent param1FocusEvent)
           {
             if (SixDiceFrame.this.m_core.getAnimation() != null && SixDiceFrame.this.m_direction_list.getSelectedIndex() != -1 && SixDiceFrame.this.m_frame_list.getSelectedIndex() != -1) {
               AnimationFrame animationFrame = SixDiceFrame.this.m_core.getAnimation().getFrame(SixDiceFrame.this.m_direction_list.getSelectedIndex(), SixDiceFrame.this.m_frame_list.getSelectedIndex());
               try {
                 animationFrame.setYOffset(Integer.parseInt(SixDiceFrame.this.m_offset_y.getText()));
               } catch (NumberFormatException numberFormatException) {
                 JOptionPane.showMessageDialog((Component)scoped_this, "The contents of the Y-Offset field must be an integer.", "Not An Integer", 0);
                 SixDiceFrame.this.m_offset_y.setText(Integer.toString(animationFrame.getYOffset()));
                 SixDiceFrame.this.m_offset_y.requestFocus();
               } 
             } 
           }
         });
     this.m_offset_y.getDocument().addDocumentListener(new DocumentListener()
         {
           public void removeUpdate(DocumentEvent param1DocumentEvent)
           {
             performUpdate();
           }
           public void insertUpdate(DocumentEvent param1DocumentEvent) {
             performUpdate();
           }
           public void changedUpdate(DocumentEvent param1DocumentEvent) {
             performUpdate();
           }
           private void performUpdate() {
             if (SixDiceFrame.this.m_core.getAnimation() != null && SixDiceFrame.this.m_direction_list.getSelectedIndex() != -1 && SixDiceFrame.this.m_frame_list.getSelectedIndex() != -1) {
               AnimationFrame animationFrame = SixDiceFrame.this.m_core.getAnimation().getFrame(SixDiceFrame.this.m_direction_list.getSelectedIndex(), SixDiceFrame.this.m_frame_list.getSelectedIndex());
               try {
                 animationFrame.setYOffset(Integer.parseInt(SixDiceFrame.this.m_offset_y.getText()));
               } catch (NumberFormatException numberFormatException) {}
             } 
           }
         });
     DropTarget dropTarget = new DropTarget((Component)this.m_image_component, new DropTargetAdapter()
         {
           public void drop(DropTargetDropEvent param1DropTargetDropEvent)
           {
             for (DataFlavor dataFlavor : param1DropTargetDropEvent.getCurrentDataFlavors()) {
               if (dataFlavor.isFlavorJavaFileListType()) {
                 param1DropTargetDropEvent.acceptDrop(param1DropTargetDropEvent.getDropAction());
                 List list = null;
                 try {
                   list = (List)param1DropTargetDropEvent.getTransferable().getTransferData(dataFlavor);
                 } catch (UnsupportedFlavorException unsupportedFlavorException) {
                 }
                 catch (IOException iOException) {}
                 if (list != null)
                 {
                   for (File file : list) {
                     if (file instanceof File) {
                       if (((Boolean)SixDiceFrame.this.m_configuration.getValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_UI_CLEAR_ON_DND_IMPORTS)).booleanValue())
                       {
                         SixDiceFrame.this.performClear();
                       }
                       if (SixDiceFrame.this.performLoad(file, false)) {
                         SixDiceFrame.this.setActiveFile(file);
                         continue;
                       } 
                       SixDiceFrame.this.setActiveFile(null);
                       SixDiceFrame.this.performClear();
                     } 
                   } 
                 }
                 return;
               } 
             } 
           }
         });
     dropTarget.isActive();
     jButton.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             StringBuffer stringBuffer = new StringBuffer();
             for (String str1 : SixDiceFrame.this.m_core.getAnimation().getWarnings()) {
               stringBuffer.append("* ");
               stringBuffer.append(str1);
               stringBuffer.append('\n');
             } 
             stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
             final String errors = stringBuffer.toString();
             (new Thread("Animation Warnings Display")
               {
                 public void run()
                 {
                   synchronized (show_warnings_button) {
                     ScrollableTextDialog.displayMessage((Frame)scoped_this, "Animation Warnings", "The following warnings exist for your animation file:", errors);
                   } 
                 }
               }).start();
           }
         });
     this.m_menu_bar.doLayout();
     updateDisplay();
     setSaveEnabled(false);
     pack();
   }
   private void setContentPane(int paramInt) {
     String str;
     if ((paramInt & 0x1) == 0) {
       str = "East";
     } else {
       str = "West";
     } 
     this.m_image_scroll_pane = new JScrollPane((Component)this.m_image_component);
     ComponentConstructorPanel componentConstructorPanel1 = new ComponentConstructorPanel(new BorderLayout(2, 2), new Pair[] { new Pair(this.m_image_scroll_pane, "Center"), new Pair(new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL, false, false), new JComponent[] { (JComponent)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL, true, false), new JComponent[] { new JLabel("Zoom: "), this.m_zoom_field, new JLabel("%") }), this.m_zoom_slider }), "South") });
     ComponentConstructorPanel componentConstructorPanel2 = new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL), new JComponent[] { (JComponent)new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 4, 5, 5, false), new JComponent[] { (JComponent)(new SelfReturningJLabel("Width:")).setAlignmentXAndReturn(1.0F), this.m_label_width, (JComponent)(new SelfReturningJLabel("Height:")).setAlignmentXAndReturn(1.0F), this.m_label_height, (JComponent)(new SelfReturningJLabel("X Offset:")).setAlignmentXAndReturn(1.0F), this.m_offset_x, (JComponent)(new SelfReturningJLabel("Y Offset:")).setAlignmentXAndReturn(1.0F), this.m_offset_y }), (JComponent)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL, false, false), new JComponent[] { (JComponent)this.m_warnings_panel }), (JComponent)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL, false, false), new JComponent[] { (JComponent)new SelfReturningJLabel("Palette:"), this.m_palette_box, this.m_palette_rendering }) });
     ComponentConstructorPanel componentConstructorPanel3 = new ComponentConstructorPanel(new BorderLayout(), new Pair[] { new Pair((new SelfReturningJLabel("Directions:")).setFontAndReturn(new Font("dialog", 1, 14)), "North"), new Pair(new SizeConstructorScrollPane(this.m_direction_list, 75, 150), "Center"), new Pair(new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL), new JComponent[] { this.m_direction_add_button, this.m_direction_insert_button, this.m_direction_remove_button }), "South") });
     ComponentConstructorPanel componentConstructorPanel4 = new ComponentConstructorPanel(new BorderLayout(), new Pair[] { new Pair((new SelfReturningJLabel("Frames:")).setFontAndReturn(new Font("dialog", 1, 14)), "North"), new Pair(new SizeConstructorScrollPane(this.m_frame_list, 75, 150), "Center"), new Pair(new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL), new JComponent[] { this.m_frame_add_button, this.m_frame_insert_button, this.m_frame_remove_button }), "South") });
     if ((paramInt & 0x2) == 0) {
       setContentPane((Container)new ComponentConstructorPanel(new BorderLayout(), new Pair[] { new Pair(componentConstructorPanel1, "Center"), new Pair(new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL), new JComponent[] { (JComponent)componentConstructorPanel2, (JComponent)componentConstructorPanel3, (JComponent)componentConstructorPanel4 }), str) }));
     }
     else {
       setContentPane((Container)new ComponentConstructorPanel(new BorderLayout(), new Pair[] { new Pair(new ComponentConstructorPanel(new BorderLayout(), new Pair[] { new Pair(componentConstructorPanel1, "Center"), new Pair(new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL), new JComponent[] { (JComponent)componentConstructorPanel2 }), str) }), "Center"), new Pair(new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL), new JComponent[] { (JComponent)componentConstructorPanel3, (JComponent)componentConstructorPanel4 }), "South") }));
     } 
     pack();
   }
   protected void performDirectionRemove(int... paramVarArgs) {
     if (this.m_core.getAnimation() == null) {
       JOptionPane.showMessageDialog((Component)this, "No animation currently loaded.", "No Animation Loaded", 0);
     }
     else if (paramVarArgs.length == 0 || paramVarArgs[0] == -1) {
       JOptionPane.showMessageDialog((Component)this, "You must select a direction to remove.", "No Direction Selected", 0);
     }
     else {
       boolean bool = false;
       for (int i : paramVarArgs) {
         for (byte b = 0; b < this.m_core.getAnimation().getFrameCount(); b++) {
           BufferedImage bufferedImage = this.m_core.getAnimation().getFrame(i, b).getImage();
           if (bufferedImage.getWidth() > 1 || bufferedImage.getHeight() > 1) {
             bool = true;
             break;
           } 
         } 
       } 
       if (!bool || JOptionPane.showConfirmDialog((Component)this, "<html>Are you sure you want to delete all of the images in that direction?<br>This operation cannot be undone.", "Are You Sure?", 0, 3) == 0) {
         for (int i = paramVarArgs.length - 1; i >= 0; i--)
         {
           this.m_core.getAnimation().removeDirection(paramVarArgs[i]);
         }
         ((IntegerRangeListModel)this.m_direction_list.getModel()).setMaximum(this.m_core.getAnimation().getDirectionCount());
         this.m_direction_list.setSelectedIndices(Utilities.EMPTY_INT_ARRAY);
         contentsUpdated();
         updateDisplay();
       } 
     } 
   }
   public FileType[] getFileTypes() {
     ArrayList<FileType> arrayList = new ArrayList();
     for (AnimationCodec animationCodec : this.m_codecs)
     {
       arrayList.add(animationCodec.getFileType());
     }
     return arrayList.<FileType>toArray(FileType.EMPTY_FILE_TYPE_ARRAY);
   }
   public boolean performLoad(File paramFile) {
     return performLoad(paramFile, true);
   }
   public boolean performLoad(File paramFile, boolean paramBoolean) {
     try {
       ProgressBarTracker progressBarTracker = new ProgressBarTracker(this.m_waiting_dialog_progress_bar);
       this.m_waiting_dialog_displayer.setVisible(true);
       this.m_core.loadAnimation(paramFile, getPalette(), (ProgressTracker)progressBarTracker);
       this.m_waiting_dialog_displayer.setVisible(false);
       if (this.m_core.getAnimation() == null)
       {
         if (!paramBoolean || JOptionPane.showConfirmDialog((Component)this, "<html>" + paramFile + " does not appear to be a valid animation file.<br>Would you like to try to import that image now?", "Could Not Load Animation", 0, 2) == 0)
         {
           return performImport(paramFile);
         }
         performClear();
         JOptionPane.showMessageDialog((Component)this, "File not loaded.", "Could Not Load File", 0);
         return false;
       }
     } catch (IOException iOException) {
       this.m_waiting_dialog_displayer.setVisible(false);
       performClear();
       JOptionPane.showMessageDialog((Component)this, "<html>The following error occurred when trying to load " + paramFile + ":<br>" + iOException.getMessage(), "Could Not Load Animation", 0);
       return false;
     } finally {
       this.m_waiting_dialog_displayer.setVisible(false);
     } 
     resetListSelection();
     updateDisplay();
     return true;
   }
   public void performNew() {
     this.m_core.createNewAnimation();
     resetListSelection();
     contentsUpdated();
     updateDisplay();
   }
   public void performClear() {
     this.m_core.clearAnimation();
     resetListSelection();
     contentsUpdated();
     updateDisplay();
   }
   public boolean performSave(File paramFile) {
     if (this.m_core.getAnimation() == null) return true; 
     List<Pair<String, AnimationCodec.MessageType>> list = this.m_core.checkAnimation(paramFile);
     boolean bool = true;
     if (list.size() > 0) {
       StringBuffer stringBuffer = new StringBuffer();
       boolean bool1 = false;
       for (Pair<String, AnimationCodec.MessageType> pair : list) {
         if (stringBuffer.length() > 0) stringBuffer.append('\n'); 
         if (((AnimationCodec.MessageType)pair.getSecond()).equals(AnimationCodec.MessageType.FATAL)) {
           bool1 = true;
           stringBuffer.append("FATAL: ");
         } 
         stringBuffer.append((String)pair.getFirst());
       } 
       if (bool1) {
         bool = false;
         ScrollableTextDialog.displayMessage((Frame)this, "Cannot Save", "Your animation cannot be saved in the specified format.", stringBuffer.toString());
       }
       else if (JOptionPane.showConfirmDialog((Component)this, "<html>Your animation raised the following warnings:<ul><li>" + stringBuffer.toString().replaceAll("\n", "<li>") + "</ul>Would you like to save anyway?", "Warnings", 0, 2) != 0) {
         bool = false;
       } 
     } 
     if (bool) {
       boolean bool1 = false;
       ProgressBarTracker progressBarTracker = new ProgressBarTracker(this.m_waiting_dialog_progress_bar);
       this.m_waiting_dialog_displayer.setVisible(true);
       try {
         try {
           this.m_core.saveAnimation(paramFile, getPalette(), (ProgressTracker)progressBarTracker);
           bool1 = true;
         } catch (IOException iOException) {
           this.m_waiting_dialog_displayer.setVisible(false);
           JOptionPane.showMessageDialog((Component)this, "<html>The following error occurred while trying to save " + paramFile + ":<br>" + iOException.getMessage(), "Could Not Save Animation", 0);
         }
       }
       finally {
         this.m_waiting_dialog_displayer.setVisible(false);
       } 
       if (bool1 && ((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_UI_SAVED_FILES_RELOADED)).booleanValue())
       {
         performLoad(paramFile);
       }
       return bool1;
     } 
     return false;
   }
   public void performMenuImport() {
     JFileChooser jFileChooser = this.m_file_chooser_wrapper.getJFileChooser((Frame)this, true);
     String str = (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER);
     JZSwingUtilities.setJFileChooserFilters(jFileChooser, (str == null) ? (FileFilter)JZSwingUtilities.getAllImageReadersFileFilter() : (FileFilter)JZSwingUtilities.getImageReaderFileFilterFor(str), true, (FileFilter[])JZSwingUtilities.getImageLoadingFileFilters());
     if (jFileChooser.showOpenDialog((Component)this) == 0) {
       FileFilter fileFilter = jFileChooser.getFileFilter();
       if (fileFilter instanceof SwingFileFilterWrapper && ((SwingFileFilterWrapper)fileFilter).getFilter() instanceof FileExtensionFilter) {
         FileExtensionFilter fileExtensionFilter = (FileExtensionFilter)((SwingFileFilterWrapper)fileFilter).getFilter();
         str = fileExtensionFilter.getExtensions()[0];
         this.m_configuration.setValue((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER, str);
       } else {
         this.m_configuration.revertToDefault((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER);
       } 
       File file = jFileChooser.getSelectedFile();
       if (str != null && !file.exists())
       {
         file = FileUtilities.coerceFileExtension(file, new String[] { '.' + str });
       }
       performImport(file);
     } 
   }
   public boolean performImport(File paramFile) {
     String str;
     this.m_waiting_dialog_displayer.setVisible(true);
     try {
       if (this.m_core.getAnimation() == null) {
         str = this.m_core.importImage(paramFile);
         resetListSelection();
       } else {
         str = this.m_core.importImage(paramFile, this.m_direction_list.getSelectedIndex(), this.m_frame_list.getSelectedIndex(), ((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_IMPORT_INSERTS)).booleanValue());
       }
     }
     finally {
       this.m_waiting_dialog_displayer.setVisible(false);
     } 
     if (str != null) {
       JOptionPane.showMessageDialog((Component)this, "<html>" + str.replaceAll("\n", "<br>"), "Could Not Load Image", 0);
       return false;
     } 
     boolean bool = false;
     BufferedImage bufferedImage = this.m_core.getAnimation().getFrame(this.m_direction_list.getSelectedIndex(), this.m_frame_list.getSelectedIndex()).getImage();
     if (bufferedImage.getWidth() > 256 || bufferedImage.getHeight() > 256) {
       int i = (int)Math.ceil(bufferedImage.getWidth() / 256.0D);
       int j = (int)Math.ceil(bufferedImage.getHeight() / 256.0D);
       int k = JOptionPane.showConfirmDialog((Component)this, "<html>" + AWTUtilities.getNewlinedString("The provided image is larger than 256x256, which is the maximum size Diablo II supports for DC6 frames.  If this file will be saved as a DC6, this image will have to be split over multiple frames.  If there are already frames after the currently-selected frame, they will be overwritten to make room for this process.  This operation will require " + (i * j) + " frames.  If there are not enough frames, " + "they will be created.\n\nWould you like SixDice to split this image for you?", new Font("dialog", 1, 12), 600).replaceAll("\n", "<br>"), "Multi-Frame Import Suggested", 1, 2);
       if (k == 0) {
         bool = true;
       } else if (k != 1) {
         return false;
       } 
     } 
     if (bool)
     {
       performMenuSplitFrame();
     }
     setSaveEnabled(true);
     contentsUpdated();
     updateDisplay();
     return true;
   }
   public void performMenuImportSeries() {
     JFileChooser jFileChooser = this.m_file_chooser_wrapper.getJFileChooser((Frame)this, true);
     String str = (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER);
     JZSwingUtilities.setJFileChooserFilters(jFileChooser, (str == null) ? (FileFilter)JZSwingUtilities.getAllImageReadersFileFilter() : (FileFilter)JZSwingUtilities.getImageReaderFileFilterFor(str), true, (FileFilter[])JZSwingUtilities.getImageLoadingFileFilters());
     if (jFileChooser.showOpenDialog((Component)this) == 0) {
       FileFilter fileFilter = jFileChooser.getFileFilter();
       if (fileFilter instanceof SwingFileFilterWrapper && ((SwingFileFilterWrapper)fileFilter).getFilter() instanceof FileExtensionFilter) {
         FileExtensionFilter fileExtensionFilter = (FileExtensionFilter)((SwingFileFilterWrapper)fileFilter).getFilter();
         str = fileExtensionFilter.getExtensions()[0];
         this.m_configuration.setValue((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER, str);
       } else {
         this.m_configuration.revertToDefault((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER);
       } 
       File file = jFileChooser.getSelectedFile();
       if (str != null && !file.exists())
       {
         file = FileUtilities.coerceFileExtension(file, new String[] { '.' + str });
       }
       String str1 = (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_PORT_SEPARATOR);
       String[] arrayOfString = FileUtilities.removeFileExtension(file).getName().split(str1);
       if (arrayOfString.length != 1 && (arrayOfString.length != 3 || (arrayOfString.length > 1 && !MathUtilities.isNumber(arrayOfString[1], 10)) || Integer.parseInt(arrayOfString[1]) < 0 || (arrayOfString.length > 2 && !MathUtilities.isNumber(arrayOfString[2], 10)) || Integer.parseInt(arrayOfString[2]) < 0)) {
         ScrollableTextDialog.displayMessage((Frame)this, "Import Failed", "Your import selection was invalid.", "Your import selection was invalid.  To import, you must select a file form a batch-importable series.  A batch importable series has the format:\n\n     filename" + str1 + 'x' + str1 + "y.ext\n\n" + "where ext is the extension of the file, and X and Y are non-negative integers.  The X " + "number is interpreted as a direction number and the Y number is interpreted as a frame " + "number.  For example, the files \"flippy" + str1 + '0' + str1 + "0.gif\" through " + "\"flippy" + str1 + '0' + str1 + "16.gif\" would be imported as a animation with a single " + "direction and 17 frames in that direction.\n\n" + "The numbers can optionally have leading zeroes (i.e., \"flippy" + str1 + '0' + str1 + "00.gif\") without affecting the import.  The separator sequence (currently \"" + str1 + "\") can be specified in this program's configuration.");
         return;
       } 
       String str2 = FileUtilities.getFileExtension(file);
       if (performImportSeries(new File(file.getParent() + File.separatorChar + arrayOfString[0] + ((str2.length() > 0) ? ('.' + str2) : "")))) {
         resetListSelection();
         setSaveEnabled(true);
         updateDisplay();
       } else {
         performClear();
       } 
     } 
   }
   protected boolean performImportSeries(File paramFile) {
     String str = this.m_core.importSeries(paramFile, (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_PORT_SEPARATOR));
     if (str != null)
     {
       JOptionPane.showMessageDialog((Component)this, "<html>" + str.replaceAll("\n", "<br>"), "Import Series Failed", 0);
     }
     return (str == null);
   }
   public void performMenuExport() {
     JFileChooser jFileChooser = this.m_file_chooser_wrapper.getJFileChooser((Frame)this, true);
     String str = (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER);
     JZSwingUtilities.setJFileChooserFilters(jFileChooser, (str == null) ? null : (FileFilter)JZSwingUtilities.getImageWriterFileFilterFor(str), false, (FileFilter[])JZSwingUtilities.getImageSavingFileFilters());
     if (jFileChooser.showSaveDialog((Component)this) == 0) {
       FileFilter fileFilter = jFileChooser.getFileFilter();
       if (fileFilter instanceof SwingFileFilterWrapper && ((SwingFileFilterWrapper)fileFilter).getFilter() instanceof FileExtensionFilter) {
         FileExtensionFilter fileExtensionFilter = (FileExtensionFilter)((SwingFileFilterWrapper)fileFilter).getFilter();
         str = fileExtensionFilter.getExtensions()[0];
         this.m_configuration.setValue((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER, str);
       } else {
         this.m_configuration.revertToDefault((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER);
       } 
       File file = jFileChooser.getSelectedFile();
       if (str != null)
       {
         file = FileUtilities.coerceFileExtension(file, new String[] { '.' + str });
       }
       performExport(file, (str == null) ? FileUtilities.getFileExtension(file) : str);
     } 
   }
   public boolean performExport(File paramFile, String paramString) {
     String str = this.m_core.exportImage(paramFile, paramString, this.m_direction_list.getSelectedIndex(), this.m_frame_list.getSelectedIndex());
     if (str != null) {
       JOptionPane.showMessageDialog((Component)this, "<html>" + StringUtilities.getHtmlSafeString(str).replaceAll("\n", "<br>"), "Export Failed", 0);
       return false;
     } 
     return true;
   }
   public void performMenuExportSeries() {
     JFileChooser jFileChooser = this.m_file_chooser_wrapper.getJFileChooser((Frame)this, true);
     String str1 = (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER);
     JZSwingUtilities.setJFileChooserFilters(jFileChooser, (str1 == null) ? (FileFilter)JZSwingUtilities.getAllImageWritersFileFilter() : (FileFilter)JZSwingUtilities.getImageWriterFileFilterFor(str1), false, (FileFilter[])JZSwingUtilities.getImageSavingFileFilters());
     if (jFileChooser.showSaveDialog((Component)this) != 0)
       return; 
     String[] arrayOfString = StringUtilities.EMPTY_STRING_ARRAY;
     FileFilter fileFilter = jFileChooser.getFileFilter();
     if (fileFilter instanceof SwingFileFilterWrapper && ((SwingFileFilterWrapper)fileFilter).getFilter() instanceof FileExtensionFilter) {
       FileExtensionFilter fileExtensionFilter = (FileExtensionFilter)((SwingFileFilterWrapper)fileFilter).getFilter();
       arrayOfString = fileExtensionFilter.getExtensions();
       str1 = arrayOfString[0];
       for (byte b = 0; b < arrayOfString.length; b++)
       {
         arrayOfString[b] = '.' + arrayOfString[b];
       }
       this.m_configuration.setValue((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER, str1);
     } else {
       this.m_configuration.revertToDefault((ConfigurationElement)CONFIG_ELEMENT_PORT_FILTER);
     } 
     File file = FileUtilities.coerceFileExtension(jFileChooser.getSelectedFile(), arrayOfString);
     String str2 = FileUtilities.getFileExtension(file);
     file = FileUtilities.removeFileExtension(file);
     String str3 = (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_PORT_SEPARATOR);
     boolean bool = file.getName().contains(str3);
     if (bool)
     {
       file = new File(file.getParent() + File.separatorChar + file.getName().split(str3)[0]);
     }
     file = new File(file.getPath() + '.' + str2);
     if (bool)
     {
       if (JOptionPane.showConfirmDialog((Component)this, "<html>The filename you specified contains the import/export separator string.  It should not;<br> otherwise, SixDice will be unable to import the series.<br><br>Instead, you could use the filename:<br>    " + file + "<br>Is that okay?", "Invalid Export Series Filename", 0, 3) != 0) {
         return;
       }
     }
     performExportSeries(file, str1);
   }
   protected void performExportSeries(File paramFile, String paramString) {
     String str = this.m_core.exportSeries(paramFile, paramString, (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_PORT_SEPARATOR));
     if (str != null)
     {
       ScrollableTextDialog.displayMessage((Frame)this, "Errors During Series Export", "<html>The following errors occurred during the series export:", str.replaceAll("\n", "<html>"));
     }
   }
   public void performMenuSplitFrame() {
     final JDialog dialog = new JDialog((Frame)this, "Split Frame", true);
     final InterpretedTextField width_field = new InterpretedTextField((ValueInterpreter)new BoundedIntegerInterpreter(1, 2147483647), Integer.valueOf(256), 4);
     final InterpretedTextField height_field = new InterpretedTextField((ValueInterpreter)new BoundedIntegerInterpreter(1, 2147483647), Integer.valueOf(256), 4);
     final JCheckBox insert_box = new JCheckBox("", ((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_FRAME_SPLIT_INSERTS)).booleanValue());
     final SixDiceFrame scoped_this = this;
     ApprovalButtonPanel approvalButtonPanel = new ApprovalButtonPanel(true, SpongyLayout.Orientation.HORIZONTAL, false)
       {
         public boolean apply()
         {
           if (width_field.getValue() == null) {
             JOptionPane.showMessageDialog((Component)scoped_this, "Width must be a positive integer.", "Invalid Width", 0);
             return false;
           } 
           if (height_field.getValue() == null) {
             JOptionPane.showMessageDialog((Component)scoped_this, "Height must be a positive integer.", "Invalid Height", 0);
             return false;
           } 
           SixDiceFrame.this.performSplitFrame(((Integer)width_field.getValue()).intValue(), ((Integer)height_field.getValue()).intValue(), insert_box.isSelected());
           return true;
         }
         public void close() {
           dialog.setVisible(false);
           dialog.dispose();
         }
       };
     jDialog.setContentPane((Container)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL), new Pair[] { new Pair(new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 3, 2, 2, true), new JComponent[] { (JComponent)(new SelfReturningJLabel("Width: ")).setAlignmentXAndReturn(1.0F), (JComponent)interpretedTextField1, (JComponent)(new SelfReturningJLabel("Height: ")).setAlignmentXAndReturn(1.0F), (JComponent)interpretedTextField2, (JComponent)(new SelfReturningJLabel("Insert? ")).setAlignmentXAndReturn(1.0F), jCheckBox }), SpongyLayout.PRIORITY_NORMAL), new Pair(approvalButtonPanel, SpongyLayout.PRIORITY_PREFERRED) }));
     jDialog.pack();
     jDialog.setLocationRelativeTo((Component)this);
     jDialog.setVisible(true);
   }
   protected void performSplitFrame(int paramInt1, int paramInt2, boolean paramBoolean) {
     if (this.m_core.getAnimation() != null) {
       this.m_core.splitFrame(this.m_direction_list.getSelectedIndex(), this.m_frame_list.getSelectedIndex(), paramInt1, paramInt2, paramBoolean);
       contentsUpdated();
       updateDisplay();
     } 
   }
   public void performMenuJoinFrames() {
     if (this.m_core.getAnimation() == null)
     {
       JOptionPane.showMessageDialog((Component)this, "Cannot join: no animation loaded.", "No Animation Loaded", 0);
     }
     final int[] indices = this.m_frame_list.getSelectedIndices();
     if (arrayOfInt.length < 2) {
       JOptionPane.showMessageDialog((Component)this, "Select at least two frames to join.", "Insufficient Frames", 0);
       return;
     } 
     final BufferedImage[] images = new BufferedImage[arrayOfInt.length];
     int i = 0;
     int j = 0; int k;
     for (k = 0; k < arrayOfInt.length; k++) {
       arrayOfBufferedImage1[k] = this.m_core.getAnimation().getFrame(this.m_direction_list.getSelectedIndex(), arrayOfInt[k]).getImage();
       i = Math.max(i, arrayOfBufferedImage1[k].getWidth());
       j = Math.max(j, arrayOfBufferedImage1[k].getHeight());
     } 
     int n = 0;
     byte b = -1;
     int i1 = 0;
     byte b1 = 0;
     for (BufferedImage bufferedImage : arrayOfBufferedImage1) {
       if (bufferedImage.getWidth() < i && n < bufferedImage.getWidth()) {
         n = Math.max(n, bufferedImage.getWidth());
         b = b1;
       } 
       if (bufferedImage.getHeight() < j)
       {
         i1 = Math.max(i1, bufferedImage.getHeight());
       }
       b1++;
     } 
     if (b == -1) {
       k = Math.max(1, (int)Math.sqrt(arrayOfBufferedImage1.length));
     } else {
       k = b + 1;
     } 
     int m = (arrayOfBufferedImage1.length + k - 1) / k;
     final InterpretedTextField width_field = new InterpretedTextField((ValueInterpreter)new BoundedIntegerInterpreter(1, arrayOfBufferedImage1.length), Integer.valueOf(k), 2);
     final InterpretedTextField height_field = new InterpretedTextField((ValueInterpreter)new BoundedIntegerInterpreter(1, arrayOfBufferedImage1.length), Integer.valueOf(m), 2);
     final JScrollPane image_scroll_pane = new JScrollPane();
     final BufferedImage[] joined_image = new BufferedImage[1];
     final ActionListener image_refresh_action_listener = new ActionListener()
       {
         public void actionPerformed(ActionEvent param1ActionEvent)
         {
           if (width_field.getValue() == null || height_field.getValue() == null)
             return;  int i = ((Integer)width_field.getValue()).intValue();
           int j = ((Integer)height_field.getValue()).intValue();
           int k = 0;
           int m = 0;
           for (byte b1 = 0; b1 < j; b1++) {
             int i1 = 0;
             int i2 = 0;
             for (byte b = 0; b < i; b++) {
               int i3 = b1 * i + b;
               if (i3 < images.length) {
                 i1 = Math.max(i1, images[i3].getHeight(null));
                 i2 += images[i3].getWidth();
               } 
             } 
             k = Math.max(k, i2);
             m += i1;
           } 
           BufferedImage bufferedImage = new BufferedImage(k, m, 2);
           Graphics graphics = bufferedImage.getGraphics();
           graphics.setColor((Color)SixDiceFrame.this.m_configuration.getValue(SixDiceFrame.CONFIG_ELEMENT_IMPORT_VIRTUAL_CLEAR_COLOR));
           graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
           int n = 0;
           for (byte b2 = 0; b2 < j; b2++) {
             int i1 = 0;
             int i2 = 0;
             for (byte b = 0; b < i; b++) {
               int i3 = b2 * i + b;
               if (i3 < images.length) {
                 graphics.drawImage(images[i3], i1, n, null);
                 i2 = Math.max(i2, images[i3].getHeight(null));
                 i1 += images[i3].getWidth();
               } 
             } 
             n += i2;
           } 
           joined_image[0] = bufferedImage;
           image_scroll_pane.setViewportView(new JLabel(new ImageIcon(bufferedImage)));
         }
       };
     actionListener.actionPerformed(new ActionEvent(this, 1001, "updateImage"));
     Rectangle rectangle = getGraphicsConfiguration().getBounds();
     jScrollPane.setPreferredSize(new Dimension((int)Math.min(rectangle.getWidth() * 0.9D, (arrayOfBufferedImage2[0].getWidth() + 20)), (int)Math.min(rectangle.getHeight() * 0.9D, (arrayOfBufferedImage2[0].getHeight() + 20))));
     JButton jButton1 = new JButton("Refresh");
     jButton1.setMnemonic(82);
     JButton jButton2 = new JButton("Replace");
     jButton2.setMnemonic(80);
     JButton jButton3 = new JButton("Export");
     jButton3.setMnemonic(69);
     JButton jButton4 = new JButton("Close");
     jButton4.setMnemonic(67);
     final JDialog join_dialog = new JDialog((Frame)this, "Join Frames", true);
     jDialog.setContentPane((Container)new ComponentConstructorPanel(new BorderLayout(), new Pair[] { new Pair(jScrollPane, "Center"), new Pair(new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL), new JComponent[] { new JLabel("Grid Width: "), (JComponent)interpretedTextField1, new JLabel("Grid Height: "), (JComponent)interpretedTextField2, (JComponent)new ComponentConstructorPanel(new GridLayout(1, 4), new JComponent[] { jButton1, jButton2, jButton3, jButton4 }) }), "South") }));
     final SixDiceFrame scoped_this = this;
     DocumentListener documentListener = new DocumentListener()
       {
         public void changedUpdate(DocumentEvent param1DocumentEvent)
         {
           image_refresh_action_listener.actionPerformed(new ActionEvent(param1DocumentEvent.getDocument(), 1001, "updateImage"));
         }
         public void insertUpdate(DocumentEvent param1DocumentEvent) {
           image_refresh_action_listener.actionPerformed(new ActionEvent(param1DocumentEvent.getDocument(), 1001, "updateImage"));
         }
         public void removeUpdate(DocumentEvent param1DocumentEvent) {
           image_refresh_action_listener.actionPerformed(new ActionEvent(param1DocumentEvent.getDocument(), 1001, "updateImage"));
         }
       };
     interpretedTextField1.getDocument().addDocumentListener(documentListener);
     interpretedTextField2.getDocument().addDocumentListener(documentListener);
     jButton1.addActionListener(actionListener);
     jButton2.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             for (byte b = 1; b < indices.length; b++)
             {
               SixDiceFrame.this.m_core.getAnimation().setFrame(SixDiceFrame.this.m_direction_list.getSelectedIndex(), indices[b], new AnimationFrame());
             }
             SixDiceFrame.this.m_core.getAnimation().getFrame(SixDiceFrame.this.m_direction_list.getSelectedIndex(), indices[0]).setImage(joined_image[0]);
             join_dialog.setVisible(false);
             SixDiceFrame.this.contentsUpdated();
             SixDiceFrame.this.updateDisplay();
           }
         });
     jButton3.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             JFileChooser jFileChooser = SixDiceFrame.this.m_file_chooser_wrapper.getJFileChooser((Frame)scoped_this, true);
             JZSwingUtilities.setJFileChooserFilters(jFileChooser, (FileFilter)JZSwingUtilities.getImageWriterFileFilterFor((String)SixDiceFrame.this.m_configuration.getValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_PORT_FILTER)), false, (FileFilter[])JZSwingUtilities.getImageSavingFileFilters());
             if (jFileChooser.showSaveDialog((Component)scoped_this) == 0) {
               try {
                 String str = (String)JZSwingUtilities.getImageSavingFileFilterMap().get(jFileChooser.getFileFilter());
                 File file = FileUtilities.coerceFileExtension(jFileChooser.getSelectedFile(), new String[] { '.' + str });
                 ImageIO.write(SixDiceFrame.this.m_core.preProcessSavingImage(joined_image[0]), str, file);
                 SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_PORT_FILTER, str);
                 join_dialog.setVisible(false);
               } catch (IOException iOException) {
                 JOptionPane.showMessageDialog((Component)scoped_this, "<html>The following error occurred while attempting the export:<br>" + iOException.getMessage());
               } 
             }
           }
         });
     jButton4.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent param1ActionEvent)
           {
             join_dialog.setVisible(false);
           }
         });
     jDialog.pack();
     jDialog.setLocationRelativeTo((Component)this);
     jDialog.setVisible(true);
     jDialog.dispose();
   }
   public void performMenuColorChange() {
     final JDialog dialog = new JDialog((Frame)this, "Color Change", true);
     Color[] arrayOfColor = new Color[this.m_core.getAnimation().getDirectionCount() * this.m_core.getAnimation().getFrameCount()];
     for (byte b = 0; b < this.m_core.getAnimation().getDirectionCount(); b++) {
       for (byte b1 = 0; b1 < this.m_core.getAnimation().getFrameCount(); b1++)
       {
         arrayOfColor[b * this.m_core.getAnimation().getFrameCount() + b1] = ImageUtilities.getAverageColorIn(this.m_core.getAnimation().getFrame(b, b1).getImage());
       }
     } 
     final Color old_color = AWTUtilities.blendColors(arrayOfColor);
     final ColoredBlockIcon new_color_icon = new ColoredBlockIcon(30, 15, color);
     final JLabel preview_label = new JLabel(new ImageIcon(ImageUtilities.copyImage(this.m_core.getAnimation().getFrame(this.m_direction_list.getSelectedIndex(), this.m_frame_list.getSelectedIndex()).getImage())));
     final JSlider hue_slider = new JSlider(-128, 128, 0);
     jSlider1.setMajorTickSpacing(8);
     jSlider1.setMinorTickSpacing(1);
     jSlider1.setSnapToTicks(true);
     final JSlider saturation_slider = new JSlider(-128, 128, 0);
     jSlider2.setMajorTickSpacing(8);
     jSlider2.setMinorTickSpacing(1);
     jSlider2.setSnapToTicks(true);
     final JSlider brightness_slider = new JSlider(-128, 128, 0);
     jSlider3.setMajorTickSpacing(8);
     jSlider3.setMinorTickSpacing(1);
     jSlider3.setSnapToTicks(true);
     ApprovalButtonPanel approvalButtonPanel = new ApprovalButtonPanel(true, SpongyLayout.Orientation.HORIZONTAL, false)
       {
         public boolean apply()
         {
           for (byte b = 0; b < SixDiceFrame.this.m_core.getAnimation().getDirectionCount(); b++) {
             for (byte b1 = 0; b1 < SixDiceFrame.this.m_core.getAnimation().getFrameCount(); b1++) {
               AnimationFrame animationFrame = SixDiceFrame.this.m_core.getAnimation().getFrame(b, b1);
               animationFrame.setImage(ImageUtilities.adjustHSB(animationFrame.getImage(), hue_slider.getValue() / 128.0D, saturation_slider.getValue() / 128.0D, brightness_slider.getValue() / 128.0D));
             } 
           } 
           SixDiceFrame.this.contentsUpdated();
           SixDiceFrame.this.updateDisplay();
           return true;
         }
         public void close() {
           dialog.setVisible(false);
         }
       };
     ChangeListener changeListener = new ChangeListener()
       {
         public void stateChanged(ChangeEvent param1ChangeEvent)
         {
           new_color_icon.setColor(new Color(AWTUtilities.adjustHSB(old_color.getRGB(), hue_slider.getValue() / 128.0D, saturation_slider.getValue() / 128.0D, brightness_slider.getValue() / 128.0D)));
           preview_label.setIcon(new ImageIcon(ImageUtilities.adjustHSB(ImageUtilities.copyImage(SixDiceFrame.this.m_core.getAnimation().getFrame(SixDiceFrame.this.m_direction_list.getSelectedIndex(), SixDiceFrame.this.m_frame_list.getSelectedIndex()).getImage()), hue_slider.getValue() / 128.0D, saturation_slider.getValue() / 128.0D, brightness_slider.getValue() / 128.0D)));
           dialog.repaint();
         }
       };
     jSlider1.addChangeListener(changeListener);
     jSlider2.addChangeListener(changeListener);
     jSlider3.addChangeListener(changeListener);
     jDialog.setContentPane((Container)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL), new JComponent[] { (JComponent)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL), new JComponent[] { new JLabel("Old Average Color: "), new JLabel((Icon)new ColoredBlockIcon(30, 15, color)), (JComponent)new SpacingComponent(10, 10), new JLabel("New Average Color: "), new JLabel((Icon)coloredBlockIcon) }), (JComponent)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.HORIZONTAL), new JComponent[] { new JLabel("Preview: "), jLabel }), (JComponent)new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 3, 2, 2, false), new JComponent[] { new JLabel("Hue: "), jSlider1, new JLabel("Saturation: "), jSlider2, new JLabel("Brightness: "), jSlider3 }), (JComponent)approvalButtonPanel }));
     jDialog.pack();
     jDialog.setLocationRelativeTo((Component)this);
     jDialog.setVisible(true);
     jDialog.dispose();
   }
   public void performMenuResize() {
     final JDialog resize_dialog = new JDialog((Frame)this, "Resize", true);
     final JTextField scale_field = new JTextField("100", 3);
     final JCheckBox adjust_offsets = new JCheckBox("", true);
     final JComboBox scaling_method = new JComboBox(new Object[] { "Replicate", "Smooth" });
     final int[] scaling_method_constants = { 8, 4 };
     ApprovalButtonPanel approvalButtonPanel = new ApprovalButtonPanel(true, SpongyLayout.Orientation.HORIZONTAL, false)
       {
         public boolean apply()
         {
           int i;
           try {
             i = Integer.parseInt(scale_field.getText());
           } catch (NumberFormatException numberFormatException) {
             JOptionPane.showMessageDialog(resize_dialog, "Scale must be a positive integer.", "Invalid Scale", 0);
             return false;
           } 
           if (i < 0 || i > 10000) {
             JOptionPane.showMessageDialog(resize_dialog, "Scale must be between 1% and 10,000%.", "Invalid Scale", 0);
             return false;
           } 
           SixDiceFrame.this.m_core.getAnimation().scale(i / 100.0D, adjust_offsets.isSelected(), scaling_method_constants[scaling_method.getSelectedIndex()]);
           SixDiceFrame.this.contentsUpdated();
           SixDiceFrame.this.updateDisplay();
           return true;
         }
         public void close() {
           resize_dialog.dispose();
         }
       };
     jDialog.setContentPane((Container)new ComponentConstructorPanel(new BorderLayout(), new Pair[] { new Pair(new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 3, 2, 2), new JComponent[] { new JLabel("Scale Percentage: "), jTextField, new JLabel("Adjust Offsets? "), jCheckBox, new JLabel("Scaling Method: "), jComboBox }), "Center"), new Pair(approvalButtonPanel, "South") }));
     jDialog.pack();
     jDialog.setLocationRelativeTo((Component)this);
     jDialog.setVisible(true);
   }
   public void performMenuBatchConvert() {
     final JDialog batch_convert_dialog = new JDialog((Frame)this, "Batch Conversion", true);
     jDialog.setDefaultCloseOperation(0);
     final DirectoryPathField dpf = new DirectoryPathField((Frame)this, (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_PATH), 20);
     SwingFileFilterWrapper[] arrayOfSwingFileFilterWrapper1 = JZSwingUtilities.getImageLoadingFileFilters();
     Object[] arrayOfObject1 = new Object[this.m_codecs.length + arrayOfSwingFileFilterWrapper1.length];
     System.arraycopy(this.m_codecs, 0, arrayOfObject1, 0, this.m_codecs.length);
     System.arraycopy(arrayOfSwingFileFilterWrapper1, 0, arrayOfObject1, this.m_codecs.length, arrayOfObject1.length - this.m_codecs.length);
     SwingFileFilterWrapper[] arrayOfSwingFileFilterWrapper2 = JZSwingUtilities.getImageSavingFileFilters();
     Object[] arrayOfObject2 = new Object[this.m_codecs.length + arrayOfSwingFileFilterWrapper2.length];
     System.arraycopy(this.m_codecs, 0, arrayOfObject2, 0, this.m_codecs.length);
     System.arraycopy(arrayOfSwingFileFilterWrapper2, 0, arrayOfObject2, this.m_codecs.length, arrayOfObject2.length - this.m_codecs.length);
     final JComboBox source_type = new JComboBox(arrayOfObject1);
     final JComboBox target_type = new JComboBox(arrayOfObject2);
     jComboBox1.setSelectedIndex(MathUtilities.bound(((Integer)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_SOURCE_INDEX)).intValue(), 0, arrayOfObject1.length - 1));
     jComboBox2.setSelectedIndex(MathUtilities.bound(((Integer)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_TARGET_INDEX)).intValue(), 0, arrayOfObject2.length - 1));
     final JCheckBox recursive_box = new JCheckBox("Recursive");
     jCheckBox.setSelected(((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_RECURSIVE)).booleanValue());
     final JComboBox palette_box = new JComboBox((ComboBoxModel<?>)new SortedListModel(Diablo2DefaultPalettes.PALETTE_MAP.keySet().toArray((Object[])StringUtilities.EMPTY_STRING_ARRAY), true));
     String str = (String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_BATCH_CONVERSION_PALETTE);
     if (str == null) {
       jComboBox3.setSelectedIndex(0);
     } else {
       jComboBox3.setSelectedItem(str);
     } 
     final SixDiceFrame scoped_this = this;
     ApprovalButtonPanel approvalButtonPanel = new ApprovalButtonPanel(true, SpongyLayout.Orientation.HORIZONTAL, false)
       {
         public boolean apply()
         {
           (new Thread("Batch Convert Initiator")
             {
               public void run()
               {
                 final StreamPipe stream_pipe = new StreamPipe(false);
                 final OutputStream output_stream = streamPipe.getOutputStream();
                 final File directory = new File(dpf.getPath());
                 final boolean recursive = recursive_box.isSelected();
                 SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_BATCH_CONVERSION_PATH, file.getPath());
                 SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_BATCH_CONVERSION_RECURSIVE, Boolean.valueOf(bool));
                 SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_BATCH_CONVERSION_SOURCE_INDEX, Integer.valueOf(source_type.getSelectedIndex()));
                 SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_BATCH_CONVERSION_TARGET_INDEX, Integer.valueOf(target_type.getSelectedIndex()));
                 SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_BATCH_CONVERSION_PALETTE, palette_box.getSelectedItem().toString());
                 final JDialog log_dialog = new JDialog((Frame)scoped_this, "Batch Conversion", true);
                 final JTextArea log_area = new JTextArea();
                 jTextArea.setEditable(false);
                 JScrollPane jScrollPane = new JScrollPane(jTextArea);
                 jScrollPane.setPreferredSize(new Dimension(400, 300));
                 final JButton close_button = new JButton("Close");
                 jButton.setEnabled(false);
                 jDialog.setContentPane((Container)new ComponentConstructorPanel(new BorderLayout(), new Pair[] { new Pair(jScrollPane, "Center"), new Pair(jButton, "South") }));
                 jDialog.pack();
                 jDialog.setLocationRelativeTo((Component)scoped_this);
                 jButton.addActionListener(new ActionListener()
                     {
                       public void actionPerformed(ActionEvent param3ActionEvent)
                       {
                         log_dialog.setVisible(false);
                       }
                     });
                 (new Thread("Log Update")
                   {
                     public void run()
                     {
                       FileOutputStream fileOutputStream = null;
                       try {
                         byte b = 0;
                         try {
                           fileOutputStream = new FileOutputStream(System.getProperty("user.home") + File.separatorChar + ".sixdice-batchconvert.log");
                         }
                         catch (FileNotFoundException fileNotFoundException) {}
                         PrintStream printStream = new PrintStream((fileOutputStream == null) ? (OutputStream)new NullOutputStream() : fileOutputStream);
                         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream_pipe.getInputStream()));
                         boolean bool = true;
                         String str = bufferedReader.readLine();
                         while (str != null) {
                           printStream.println(str);
                           b++;
                           if (b > 'Ǵ') {
                             String str1 = log_area.getText();
                             int i = str1.indexOf("\n");
                             log_area.setText(str1.substring(i + 1) + '\n' + str);
                           }
                           else if (log_area.getText().length() > 0) {
                             log_area.setText(log_area.getText() + '\n' + str);
                           } else {
                             log_area.setText(str);
                           } 
                           log_area.setText(log_area.getText().replaceAll(".\b", ""));
                           bool = false;
                           str = bufferedReader.readLine();
                         } 
                         if (bool) {
                           log_area.setText("Nothing to do.");
                           printStream.println("Nothing to do.");
                         } 
                         printStream.close();
                         if (fileOutputStream != null)
                         {
                           fileOutputStream.close();
                         }
                       } catch (IOException iOException) {
                         log_area.setText("Internal logging error: " + iOException.getMessage());
                       } 
                     }
                   }).start();
                 (new Thread("Batch Converter")
                   {
                     public void run()
                     {
                       if (source_type.getSelectedItem() instanceof AnimationCodec) {
                         if (target_type.getSelectedItem() instanceof AnimationCodec)
                         {
                           SixDiceFrame.this.m_core.copy().batchConvert(directory, recursive, (AnimationCodec)source_type.getSelectedItem(), (AnimationCodec)target_type.getSelectedItem(), Diablo2DefaultPalettes.PALETTE_MAP.get(palette_box.getSelectedItem()), output_stream, output_stream, true);
                         }
                         else
                         {
                           SixDiceFrame.this.m_core.copy().batchConvert(directory, recursive, (AnimationCodec)source_type.getSelectedItem(), ((FileExtensionFilter)((SwingFileFilterWrapper)target_type.getSelectedItem()).getFilter()).getExtensions()[0], (String)SixDiceFrame.this.m_configuration.getValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_PORT_SEPARATOR), Diablo2DefaultPalettes.PALETTE_MAP.get(palette_box.getSelectedItem()), output_stream, output_stream, true);
                         }
                       }
                       else if (target_type.getSelectedItem() instanceof AnimationCodec) {
                         SixDiceFrame.this.m_core.copy().batchConvert(directory, recursive, ((SwingFileFilterWrapper)source_type.getSelectedItem()).getFilter(), (AnimationCodec)target_type.getSelectedItem(), (String)SixDiceFrame.this.m_configuration.getValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_PORT_SEPARATOR), Diablo2DefaultPalettes.PALETTE_MAP.get(palette_box.getSelectedItem()), output_stream, output_stream, true);
                       }
                       else {
                         PrintStream printStream = new PrintStream(output_stream);
                         printStream.println("Image-to-image conversion not supported.");
                         printStream.close();
                       } 
                       try {
                         stream_pipe.getOutputStream().close();
                       } catch (IOException iOException) {}
                       close_button.setEnabled(true);
                     }
                   }).start();
                 jDialog.addComponentListener((ComponentListener)new DisposeOnHideListener());
                 jDialog.addComponentListener(new ComponentAdapter()
                     {
                       public void componentHidden(ComponentEvent param3ComponentEvent)
                       {
                         try {
                           stream_pipe.getOutputStream().close();
                         } catch (IOException iOException) {}
                       }
                     });
                 jDialog.setVisible(true);
               }
             }).start();
           return true;
         }
         public void close() {
           batch_convert_dialog.setVisible(false);
           batch_convert_dialog.dispose();
         }
       };
     ComponentConstructorPanel componentConstructorPanel = new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL, 5, 5), new JComponent[] { (JComponent)new SelfReturningJLabel("Please choose a directory containing the files to convert:"), (JComponent)directoryPathField, jCheckBox, (JComponent)new ComponentConstructorPanel(new BorderLayout(), new Pair[] { new Pair(new JLabel("Palette: "), "West"), new Pair(jComboBox3, "Center") }), (JComponent)new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(1, 4, 2, 2), new JComponent[] { new JLabel("Source: "), jComboBox1, new JLabel("Target: "), jComboBox2 }), (JComponent)approvalButtonPanel });
     jDialog.setContentPane((Container)componentConstructorPanel);
     jDialog.pack();
     jDialog.setLocationRelativeTo((Component)this);
     jDialog.setVisible(true);
   }
   protected void setSaveEnabled(boolean paramBoolean) {
     super.setSaveEnabled(paramBoolean);
     if (this.m_menu_bar.getExtendedMenu("File").getItem("Export") != null) {
       this.m_menu_bar.getExtendedMenu("File").getItem("Export").setEnabled(paramBoolean);
       this.m_menu_bar.getExtendedMenu("File").getItem("Export Series").setEnabled(paramBoolean);
       this.m_menu_bar.getExtendedMenu("Image").getItem("Trim Borders").setEnabled(paramBoolean);
       this.m_menu_bar.getExtendedMenu("Image").getItem("Adjust Offsets").setEnabled(paramBoolean);
       this.m_menu_bar.getExtendedMenu("Image").getItem("Center Offset").setEnabled(paramBoolean);
       this.m_menu_bar.getExtendedMenu("Image").getItem("Split Frame").setEnabled(paramBoolean);
       this.m_menu_bar.getExtendedMenu("Image").getItem("Join Frames").setEnabled(paramBoolean);
       this.m_menu_bar.getExtendedMenu("Image").getItem("Color Change").setEnabled(paramBoolean);
       this.m_menu_bar.getExtendedMenu("Image").getItem("Resize Images").setEnabled(paramBoolean);
       this.m_direction_add_button.setEnabled(paramBoolean);
       this.m_direction_insert_button.setEnabled(paramBoolean);
       this.m_direction_remove_button.setEnabled(paramBoolean);
       this.m_frame_add_button.setEnabled(paramBoolean);
       this.m_frame_insert_button.setEnabled(paramBoolean);
       this.m_frame_remove_button.setEnabled(paramBoolean);
       this.m_zoom_field.setEnabled(paramBoolean);
       this.m_zoom_slider.setEnabled(paramBoolean);
       if (!paramBoolean) this.m_zoom_field.setText("100");
     } 
   }
   public void performMenuTrimBorders() {
     if (this.m_core.getAnimation() == null) {
       JOptionPane.showMessageDialog((Component)this, "Cannot trim: no animation loaded.", "No Animation Loaded", 0);
       return;
     } 
     this.m_core.getAnimation().trimBorders();
     updateDisplay();
   }
   public void performMenuAdjustOffset() {
     if (this.m_core.getAnimation() == null) {
       JOptionPane.showMessageDialog((Component)this, "Cannot adjust offsets: no animation loaded.", "No Animation Loaded", 0);
       return;
     } 
     final JDialog dialog = new JDialog((Frame)this, "Adjust Offsets", true);
     final JTextField x_field = new JTextField("0", 3);
     final JTextField y_field = new JTextField("0", 3);
     ApprovalButtonPanel approvalButtonPanel = new ApprovalButtonPanel(true, SpongyLayout.Orientation.HORIZONTAL, false)
       {
         public boolean apply()
         {
           int i, j;
           try {
             i = Integer.parseInt(x_field.getText());
           } catch (NumberFormatException numberFormatException) {
             JOptionPane.showMessageDialog(dialog, "X value is not a number.", "Invalid Number", 0);
             return false;
           } 
           try {
             j = Integer.parseInt(y_field.getText());
           } catch (NumberFormatException numberFormatException) {
             JOptionPane.showMessageDialog(dialog, "Y value is not a number.", "Invalid Number", 0);
             return false;
           } 
           SixDiceFrame.this.m_core.getAnimation().adjustOffsets(-i, -j);
           SixDiceFrame.this.updateDisplay();
           return true;
         }
         public void close() {
           dialog.dispose();
         }
       };
     jDialog.setContentPane((Container)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL), new JComponent[] { (JComponent)new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 2, 2, 2, false), new JComponent[] { new JLabel("X: "), jTextField1, new JLabel("Y: "), jTextField2 }), (JComponent)approvalButtonPanel }));
     jDialog.setDefaultCloseOperation(2);
     jDialog.pack();
     jDialog.setLocationRelativeTo((Component)this);
     jDialog.setVisible(true);
   }
   public void performMenuCenterOffset() {
     if (this.m_core.getAnimation() == null) {
       JOptionPane.showMessageDialog((Component)this, "Cannot center: no animation loaded.", "No Animation Loaded", 0);
       return;
     } 
     if (this.m_direction_list.getSelectedIndex() == -1 || this.m_frame_list.getSelectedIndex() == -1) {
       JOptionPane.showMessageDialog((Component)this, "Select a frame to act as the center-point.", "No Frame Selected.", 0);
       return;
     } 
     this.m_core.centerOffsets(this.m_direction_list.getSelectedIndex(), this.m_frame_list.getSelectedIndex());
     updateDisplay();
   }
   public void performMenuLookAndFeel() {
     this.m_lafd.execute();
     this.m_configuration.setValue((ConfigurationElement)CONFIG_ELEMENT_UI_LOOK_AND_FEEL_CLASSNAME, this.m_lafd.getLastAppliedLookAndFeel());
   }
   public void performMenuPreferences() {
     final JDialog preferences_dialog = new JDialog((Frame)this, "Preferences", true);
     JLabel jLabel1 = new JLabel("UI Layout Style: ");
     final JComboBox<String> layout_combo_box = new JComboBox<String>(new String[] { "Wide-Left Layout", "Wide-Right Layout", "Tall-Left Layout", "Tall-Right Layout" });
     jComboBox.setSelectedIndex(((Integer)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_UI_LAYOUT)).intValue());
     JLabel jLabel2 = new JLabel("Image Background Color: ");
     final ColorSelectorButton background_color_button = new ColorSelectorButton((Color)this.m_configuration.getValue(CONFIG_ELEMENT_UI_IMAGE_BACKGROUND), new Dimension(20, 12));
     JLabel jLabel3 = new JLabel("Image Underlay Color: ");
     final ColorSelectorButton underlay_color_button = new ColorSelectorButton((Color)this.m_configuration.getValue(CONFIG_ELEMENT_UI_IMAGE_UNDERLAY), new Dimension(20, 12));
     JLabel jLabel4 = new JLabel("Clear on Drag & Drop Imports? ");
     final JCheckBox drag_and_drop_import_clear = new JCheckBox("", ((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_UI_CLEAR_ON_DND_IMPORTS)).booleanValue());
     JLabel jLabel5 = new JLabel("Insert Imported Frames? ");
     final JCheckBox insert_imported_frames = new JCheckBox("", ((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_IMPORT_INSERTS)).booleanValue());
     JLabel jLabel6 = new JLabel("Insert Split Frames? ");
     final JCheckBox insert_split_frames = new JCheckBox("", ((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_FRAME_SPLIT_INSERTS)).booleanValue());
     JLabel jLabel7 = new JLabel("Saved Files Reloaded? ");
     final JCheckBox load_on_save = new JCheckBox("", ((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_UI_SAVED_FILES_RELOADED)).booleanValue());
     JLabel jLabel8 = new JLabel("Use Virtual Clear Color? ");
     final JCheckBox virtual_clear_color_enabled = new JCheckBox("", (this.m_configuration.getValue(CONFIG_ELEMENT_IMPORT_VIRTUAL_CLEAR_COLOR) != null));
     JLabel jLabel9 = new JLabel("Virtual Clear Color: ");
     final ColorSelectorButton virtual_clear_color_button = new ColorSelectorButton((Color)this.m_configuration.getValue(CONFIG_ELEMENT_IMPORT_VIRTUAL_CLEAR_COLOR), new Dimension(20, 12));
     JLabel jLabel10 = new JLabel("Clear to Virtual on Export? ");
     final JCheckBox clear_to_virtual_on_export = new JCheckBox("", ((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_EXPORT_CLEAR_TO_VIRTUAL)).booleanValue());
     JLabel jLabel11 = new JLabel("Separator String: ");
     final JTextField multi_frame_separator = new JTextField((String)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_PORT_SEPARATOR), 4);
     JLabel jLabel12 = new JLabel("Clear Index: ");
     final JTextField dc6_codec_clear_index = new JTextField(String.valueOf(this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_CODEC_DC6_CLEAR_INDEX)), 3);
     JLabel jLabel13 = new JLabel("Clear Index: ");
     final JTextField dcc_codec_clear_index = new JTextField(String.valueOf(this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_CODEC_DCC_CLEAR_INDEX)), 3);
     jCheckBox5.addActionListener((ActionListener)new ComponentTogglingAction((JComponent)colorSelectorButton3));
     jCheckBox5.addActionListener((ActionListener)new ComponentTogglingAction(jCheckBox6));
     WhatIsThisMouseListener.batchAdd("Layout Style", "The layout style defines how the components in the SixDice frame are laid out.  The default value for SixDice is Wide-Left Layout.  The Wide layouts are meant to be easier to use on a larger screen, while the Tall layouts are easier to view on a smaller screen.  The sidedness (left or right) of the layout defines where the image display panel appears with respect to the rest of the controls.", new JComponent[] { jLabel1, jComboBox });
     WhatIsThisMouseListener.batchAdd("Image Background Color", "Selects the color used as the background in the display pane.  This is purely a display detail; it has nothing to do with the content of the animation file.  It is useful for determining where an image stops and the unused space on the image display pane starts.", new JComponent[] { jLabel2, (JComponent)colorSelectorButton1 });
     WhatIsThisMouseListener.batchAdd("Image Underlay Color", "Selects the color used as the underlay in the display pane.  This is purely a display detail; it has nothing to do with the content of the animation file.  The underlay is displayed under the image itself; this means that transparent pixels will show as this color instead of the background color.", new JComponent[] { jLabel3, (JComponent)colorSelectorButton2 });
     WhatIsThisMouseListener.batchAdd("Clear on Drag-and-Drop Loads", "It is possible to load or import a file by dragging it onto the image panel.  For example, dragging a JPG from Windows Explorer to SixDice will cause SixDice to import the JPG.  If you want SixDice to clear the workspace before doing this, check this box.  If you want to import the file into the current workspace, clear this box.", new JComponent[] { jLabel4, jCheckBox1 });
     WhatIsThisMouseListener.batchAdd("Insert Imported Frames", "When a frame is imported, it will either replace the currently-selected frame (if this box is clear) or be inserted before the currently selected frame (if this box is checked).", new JComponent[] { jLabel5, jCheckBox2 });
     WhatIsThisMouseListener.batchAdd("Insert Split Frames", "Splitting a frame allows it to be divided by a specific size.  For example, Diablo II requires that images larger than 256x256 are split into 256x256 tiles.  If this checkbox is selected, the new frames created by this operation will be inserted into the location of the old frame.  Otherwise, the first new frame will replace the old frame and successive frames will overwrite the following frames.  For example, if an animation contains a single direction with the frames A, B, and C, frame B could be split in two ways.  If frame B is split using insertion, the new frame list will be A, B1, B2, ..., Bn, C.  If frame B is split using overwrite, the new frame list will be A, B1, B2, ..., Bn.  Note that if insertion is used, the inserted frames will appear in other directions as well as new, blank frames.", new JComponent[] { jLabel6, jCheckBox3 });
     WhatIsThisMouseListener.batchAdd("Reloading Saved Files", "The image as it appears in the image display pane is the image that SixDice has in memory (possibly filtered through the selected palette); it is not necessarily what will be saved to the disk.  As a result, the displayed image may be misleading since, due to compression, the quality of the image displayed by SixDice may be higher than that of the saved file.  If this box is checked, any file which is saved is immediately reloaded, ensuring that SixDice has the actual contents of the saved file in memory.", new JComponent[] { jLabel7, jCheckBox4 });
     WhatIsThisMouseListener.batchAdd("Virtual Clear Color", "The virtual clear color is a color which is assumed to be transparent in imported images.  For example, one may store image files with a black background with the assumption that black pixels in the animation are actually transparent; another common color to use is bright pink (1.0, 0.0, 1.0), since it isn't commonly used.  Transparent pixels in imported images will still be treated as transparent, so this option isn't strictly necessary, but it is often used in practice.", new JComponent[] { jLabel8, jCheckBox5, jLabel9, (JComponent)colorSelectorButton3 });
     WhatIsThisMouseListener.batchAdd("Clear-to-Virtual Mapping on Export", "Since it may be useful to specify a color to be treated as clear in imported images, it would often be expected that exported images use that color instead of clear.  If this behavior is desired, check this box.  Note that this task is not performed if the virtual clear color is disabled.", new JComponent[] { jLabel10, jCheckBox6 });
     WhatIsThisMouseListener.batchAdd("Multi-frame Spearator String", "When a multi-frame animation file is exported or imported, such an operation is performed with a set of image files.  The image files are named \"filename__x__y.ext\", where X and Y are the direction and frame number of that image, respectively.  For exmaple, a flippy file may be represented in PNG format by seventeen files, named \"flippy__0__0.png\"through \"flippy__0__16.png\".  The string \"__\" represents the separator that is used when generating or searching for files; indeed, the separator string is \"__\" by default.  If you prefer your multi-frame files to be named differently, feel free to change this string.\n\nNote to advanced users: the separator string is processed as a Java regular expression.  Therefore, some special characters must be avoided (such as \"(\" and \")\").However, for purposes of importing, this fact may be useful.", new JComponent[] { jLabel11, jTextField1 });
     WhatIsThisMouseListener.batchAdd("Clear Index", "The DCC and DC6 file formats have support for fully opaque indices and a single transparent index.  However, there is nothing preventing these file formats from using any index in the palette as transparent, as the transparency support is simply in some form of run-length encoding (in the pixel data for DC6 files and in the palette data for DCC files).  Diablo II assumes that the transparent index for these files is always 0; however, in the event that it needs to be changed for another use, that can be done here.", new JComponent[] { jLabel12, jTextField2, jLabel13, jTextField3 });
     final SixDiceFrame scoped_this = this;
     ApprovalButtonPanel approvalButtonPanel = new ApprovalButtonPanel()
       {
         public boolean apply()
         {
           String str = null;
           if (!MathUtilities.isNumber(dcc_codec_clear_index.getText(), 10) || !MathUtilities.isBoundedBy(Integer.valueOf(dcc_codec_clear_index.getText()).intValue(), SixDiceFrame.CONFIG_ELEMENT_CODEC_DCC_CLEAR_INDEX.getMinimumBound(), SixDiceFrame.CONFIG_ELEMENT_CODEC_DCC_CLEAR_INDEX.getMaximumBound()))
           {
             str = "DCC codec clear index must be within [" + SixDiceFrame.CONFIG_ELEMENT_CODEC_DCC_CLEAR_INDEX.getMinimumBound() + ',' + SixDiceFrame.CONFIG_ELEMENT_CODEC_DCC_CLEAR_INDEX.getMaximumBound() + ']';
           }
           if (!MathUtilities.isNumber(dc6_codec_clear_index.getText(), 10) || !MathUtilities.isBoundedBy(Integer.valueOf(dc6_codec_clear_index.getText()).intValue(), SixDiceFrame.CONFIG_ELEMENT_CODEC_DC6_CLEAR_INDEX.getMinimumBound(), SixDiceFrame.CONFIG_ELEMENT_CODEC_DC6_CLEAR_INDEX.getMaximumBound()))
           {
             str = "DC6 codec clear index must be within [" + SixDiceFrame.CONFIG_ELEMENT_CODEC_DC6_CLEAR_INDEX.getMinimumBound() + ',' + SixDiceFrame.CONFIG_ELEMENT_CODEC_DC6_CLEAR_INDEX.getMaximumBound() + ']';
           }
           if (multi_frame_separator.getText().length() < 1)
           {
             str = "Separator text cannot be empty.";
           }
           if (str != null) {
             JOptionPane.showMessageDialog((Component)scoped_this, str, "Input Error", 0);
             return false;
           } 
           boolean bool = SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_UI_LAYOUT, Integer.valueOf(layout_combo_box.getSelectedIndex()));
           SixDiceFrame.this.m_configuration.setValue(SixDiceFrame.CONFIG_ELEMENT_UI_IMAGE_BACKGROUND, background_color_button.getColor());
           SixDiceFrame.this.m_configuration.setValue(SixDiceFrame.CONFIG_ELEMENT_UI_IMAGE_UNDERLAY, underlay_color_button.getColor());
           SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_UI_CLEAR_ON_DND_IMPORTS, Boolean.valueOf(drag_and_drop_import_clear.isSelected()));
           SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_IMPORT_INSERTS, Boolean.valueOf(insert_imported_frames.isSelected()));
           SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_FRAME_SPLIT_INSERTS, Boolean.valueOf(insert_split_frames.isSelected()));
           SixDiceFrame.this.m_configuration.setValue(SixDiceFrame.CONFIG_ELEMENT_IMPORT_VIRTUAL_CLEAR_COLOR, virtual_clear_color_enabled.isSelected() ? virtual_clear_color_button.getColor() : null);
           SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_EXPORT_CLEAR_TO_VIRTUAL, Boolean.valueOf((virtual_clear_color_enabled.isSelected() && clear_to_virtual_on_export.isSelected())));
           SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_PORT_SEPARATOR, multi_frame_separator.getText());
           SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_CODEC_DC6_CLEAR_INDEX, Integer.valueOf(dc6_codec_clear_index.getText()));
           SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_CODEC_DCC_CLEAR_INDEX, Integer.valueOf(dcc_codec_clear_index.getText()));
           SixDiceFrame.this.m_configuration.setValue((ConfigurationElement)SixDiceFrame.CONFIG_ELEMENT_UI_SAVED_FILES_RELOADED, Boolean.valueOf(load_on_save.isSelected()));
           SixDiceFrame.this.applyConfiguration(bool);
           return true;
         }
         public void close() {
           preferences_dialog.setVisible(false);
           preferences_dialog.dispose();
         }
       };
     jDialog.setContentPane((Container)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL), new Pair[] { new Pair(new ContentConstructorTabbedPane(new ContentConstructorTabbedPane.TabData[] { new ContentConstructorTabbedPane.TabData("User Interface Preferences", (JComponent)new ComponentConstructorPanel((LayoutManager)new SingleComponentPositioningLayout(0.5D, 0.0D), new JComponent[] { (JComponent)new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 3, 2, 2, false), new JComponent[] { jLabel1, jComboBox, jLabel2, (JComponent)colorSelectorButton1, jLabel3, (JComponent)colorSelectorButton2 }) })), new ContentConstructorTabbedPane.TabData("File Input/Output Settings", (JComponent)new ComponentConstructorPanel((LayoutManager)new SingleComponentPositioningLayout(0.5D, 0.0D), new JComponent[] { (JComponent)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL), new JComponent[] { (JComponent)(new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 4, 2, 2, false), new JComponent[] { jLabel4, jCheckBox1, jLabel5, jCheckBox2, jLabel6, jCheckBox3, jLabel7, jCheckBox4 })).setBorderAndReturn(new TitledBorder(new EtchedBorder(), "Interface Behavior")), (JComponent)(new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 3, 2, 2, false), new JComponent[] { jLabel8, jCheckBox5, jLabel9, (JComponent)colorSelectorButton3, jLabel10, jCheckBox6 })).setBorderAndReturn(new TitledBorder(new EtchedBorder(), "Image Translation")), (JComponent)(new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 1, 2, 2, false), new JComponent[] { jLabel11, jTextField1 })).setBorderAndReturn(new TitledBorder(new EtchedBorder(), "Other Settings")) }) })), new ContentConstructorTabbedPane.TabData("Codec Settings", (JComponent)new ComponentConstructorPanel((LayoutManager)new SingleComponentPositioningLayout(0.5D, 0.0D), new JComponent[] { (JComponent)new ComponentConstructorPanel((LayoutManager)new SpongyLayout(SpongyLayout.Orientation.VERTICAL), new JComponent[] { (JComponent)(new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 1, 2, 2, false), new JComponent[] { jLabel12, jTextField2 })).setBorderAndReturn(new TitledBorder(new EtchedBorder(), "DC6 Codec")), (JComponent)(new ComponentConstructorPanel((LayoutManager)new InformalGridLayout(2, 1, 2, 2, false), new JComponent[] { jLabel13, jTextField3 })).setBorderAndReturn(new TitledBorder(new EtchedBorder(), "DCC Codec")) }) })) }), SpongyLayout.PRIORITY_NORMAL), new Pair(approvalButtonPanel, SpongyLayout.PRIORITY_PREFERRED) }));
     jDialog.pack();
     jDialog.setLocationRelativeTo((Component)this);
     jDialog.setVisible(true);
   }
   protected void applyConfiguration(boolean paramBoolean) {
     if (paramBoolean)
     {
       setContentPane(((Integer)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_UI_LAYOUT)).intValue());
     }
     this.m_image_component.setBackground((Color)this.m_configuration.getValue(CONFIG_ELEMENT_UI_IMAGE_BACKGROUND));
     this.m_image_component.setUnderlay((Color)this.m_configuration.getValue(CONFIG_ELEMENT_UI_IMAGE_UNDERLAY));
     this.m_core.setVirtualTransparentColor((Color)this.m_configuration.getValue(CONFIG_ELEMENT_IMPORT_VIRTUAL_CLEAR_COLOR));
     this.m_core.setTransparentToVirtualOnSave(((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_EXPORT_CLEAR_TO_VIRTUAL)).booleanValue());
   }
   protected RestrictableIndexColorModel getPalette() {
     RestrictableIndexColorModel restrictableIndexColorModel = Diablo2DefaultPalettes.PALETTE_MAP.get(this.m_palette_box.getModel().getSelectedItem());
     assert restrictableIndexColorModel != null;
     restrictableIndexColorModel = restrictableIndexColorModel.deriveWithTransparentInices(new int[] { 0 });
     return restrictableIndexColorModel;
   }
   protected void resetListSelection() {
     ((IntegerRangeListModel)this.m_direction_list.getModel()).setMaximum((this.m_core.getAnimation() == null) ? 0 : this.m_core.getAnimation().getDirectionCount());
     ((IntegerRangeListModel)this.m_frame_list.getModel()).setMaximum((this.m_core.getAnimation() == null) ? 0 : this.m_core.getAnimation().getFrameCount());
     if (this.m_direction_list.getModel().getSize() > 0) this.m_direction_list.setSelectedIndex(0); 
     if (this.m_frame_list.getModel().getSize() > 0) this.m_frame_list.setSelectedIndex(0);
   }
   protected void updateDisplay() {
     if (this.m_core.getAnimation() != null) {
       if (this.m_direction_list.getModel().getSize() != this.m_core.getAnimation().getDirectionCount())
       {
         ((IntegerRangeListModel)this.m_direction_list.getModel()).setMaximum(this.m_core.getAnimation().getDirectionCount());
       }
       if (this.m_frame_list.getModel().getSize() != this.m_core.getAnimation().getFrameCount())
       {
         ((IntegerRangeListModel)this.m_frame_list.getModel()).setMaximum(this.m_core.getAnimation().getFrameCount());
       }
       if (this.m_frame_list.getSelectedIndex() == -1 && this.m_core.getAnimation().getFrameCount() > 0)
       {
         this.m_frame_list.setSelectedIndex(0);
       }
       if (this.m_direction_list.getSelectedIndex() == -1 && this.m_core.getAnimation().getDirectionCount() > 0)
       {
         this.m_direction_list.setSelectedIndex(0);
       }
       if (this.m_frame_list.getSelectedIndex() >= this.m_core.getAnimation().getFrameCount())
       {
         this.m_frame_list.setSelectedIndex(0);
       }
       if (this.m_direction_list.getSelectedIndex() >= this.m_core.getAnimation().getDirectionCount())
       {
         this.m_direction_list.setSelectedIndex(0);
       }
     } 
     if (this.m_core.getAnimation() == null || this.m_frame_list.getSelectedIndex() == -1 || this.m_direction_list.getSelectedIndex() == -1) {
       this.m_image_component.setImage(this.m_splash_image);
       this.m_image_scroll_pane.getViewport().setView((Component)this.m_image_component);
       this.m_offset_x.setText("-");
       this.m_offset_y.setText("-");
       this.m_label_width.setText("-");
       this.m_label_height.setText("-");
       this.m_offset_x.setEnabled(false);
       this.m_offset_y.setEnabled(false);
       ((IntegerRangeListModel)this.m_direction_list.getModel()).setMaximum(0);
       ((IntegerRangeListModel)this.m_frame_list.getModel()).setMaximum(0);
       this.m_warnings_panel.setPainted(false);
     } else {
       final AnimationFrame frame = this.m_core.getAnimation().getFrame(this.m_direction_list.getSelectedIndex(), this.m_frame_list.getSelectedIndex());
       this.m_offset_x.setText(Integer.toString(animationFrame.getXOffset()));
       this.m_offset_y.setText(Integer.toString(animationFrame.getYOffset()));
       this.m_label_width.setText(Integer.toString(animationFrame.getImage().getWidth()));
       this.m_label_height.setText(Integer.toString(animationFrame.getImage().getHeight()));
       if (((Boolean)this.m_configuration.getValue((ConfigurationElement)CONFIG_ELEMENT_UI_IMAGE_RENDERED_IN_PALETTE)).booleanValue()) {
         (new Thread("Waiting Dialog")
           {
             public void run()
             {
               SixDiceFrame.this.m_waiting_dialog_displayer.setVisible(true);
               try {
                 SixDiceFrame.this.m_image_component.setImage(SixDiceFrame.this.getPalette().redraw(frame.getImage()));
               } finally {
                 SixDiceFrame.this.m_waiting_dialog_displayer.setVisible(false);
               } 
               SixDiceFrame.this.m_image_scroll_pane.getViewport().setView((Component)SixDiceFrame.this.m_image_component);
             }
           }).start();
       } else {
         this.m_image_component.setImage(animationFrame.getImage());
         this.m_image_scroll_pane.getViewport().setView((Component)this.m_image_component);
       } 
       this.m_offset_x.setEnabled(true);
       this.m_offset_y.setEnabled(true);
       ((IntegerRangeListModel)this.m_direction_list.getModel()).setMaximum(this.m_core.getAnimation().getDirectionCount());
       ((IntegerRangeListModel)this.m_frame_list.getModel()).setMaximum(this.m_core.getAnimation().getFrameCount());
       this.m_warnings_panel.setPainted(((this.m_core.getAnimation().getWarnings()).length > 0));
     } 
   }
 }