package com.ligh.whiteboardpainting.model;


import java.util.List;
import com.google.gson.annotations.Expose;

/**
 * 画板画出的	样式，数据整合对象
 */
public class StyleObjAttr {
	//橡皮擦选定擦除的对象id
	@Expose
	private int objId;
	//删除的id的数量，橡皮擦独有
	@Expose
	private int delNumber;
	//文档批注模式，需要记录页数，删除的时候根据页数删除，生成的时候也根据页数生成
	@Expose
	private int filePage = -1;
	//类型
	@Expose
	private String styleTag;
	//实心
	@Expose
	private boolean isFill;
	//自由画笔每个点坐标的存储
	@Expose
	private List<SavePointModel> penPoint;
	@Expose
	private String editText;
	//定义坐标
	@Expose
	private int startX,startY,endX,endY;
	//画笔颜色
	@Expose
	private int paintColor;
	//画笔大小
	@Expose
	private int paintSize;
	public StyleObjAttr(float startX, float startY) {
		this.startX = (int) startX;
		this.startY = (int) startY;
	}
	public StyleObjAttr() {
		this(0,0);
	}

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public int getDelNumber() {
		return delNumber;
	}

	public void setDelNumber(int delNumber) {
		this.delNumber = delNumber;
	}

	public int getFilePage() {
		return filePage;
	}

	public void setFilePage(int filePage) {
		this.filePage = filePage;
	}

	public void setStartPoint(float startX,float startY) {
		this.startX = (int)startX;
		this.startY = (int) startY;
	}
	public void setEndPoint(float endX,float endY) {
		this.endX = (int) endX;
		this.endY = (int) endY;
	}
	public void setRectPoint(float startX,float startY, float endX, float endY) {
		this.startX = (int) startX;
		this.startY = (int) startY;
		this.endX = (int) endX;
		this.endY = (int) endY;
	}

	public String getStyleTag() {
		return styleTag;
	}

	public void setStyleTag(String styleTag) {
		this.styleTag = styleTag;
	}

	public int getStartX() {
		return startX;
	}
	public boolean isFill() {
		return isFill;
	}

	public void setIsFill(boolean isFill) {
		this.isFill = isFill;
	}

	public String getEditText() {
		return editText;
	}

	public void setEditText(String editText) {
		this.editText = editText;
	}

	public void setStartX(float startX) {
		this.startX = (int) startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(float startY) {
		this.startY = (int) startY;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(float endX) {
		this.endX = (int) endX;
	}

	public int getEndY() {
		return endY;
	}

	public void setEndY(float endY) {
		this.endY = (int) endY;
	}

	public List<SavePointModel> getPenPoint() {
		return penPoint;
	}

	public void setPenPoint(List<SavePointModel> penPoint) {
		this.penPoint = penPoint;
	}

	public int getPaintSize() {
		return paintSize;
	}

	public void setPaintSize(int paintSize) {
		this.paintSize = paintSize;
	}

	public int getPaintColor() {
		return paintColor;
	}

	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
	}

	@Override
	public String toString() {
		return "StyleObjAttr{" +
				"objId=" + objId +
				", delNumber=" + delNumber +
				", filePage=" + filePage +
				", styleTag='" + styleTag + '\'' +
				", isFill=" + isFill +
				", editText='" + editText + '\'' +
				", startX=" + startX +
				", startY=" + startY +
				", endX=" + endX +
				", endY=" + endY +
				", paintColor=" + paintColor +
				", paintSize=" + paintSize +
				'}';
	}

	public static class SavePointModel{
		@Expose
		public int x;
		@Expose
		public int y;
		public SavePointModel(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}

}