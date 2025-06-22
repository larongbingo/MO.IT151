namespace MOIT151.Application;

public interface IFileRepository
{
    Task AddAsync(MOIT151.Core.File file, CancellationToken ct = default);
    Task UpdateAsync(MOIT151.Core.File file, CancellationToken ct = default);
    Task<MOIT151.Core.File?> GetByUserIdAndFileIdAsync(Guid userId, Guid fileId, CancellationToken ct = default);
    Task<List<MOIT151.Core.File>> GetListByUserIdAsync(Guid userId, CancellationToken ct = default);
    Task DeleteInvalidFilesAsync(CancellationToken ct = default);
}