namespace RoomSMSserver.Models
{
    public class MessageModel
    {
        public string message { get; set; }
        public string senderUsername { get; set; }
        public DateTime sentDate { get; set; }
        public MessageModel(string message, string senderUsername, DateTime sentDate)
        {
            this.message = message;
            this.senderUsername = senderUsername;
            this.sentDate = sentDate;
        }
    }
}
