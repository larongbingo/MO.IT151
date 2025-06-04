namespace MOIT151.Application;

public interface IFileRepository
{
    Task AddAsync(MOIT151.Core.File file, CancellationToken ct);
}