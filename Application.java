import gui.MainFrame;
import java.awt.EventQueue;
import java.io.IOException;
import javax.swing.JOptionPane;

public class Application {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				new MainFrame();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}