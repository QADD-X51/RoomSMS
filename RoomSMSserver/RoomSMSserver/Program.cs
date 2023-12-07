using RoomSMSserver.Database.Entities;
using RoomSMSserver.Hubs;
using Microsoft.EntityFrameworkCore;
using RoomSMSserver.Services;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddRazorPages();
builder.Services.AddSignalR();
builder.Services.AddDbContext<ProiectPdmContext>(options =>
    options.UseSqlServer(builder.Configuration["ConnectionStrings:DatabaseConnection"]));
builder.Services.AddScoped<IUserAuthorizationService, UserAuthorizationService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error");
}
app.UseStaticFiles();

app.UseRouting();

app.UseWebSockets();

app.UseAuthorization();

app.MapRazorPages();

app.UseEndpoints(endpoints =>
{
    endpoints.MapHub<RegisterUserHub>("/register");
});

app.Run();
