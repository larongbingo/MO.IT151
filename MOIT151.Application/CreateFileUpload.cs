using Mediator;
using Microsoft.Extensions.DependencyInjection;
using MOIT151.Core;
using File = MOIT151.Core.File;

namespace MOIT151.Application;

public static class CreateFileUpload
{
    public sealed class Handler(IStorageService storageService, IFileRepository fileRepository, IUnitOfWork unitOfWork) 
        : IRequestHandler<Request, Result<Dto>>   
    {
        public async ValueTask<Result<Dto>> Handle(Request request, CancellationToken cancellationToken)
        {
            var fileId = Guid.NewGuid();
            var key = File.GenerateKey(request.UserId, fileId);
            var requestUri = await storageService.GetPresignedUploadUriAsync(key, cancellationToken);
            if (requestUri is null)
                return Result<Dto>.Failure("Upload URI not found");

            var newFile = new File(fileId, requestUri, request.UserId);
            
            await fileRepository.AddAsync(newFile, cancellationToken);
            
            await unitOfWork.SaveChangesAsync(cancellationToken);
            
            return Result<Dto>.Success(new Dto(requestUri, newFile.Id));
        }
    }
    
    public record Request(Guid UserId) : IRequest<Result<Dto>>;

    public record Dto(string Uri, Guid FileId);
    
    public static IServiceCollection AddCreateFileUploadUseCases(this IServiceCollection services)
    {
        services.AddScoped<IRequestHandler<Request, Result<Dto>>, Handler>();
        
        return services;
    }
}