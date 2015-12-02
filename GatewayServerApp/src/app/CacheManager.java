package app;

import java.util.Hashtable;

public class CacheManager {
	private Hashtable<String, ByteCache> cache;
	
	private long maxSize;
	
	
	public CacheManager(long _maxSize){
		maxSize = _maxSize;
		
		cache = new Hashtable<String, ByteCache>();
	}
	
	public boolean add(ByteCache _byteCache){
		if(getOccupiedSize() + _byteCache.getTargetSize() > maxSize)
			return false;
		
		if(contains(_byteCache.getFileName()))
			return false;
		
		cache.put(_byteCache.getFileName(), _byteCache);
		return true;
	}
	
	public boolean contains(String _fileName){
		return cache.containsKey(_fileName);
	}
	
	public ByteCache getByteCache(String _fileName){
		return cache.get(_fileName);
	}
	
	public long getOccupiedSize(){
		long size = 0;
		for(ByteCache bc : cache.values()){
			size += bc.getTargetSize();
		}
		
		return size;
	}
	
	public ByteCache popLeastUsed(){
		if(cache.size() == 0)
			return null;
		
		String key = "";
		int curAccessCount = 10000000;
		for(ByteCache bc : cache.values()){
			if(curAccessCount > bc.getAccessCount()){
				key = bc.getFileName();
				curAccessCount = bc.getAccessCount();
			}
		}
		
		return cache.remove(key);
	}
}
