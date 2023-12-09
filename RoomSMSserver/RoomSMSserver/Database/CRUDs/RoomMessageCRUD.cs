using RoomSMSserver.Database.Entities;

namespace RoomSMSserver.Database.CRUDs
{
    public class RoomMessageCRUD : ICRUD<RoomMessage, int>
    {
        ProiectPdmContext dbContext;
        public RoomMessageCRUD(ProiectPdmContext dbContext)
        {
            this.dbContext = dbContext;
        }
        public void Add(RoomMessage addedRoomMessage)
        {
            dbContext.RoomMessages.Add(addedRoomMessage);
            dbContext.SaveChanges();
        }
        public void Update(int id,RoomMessage updatedRoomMessage) 
        {
            var roomMessages = dbContext.RoomMessages;
            dbContext.Entry(roomMessages.SingleOrDefault(x => x.Id == id)).CurrentValues.SetValues(updatedRoomMessage);
            dbContext.SaveChanges();
        }
        public void ChangeDeletedState(int id, bool isDeleted)
        {
            var roomMessages = dbContext.RoomMessages;
            RoomMessage updatedRoomMessage = roomMessages.SingleOrDefault(x => x.Id == id);
            updatedRoomMessage.IsDeleted = isDeleted;
            dbContext.Entry(roomMessages.SingleOrDefault(x => x.Id == id)).CurrentValues.SetValues(updatedRoomMessage);
            dbContext.SaveChanges();
        }
    }
}
