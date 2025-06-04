using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using MOIT151.Core;
using File = MOIT151.Core.File;

namespace MOIT151.Infrastructure.Data.Configurations;

public class FileConfiguration : IEntityTypeConfiguration<File>
{
    public void Configure(EntityTypeBuilder<File> builder)
    {
        builder.HasKey(file => file.Id);
        builder.Property(file => file.Id).ValueGeneratedNever();
        builder.HasOne<User>().WithMany().HasForeignKey(file => file.UserId);
    }
}