namespace MOIT151.Core;

public class File(Guid id, string uploadUri, Guid userId, bool isExists = false)
{
    public Guid Id { get; private set; } = id;
    public string UploadUri { get; private set; } = uploadUri;
    public Guid UserId { get; private set; } = userId;
    public bool IsExists { get; set; } = isExists;
    public string Key => $"{UserId}/{Id}";
}