package tfy_lab3;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) throws IOException {
		Scanner s = new Scanner();
		Types t = null;
		s.OpenFile();
		Analyzer analyzer = new Analyzer(s);
		analyzer.S();
		System.out.println("end");
		/*while ( (t = s.Scan()) != Types.Tend )
			if (t != Types.Terr)
				System.out.println(Scanner.TrimChars(s.TLex) + " - тип " + t + "(" + t.getValue() + ")");*/

    	
        BinaryTreeGUI bgui = new BinaryTreeGUI(analyzer.root);
        bgui.setFocusable(false);

        JFrame frame = new JFrame("Suffix Tree");
        frame.setSize(800, 600);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        frame.getContentPane().add(bgui);
        frame.pack();
        frame.setVisible(true);
        frame.setFocusable(true);
	}
}
