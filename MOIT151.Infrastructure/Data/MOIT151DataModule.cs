using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using MOIT151.Application;
using MOIT151.Infrastructure.Data.Repositories;

namespace MOIT151.Infrastructure.Data;

public static class MOIT151DataModule
{
    public static IServiceCollection AddMOIT151Data(this IServiceCollection services)
    {
        services.AddDbContext<MOIT151Context>();
        services.AddScoped<IUnitOfWork, UnitOfWork>();
        services.AddScoped<IUserRepository, UserRepository>();
        services.AddScoped<IFileRepository, FileRepository>();
        return services;
    }
}