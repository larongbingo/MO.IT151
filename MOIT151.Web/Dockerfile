FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
USER $APP_UID
WORKDIR /app
EXPOSE 8080
EXPOSE 8081

FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
ARG BUILD_CONFIGURATION=Release
WORKDIR /src
COPY ["MOIT151.Web/MOIT151.Web.csproj", "MOIT151.Web/"]
COPY ["MOIT151.Infrastructure/MOIT151.Infrastructure.csproj", "MOIT151.Infrastructure/"]
COPY ["MOIT151.Application/MOIT151.Application.csproj", "MOIT151.Application/"]
COPY ["MOIT151.Core/MOIT151.Core.csproj", "MOIT151.Core/"]
RUN dotnet restore "MOIT151.Web/MOIT151.Web.csproj"
COPY . .
WORKDIR "/src/MOIT151.Web"
RUN dotnet build "./MOIT151.Web.csproj" -c $BUILD_CONFIGURATION -o /app/build

FROM build AS publish
ARG BUILD_CONFIGURATION=Release
RUN dotnet publish "./MOIT151.Web.csproj" -c $BUILD_CONFIGURATION -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "MOIT151.Web.dll"]
