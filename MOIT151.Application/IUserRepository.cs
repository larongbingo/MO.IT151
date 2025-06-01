using MOIT151.Core;

namespace MOIT151.Application;

public interface IUserRepository
{
    public Task AddAsync(User user, CancellationToken ct = default);
    public Task<User?> GetByExternalIdAsync(string externalId, CancellationToken ct = default);
    public Task<User?> GetByUsernameAsync(string username, CancellationToken ct = default);
}