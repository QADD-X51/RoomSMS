using Microsoft.AspNetCore.SignalR;

namespace RoomSMSserver.Hubs
{
    public class RegisterUserHub : Hub
    {
        public async Task<string> RegisterUser(String username, String email, String password)
        {
            Console.WriteLine("Recived username: "+username+ " and email: "+ email + " and password: " + password);
            return "Got username: "+username+ " and email: " + email + " and password: " + password;
        }
    }
}
