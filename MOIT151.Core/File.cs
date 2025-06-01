namespace MOIT151.Core;

public class File(string name, string uri)
{
    public string Name { get; set; } = name;
    public string Uri { get; } = uri;
}