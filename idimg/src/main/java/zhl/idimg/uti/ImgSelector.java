package zhl.idimg.uti;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImgSelector extends JFrame {

	private static final long serialVersionUID = 1L;


	private JPanel infoPanel;
	
	private JButton btnBar;

	private HashSet<File> files = new HashSet<>();
	
	private HashMap<Object, JLabel> itemMap = new HashMap<>();
	
	private Object lock = new Object();

	public ImgSelector() {
		super();
		setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
			@Override
			public void drop(DropTargetDropEvent dtde) {
				try {
					// 如果拖入的文件格式受支持
					if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						// 接收拖拽来的数据
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						@SuppressWarnings("unchecked")
						List<File> list = (List<File>) (dtde.getTransferable()
								.getTransferData(DataFlavor.javaFileListFlavor));
						for (File file : list) {
							addFile(file);
						}
						// 指示拖拽操作已完成
						dtde.dropComplete(true);
					} else {
						// 拒绝拖拽来的数据
						dtde.rejectDrop();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
		setSize(500, 400);
		setLayout(new FlowLayout());
		infoPanel = new JPanel();
		infoPanel.setSize(500,320);
		infoPanel.setVisible(true);
		add(infoPanel);
		
		btnBar = new JButton("确定");
		btnBar.addActionListener(e->{
			synchronized (lock) {
				lock.notifyAll();
			}
			this.setVisible(false);
		});
		add(btnBar);
	}
	
	public void addFile(File file){
		if(file.isFile()){
			String name = file.getName();
			if(name.endsWith(".bmp") && !name.endsWith("cn.bmp")){
				if(!files.contains(file)){
					files.add(file);
					itemMap.put(file, addInfoItem(file.getPath()));
				}
			}
		}
	}
	
	public JLabel addInfoItem(String name){
		JLabel label = new JLabel(name);
		infoPanel.add(label);
		return label;
	}
	
	public File[] getSelectFiles(){
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return files.toArray(new File[files.size()]);
	}
	
 }
