namespace RoomSMSserver.Models
{
    public class MemberInfoModel
    {
        public int userId { get; set; }
        public string username { get; set; }
        public string role { get; set; }
        public MemberInfoModel(int userId, string username, string role)
        {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }
    }
}
