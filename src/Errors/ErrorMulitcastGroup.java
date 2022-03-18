package Errors;

public class ErrorMulitcastGroup extends Exception
{
	public ErrorMulitcastGroup()
	{
		this("");
	}
	
	public ErrorMulitcastGroup(String group)
	{
		super("Multicast group " + group + " doesn't work");
	}
}
