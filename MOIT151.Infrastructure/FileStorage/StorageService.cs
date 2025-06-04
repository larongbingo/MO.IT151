using Amazon.S3;
using Amazon.S3.Model;
using MOIT151.Application;

namespace MOIT151.Infrastructure.FileStorage;

public class StorageService(AmazonS3Client s3Client) : IStorageService
{
    public async Task<string?> GetPresignedUploadUriAsync(string key, CancellationToken cancellationToken)
    {
        var presignRequest = new GetPreSignedUrlRequest()
        {
            BucketName = Environment.GetEnvironmentVariable("MOIT151_S3_BUCKET_NAME"),
            Key = key,
            Verb = HttpVerb.PUT,
            Expires = DateTime.Now.AddHours(1),
        };
        
        var path = await s3Client.GetPreSignedURLAsync(presignRequest);
        
        return path;
    }
}