package Common;

import java.io.Serializable;

public class TransferMessage implements Serializable
{
	public static final byte TRANSFER_END = 0, OPERATION_REQUEST = 1, METADATA = 2, DATA = 3;
	
	private byte messageType;
	private byte data[];
	private long dataCapacity;
	
	public TransferMessage()
	{
		this((byte) 0, 0, null);
	}
	
	public TransferMessage(byte messageType)
	{
		this(messageType, 0, null);
	}
	
	public TransferMessage(byte messageType, long dataCapacity)
	{
		this(messageType, dataCapacity, null);
	}
	
	public TransferMessage(byte messageType, long dataCapacity, byte data[])
	{
		this.messageType = messageType;
		this.dataCapacity = dataCapacity;
		this.data = data;
	}
	
	public void setData(byte data[])
	{
		this.data = data;
	}
	
	public void setDataCapacity(long capacity)
	{
		this.dataCapacity = capacity;
	}
	
	public void setMessageType(byte type)
	{
		this.messageType = type;
	}

	public byte getMessageType()
	{
		return messageType;
	}

	public byte[] getData()
	{
		return data;
	}

	public long getDataCapacity()
	{
		return dataCapacity;
	}
	
}
