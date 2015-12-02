package app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteCache {
	private String fileName;
	private byte[] file;
	private boolean isFinal = false;
	private int targetSize = 0;
	
	private int accessCount = 0;
	
	private ByteArrayOutputStream baos;
	
	public ByteCache(String _fileName, int _targetSize){
		fileName = _fileName;
		targetSize = _targetSize;
		
		baos = new ByteArrayOutputStream(_targetSize);
	}
	
	public String getFileName(){
		return fileName;
	}

	public byte[] getBytes(){
		if(isFinal)
			return file;
		else
			return baos.toByteArray();
	}
	
	public boolean getIsFinal(){
		return isFinal;
	}
	
	public int getTargetSize(){
		return targetSize;
	}
	
	public int getCurrentSize(){
		if(isFinal)
			return file.length;
		else
			return baos.toByteArray().length;
	}
	
	public int getAccessCount(){
		return accessCount;
	}
	
	public void setIsFinal(boolean _isFinal){
		isFinal = _isFinal;
		
		if(isFinal == true){
			file = baos.toByteArray();
			
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void write(byte[] _bytes){
		try {
			baos.write(_bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(byte[] _bytes, int _offset, int _length){
		baos.write(_bytes, _offset, _length);
	}
}
