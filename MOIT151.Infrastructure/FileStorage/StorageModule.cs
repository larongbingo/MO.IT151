﻿using Amazon.Runtime;
using Amazon.S3;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using MOIT151.Application;

namespace MOIT151.Infrastructure.FileStorage;

public static class StorageModule
{
    public static IServiceCollection AddMOIT151S3Storage(this IServiceCollection services)
    {
        services.AddSingleton<BasicAWSCredentials>(_ => new BasicAWSCredentials(
            Environment.GetEnvironmentVariable("MOIT151_S3_ACCESS_KEY"),
            Environment.GetEnvironmentVariable("MOIT151_S3_SECRET_KEY")));
        services.AddSingleton<AmazonS3Client>(
            s => new AmazonS3Client(s.GetService<BasicAWSCredentials>(), new AmazonS3Config()
            {
                ServiceURL = Environment.GetEnvironmentVariable("MOIT151_S3_SERVICE_URL"),
            }));
        services.AddScoped<IStorageService, StorageService>();
        return services;
    }
}