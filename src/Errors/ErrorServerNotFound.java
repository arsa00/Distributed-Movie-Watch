package Errors;

public class ErrorServerNotFound extends Exception
{
	public ErrorServerNotFound()
	{
		super("Main server not discovered");
	}
}
