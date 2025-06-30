using MOIT151.Application;
using MOIT151.Core;
using Refit;
using File = MOIT151.Core.File;

namespace MOIT151.Mobile;

public interface IMoit151WebClient
{
    [Post("/api/user")]
    public Task<IApiResponse> CreateAccountAsync([Authorize] string accessToken, [Body] CreateAccountDto dto, CancellationToken cancellationToken = default);
    public record CreateAccountDto(string Username);
    
    [Get("/api/user")]
    public Task<User?> GetAccountAsync([Authorize] string accessToken, CancellationToken cancellationToken = default);

    [Post("/api/file")]
    public Task<CreateFileUpload.Dto> GetUploadUrlAsync([Authorize] string accessToken,
        CancellationToken cancellationToken = default);

    [Get("/api/file")]
    public Task<List<File>> GetFilesAsync([Authorize] string accessToken,
        CancellationToken cancellationToken = default);
}