namespace MOIT151.Application;

public interface IStorageService
{
     Task<string?> GetPresignedUploadUriAsync(string key, CancellationToken cancellationToken = default);
     Task<bool> ObjectExistsByKeyAsync(string key, CancellationToken cancellationToken = default);
}