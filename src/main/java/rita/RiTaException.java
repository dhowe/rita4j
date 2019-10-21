package rita;

public class RiTaException extends RuntimeException
{
  public RiTaException()
  {
    super();
  }

  public RiTaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public RiTaException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public RiTaException(String message)
  {
    super(message);
  }

  public RiTaException(Throwable cause)
  {
    super(cause);
  }

}
