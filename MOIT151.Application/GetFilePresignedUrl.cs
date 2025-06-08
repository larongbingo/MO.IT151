using Mediator;
using Microsoft.Extensions.DependencyInjection;
using MOIT151.Core;

namespace MOIT151.Application;

public static class GetFilePresignedUrl
{
    public class Handler(IFileRepository fileRepository, IStorageService storageService) : IRequestHandler<Request, Result<Dto>>
    {
        public async ValueTask<Result<Dto>> Handle(Request request, CancellationToken cancellationToken)
        {
            var file = await fileRepository.GetByUserIdAndFileIdAsync(request.UserId, request.FileId, cancellationToken);
            if (file is null || !file.IsExists)
            {
                return Result<Dto>.Failure("File doesn't exist");
            }

            var presignedUrl = await storageService.GetPresignedDownloadUriAsync(file.Key, cancellationToken);

            if (presignedUrl is null)
            {
                return Result<Dto>.Failure("Object doesn't exist");
            }

            return Result<Dto>.Success(new Dto(presignedUrl));
        }
    }

    public record Request(Guid UserId, Guid FileId) : IRequest<Result<Dto>>;

    public record Dto(string Uri);

    public static IServiceCollection AddGetFilePresignedUrl(this IServiceCollection services)
    {
        services.AddScoped<IRequestHandler<Request, Result<Dto>>, Handler>();
        return services;
    }
}