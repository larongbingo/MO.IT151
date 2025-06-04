namespace MOIT151.Core;

public class File(Guid id, string uploadUri, Guid userId, string? uri = null)
{
    public Guid Id { get; private set; } = id;
    public string UploadUri { get; private set; } = uploadUri;
    public Guid UserId { get; private set; } = userId;
    public string? Uri { get; private set; } = uri;
}