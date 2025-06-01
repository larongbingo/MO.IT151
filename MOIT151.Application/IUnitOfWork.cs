namespace MOIT151.Application;

public interface IUnitOfWork
{
    Task SaveChangesAsync(CancellationToken ct = default);
}