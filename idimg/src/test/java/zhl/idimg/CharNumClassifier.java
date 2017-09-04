package zhl.idimg;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class CharNumClassifier extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel charNumPanel;

	private JPanel infoPanel;

	private HashSet<File> files;

	public CharNumClassifier() {
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
	}
	
	public void addFile(File file){
		if(file.isFile()){
			String name = file.getName();
			if(name.endsWith(".bmp") && !name.endsWith("cn.bmp")){
				if(!files.contains(file)){
					files.add(file);
				}
			}
		}
		
	}
}
