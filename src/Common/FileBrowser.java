package Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileBrowser extends Thread
{
	private ConcurrentFileHandler fileHandle;
	private String find;
	private boolean found = false;
	
	public FileBrowser(ConcurrentFileHandler fileHandle, String itemToBrowse)
	{
		this.fileHandle = fileHandle;
		find = itemToBrowse;
	}
	
	public boolean browseResult()
	{
		return found;
	}
	
	public String getFileName()
	{
		return fileHandle.getFile().getName();
	}
	
	@Override
	public void run()
	{
		try(BufferedReader fileReader = new BufferedReader(new FileReader(fileHandle.getFile())))
		{
			String tmp;
			while(true)
			{
				fileHandle.startRead();
				tmp = fileReader.readLine();
				fileHandle.finishRead();
				
				if(tmp == null) break;
				
				if(find.equals(tmp))
				{
					found = true;
					break;
				}
			}
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
