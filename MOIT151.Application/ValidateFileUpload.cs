using FluentValidation;
using Mediator;
using Microsoft.Extensions.DependencyInjection;
using MOIT151.Core;

namespace MOIT151.Application;

public static class ValidateFileUpload 
{
    public class Handler(IValidator<Request> validator, IFileRepository fileRepository, IStorageService storageService, IUnitOfWork unitOfWork) : IRequestHandler<Request, Result<Dto>>
    {
        public async ValueTask<Result<Dto>> Handle(Request request, CancellationToken cancellationToken)
        {
            var validation = await validator.ValidateAsync(request, cancellationToken);
            if (!validation.IsValid)
            {
                return Result<Dto>.Failure([.. validation.Errors.Select(x => x.ErrorMessage)]);
            }

            var file = await fileRepository.GetByUserIdAndFileIdAsync(request.UserId, request.FileId, cancellationToken);

            if (file is null)
            {
                return Result<Dto>.Failure("File doesn't exist");
            }
            
            var isFileExists = await storageService.ObjectExistsByKeyAsync(file.Key, cancellationToken);
            if (!isFileExists)
            {
                return Result<Dto>.Failure("S3 doesn't have the file, possibly there's no file uploaded`");
            }

            if (file.IsExists)
            {
                return Result<Dto>.Failure("File is already validated to be uploaded");
            }

            file.IsExists = true;

            await fileRepository.UpdateAsync(file, cancellationToken);

            await unitOfWork.SaveChangesAsync(cancellationToken);

            return Result<Dto>.Success(new Dto());
        }
    }

    public class Validator : AbstractValidator<Request>
    {
        public Validator(IFileRepository fileRepository, IUserRepository userRepository)
        {
            RuleFor(x => x.UserId).NotEmpty();
            RuleFor(x => x.FileId).NotEmpty();
        }
    }

    public record Request(Guid UserId, Guid FileId) : IRequest<Result<Dto>>;

    public record Dto();

    public static IServiceCollection AddValidateFileUpload(this IServiceCollection services)
    {
        services.AddScoped<IRequestHandler<Request, Result<Dto>>, Handler>();
        services.AddScoped<IValidator<Request>, Validator>();
        return services;
    }
}