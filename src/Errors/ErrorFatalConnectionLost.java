package Errors;

public class ErrorFatalConnectionLost extends Exception
{
	public ErrorFatalConnectionLost()
	{
		super("Main server connection lost");
	}
}
