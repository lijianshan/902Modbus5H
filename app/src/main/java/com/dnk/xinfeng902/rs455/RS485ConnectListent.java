package com.dnk.xinfeng902.rs455;

/**
 * 485接收接口
 * 
 * @author Administrator
 * 
 */
public interface RS485ConnectListent {
	void communicationDataReceive(byte[] b);
}
