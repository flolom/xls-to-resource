package com.hotcocoacup.mobiletools.xlstoresouces.model;


public class Entry {

	private String fileName;
	private int sheet = 0;
	private int rowStart = 1;
	private int rowEnd = -1;
	private String columnKey = "A";
	private String columnValue = "B";
	private String groupBy = null;
	
	/**
	 * @return the xlsFile
	 */
	public String getXlsFile() {
		return fileName;
	}
	/**
	 * @param xlsFile the xlsFile to set
	 */
	public void setXlsFile(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the sheet
	 */
	public int getSheet() {
		return sheet;
	}
	/**
	 * @param sheet the sheet to set
	 */
	public void setSheet(int sheet) {
		this.sheet = sheet;
	}
	/**
	 * @return the rowStart
	 */
	public int getRowStart() {
		return rowStart;
	}
	/**
	 * @param rowStart the rowStart to set
	 */
	public void setRowStart(int rowStart) {
		this.rowStart = rowStart;
	}
	/**
	 * @return the rowEnd
	 */
	public int getRowEnd() {
		return rowEnd;
	}
	/**
	 * @param rowEnd the rowEnd to set
	 */
	public void setRowEnd(int rowEnd) {
		this.rowEnd = rowEnd;
	}
	/**
	 * @return the columnKey
	 */
	public String getColumnKey() {
		return columnKey;
	}
	/**
	 * @param columnKey the columnKey to set
	 */
	public void setColumnKey(String columnKey) {
		this.columnKey = columnKey;
	}
	/**
	 * @return the columnValue
	 */
	public String getColumnValue() {
		return columnValue;
	}
	/**
	 * @param columnValue the columnValue to set
	 */
	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}
	/**
	 * @return the groupBy
	 */
	public String getGroupBy() {
		return groupBy;
	}
	/**
	 * @param groupBy the groupBy to set
	 */
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	
	
}
