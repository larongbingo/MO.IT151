namespace MOIT151.Core;

public class Result<T>
{

    public bool IsSuccess { get; }
    public string[] ErrorMessages { get; } = [];
    public T? Value { get; }

    public Result(T value)
    {
        IsSuccess = true;
        Value = value;
    }

    public Result(string message)
    {
        IsSuccess = false;
        ErrorMessages = [message];
    }

    public Result(params string[] errors)
    {
        IsSuccess = false;
        ErrorMessages = errors;
    }

    public static Result<T> Success(T value)
    {
        return new Result<T>(value);
    }

    public static Result<Y> Failure<Y>(string message) where Y : class
    {
        return new Result<Y>(message);
    }

    public static Result<T> Failure(params string[] messages)
    {
        return new Result<T>(messages);
    }
}
