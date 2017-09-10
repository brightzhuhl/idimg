package zhl.idimg.img;

import java.util.List;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;


public class SVMRecognizer {
	
	private svm_node[][] trainingSet;
	
	private double[] labelSet;
	
	private svm_model model;
	
	public SVMRecognizer(List<ImgExample> examples){
		init(examples);
		train();
	}
	
	public char predict(byte[] example){
		svm_node[] testVector = formatNode(example);
		Double label = svm.svm_predict(model, testVector);
		return (char) label.intValue();
	}
	
	private void init(List<ImgExample> examples){
		int vNum = 0;
		for(ImgExample e:examples){
			vNum += e.getImgVectors().size();
		}
		
		trainingSet = new svm_node[vNum][];
		labelSet = new double[vNum];
		
		int i = 0;
		for(ImgExample e:examples){
			double label = e.getLabel();
			for(byte[] v:e.getImgVectors()){
				trainingSet[i] = formatNode(v);
				labelSet[i] = label;
				i++;
			}
		}
	}
	
	private svm_node[] formatNode(byte[] imgVector){
		svm_node[] img = new svm_node[imgVector.length];
		for(int vi=0; vi<img.length; vi++){
			svm_node pixel = new svm_node();
			pixel.index = vi;
			pixel.value = imgVector[vi];
			img[vi] = pixel;
		}
		return img;
	}
	
	private void train(){
		svm_problem p = new svm_problem();
		p.l = labelSet.length;
		p.x = trainingSet;
		p.y = labelSet;
		
		svm_parameter param = new svm_parameter();
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		
		model = svm.svm_train(p, param);
	}
}
