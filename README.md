# MOIT151

MS2 Project 

A prototype on the "Financial Analysis" of their case study

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