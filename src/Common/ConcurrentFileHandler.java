package Common;

import java.io.File;
import java.util.concurrent.Semaphore;

public class ConcurrentFileHandler
{
	private File file;
	private Semaphore readMutex, globalMutex, enter;
	private int numOfReaders, numOfWriters;
	
	public ConcurrentFileHandler(File file)
	{
		this.file = file;
		enter = new Semaphore(1, true);   // FIFO semaphore
		readMutex = new Semaphore(1);
		globalMutex = new Semaphore(1);
		numOfReaders = 0;
		numOfWriters = 0;
	}
	
	public synchronized File getFile()
	{
		return file;
	}
	
	public synchronized void changeFile(File newFile)
	{
		this.file = newFile;
	}
	
	public void startRead()
	{
		enter.acquireUninterruptibly();
		readMutex.acquireUninterruptibly();
		numOfReaders++;
		if(numOfReaders == 1) globalMutex.acquireUninterruptibly();  // first one "fights" for permit to edit file
		readMutex.release();
		enter.release();
	}
	
	public void finishRead()
	{
		readMutex.acquireUninterruptibly();
		numOfReaders--;
		if(numOfReaders == 0) globalMutex.release();
		readMutex.release();
	}
	
	public void startWrite()
	{
		enter.acquireUninterruptibly();
		globalMutex.acquireUninterruptibly();
		enter.release();
	}
	
	public void finishWrite()
	{
		globalMutex.release();
	}
}
