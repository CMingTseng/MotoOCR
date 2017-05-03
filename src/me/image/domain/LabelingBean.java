package me.image.domain;

import java.util.HashMap;
import java.util.Map;

public class LabelingBean {

	private  int[][] labelsImage=null;
	
	private int width=0;
	
	private int height=0;
	
	//key值為由1~map size的標記點
	private Map<Integer,CharaterImage> labelsCharMap=new HashMap<Integer,CharaterImage>();

	public int[][] getLabelsImage() {
		return labelsImage;
	}
	
	

	public int getWidth() {
		return width;
	}



	public void setWidth(int width) {
		this.width = width;
	}



	public int getHeight() {
		return height;
	}



	public void setHeight(int height) {
		this.height = height;
	}



	public void setLabelsImage(int[][] labelsImage) {
		this.labelsImage = labelsImage;
	}

	public Map<Integer, CharaterImage> getLabelsCharMap() {
		return labelsCharMap;
	}

	public void setLabelsCharMap(Map<Integer, CharaterImage> labelsCharMap) {
		this.labelsCharMap = labelsCharMap;
	}
	
	
	
}
