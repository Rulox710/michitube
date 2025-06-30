package app.engine.maker.guis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import app.Constants;
import app.engine.Observer;
import app.engine.readers.PropertiesM;
import app.engine.readers.TranslationM;

public class MainGUI extends JFrame implements Observer {

    private MenuGUI menuGUI;
    private OptionsGUI optionGUI;
    private LayersGUI layersGUI;
    private SheetGUI sheetGUI;
    
    /**
     * Constructor de la clase ConfigGUI.
     * Configura y muestra la ventana principal.
     */
    public MainGUI() {
        int taskbarHeight = Integer.parseInt(
            PropertiesM.getVtuberProperty("windows_taskbar_height")
        );
        setLayout(null);
        setResizable(true);
        setUndecorated(false);
        setBackground(Color.BLUE);
        setTitle(TranslationM.getTranslatedLabel("gui_title"));
        setPreferredSize(
            new Dimension(Constants.FULLSCREEN_WIDTH,
                Constants.FULLSCREEN_HEIGHT-taskbarHeight)
        );
        int FIFTH_WIDTH = Constants.FULLSCREEN_WIDTH / 5;
        int HALF_HEIGHT = (Constants.FULLSCREEN_HEIGHT) / 2;
        setMinimumSize(new Dimension(FIFTH_WIDTH*3, HALF_HEIGHT+100));
        setSize(getPreferredSize());
        //window.setExtendedState(JFrame.MAXIMIZED_BOTH);


        // Menus
        menuGUI = new MenuGUI(this);
        menuGUI.addObserver(this);


        
        // Panel izquierdo
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBounds(0, 0, FIFTH_WIDTH, Constants.FULLSCREEN_HEIGHT-taskbarHeight-60);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 10, 20, 10),
            BorderFactory.createEtchedBorder(Color.CYAN, Color.BLACK)
        ));
        optionGUI = new OptionsGUI(leftPanel);
        optionGUI.addObserver(this);
        // JScrollPane scroll = (optionGUI.getPanel());
        // scroll.setBounds(0, 0, FIFTH_WIDTH-20, Constants.FULLSCREEN_HEIGHT-taskbarHeight-40);
        // window.add(scroll);
        

        // Panel superior del panel central
        JPanel layerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        layerPanel.setBackground(Constants.BLACK_78);
        int TOP_HEIGHT = 240;
        layerPanel.setBounds(FIFTH_WIDTH, 0, FIFTH_WIDTH*4-14, TOP_HEIGHT);
        // topPanel.setBorder(BorderFactory.createEtchedBorder(Color.CYAN, Color.BLACK));
        layersGUI = new LayersGUI(layerPanel);
        layersGUI.addObserver(this);


        // Panel central
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        centerPanel.setBackground(Color.BLACK);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createEtchedBorder(Color.CYAN, Color.BLACK)
        ));
        centerPanel.setBounds(FIFTH_WIDTH, TOP_HEIGHT, FIFTH_WIDTH*4-14, Constants.FULLSCREEN_HEIGHT-taskbarHeight-TOP_HEIGHT-60);
        sheetGUI = new SheetGUI(centerPanel);
        sheetGUI.addObserver(this);
        


        // Panel derecho con botones
        // int layers = 7;
        // JPanel rightPanel = new JPanel();
        // rightPanel.setLayout(new GridLayout(layers, 1, 5, 5)); 
        // rightPanel.setBackground(Color.GRAY);
        // rightPanel.setPreferredSize(new Dimension(FIFTH_WIDTH, Constants.FULLSCREEN_HEIGHT - taskbarHeight));
        // rightPanel.setMaximumSize(rightPanel.getPreferredSize());
        
        // String[] labels_layers = {
        //     "label_body",
        //     "label_eyes",
        //     "label_mouth",
        //     "label_rhand",
        //     "label_lhand",
        //     "label_front",
        //     "label_back",
        // };
        // for (int i = 0; i < layers; i++) {
        //     LayerButton button = new LayerButton(
        //         Constants.FULLSCREEN_WIDTH / 5,
        //         Constants.FULLSCREEN_HEIGHT / layers, i,
        //         labels_layers[i]
        //     );
        //     rightPanel.add(button);
        //     layerButtons[i] = button;
        // }
        
        // Agregar paneles
        add(leftPanel);
        add(layerPanel);
        add(centerPanel);

        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveConfig();
                dispose();
                System.exit(0);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newHeight = Math.max(getHeight(), HALF_HEIGHT)-60;
                leftPanel.setBounds(0, 0, FIFTH_WIDTH, newHeight);
                optionGUI.changeSize();
                leftPanel.revalidate();
                leftPanel.repaint();

                int newWidth = Math.max(getWidth()-FIFTH_WIDTH-14, FIFTH_WIDTH);
                layerPanel.setBounds(FIFTH_WIDTH, 0, newWidth, TOP_HEIGHT);
                layersGUI.changeSize();
                layerPanel.revalidate();
                layerPanel.repaint();

                newHeight = Math.max(getHeight(), HALF_HEIGHT)-TOP_HEIGHT-60;
                centerPanel.setBounds(FIFTH_WIDTH, TOP_HEIGHT, newWidth, newHeight);
                sheetGUI.changeSize();
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });
    }

    public void saveConfig() {
        PropertiesM.saveVtuberProperties();
        PropertiesM.saveAppProperties();
    }

    @Override
    public void update(char event, Object data) {
        switch (event) {
        case 'l':
            PropertiesM.setAppProperty("language", (String) data);
            menuGUI.updateLanguage();
            optionGUI.updateLanguage();
            layersGUI.updateLanguage();
            break;
        case 'd':
            System.out.println((String)data);
            PropertiesM.setAppProperty("default_dir", (String) data);
            break;
        case 'm':
            PropertiesM.setVtuberProperty("mouse_detection", String.valueOf(data));
            break;
        case 'k':
            PropertiesM.setVtuberProperty("keyboard_detection", String.valueOf(data));
            break;
        case 'n':
            PropertiesM.setVtuberProperty("microphone_detection", String.valueOf(data));
            break;
        case 'u':
            PropertiesM.setVtuberProperty("microphone_ups", String.valueOf(data));
            break;
        case 'c':
            PropertiesM.setVtuberProperty("microphone_channels", String.valueOf(data));
            break;
        case 's':
            PropertiesM.setVtuberProperty("microphone_threshold", String.valueOf(data));
            break;
        case 'f':
            PropertiesM.setVtuberProperty("frames_per_second", String.valueOf(data));
            break;
        case 'L':
            sheetGUI.selectLayer((int)data);
            break;
        case (char)0:
        case (char)1:
        case (char)2:
        case (char)3:
        case (char)4:
        case (char)5:
        case (char)6:
            sheetGUI.addImage((int)event, (File)data);
            break;
        default:
            break;
        }
    }
}
