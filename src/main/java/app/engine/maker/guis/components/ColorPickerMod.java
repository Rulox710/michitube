package app.engine.maker.guis.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.lars.colorpicker.ColorPicker;
import de.lars.colorpicker.utils.ColorPickerStyle;

/**
 * Esta clase estática es para crear un ColorPicker tal como el de
 * {@link de.lars.colorpicker.ColorPicker} pero permitiendo que se
 * pongan cadenas de texto para los botones personalizadas y no solo
 * las predeterminadas "OK" y "Cancel" ya que en la versión 0.4 de su
 * componente tuvo un pequeño error que no permite cambiarlas.
 */
public final class ColorPickerMod {

    private static boolean isCancel = false;

    /**
     * Muestra un diálogo de selección de color. Bloquea el hilo que inició el
     * diálogo.
	 * 
	 * @param title Título del diálogo.
	 * @param textOk Texto mostrado para el botón de Aceptar.
	 * @param textCancel Texto mostrado para el botón de Cancelar.
	 * @param size Tamaño preferido del diálogo.
	 * @param initialColor Color inicial seleccionado por defecto, si es
     *                     <code>null</code> se selecciona el color rojo.
     * 
	 * @return El color seleccionado o <code>null</code> si el botón de canclar
     *         fue seleccionado.
	 */
    public static Color showDialog(String title, String textOk, String textCancel, Dimension size, Color initialColor) {
        ColorPicker cp = new ColorPicker(initialColor != null ? initialColor : Color.RED);
        JDialog dialog = createDialog(cp, title, textOk, textCancel, size);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.dispose();
        if(isCancel) {
            isCancel = false;
            return null;
        } else {
            return cp.getColor();
        }
   }

    /**
     * Crea un nuevo {@link JDialog} con un ColorPicker.
     * 
     * @param cp {@link de.lars.colorpicker.ColorPicker}
     * @param textOk Texto mostrado en el botón de Aceptar.
     * @param textCancel Texto mostrado en el botón de Cancelar.
     * @param size Tamaño preferido del diálogo.
     * @param initialColor Color inicial seleccionado.
     * 
     * @return Un JDialog.
     */
    private static JDialog createDialog(ColorPicker cp, String title, String textOk, String textCancel, Dimension size) {
        Frame window = JOptionPane.getRootFrame();
		JDialog dialog = new JDialog(window, title, true);
		dialog.setPreferredSize(size);
		dialog.getAccessibleContext().setAccessibleDescription(title);
		
		JPanel panelButtons = new JPanel();
		panelButtons.setBackground(ColorPickerStyle.colorBackground);

        JButton btnOk = new JButton(textOk != null ? textOk : "OK");
        panelButtons.add(btnOk);
        btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
        JButton btnCancel = new JButton(textCancel != null ? textCancel : "Cancel");
        panelButtons.add(btnCancel);
        btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isCancel = true;
				dialog.setVisible(false);
			}
		});
        
        if(ColorPickerStyle.colorButtonBackground != null) {
			btnOk.setBackground(ColorPickerStyle.colorButtonBackground);
			btnCancel.setBackground(ColorPickerStyle.colorButtonBackground);
		}
		
		JPanel rootPanel = new JPanel(new BorderLayout());
		rootPanel.setBackground(ColorPickerStyle.colorBackground);
		
		rootPanel.add(cp, BorderLayout.CENTER);
		rootPanel.add(panelButtons, BorderLayout.SOUTH);
		
		dialog.setContentPane(rootPanel);
		return dialog;
    }
}
