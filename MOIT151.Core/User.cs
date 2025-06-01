namespace MOIT151.Core;

public class User(Guid id, string username, string externalId)
{
    public Guid Id { get; } = id;
    public string Username { get; set; } = username;
    public string ExternalId { get; } = externalId;
}