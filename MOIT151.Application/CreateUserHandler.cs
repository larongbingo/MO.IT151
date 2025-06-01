using FluentValidation;
using Mediator;
using Microsoft.Extensions.DependencyInjection;
using MOIT151.Core;

namespace MOIT151.Application;

public static class CreateUser
{
    public class Handler(Validator validator, IUserRepository userRepository, IUnitOfWork unitOfWork) : IRequestHandler<Request, Result<Dto>>
    {
        public async ValueTask<Result<Dto>> Handle(Request request, CancellationToken cancellationToken)
        {
            var result = await validator.ValidateAsync(request, cancellationToken);
            if (!result.IsValid)
            {
                return Result<Dto>.Failure([.. result.Errors.Select(x => x.ErrorMessage)]);
            }

            var newUser = new User(Guid.NewGuid(), request.Username, request.ExternalId);

            await userRepository.AddAsync(newUser, cancellationToken);

            await unitOfWork.SaveChangesAsync(cancellationToken);

            return Result<Dto>.Success(new Dto(newUser.Id));
        }
    }

    public class Validator : AbstractValidator<Request>
    {
        public Validator(IUserRepository userRepository)
        {
            RuleFor(x => x.Username).NotEmpty();
            RuleFor(x => x.ExternalId).NotEmpty();
            RuleFor(x => x).CustomAsync(async (x, context, ct) =>
            {
                var userWithSameExternalId = await userRepository.GetByExternalIdAsync(x.ExternalId, ct);
                if (userWithSameExternalId is not null)
                {
                    context.AddFailure("ExternalId already has existing user");
                }

                var userWithSameUsername = await userRepository.GetByUsernameAsync(x.Username, ct);
                if (userWithSameUsername is not null)
                {
                    context.AddFailure($"{x.Username} is already taken");
                }
            });
        }
    }

    public record Request(string ExternalId, string Username) : IRequest<Result<Dto>>;

    public record Dto(Guid Id);

    public static IServiceCollection AddCreateUserUseCase(this IServiceCollection serviceCollection)
    {
        serviceCollection.AddTransient<IValidator<Request>, Validator>();
        serviceCollection.AddTransient<IRequestHandler<Request, Result<Dto>>, Handler>();

        return serviceCollection;
    }
}