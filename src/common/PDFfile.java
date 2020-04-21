/**
 * common package contains client/files implemented by client
 */
package common;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * PDF file to be pass 
 * @author saleh
 */
public class PDFfile implements Serializable {
	
	private String Description=null;
	private String fileName=null;	
	private String filePath =null;
	private String value;
	ArrayList<String> arrName;

	private int size=0;
	public  byte[] mybytearray;
	
	
	public void initArray(int size)
	{
		mybytearray = new byte [size];	
	}
	
	public PDFfile( String fileName) {
		this.fileName = fileName;
	}
	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public byte[] getMybytearray() {
		return mybytearray;
	}
	
	public byte getMybytearray(int i) {
		return mybytearray[i];
	}

	public void setMybytearray(byte[] mybytearray) {
		
		for(int i=0;i<mybytearray.length;i++)
		this.mybytearray[i] = mybytearray[i];
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public ArrayList<String> getArrName() {
		return arrName;
	}

	public void setArrName(ArrayList<String> arrName) {
		this.arrName = arrName;
	}
}

