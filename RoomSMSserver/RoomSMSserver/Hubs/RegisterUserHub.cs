using Microsoft.AspNetCore.SignalR;

namespace RoomSMSserver.Hubs
{
    public class RegisterUserHub : Hub
    {
        public async Task RegisterUser(String username, String email, String password)
        {
            Console.WriteLine("Recived username: "+username+ "and email: "+ email + "and password: " + password);
            await Clients.Client(Context.ConnectionId).SendAsync("ReceiveRegistrationResult", "Got username: "+username+ "and email: " + email + "and password: " + password);
        }
    }
}
