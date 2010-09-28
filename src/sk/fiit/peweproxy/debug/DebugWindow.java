package sk.fiit.peweproxy.debug;

import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public final class DebugWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTextPane jTextPane = null;
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextPane());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane() {
				@Override
				public boolean getScrollableTracksViewportWidth() {
					return false;
				}
			};
			jTextPane.setEditable(false);
			jTextPane.setToolTipText("Right click to clone window into separate VM ...");
			jTextPane.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						System.out.println(getJTextPane().getDocument().getClass());
						try {
							File dir = new File(getClass().getResource("DebugWindow.class").toURI()).getParentFile()
								.getParentFile().getParentFile().getParentFile().getParentFile();
							final Process p = Runtime.getRuntime().exec("javaw sk.fiit.redeemer.test.DebugWindow",null,dir);
							ObjectOutputStream stream = new ObjectOutputStream(p.getOutputStream());
							stream.flush();
							stream.writeObject("Serialized output: "+getTitle());
							stream.flush();
							stream.writeObject((DefaultStyledDocument)getJTextPane().getDocument());
							stream.flush();
							System.out.println("Zapisane");
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
		return jTextPane;
	}
	
	static class ReturnObject {
		Object returnObj = null;
		
		synchronized void setReturnObject(Object retObj) {
			returnObj = retObj;
			this.notifyAll();
		}
	}

	static Point lastLoc = new Point(20, 20);
	
	public static DebugWindow newWindow(final String title, final boolean factory) {
		final ReturnObject rObj = new ReturnObject();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DebugWindow thisClass = new DebugWindow();
				thisClass.setTitle(title);
				thisClass.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				//thisClass.setVisible(true);
				lastLoc.translate(20, 20);
				thisClass.setLocation(lastLoc);
				rObj.setReturnObject(thisClass);
				if (factory)
					thisClass.setVisible(false);
			}
		});
		synchronized (rObj) {
			if (rObj.returnObj == null)
				try {
					rObj.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		return (DebugWindow)rObj.returnObj;
	}
	
	public void printText(final String text, final Color color, final int headStart, final int headEnd, final boolean setVisible) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Document doc = getJTextPane().getDocument();
				SimpleAttributeSet style = new SimpleAttributeSet();
				SimpleAttributeSet style2 = new SimpleAttributeSet();
				StyleConstants.setForeground(style, color);
				StyleConstants.setForeground(style2, color);
				StyleConstants.setBold(style2, true);
				StyleConstants.setUnderline(style2, true);
				try {
					doc.insertString(doc.getLength(), text.substring(0, headStart), style);
					doc.insertString(doc.getLength(), text.substring(headStart, headEnd), style2);
					doc.insertString(doc.getLength(), text.substring(headEnd, text.length()), style);
					if (setVisible) {
						setVisible(true);
						getJTextPane().requestFocusInWindow();
						requestFocus();
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void hideWindow() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setVisible(false);
			}
		});
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DebugWindow thisClass = new DebugWindow();
				thisClass.setTitle("Test");
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//thisClass.setLocation(lastLoc);
				thisClass.setVisible(true);
				ObjectInputStream stream;
				try {
					thisClass.setTitle("Idem citat ...");
					stream = new ObjectInputStream(System.in);
					String title = (String) stream.readObject(); 
					thisClass.setTitle(title);
					DefaultStyledDocument doc = (DefaultStyledDocument) stream.readObject();
					thisClass.getJTextPane().setDocument(doc);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public DebugWindow() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 400);
		this.setContentPane(getJContentPane());
		this.setTitle("Debug output");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getJScrollPane(), gridBagConstraints);
		}
		return jContentPane;
	}

}
