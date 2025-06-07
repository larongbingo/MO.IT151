using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MOIT151.Infrastructure.Data.Migrations
{
    /// <inheritdoc />
    public partial class UpdateFileEntityToBool : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Uri",
                table: "Files");

            migrationBuilder.AddColumn<bool>(
                name: "IsExists",
                table: "Files",
                type: "boolean",
                nullable: false,
                defaultValue: false);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "IsExists",
                table: "Files");

            migrationBuilder.AddColumn<string>(
                name: "Uri",
                table: "Files",
                type: "text",
                nullable: true);
        }
    }
}
