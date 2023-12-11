namespace RoomSMSserver.Models
{
    public class RoomInfoModel
    {
        public int roomId { get; set; }
        public string roomName { get; set; }
        public string roomOwnerName { get; set; }
        public RoomInfoModel(int roomId, string roomName, string roomOwnerName)
        {
            this.roomId = roomId;
            this.roomName = roomName;
            this.roomOwnerName = roomOwnerName;
        }
    }
}
