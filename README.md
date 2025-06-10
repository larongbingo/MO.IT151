# MOIT151

MS2 Project 

A prototype on the "Financial Analysis" of their case study

## Documentation
### What you setup and why
We have implemented the File Upload and Download. As mentioned in the MS1 documentation, moving those long running 
processes would reduce loads on the application server. We have implemented the prototype as a Web API using C# and 
Dotnet. 

We also integrated Auth0 for authentication, Cloudflare R2 for file storage and Neon Postgres for database.

### Challenges you encountered
The team is unfamiliar with development in C# and dotnet. As such, only one developer took the lead on implementing the 
prototype.

There's additional challenges on reading up on S3, AWS SDK for .Net and Cloudflare R2.

### What worked and what needs refinement
There's a very smelly implementation on validating files uploaded. Ideally, there would be some sort of Webhooks to
check on file upload status, but unfortunately, no webhooks. Any additional workaround doesn't exist in Cloudflare R2. 

As such, the current Web API implementation is to call a separate Web API that would check to the S3 if there's a file 
uploaded.


## How to run
1. Setup the needed env vars in the .env.sample
2. Install Dotnet 10
3. Run the migration scripts to the database (if needed)
   1. Install efcore tool (if needed) 
      - <code>dotnet tool install --global dotnet-ef</code>
   2. Run migration (assuming at root project directory)
      - <code>dotnet ef database update --project ./MOIT151.Infrastructure</code>
4. Run the webapi (assuming at root project directory)
   - <code>dotnet run --project ./MOIT151.Web</code>

## Note on running Source command with .env
<code>
set -o allexport
source ./.env
set +a allexport
</code>